package pl.tpolgrabia.urbanexplorer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.callbacks.PanoramioResponseCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.PanoramioResponseStatus;
import pl.tpolgrabia.urbanexplorer.callbacks.ProviderStatusCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioCacheDto;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.utils.NetUtils;
import pl.tpolgrabia.urbanexplorer.utils.PanoramioUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements Refreshable {

    private static final Logger lg = LoggerFactory.getLogger(HomeFragment.class);

    public static final String TAG = HomeFragment.class.getSimpleName();
    private static final String PHOTO_LIST = "PHOTO_LIST_KEY";
    private boolean initialized = false;

    private View inflatedView;
    private Long pageId;
    private Semaphore loading;
    private ArrayList<PanoramioImageInfo> photos;
    private boolean noMorePhotos;

    public int getPanoramioBulkDataSize() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String sValue = sharedPrefs.getString(AppConstants.PANORAMIO_BULK_SIZE_KEY,
            String.valueOf(AppConstants.PANORAMIO_BULK_SIZE_DEF_VALUE));
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            lg.warn("Invalid panoramio bulk data size {}", sValue, e);
            return AppConstants.PANORAMIO_BULK_SIZE_DEF_VALUE;
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lg.trace("onCreate {}", System.identityHashCode(this));
        pageId = 1L;
        loading = new Semaphore(1, true);
        noMorePhotos = false;

        updateLocationInfo();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLocationCallback();
    }

    private void initLocationCallback() {
        MainActivity mainActivity = ((MainActivity) getActivity());
        mainActivity.getLocationCallback()
            .addCallback(new StandardLocationListenerCallback() {
                @Override
                public void callback(Location location) {
                    noMorePhotos = false;
                    photos = new ArrayList<>();
                    updateLocationInfo();
                    try {
                        fetchAdditionalPhotos();
                    } catch (InterruptedException e) {
                        lg.error("Failed trying acquring lock to load photos", e);
                    }
                }
            });
        mainActivity.getLocationCallback()
                .addProviderCallback(new ProviderStatusCallback() {
                    @Override
                    public void callback(String provider, boolean enabled) {
                        if (enabled) {
                            lg.trace("Handling provider enabling - refreshing panoramio listing");
                            fetchPanoramioPhotos();
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        lg.trace("TAG: {}", getTag());
        for (Fragment frag : getFragmentManager().getFragments()) {
            if (frag == null) {
                lg.trace("Fragment is null");
            } else {
                lg.trace("Fragment TAG {}", frag.getTag());
            }
        }
        inflatedView = inflater.inflate(R.layout.fragment_home, container, false);
        ListView locations = (ListView)inflatedView.findViewById(R.id.locations);
        final ListView finalLocations = locations;
        locations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
                PanoramioAdapter panAdapter = (PanoramioAdapter) finalLocations.getAdapter();
                PanoramioImageInfo photoInfo = panAdapter.getItem(pos);
                MainActivity activity = (MainActivity) getActivity();
                activity.switchToPhoto(photoInfo);
                return false;
            }
        });

        initialized = true;

        lg.trace("Saved instance state {}", savedInstanceState);
        if (photos == null) {
            if (savedInstanceState == null) {
                lg.trace("Saved instance state is null");
                photos = new ArrayList<>();
            }
            else {
                final Serializable serializable = savedInstanceState.getSerializable(PHOTO_LIST);
                lg.trace("Photo list serializable {}", serializable);
                photos = (ArrayList<PanoramioImageInfo>) serializable;
                if (photos == null) {
                    photos = new ArrayList<>();
                }
            }
        }

        if (photos == null || photos.isEmpty()) {
            // maybe we find something in our cache file
            try (Reader br =
                new InputStreamReader(
                    new FileInputStream(
                        new File(getActivity().getCacheDir(),
                            AppConstants.PANORAMIO_CACHE_FILENAME)))) {
                PanoramioCacheDto dto = new Gson().fromJson(new JsonReader(br), PanoramioCacheDto.class);
                if (dto != null) {
                    photos = new ArrayList<>(dto.getPanoramioImages());
                    lg.trace("Photos size from I/O cache is {}", photos.size());
                } else {
                    lg.trace("Sorry, photos I/O cache is null");
                }

            } catch (FileNotFoundException e) {
                lg.error("File not found", e);
            } catch (IOException e) {
                lg.error("I/O error", e);
            } catch (Throwable t) {
                lg.error("Throwable", t);
            }
            lg.trace("I've read photos from I/O cache");
        }

        locations.setAdapter(new PanoramioAdapter(getActivity(), R.layout.location_item, photos));
        lg.trace("Photos initialized {}", photos);

        locations.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }


            @Override
            public void onScroll(AbsListView view,
                                 int firstVisibleItem,
                                 int visibleItemCount,
                                 int totalItemCount) {

                try {

                    if (firstVisibleItem <= 0) {
                        // scrolled to the top
                        lg.trace("Scrolled to the top");
                    }

                    if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                        lg.trace("Scrolled to the bottom");
                        // scrolled to the bottom
                        final View fragView = getView();
                        if (fragView == null) {
                            lg.trace("Frag still not initialized");
                            return;
                        }
                        fetchAdditionalPhotos();

                    }

                } catch (InterruptedException e) {
                    lg.error("Aquiring lock interrupted exception", e);
                }

            }
        });
        ;

        return inflatedView;
    }

    private void fetchAdditionalPhotos() throws InterruptedException {

        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.trace("Activity shouldn't be null. No headless fragment");
            return;
        }

        MainActivity mainActivity = (MainActivity)getActivity();

        if (noMorePhotos) {
            lg.trace("No more photos - last query was zero result");
            mainActivity.hideProgress();
            return;
        }

        if (!initialized) {
            lg.trace("Fetching additional photos blocked till system is initialized");
            mainActivity.hideProgress();
            return;
        }


        if (getView() == null) {
            lg.trace("Application still not initialized");
            mainActivity.hideProgress();
            return;
        }


        LocationManager locationService = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        final Location location = NetUtils.getLastKnownLocation(activity);

        if (location == null) {
            lg.info("Location still not available");
            Toast.makeText(activity, "Location still not available", Toast.LENGTH_SHORT).show();
            mainActivity.hideProgress();
            return;
        }

        lg.trace("Fetching additional photos. Trying loading acquirng lock");
        if (!loading.tryAcquire()) {
            lg.info("Fetching additional photos. Loading in progress");
            return;
        }


        int offset = photos.size();
        lg.debug("Fetching additional photos offset: {}, count: {}", offset, getPanoramioBulkDataSize());

        PanoramioUtils.fetchPanoramioImages(
            activity,
            location.getLatitude(),
            location.getLongitude(),
            fetchRadiusX(),
            fetchRadiusY(),
            (long)(offset),
            fetchLocationPageSize(),
            new PanoramioResponseCallback() {
                @Override
                public void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount) {
                    try {
                        lg.debug("Fetched with status: {}, images: {}, count: {}", status, images, imagesCount);
                        if (status != PanoramioResponseStatus.SUCCESS) {
                            return;
                        }

                        final View view = getView();
                        if (view == null) {
                            lg.debug("View still not initialized");
                            return;
                        }

                        ListView locations = (ListView) view.findViewById(R.id.locations);
                        if (locations == null) {
                            lg.trace("Empty locations");
                            return;
                        }
                        ArrayAdapter<PanoramioImageInfo> adapter = (ArrayAdapter<PanoramioImageInfo>) locations.getAdapter();
                        photos.addAll(images);
                        if (photos.isEmpty()) {
                            Toast.makeText(getActivity(), "No results", Toast.LENGTH_SHORT).show();
                        }
                        noMorePhotos = images.isEmpty();
                        if (adapter == null) {
                            locations.setAdapter(new PanoramioAdapter(activity, R.id.list_item, images));
                        } else {
                            adapter.addAll(images);
                        }

                        // TODO we can think about removing first items also and last if the number
                        // TODO of items exceeds the limit (to save the memory)

                        lg.debug("Finished Fetching additional photos count: {}", photos.size());

                    } finally {
                        lg.trace("Releasing fetching lock");
                        loading.release();
                    }

                }
            }

        );
    }

    public void fetchPanoramioPhotos() {
        lg.trace("Fetch panoramio photos");
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity shouldn't be null. It isn't headless fragment");
            return;
        }

        MainActivity mainActivity = (MainActivity) getActivity();

        final Location location = NetUtils.getLastKnownLocation(activity);
        if (location == null) {
            lg.info("Location is still not available");
            mainActivity.hideProgress();
            Toast.makeText(getActivity(), "Location is still not available", Toast.LENGTH_SHORT).show();
            return;
        }
        Double radiusX = fetchRadiusX();
        Double radiusY = fetchRadiusY();
        PanoramioUtils.fetchPanoramioImages(
            activity,
            location.getLatitude(),
            location.getLongitude(),
            radiusX,
            radiusY,
            (pageId - 1) * fetchLocationPageSize(),
            fetchLocationPageSize(),
            new PanoramioResponseCallback() {
                @Override
                public void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount) {
                    Long pageSize = fetchLocationPageSize();

                    ArrayAdapter<PanoramioImageInfo> adapter = new PanoramioAdapter(activity,
                        R.layout.location_item,
                        images);

                    if (images.isEmpty()) {
                        Toast.makeText(getActivity(), "No results", Toast.LENGTH_SHORT).show();
                    }
                    ListView locations = (ListView)getView().findViewById(R.id.locations);
                    locations.setAdapter(adapter);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity == null) {
                        return;
                    }

                    mainActivity.hideProgress();
                }
            }
        );
    }

    private Long fetchLocationPageSize() {
        return new Long(getPanoramioBulkDataSize());
    }

    private Double fetchRadiusX() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String pref_panoramio_radiusx = sharedPreferences.getString(
            "pref_panoramio_radiusx",
            String.valueOf(AppConstants.PAMNORAMIO_DEF_RADIUSX));
        lg.debug("Panoramio radiusx pref equals {}", pref_panoramio_radiusx);
        return Double.parseDouble(
            pref_panoramio_radiusx);
    }

    private Double fetchRadiusY() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String pref_panoramio_radiusy = sharedPreferences.getString(
            "pref_panoramio_radiusy",
            String.valueOf(AppConstants.PAMNORAMIO_DEF_RADIUSY));
        lg.debug("Panoramio radiusy pref equals {}", pref_panoramio_radiusy);
        return Double.parseDouble(
            pref_panoramio_radiusy);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Panoramio search");
        lg.trace("onResume");
        updateLocationInfo();
    }

    public void updateLocationInfo() {
        lg.trace("Update locations info");
        final View view = getView();
        if (view == null) {
            lg.warn("Fragment has no view");
            return;
        }
        TextView locationInfo = (TextView) view.findViewById(R.id.locationInfo);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity should'nt be null. No headless fragment");
            return;
        }
        Location currLocation = NetUtils.getLastKnownLocation(activity);
        lg.trace("Current location: {}, locationInfo: {}", currLocation, locationInfo);
        if (currLocation != null && locationInfo != null) {
            // update home fragment's location info
            locationInfo.setText("Your current location: ("
                + currLocation.getLatitude()
                + "," +
                currLocation.getLongitude() + ")");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        lg.trace("onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lg.trace("onDestroy");

        File cacheDir = getActivity().getCacheDir();
        try (BufferedWriter br = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(
                    new File(cacheDir, AppConstants.PANORAMIO_CACHE_FILENAME))))) {

            PanoramioCacheDto dto = new PanoramioCacheDto();
            dto.setPanoramioImages(photos);

            Location location = NetUtils.getLastKnownLocation(getActivity());
            if (location != null) {
                dto.setLongitude(location.getLongitude());
                dto.setLatitude(location.getLatitude());
                dto.setAltitude(location.getAltitude());
            }

            dto.setFetchedAt(new GregorianCalendar().getTime());
            // FIXME this should be a fetch time, not persist time

            new Gson().toJson(dto, br);

        } catch (FileNotFoundException e) {
            lg.error("File not found", e);
        } catch (IOException e) {
            lg.error("I/O Exception", e);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        lg.trace("Saving state");
        outState.putSerializable(PHOTO_LIST, photos);
        lg.trace("Saved photos: {}", photos);
    }

    @Override
    public void onStop() {
        super.onStop();

        lg.trace("onStop {}", System.identityHashCode(this));
    }

    @Override
    public void onStart() {
        super.onStart();

        lg.trace("onStart {}", System.identityHashCode(this));
    }

    @Override
    public void refresh() {
        fetchPanoramioPhotos();
    }
}

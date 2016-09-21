package pl.tpolgrabia.urbanexplorer.fragments;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.callbacks.*;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioCacheDto;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.handlers.PanoramioItemLongClickHandler;
import pl.tpolgrabia.urbanexplorer.handlers.PanoramioLocationsScrollListener;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
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
    private String currentGeocodedLocation;

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
            .addCallback(new PanoramioLocationCallback(this));
        mainActivity.getLocationCallback()
                .addProviderCallback(new PanoramioProviderCallback(this));
    }

    public void updateGeocodedLocation() {
        if (getActivity() == null) {
            lg.debug("Activity still not attached");
            return;
        }

        Location currLocation = LocationUtils.getLastKnownLocation(getActivity());
        lg.debug("Current location is {}", currLocation);
        if (currLocation == null) {
            lg.debug("Current location is not available");
            return;
        }

        LocationUtils.getGeoCodedLocation(getActivity(),
            currLocation.getLatitude(),
            currLocation.getLongitude(),
            new GeocodedLocationCallback(this));

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
        locations.setOnItemLongClickListener(new PanoramioItemLongClickHandler(this, finalLocations));

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

        if (photos.isEmpty()) {
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

        lg.trace("Photos size: {}", photos.size());
        locations.setOnScrollListener(new PanoramioLocationsScrollListener(this));
        return inflatedView;
    }

    public void fetchAdditionalPhotos() throws InterruptedException {

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


        final Location location = LocationUtils.getLastKnownLocation(activity);

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
            new FetchAdditionalPanoramioPhotosCallback(this, activity)

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

        final Location location = LocationUtils.getLastKnownLocation(activity);
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
            new FetchPanoramioPhotosCallback(this, activity)
        );
    }

    private Long fetchLocationPageSize() {
        return Long.valueOf(getPanoramioBulkDataSize());
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
        if (currentGeocodedLocation != null) {
            updateLocationInfo();
        }
        else {
            updateGeocodedLocation();
        }
    }

    public void updateLocationInfo() {
        lg.trace("Update locations info");
        final View view = getView();
        if (view == null) {
            lg.warn("Fragment has no view");
            return;
        }
        final TextView locationInfo = (TextView) view.findViewById(R.id.locationInfo);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity should'nt be null. No headless fragment");
            return;
        }
        final Location currLocation = LocationUtils.getLastKnownLocation(activity);
        lg.trace("Current location: {}, locationInfo: {}", currLocation, locationInfo);
        locationInfo.setText(currentGeocodedLocation);

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
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(
                        new File(cacheDir, AppConstants.PANORAMIO_CACHE_FILENAME))));

            PanoramioCacheDto dto = new PanoramioCacheDto();
            dto.setPanoramioImages(photos);

            Location location = LocationUtils.getLastKnownLocation(getActivity());
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
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    lg.error("I/O error during photos cache saving", e);
                }
            }
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

    public Semaphore getLoading() {
        return loading;
    }

    public void setNoMorePhotos(boolean noMorePhotos) {
        this.noMorePhotos = noMorePhotos;
    }

    public int getPhotosCount() {
        return photos.size();
    }

    public void addPhotos(List<PanoramioImageInfo> images) {
        photos.addAll(images);
    }

    public void setCurrentGeocodedLocation(String currentGeocodedLocation) {
        this.currentGeocodedLocation = currentGeocodedLocation;
    }

    public void setPhotos(ArrayList<PanoramioImageInfo> photos) {
        this.photos = photos;
    }

}

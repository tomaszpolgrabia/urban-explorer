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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.callbacks.PanoramioResponseCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.PanoramioResponseStatus;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.PanoramioUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  {

    private static final String CLASS_TAG = HomeFragment.class.getSimpleName();

    private static final int PANORAMIA_BULK_DATA_SIZE = 10;
    public static final String TAG = HomeFragment.class.getSimpleName();
    public static final int FRAG_ID = 1;
    private LocationManager locationService;
    private boolean initialized = false;

    private View inflatedView;
    private Long pageId;
    private Semaphore loading;
    private List<PanoramioImageInfo> photos;
    private boolean noMorePhotos;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(CLASS_TAG, "onCreate");
        pageId = 1L;
        loading = new Semaphore(1, true);
        photos = new ArrayList<>();
        noMorePhotos = false;

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
                        Log.e(CLASS_TAG, "Failed trying acquring lock to load photos", e);
                    }
                }
            });
    }

    private Double safeParseDouble(CharSequence text) {
        if (text == null) {
            return null;
        }

        try {
            return Double.parseDouble(text.toString());
        } catch (NumberFormatException e) {
            Log.w(CLASS_TAG, "Wrong number format", e);
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
                        Log.v(CLASS_TAG, "Scrolled to the top");
                    }

                    if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                        Log.v(CLASS_TAG, "Scrolled to the bottom");
                        // scrolled to the bottom
                        final View fragView = getView();
                        if (fragView == null) {
                            Log.v(CLASS_TAG, "Frag still not initialized");
                            return;
                        }
                        fetchAdditionalPhotos();

                    }

                } catch (InterruptedException e) {
                    Log.e(CLASS_TAG, "Aquiring lock interrupted exception", e);
                }

            }
        });

        initialized = true;

        return inflatedView;
    }

    private void fetchAdditionalPhotos() throws InterruptedException {

        if (noMorePhotos) {
            Log.v(CLASS_TAG, "No more photos - last query was zero result");
            return;
        }

        if (!initialized) {
            Log.v(CLASS_TAG, "Fetching additional photos blocked till system is initialized");
            return;
        }


        if (getView() == null) {
            Log.v(CLASS_TAG, "Application still not initialized");
            return;
        }

        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.w(CLASS_TAG, "Activity shouldn't be null. No headless fragment");
            return;
        }

        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(activity));

        if (location == null) {
            Log.i(CLASS_TAG, "Location still not available");
            Toast.makeText(activity, "Location still not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.v(CLASS_TAG, "Fetching additional photos. Trying loading acquirng lock");
        if (!loading.tryAcquire()) {
            Log.v(CLASS_TAG, "Fetching additional photos. Loading in progress");
            return;
        }


        int offset = photos.size();
        Log.v(CLASS_TAG, "Fetching additional photos offset: " + offset + ", count: " + PANORAMIA_BULK_DATA_SIZE);

        PanoramioUtils.fetchPanoramioImages(
            activity,
            location.getLatitude(),
            location.getLongitude(),
            fetchRadiusX(),
            fetchRadiusY(),
            (long)(offset + PANORAMIA_BULK_DATA_SIZE),
            fetchLocationPageSize(),
            new PanoramioResponseCallback() {
                @Override
                public void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount) {
                    Log.v(CLASS_TAG, "Fetched with status: " + status + ", images: " + images + ", count: " +
                        imagesCount);
                    if (status != PanoramioResponseStatus.SUCCESS) {
                        return;
                    }

                    ListView locations = (ListView) getView().findViewById(R.id.locations);
                    ArrayAdapter<PanoramioImageInfo> adapter = (ArrayAdapter<PanoramioImageInfo>) locations.getAdapter();
                    photos.addAll(images);
                    noMorePhotos = images.isEmpty();
                    if (adapter == null) {
                        locations.setAdapter(new PanoramioAdapter(activity, R.id.list_item, images));
                    } else {
                        adapter.addAll(images);
                    }

                    // TODO we can think about removing first items also and last if the number
                    // TODO of items exceeds the limit (to save the memory)

                    Log.v(CLASS_TAG, "Finished Fetching additional photos count: " + photos.size());

                    loading.release();

                }
            }

        );
    }

    public void fetchPanoramioPhotos() {
        Log.v(CLASS_TAG, "Fetch panoramio photos");
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.w(CLASS_TAG, "Activity shouldn't be null. It isn't headless fragment");
            return;
        }

        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(activity));
        if (location == null) {
            Log.i(CLASS_TAG, "Location is still not available");
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
                    Long start = (pageId - 1) * pageSize + 1;
                    Long end = pageId * pageSize;

                    ArrayAdapter<PanoramioImageInfo> adapter = new PanoramioAdapter(activity,
                        R.layout.location_item,
                        images);
                    ListView locations = (ListView)getView().findViewById(R.id.locations);
                    locations.setAdapter(adapter);
                }
            }
        );
    }

    private Long fetchLocationPageSize() {
        return new Long(PANORAMIA_BULK_DATA_SIZE);
    }

    private Double fetchRadiusX() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String pref_panoramio_radiusx = sharedPreferences.getString(
            "pref_panoramio_radiusx",
            String.valueOf(AppConstants.PAMNORAMIO_DEF_RADIUSX));
        Log.d(CLASS_TAG, "Panoramio radiusx pref equals " + pref_panoramio_radiusx);
        return Double.parseDouble(
            pref_panoramio_radiusx);
    }

    private Double fetchRadiusY() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String pref_panoramio_radiusy = sharedPreferences.getString(
            "pref_panoramio_radiusy",
            String.valueOf(AppConstants.PAMNORAMIO_DEF_RADIUSY));
        Log.d(CLASS_TAG, "Panoramio radiusy pref equals " + pref_panoramio_radiusy);
        return Double.parseDouble(
            pref_panoramio_radiusy);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(CLASS_TAG, "onResume");
        updateLocationInfo();
    }

    public void updateLocationInfo() {
        Log.v(CLASS_TAG, "Update locations info");
        final View view = getView();
        if (view == null) {
            Log.wtf(CLASS_TAG, "Fragment has no view");
            return;
        }
        TextView locationInfo = (TextView) view.findViewById(R.id.locationInfo);
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            Log.w(CLASS_TAG, "Activity should'nt be null. No headless fragment");
            return;
        }
        locationService = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        Location currLocation = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(activity));
        Log.v(CLASS_TAG, "Current location: " + currLocation + ", locationInfo: " + locationInfo);
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
        Log.v(CLASS_TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(CLASS_TAG, "onDestroy");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.v(CLASS_TAG, "Saving state");
    }

}

package pl.tpolgrabia.urbanexplorer.fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
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
    private LocationManager locationService;
    private AQuery aq;
    private boolean initialized = false;

    private View inflatedView;
    private Long pageId = 1L;
    private Semaphore loading = new Semaphore(1, true);
    private List<PanoramioImageInfo> photos = new ArrayList<>();
    private String locationProvider;
    private boolean noMorePhotos = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(getActivity());
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

        if (locationProvider == null) {
            Log.i(CLASS_TAG, "Location providers not available");
            Toast.makeText(getActivity(), "Location provicers not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getView() == null) {
            Log.v(CLASS_TAG, "Application still not initialized");
            return;
        }

        final Location location = locationService.getLastKnownLocation(locationProvider);

        if (location == null) {
            Log.i(CLASS_TAG, "Location still not available");
            Toast.makeText(getActivity(), "Location still not available", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.v(CLASS_TAG, "Fetching additional photos. Trying loading acquirng lock");
        if (!loading.tryAcquire()) {
            Log.v(CLASS_TAG, "Fetching additional photos. Loading in progress");
            return;
        }


        int offset = photos.size();

        Log.v(CLASS_TAG, "Fetching additional photos offset: " + offset + ", count: " + PANORAMIA_BULK_DATA_SIZE);
        Log.d(CLASS_TAG, "Fetching location using " + locationProvider + " provider");

        PanoramioUtils.fetchPanoramioImages(
            getActivity(),
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
                        locations.setAdapter(new PanoramioAdapter(getActivity(), R.id.list_item, images));
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

    private void fetchPanoramioPhotos() {
        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
        Double radiusX = fetchRadiusX();
        Double radiusY = fetchRadiusY();
        PanoramioUtils.fetchPanoramioImages(
            getActivity(),
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

                    ArrayAdapter<PanoramioImageInfo> adapter = new PanoramioAdapter(getActivity(),
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
        final TextView radiusxTextView = (TextView) inflatedView.findViewById(R.id.location_xrange);
        return safeParseDouble(radiusxTextView.getText());
    }

    private Double fetchRadiusY() {
        final TextView radiusyTextView = (TextView) inflatedView.findViewById(R.id.location_yrange);
        return safeParseDouble(radiusyTextView.getText());
    }

    @Override
    public void onResume() {
        super.onResume();
        locationProvider = LocationUtils.getDefaultLocation(getActivity());
        updateLocationInfo();
    }

    public void updateLocationInfo() {
        final View view = getView();
        if (view == null) {
            Log.wtf(CLASS_TAG, "Fragment has no view");
            return;
        }
        TextView locationInfo = (TextView) view.findViewById(R.id.locationInfo);
        locationService = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location currLocation = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
        Log.v(CLASS_TAG, "Current location: " + currLocation + ", locationInfo: " + locationInfo);
        if (currLocation != null && locationInfo != null) {
            // update home fragment's location info
            locationInfo.setText("Location: " + currLocation.getLatitude() + "," + currLocation.getLongitude());
        }
    }
}

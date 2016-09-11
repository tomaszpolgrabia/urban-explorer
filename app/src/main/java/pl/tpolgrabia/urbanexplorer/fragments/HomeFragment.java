package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
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
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.utils.PanoramioUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationListener {

    private static final String CLASS_TAG = HomeFragment.class.getSimpleName();
    private static final long MIN_TIME = 60000;
    private static final float MIN_DISTANCE = 100;
    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private static final String LOCATIONS_LIST_IMAGE_SIZE = "medium";
    private static final String LOCATIONS_ORDER = "popularity";
    private static final int PANORAMIA_BULK_DATA_SIZE = 10;
    private boolean gpsLocationEnabled;
    private boolean networkLocationEnabled;
    private boolean locationEnabled;
    private LocationManager locationService;
    private String locationProvider;
    private boolean locationServicesActivated = false;
    private AQuery aq;
    private boolean initialized = false;

    private View inflatedView;
    private Long pageId = 1L;
    private Semaphore loading = new Semaphore(1, true);
    private List<PanoramioImageInfo> photos = new ArrayList<>();
    private boolean photosInitialized = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(getActivity());

        locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        checkLocationSourceAvailability();

        if (!locationEnabled) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_ID);
            return;
        }

        // loading.release();

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

        //        getActivity().findViewById(R.id.update_places).setOnClickListener(
//            new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Location location = locationService.getLastKnownLocation(locationProvider);
//                    aq.ajax("https://maps.googleapis.com/maps/api/place/nearbysearch/output?" +
//                        "key=" + AppConstants.GOOGLE_API_KEY
//                        + "&location=" + location.getLatitude() + "," + location.getLongitude()
//                        + "&radius" + safeParseDouble(aq.id(R.id.location_range).getText())
//                        + "&rankby=distance",
//                        JSONObject.class,
//                        new AjaxCallback<JSONObject>() {
//                            @Override
//                            public void callback(String url, JSONObject object, AjaxStatus status) {
//                                object
//                            }
//                        });
//                }
//            }
//        );

        locations = (ListView)inflatedView.findViewById(R.id.locations);

        initialized = true;

//        try {
//            fetchAdditionalPhotos(0, PANORAMIA_BULK_DATA_SIZE);
//        } catch (InterruptedException e) {
//            Log.e(CLASS_TAG, "Acquiring lock interrupted", e);
//        }
        // FIXME hardcoded values

        return inflatedView;
    }

    private void fetchAdditionalPhotos() throws InterruptedException {

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
                    if (adapter == null) {
                        locations.setAdapter(new PanoramioAdapter(getActivity(), R.id.list_item, images));
                    } else {
                        adapter.addAll(images);
                    }
                    // locations.setSelection(photos.size() - 1 - PANORAMIA_BULK_DATA_SIZE);

                    // TODO loading on end scroll should now working
                    // TODO we can remove pagination
                    // TODO we can think about removing first items also and last if the number
                    // TODO of items exceeds the limit (to save the memory)

                    Log.v(CLASS_TAG, "Finished Fetching additional photos count: " + photos.size());

                    photosInitialized = true;
                    loading.release();

                }
            }

        );
    }

    private void fetchPanoramioLocations() {

        final Location location = locationService.getLastKnownLocation(locationProvider);
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
    public void onLocationChanged(Location location) {
        Log.i(CLASS_TAG, "Location provider changed: " + location);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        TextView locationInfo = (TextView) getActivity().findViewById(R.id.locationInfo);
        locationInfo.setText("Location: (" + lat + "," + lng + ")");
        if (!photosInitialized) {
            try {
                fetchAdditionalPhotos();
            } catch (InterruptedException e) {
                Log.e(CLASS_TAG, "Failed acquirng loading lock", e);
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Log.i(CLASS_TAG, "Location provider status changed")
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(CLASS_TAG, "Provider " + provider + " enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(CLASS_TAG, "Provider " + provider + " disabled");
    }

    @Override
    public void onResume() {
        super.onResume();

        if (locationProvider != null) {
            locationService.requestLocationUpdates(locationProvider,
                MIN_TIME,
                MIN_DISTANCE,
                this);
            locationServicesActivated = true;
            Toast.makeText(getActivity(), "Location resumed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationServicesActivated) {
            locationService.removeUpdates(this);
        }
    }

    private void checkLocationSourceAvailability() {
        gpsLocationEnabled = locationService.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkLocationEnabled = locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationEnabled = gpsLocationEnabled || networkLocationEnabled;
        if (gpsLocationEnabled) {
            locationProvider = LocationManager.GPS_PROVIDER;
            return;
        }

        if (networkLocationEnabled) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST_ID:
                checkLocationSourceAvailability();
                if (!locationEnabled) {
                    // sadly, nothing to do except from notifing user that program is not enable working
                    Toast.makeText(getActivity(), "Sorry location services are not working." +
                            " Program cannot work properly - check location settings to allow program working correctly",
                        Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONException;
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.utils.PanoramioUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
    private boolean gpsLocationEnabled;
    private boolean networkLocationEnabled;
    private boolean locationEnabled;
    private LocationManager locationService;
    private String locationProvider;
    private boolean locationServicesActivated = false;
    private AQuery aq;
    
    private View inflatedView;
    private TextView pageSizeWidget;
    private TextView pageIdWidget;
    private Long pageId = 1L;
    private ListView locations;
    private ImageView prevWidget;
    private ImageView nextWidget;
    private Long photosCount;
    private TextView locationsResultInfo;

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
        inflatedView.findViewById(R.id.update_places).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fetchPanoramioLocations();
                }
            }
        );

        pageSizeWidget = (TextView) inflatedView.findViewById(R.id.locations_page_size);
        pageIdWidget = (TextView) inflatedView.findViewById(R.id.locations_page_id);

        pageIdWidget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(CLASS_TAG, "Before text changed");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pageId = Math.max(1, NumberUtils.safeParseLong(charSequence));
                Log.d(CLASS_TAG, "text changed");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(CLASS_TAG, "After text changed");
            }
        });

        pageSizeWidget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(CLASS_TAG, "Before text changed");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                fetchPanoramioLocations();
                Log.d(CLASS_TAG, "text changed");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(CLASS_TAG, "After text changed");
            }
        });

        prevWidget = (ImageView)inflatedView.findViewById(R.id.prev);
        nextWidget = (ImageView)inflatedView.findViewById(R.id.next);

        prevWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pageId > 1) {
                    pageId--;
                    pageIdWidget.setText(Long.toString(pageId));
                    fetchPanoramioLocations();
                }
            }
        });

        nextWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pageId++;
                pageIdWidget.setText(Long.toString(pageId));
                fetchPanoramioLocations();
            }
        });

        return inflatedView;
    }

    private void fetchPanoramioLocations() {

        fetchPanoramioPhotos();
    }

    private void fetchPanoramioPhotos() {
        final Location location = locationService.getLastKnownLocation(locationProvider);
        Double radiusX = fetchRadiusX();
        Double radiusY = fetchRadiusY();
        final String aqQuery = "http://www.panoramio.com/map/get_panoramas.php?" +
            "set=public" +
            "&from=" + (pageId - 1) * fetchLocationPageSize() +
            "&to="   + pageId * fetchLocationPageSize() +
            "&minx=" + (location.getLongitude() - radiusX) +
            "&miny=" + (location.getLatitude() - radiusY) +
            "&maxx=" + (location.getLongitude() + radiusX) +
            "&maxy=" + (location.getLatitude() + radiusX) +
            "&size=" + LOCATIONS_LIST_IMAGE_SIZE +
            "&order=" + LOCATIONS_ORDER +
            "&mapfilter=true";
        Log.d(CLASS_TAG, "Query: " + aqQuery);
        aq.ajax(aqQuery,
            JSONObject.class,
            new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        Log.d(CLASS_TAG, "Query code: " + status.getCode()
                            + ", error: " + status.getError() + ", message: " + status.getMessage());
                        if (object == null) {
                            return;
                        }

                        List<PanoramioImageInfo> photosInfos;
                        try {
                            photosInfos = PanoramioUtils.fetchPanoramioImagesFromResponse(object.getJSONArray("photos"));
                        } catch (ParseException e) {
                            Log.w(CLASS_TAG, "Parse exception", e);
                            photosInfos = new ArrayList<>();
                        }

                        photosCount = PanoramioUtils.fetchPanoramioImagesCountFromResponse(object);
                        locationsResultInfo = (TextView)inflatedView.findViewById(R.id.locations_result_info);
                        Long pageSize = fetchLocationPageSize();
                        Long start = (pageId - 1) * pageSize + 1;
                        Long end = pageId * pageSize;
                        locationsResultInfo.setText("" + start + "-" + end + " from " + photosCount);

                        ArrayAdapter<PanoramioImageInfo> adapter = new PanoramioAdapter(getActivity(),
                            R.layout.location_item,
                            photosInfos);
                        locations.setAdapter(adapter);

                        locations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
                                PanoramioAdapter panAdapter = (PanoramioAdapter) locations.getAdapter();
                                PanoramioImageInfo photoInfo = panAdapter.getItem(pos);
                                MainActivity activity = (MainActivity) getActivity();
                                activity.switchToPhoto(photoInfo);
                                return false;
                            }
                        });

                    } catch (JSONException e) {
                        Log.w(CLASS_TAG, "Json not supported format", e);
                    }
                }
            });
    }

    private Long fetchLocationPageSize() {
        return NumberUtils.safeParseLong(pageSizeWidget.getText());
    }

    private Long fetchLocationPageId() {
        return Math.max(0L, NumberUtils.safeParseLong(pageIdWidget.getText()));
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

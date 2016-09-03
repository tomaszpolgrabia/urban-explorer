package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
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
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListener;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.utils.PanoramioUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment  {

    private static final String CLASS_TAG = HomeFragment.class.getSimpleName();
    private static final String LOCATIONS_LIST_IMAGE_SIZE = "medium";
    private static final String LOCATIONS_ORDER = "popularity";
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
    private LocationManager locationService;

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
                    updateLocationInfo();
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
                pageId = Math.max(1, NumberUtils.safeParseLong(safeAndroidText2String(charSequence)));
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
        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
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
        CharSequence page_size = pageSizeWidget.getText();
        return NumberUtils.safeParseLong(safeAndroidText2String(page_size));
    }

    private String safeAndroidText2String(CharSequence page_size) {
        return page_size != null ? page_size.toString() : null;
    }

    private Long fetchLocationPageId() {
        return Math.max(0L, NumberUtils.safeParseLong(safeAndroidText2String(pageIdWidget.getText())));
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
        updateLocationInfo();
    }

    public void updateLocationInfo() {
        TextView locationInfo = (TextView) getView().findViewById(R.id.locationInfo);
        locationService = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location currLocation = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
        if (currLocation != null) {
            // update home fragment's location info
            locationInfo.setText("Location: " + currLocation.getLatitude() + "," + currLocation.getLongitude());
        }
    }
}

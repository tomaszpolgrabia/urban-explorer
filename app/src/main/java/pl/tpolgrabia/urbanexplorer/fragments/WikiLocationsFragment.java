package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
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
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiResponseCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.WikiPage;
import pl.tpolgrabia.urbanexplorer.dto.WikiResponse;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.utils.WikiUtils;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {


    private static final String CLASS_TAG = WikiLocationsFragment.class.getSimpleName();
    private LocationManager locationService;
    private TextView currentLocation;
    private Button fetchPlaces;

    public WikiLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflatedView = inflater.inflate(R.layout.fragment_wiki_locations, container, false);

        inflatedView.findViewById(R.id.wiki_fetch_places).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO replace this
                    Toast.makeText(getActivity(), "Fetch wiki objects", Toast.LENGTH_SHORT);
                }
            }
        );

        locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        currentLocation = (TextView) inflatedView.findViewById(R.id.wiki_current_location);
        fetchPlaces = (Button)inflatedView.findViewById(R.id.wiki_fetch_places);
        fetchPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
                Editable search_limit = ((EditText) inflatedView.findViewById(R.id.wiki_search_limit)).getText();
                Editable radius_limit = ((EditText) inflatedView.findViewById(R.id.wiki_search_radius)).getText();
                WikiUtils.fetchNearPlaces(
                    getActivity(),
                    location.getLatitude(),
                    location.getLongitude(),
                    NumberUtils.safeParseLong(
                            search_limit != null ? search_limit.toString(): null),
                    NumberUtils.safeParseLong(
                            radius_limit != null ? radius_limit.toString() : null),
                    new WikiResponseCallback() {
                        @Override
                        public void callback(WikiStatus status, final WikiResponse response) {
                            // handling here wiki locations
                            if (status != WikiStatus.SUCCESS) {
                                Toast.makeText(getActivity(), "Sorry, currently we have problem with interfacing wiki" +
                                    ": " + status + ". Try again later", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // TODO on success

                            ListView locations = (ListView) inflatedView.findViewById(R.id.wiki_places);
                            locations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                                @Override
                                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//                                    WikiPage item = response.getPages().get(position);
//                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                                            Uri.parse(item.get);
//                                    startActivity(intent);
                                    final WikiPage item = response.getPages().get(position);
                                    new AQuery(getActivity()).ajax(
                                            "https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids="
                                                    + item.getPageId() + "&inprop=url&format=json",
                                            JSONObject.class,
                                            new AjaxCallback<JSONObject>() {
                                                @Override
                                                public void callback(String url, JSONObject object, AjaxStatus status) {
                                                    if (status.getCode() != 200) {
                                                        Toast.makeText(getActivity(),
                                                                "Sorry, network error code: " + status.getCode(),
                                                                Toast.LENGTH_LONG)
                                                                .show();
                                                        return;
                                                    }


                                                    try {
                                                        String wikiUrl = object.getJSONObject("query")
                                                                .getJSONObject("pages")
                                                                .getJSONObject(item.getPageId().toString())
                                                                .getString("fullurl");
                                                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                                                Uri.parse(wikiUrl));
                                                        startActivity(intent);
                                                    } catch (JSONException e) {
                                                        Log.e(CLASS_TAG, "Error", e);
                                                    }
                                                }
                                            }
                                    );
                                    return false;
                                }
                            });
                            locations.setAdapter(new WikiLocationsAdapter(getActivity(), response.getPages()));
                        }
                    });
            }
        });

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
        currentLocation.setText("Location: " + location.getLatitude() + "," + location.getLongitude());
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}

package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
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
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.FetchWikiLocationsCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.utils.WikiAppResponseCallback;
import pl.tpolgrabia.urbanexplorer.utils.WikiUtils;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {


    private static final String CLASS_TAG = WikiLocationsFragment.class.getSimpleName();
    private LocationManager locationService;
    private TextView currentLocation;

    public WikiLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflatedView = inflater.inflate(R.layout.fragment_wiki_locations, container, false);

        locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        currentLocation = (TextView) inflatedView.findViewById(R.id.wiki_current_location);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getLocationCallback().addCallback(new StandardLocationListenerCallback() {
            @Override
            public void callback(Location location) {
                updateLocationInfo();
                fetchWikiLocations();
            }
        });

        return inflatedView;
    }

    private void fetchWikiLocations() {
        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));

        if (location == null) {
            Log.i(CLASS_TAG, "Sorry, location is still not available");
            Toast.makeText(getActivity(), "Sorry, location is still not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (getView() == null) {
            Log.i(CLASS_TAG, "Wiki view is not yet initialized");
            return;
        }

        Editable search_limit = ((EditText) getView().findViewById(R.id.wiki_search_limit)).getText();
        Editable radius_limit = ((EditText) getView().findViewById(R.id.wiki_search_radius)).getText();


        WikiUtils.fetchAppData(getActivity(),
            location.getLatitude(),
            location.getLongitude(),
            NumberUtils.safeParseDouble(search_limit != null ? search_limit.toString() : null),
            NumberUtils.safeParseLong(
                radius_limit != null ? radius_limit.toString() : null),
            new WikiAppResponseCallback() {
                @Override
                public void callback(WikiStatus status, final List<WikiAppObject> appObjects) {
                    // handling here wiki locations
                    if (status != WikiStatus.SUCCESS) {
                        Toast.makeText(getActivity(), "Sorry, currently we have problem with interfacing wiki" +
                            ": " + status + ". Try again later", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // TODO on success

                    ListView locations = (ListView) getView().findViewById(R.id.wiki_places);
                    locations.setOnItemLongClickListener(new FetchWikiLocationsCallback(WikiLocationsFragment.this, appObjects));
                    locations.setAdapter(new WikiLocationsAdapter(getActivity(), appObjects));
                }
            }
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLocationInfo();
        fetchWikiLocations();
    }

    public void updateLocationInfo() {
        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
        if (location != null) {
            currentLocation.setText("Your current location: ("
                + location.getLatitude()
                + ","
                + location.getLongitude() + ")");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}

package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.FetchWikiLocationsCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.wiki.WikiCacheDto;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.utils.WikiAppResponseCallback;
import pl.tpolgrabia.urbanexplorer.utils.WikiUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {

    private static final Logger lg = LoggerFactory.getLogger(WikiLocationsFragment.class);
    private static final String CLASS_TAG = WikiLocationsFragment.class.getSimpleName();
    private static final double WIKI_DEF_RADIUS = 10.0;
    private static final long WIKI_DEF_LIMIT = 100;
    public static final String TAG = WikiLocationsFragment.class.getSimpleName();
    private static final String WIKI_APP_OBJECTS = "WIKI_APP_OBJECTS";
    private LocationManager locationService;
    private TextView currentLocation;
    private ArrayList<WikiAppObject> appObjects;
    private int lastFetchSize = -1;

    public WikiLocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lg.trace("onCreate {}", System.identityHashCode(this));
        appObjects = savedInstanceState == null ? new ArrayList<WikiAppObject>()
            : (ArrayList<WikiAppObject>)savedInstanceState.getSerializable(WIKI_APP_OBJECTS);

        if (appObjects == null) {
            try (InputStreamReader ir = new InputStreamReader(
                new FileInputStream(
                    new File(getActivity().getCacheDir(),
                        AppConstants.WIKI_CACHE_FILENAME)))) {

                WikiCacheDto dto = new Gson().fromJson(ir, WikiCacheDto.class);
                appObjects = new ArrayList<>(dto.getAppObject());

            } catch (FileNotFoundException e) {
                lg.error("File not found", e);
            } catch (IOException e) {
                lg.error("I/O error", e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflatedView = inflater.inflate(R.layout.fragment_wiki_locations, container, false);
        lg.trace("TAG: {}", getTag());
        for (Fragment frag : getFragmentManager().getFragments()) {
            if (frag == null) {
                lg.trace("Got null fragment");
            } else {
                lg.trace("Fragment TAG {}", frag.getTag());
            }
        }

        locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        currentLocation = (TextView) inflatedView.findViewById(R.id.wiki_current_location);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getLocationCallback().addCallback(new StandardLocationListenerCallback() {
            @Override
            public void callback(Location location) {
                lastFetchSize = -1;
                appObjects = new ArrayList<>();
                updateLocationInfo();
                fetchWikiLocations();
            }
        });

        ListView locations = (ListView) inflatedView.findViewById(R.id.wiki_places);
        locations.setOnItemLongClickListener(new FetchWikiLocationsCallback(WikiLocationsFragment.this, appObjects));
        locations.setAdapter(new WikiLocationsAdapter(getActivity(), appObjects));

        return inflatedView;
    }

    public void fetchWikiLocations() {
        lg.trace("Fetch wiki locations");

        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity shouldn't be null. No headless fragment");
            return;
        }

        MainActivity mainActivity = (MainActivity) getActivity();

        if (lastFetchSize == 0) {
            lg.trace("There is no results");
            mainActivity.hideProgress();
            return;
        }

        if (!appObjects.isEmpty()) {
            lg.trace("There are fetched objects");
            mainActivity.hideProgress();
            return;
        }

        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(activity));

        if (location == null) {
            lg.info("Sorry, location is still not available");
            Toast.makeText(activity, "Sorry, location is still not available", Toast.LENGTH_SHORT).show();
            mainActivity.hideProgress();
            return;
        }

        if (getView() == null) {
            lg.info("Wiki view is not yet initialized");
            mainActivity.hideProgress();
            return;
        }

        WikiUtils.fetchAppData(activity,
            location.getLatitude(),
            location.getLongitude(),
            fetchRadiusLimit(),
            fetchSearchLimit(),
            new WikiAppResponseCallback() {

                @Override
                public void callback(WikiStatus status, final List<WikiAppObject> objects) {
                    appObjects.clear();
                    appObjects.addAll(objects);

                    // handling here wiki locations
                    if (status != WikiStatus.SUCCESS) {
                        Toast.makeText(activity, "Sorry, currently we have problem with interfacing wiki" +
                            ": " + status + ". Try again later", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // TODO on success

                    ListView locations = (ListView) getView().findViewById(R.id.wiki_places);
                    locations.setOnItemLongClickListener(new FetchWikiLocationsCallback(WikiLocationsFragment.this, appObjects));
                    locations.setAdapter(new WikiLocationsAdapter(activity, appObjects));
                    if (objects.isEmpty()) {
                        Toast.makeText(getActivity(), "No results", Toast.LENGTH_SHORT).show();
                    }

                    MainActivity mainActivity = (MainActivity) getActivity();
                    if (mainActivity == null) {
                        return;
                    }

                    mainActivity.hideProgress();
                }
            }
        );
    }

    private Double fetchRadiusLimit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String prefWikiRadius = sharedPreferences.getString("pref_wiki_radius", String.valueOf(WIKI_DEF_RADIUS));
        lg.debug("Pref wiki radius limit {}", prefWikiRadius);
        return NumberUtils.safeParseDouble(prefWikiRadius)*1000.0; // in m, settings are in km unit
    }
    private Long fetchSearchLimit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String prefWikiResultsLimit = sharedPreferences.getString("pref_wiki_limit", String.valueOf(WIKI_DEF_LIMIT));
        lg.debug("Pref wiki search results limit {}", prefWikiResultsLimit);
        return NumberUtils.safeParseLong(prefWikiResultsLimit);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Wiki search");
        updateLocationInfo();
        fetchWikiLocations();
        lg.trace("onResume {}", System.identityHashCode(this));
    }

    public void updateLocationInfo() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity shouldn't be null. No headless fragment");
            return;
        }
        final Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(activity));
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
        lg.trace("onPause {}", System.identityHashCode(this));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lg.trace("onSaveInstanceState");

        outState.putSerializable(WIKI_APP_OBJECTS, appObjects);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        lg.trace("onDestroy {}", System.identityHashCode(this));

        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(
                    new File(getActivity().getCacheDir(),
                        AppConstants.WIKI_CACHE_FILENAME))))) {

            WikiCacheDto dto = new WikiCacheDto();
            dto.setAppObject(appObjects);
            LocationManager locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
            Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
            if (location != null) {
                dto.setLongitude(location.getLongitude());
                dto.setLatitude(location.getLatitude());
                dto.setAltitude(location.getAltitude());
            }

            dto.setFetchedAt(new GregorianCalendar().getTime());
            // FIXME should be a fetched time, not persist time

            new Gson().toJson(bw);

        } catch (FileNotFoundException e) {
            lg.error("File not found", e);
        } catch (IOException e) {
            lg.error("I/O error", e);
        }
    }
}

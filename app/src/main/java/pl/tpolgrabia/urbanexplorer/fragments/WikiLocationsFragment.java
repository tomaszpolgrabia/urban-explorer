package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.*;
import pl.tpolgrabia.urbanexplorer.dto.wiki.WikiCacheDto;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorer.events.RefreshEvent;
import pl.tpolgrabia.urbanexplorer.utils.*;

import java.io.*;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {

    private static final Logger lg = LoggerFactory.getLogger(WikiLocationsFragment.class);
    public static final String TAG = WikiLocationsFragment.class.getSimpleName();
    private static final String WIKI_APP_OBJECTS = "WIKI_APP_OBJECTS";
    private LocationManager locationService;
    private TextView currentLocation;
    private ArrayList<WikiAppObject> appObjects = new ArrayList<>();
    private int lastFetchSize = -1;
    private String currentGeocodedLocation;

    public WikiLocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lg.trace("onCreate {}", System.identityHashCode(this));
        EventBus.getDefault().register(this);
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
        DebugUtils.dumpFragments(getFragmentManager().getFragments());

        locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        currentLocation = (TextView) inflatedView.findViewById(R.id.wiki_current_location);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getLocationCallback().addCallback(new WikiLocationCallback(this));

        mainActivity.getLocationCallback().addProviderCallback(new WikiLocationProviderStatusCallback(this));

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
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

        if (!appObjects.isEmpty()) {
            lg.trace("There are fetched objects");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

        final Location location = LocationUtils.getLastKnownLocation(activity);

        if (location == null) {
            lg.info("Sorry, location is still not available");
            Toast.makeText(activity, "Sorry, location is still not available", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

        if (getView() == null) {
            lg.info("Wiki view is not yet initialized");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

        WikiUtils.fetchAppData(activity,
            location.getLatitude(),
            location.getLongitude(),
            SettingsUtils.fetchRadiusLimit(getActivity()),
            SettingsUtils.fetchSearchLimit(getActivity()),
            new WikiFetchAppDataCallback(this, activity)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Wiki search");
        if (currentGeocodedLocation != null) {
            updateLocationInfo();
        } else {
            updateGeocodedLocation();
        }
        fetchWikiLocations();
        lg.trace("onResume {}", System.identityHashCode(this));
    }

    private void updateGeocodedLocation() {
        if (getActivity() == null) {
            lg.debug("Activity is not attached");
            return;
        }

        Location location = LocationUtils.getLastKnownLocation(getActivity());

        if (location == null) {
            lg.debug("Location is still not available");
            return;
        }

        LocationUtils.getGeoCodedLocation(getActivity(), location.getLatitude(), location.getLongitude(), new LocationGeoCoderCallback() {
            @Override
            public void callback(int code, String message, String googleStatus, String geocodedLocation) {
                lg.debug("Geocoded result code {}, message {}, status: {}, value {}",
                        code, message, googleStatus, geocodedLocation);

                currentGeocodedLocation = geocodedLocation;
                updateLocationInfo();
            }
        });
    }

    public void updateLocationInfo() {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            lg.warn("Activity shouldn't be null. No headless fragment");
            return;
        }

        currentLocation.setText(currentGeocodedLocation);
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
        EventBus.getDefault().unregister(this);
        lg.trace("onDestroy {}", System.identityHashCode(this));

        CacheUtils.saveWikiObjectsToCache(getActivity(), appObjects);
    }

    @Subscribe
    public void refresh(RefreshEvent event) {
        appObjects.clear();
        fetchWikiLocations();
    }

    public void setLastFetchSize(int lastFetchSize) {
        this.lastFetchSize = lastFetchSize;
    }

    public void setAppObjects(ArrayList<WikiAppObject> appObjects) {
        this.appObjects = appObjects;
    }

}

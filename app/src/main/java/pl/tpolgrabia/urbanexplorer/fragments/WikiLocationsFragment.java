package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.utils.GeocoderUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.wiki.*;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;
import pl.tpolgrabia.urbanexplorer.utils.*;
import pl.tpolgrabia.urbanexplorerutils.utils.DebugUtils;
import pl.tpolgrabia.wikibinding.utils.WikiUtils;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {

    private static final Logger lg = LoggerFactory.getLogger(WikiLocationsFragment.class);
    public static final String TAG = WikiLocationsFragment.class.getSimpleName();
    public static final String WIKI_APP_OBJECTS = "WIKI_APP_OBJECTS";
    private LocationManager locationService;
    private TextView currentLocation;
    private ArrayList<WikiAppObject> appObjects = new ArrayList<>();
    private int lastFetchSize = -1;
    private String currentGeocodedLocation;
    private GeocoderUtils geocoderUtils;
    private WikiUtils wikiUtils;

    public WikiLocationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lg.trace("onCreate {}", System.identityHashCode(this));
        EventBus.getDefault().register(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        appObjects = WikiCacheUtils.loadWikiObjectsFromCache(getActivity(), savedInstanceState);
        geocoderUtils = new GeocoderUtils(getActivity(), AppConstants.GOOGLE_API_KEY);
        wikiUtils = new WikiUtils(getActivity(), AppConstants.DEF_WIKI_COUNTRY_CODE);
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
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

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

        wikiUtils.fetchAppData(new WikiFetchAppDataCallback(this, activity));
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
        geocoderUtils.getGeoCodedLocation(new WikiLocationGeoCoderCallback(this));
    }

    public void updateLocationInfo() {
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

        WikiCacheUtils.saveWikiObjectsToCache(getActivity(), appObjects);
    }

    @Subscribe
    public void refresh(RefreshEvent event) {
        lg.debug("Refreshing event...");
        appObjects.clear();
        fetchWikiLocations();
    }

    public void setLastFetchSize(int lastFetchSize) {
        this.lastFetchSize = lastFetchSize;
    }

    public void setAppObjects(ArrayList<WikiAppObject> appObjects) {
        this.appObjects = appObjects;
    }

    public void setCurrentGeocodedLocation(String currentGeocodedLocation) {
        this.currentGeocodedLocation = currentGeocodedLocation;
    }
}

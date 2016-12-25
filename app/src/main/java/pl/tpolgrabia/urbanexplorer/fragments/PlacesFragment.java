package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.callback.LocationGeoCoderCallback;
import pl.tpolgrabia.googleutils.constants.GooglePlacesConstants;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.googleutils.utils.GeocoderUtils;
import pl.tpolgrabia.googleutils.utils.PlacesUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.PlacesAdapter;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesRequest;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesResponse;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesState;
import pl.tpolgrabia.urbanexplorer.events.LocationChangedEvent;
import pl.tpolgrabia.urbanexplorer.handlers.GooglePlacesLongClickItemHandler;
import pl.tpolgrabia.urbanexplorer.handlers.GooglePlacesScrollListener;
import pl.tpolgrabia.urbanexplorer.worker.GooglePlacesWorker;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.SettingsUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

    private static final Logger lg = LoggerFactory.getLogger(PlacesFragment.class);
    public static final String TAG = PlacesFragment.class.getSimpleName();
    private PlacesUtils placesUtils;
    private GeocoderUtils geocoderUtils;
    private String nextPageToken;
    private Long pageId = 0L;
    private List<GooglePlaceResult> places = new ArrayList<>();

    private Semaphore semaphore = new Semaphore(1);
    private boolean noMoreResults = false;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflatedView = inflater.inflate(R.layout.fragment_places, container, false);

        final ListView placesWidget = (ListView) inflatedView.findViewById(R.id.google_places);
        placesWidget.setOnItemLongClickListener(new GooglePlacesLongClickItemHandler(this, placesWidget));
        placesWidget.setOnScrollListener(new GooglePlacesScrollListener(this));
        placesWidget.setAdapter(new PlacesAdapter(getActivity(), places));

        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        placesUtils = new PlacesUtils(getActivity(), AppConstants.GOOGLE_API_KEY);

        MainActivity mainActivity = (MainActivity) getActivity();

        geocoderUtils = new GeocoderUtils(getActivity(), AppConstants.GOOGLE_API_KEY);
        BufferedReader br = null;
        try {
            br = new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(
                        new File(getActivity().getCacheDir(),
                            GooglePlacesConstants.GOOGLE_PLACES_CACHE_FILE))));

            Gson g = new GsonBuilder().create();
            GooglePlacesState state = g.fromJson(br, GooglePlacesState.class);
            places = state.getPlaces();
            nextPageToken = state.getNextPageToken();
            noMoreResults = state.isNoMoreResults();
            pageId = state.getPageId();

            if (places != null && !places.isEmpty()) {
                ListView placesWidget = (ListView) getView().findViewById(R.id.google_places);
                placesWidget.setAdapter(new PlacesAdapter(getActivity(), places));
            }

        } catch (FileNotFoundException e) {
            // no cache, ok, it can happen
        } catch (IOException e) {
            lg.error("I/O error during reading cache file", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    lg.error("I/O error", e);
                }
            }
        }

    }

    @Subscribe
    public void handleProviderStatusChanged(String provider, boolean enabled) {
        lg.debug("Provider {} has changed the status to {}", provider, enabled);

        if (!enabled) {
            return;
        }

        if (getActivity() == null) {
            return;
        }

        Location location = LocationUtils.getLastKnownLocation(getActivity());
        if (location == null) {
            return;
        }

        fetchNearbyPlacesAndPresent(location);
    }

    @Subscribe
    public void handleLocationChanged(LocationChangedEvent event) {
        Location location = event.getLocation();

        lg.debug("Location changed: {}", location);

        if (location == null) {
            return;
        }

//        Toast.makeText(getActivity(),
//            String.format(AppConstants.DEF_APP_LOCALE,
//                "Location changed: %.3f,%.3f",
//                location.getLatitude(), location.getLongitude()),
//            Toast.LENGTH_SHORT).show();

        cleanAdapter();

        places = null;
        nextPageToken = null;
        noMoreResults = false;
        pageId = 0L;
        fetchNearbyPlacesAndPresent(location);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() == null) {
            return;
        }

        getActivity().setTitle("Google places search");

        Location location = LocationUtils.getLastKnownLocation(getActivity());
        if (location == null) {
            return;
        }

        geocoderUtils.getGeoCodedLocation(new LocationGeoCoderCallback() {
            @Override
            public void callback(int code, String message, String googleStatus, String geocodedLocation) {
                lg.trace("Geocoded code: {}, message: {}, google status: {}, location: {}",
                    code,
                    message,
                    googleStatus,
                    geocodedLocation);

                TextView locationWidget = (TextView) getView().findViewById(R.id.google_places_location);
                locationWidget.setText(geocodedLocation);
            }
        });
        lg.debug("Fetching nearby places {}", location);

        if ((places == null || places.isEmpty()) && !noMoreResults) {
            fetchNearbyPlacesAndPresent(location);
        } else {
            lg.debug("Places: {}, no more results: {}", places, noMoreResults);
        }

    }

    private void fetchNearbyPlacesAndPresent(Location location) {
        if (!semaphore.tryAcquire()) {
            // running
            lg.debug("Active fetching nearby, quitting...");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

//        Toast.makeText(getActivity(),Comm
//            String.format(AppConstants.DEF_APP_LOCALE,
//                "Fetching nearby places %.3f,%.3f",
//                location.getLatitude(),
//                location.getLongitude()),
//            Toast.LENGTH_SHORT).show();


        GooglePlacesRequest request = new GooglePlacesRequest();
        request.setLocation(location);
        request.setSearchRadius(SettingsUtils.getDefaultPlacesSearchRadius(getActivity()));
        request.setSearchItemType(SettingsUtils.getPlacesSearchCategories(getActivity()));
        new GooglePlacesWorker(getActivity()).execute(request);
    }

    public void loadNextPage() {

        if (!semaphore.tryAcquire()) {
            // running
            lg.debug("Active fetching nearby, quitting...");
            return;
        }

        if (noMoreResults) {
            lg.debug("There is no results, quitting...");
            semaphore.release();
            return;
        }

        if (getActivity() == null) {
            lg.debug("Headless fragment, no activity - no context");
            return;
        }

        lg.debug("Loading next page");

        Location location = LocationUtils.getLastKnownLocation(getActivity());
        GooglePlacesRequest request = new GooglePlacesRequest();
        request.setLocation(location);
        request.setSearchRadius(SettingsUtils.getDefaultPlacesSearchRadius(getActivity()));
        request.setSearchItemType(SettingsUtils.getPlacesSearchCategories(getActivity()));
        request.setPageToken(nextPageToken);
        new GooglePlacesWorker(getActivity()).execute(request);
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @Subscribe
    public void handleGooglePlacesResult(GooglePlacesResponse response) {
        ++pageId;
        lg.debug("Page {}. Handling google places results with original {} and next page token {}",
            pageId,
            response.getOriginalPageToken(),
            response.getNextPageToken());

        ListView placesWidget = (ListView) getView().findViewById(R.id.google_places);
        PlacesAdapter adapter = (PlacesAdapter)placesWidget.getAdapter();
        if (adapter == null) {
            adapter = new PlacesAdapter(getActivity(), new ArrayList<GooglePlaceResult>());
            placesWidget.setAdapter(adapter);
        }

        if (response.getPlaces() != null) {
            adapter.addAll(response.getPlaces());
        }

        nextPageToken = response.getNextPageToken();
        if (nextPageToken == null) {
            noMoreResults = true;
        }

        EventBus.getDefault().post(new DataLoadingFinishEvent(this));
        semaphore.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
        GooglePlacesState state = new GooglePlacesState();
        state.setPlaces(places);
        state.setNextPageToken(nextPageToken);
        state.setNoMoreResults(noMoreResults);
        state.setPageId(pageId);

        Gson g = new GsonBuilder().create();
        BufferedWriter bw = null;
        try {
            bw  = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(
                    new File(getActivity().getCacheDir(),
                        GooglePlacesConstants.GOOGLE_PLACES_CACHE_FILE))));

            g.toJson(state, bw);

        } catch (FileNotFoundException e) {
            lg.error("File not found error during saving a state", e);
        } catch (IOException e) {
            lg.error("I/O error during saving a state");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    lg.error("I/O Error", e);
                }
            }
        }

    }

    @Subscribe
    public void refresh(RefreshEvent event) {
        lg.debug("Refreshing event...");
        Toast.makeText(getActivity(), "Refreshing event google places", Toast.LENGTH_SHORT).show();

        if (getView() == null) {
            lg.debug("Sorry, headless fragment");
            EventBus.getDefault().post(new DataLoadingFinishEvent(this));
            return;
        }

        cleanAdapter();

        pageId = 0L;
        places = null;
        nextPageToken = null;
        noMoreResults = false;
        fetchNearbyPlacesAndPresent(LocationUtils.getLastKnownLocation(getActivity()));
    }

    private void cleanAdapter() {
        ListView plagesWidget = (ListView) getView().findViewById(R.id.google_places);
        PlacesAdapter adapter = (PlacesAdapter) plagesWidget.getAdapter();
        if (adapter != null) {
            adapter.clear();
        }
    }

}

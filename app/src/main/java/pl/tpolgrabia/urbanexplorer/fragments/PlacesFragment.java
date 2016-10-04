package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.callback.LocationGeoCoderCallback;
import pl.tpolgrabia.googleutils.constants.GooglePlacesConstants;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.googleutils.utils.GeocoderUtils;
import pl.tpolgrabia.googleutils.utils.PlacesUtils;
import pl.tpolgrabia.panoramiobindings.callback.ProviderStatusCallback;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.PlacesAdapter;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesRequest;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesResponse;
import pl.tpolgrabia.urbanexplorer.handlers.GooglePlacesScrollListener;
import pl.tpolgrabia.urbanexplorer.worker.GooglePlacesWorker;
import pl.tpolgrabia.urbanexplorerutils.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;

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
    private List<GooglePlaceResult> places = new ArrayList<>();

    private Semaphore semaphore = new Semaphore(1);
    private boolean finished = false;

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

        ListView placesWidget = (ListView) inflatedView.findViewById(R.id.google_places);
        placesWidget.setOnScrollListener(new GooglePlacesScrollListener(this));

        return inflatedView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        placesUtils = new PlacesUtils(getActivity(), AppConstants.GOOGLE_API_KEY);

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getLocationCallback()
            .addCallback(new StandardLocationListenerCallback() {
                @Override
                public void callback(Location location) {
                    lg.debug("Location changed: {}", location);

                    if (location == null) {
                        return;
                    }

                    fetchNearbyPlacesAndPresemt(location);

                }
            });

        mainActivity.getLocationCallback()
            .addProviderCallback(new ProviderStatusCallback() {
                @Override
                public void callback(String provider, boolean enabled) {

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

                    fetchNearbyPlacesAndPresemt(location);

                }
            });

        geocoderUtils = new GeocoderUtils(getActivity(), AppConstants.GOOGLE_API_KEY);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() == null) {
            return;
        }

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
        fetchNearbyPlacesAndPresemt(location);

    }

    private void fetchNearbyPlacesAndPresemt(Location location) {
        if (!semaphore.tryAcquire()) {
            // running
            return;
        }

        GooglePlacesRequest request = new GooglePlacesRequest();
        request.setLocation(location);
        request.setSearchRadius(AppConstants.DEF_PLACES_RADIUS);
        request.setSearchItemType(GooglePlacesConstants.PLACES_SEARCH_TYPE);
        new GooglePlacesWorker(getActivity(), this).execute(request);
    }

    public void loadNextPage() {

        if (!semaphore.tryAcquire()) {
            // running
            return;
        }

        if (finished) {
            semaphore.release();
            return;
        }

        lg.debug("Loading next page");

        Location location = LocationUtils.getLastKnownLocation(getActivity());
        GooglePlacesRequest request = new GooglePlacesRequest();
        request.setLocation(location);
        request.setSearchRadius(AppConstants.DEF_PLACES_RADIUS);
        request.setSearchItemType(GooglePlacesConstants.PLACES_SEARCH_TYPE);
        request.setPageToken(nextPageToken);
        new GooglePlacesWorker(getActivity(), this).execute(request);
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    @Subscribe
    public void handleGooglePlacesResult(GooglePlacesResponse response) {
        lg.debug("Handling google places results with original {} and next page token {}",
            response.getOriginalPageToken(),
            response.getNextPageToken());

        ListView placesWidget = (ListView) getView().findViewById(R.id.google_places);
        if (nextPageToken == null) {
            places = response.getPlaces();
            PlacesAdapter adapter = new PlacesAdapter(getActivity(), places);
            placesWidget.setAdapter(adapter);
        } else {
            places.addAll(response.getPlaces());
            PlacesAdapter adapter = (PlacesAdapter)placesWidget.getAdapter();
            adapter.addAll(places);
        }

        nextPageToken = response.getNextPageToken();
        if (nextPageToken == null) {
            finished = true;
        }
        semaphore.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }
}

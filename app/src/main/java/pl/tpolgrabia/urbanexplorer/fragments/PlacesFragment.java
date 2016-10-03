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
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.callback.LocationGeoCoderCallback;
import pl.tpolgrabia.googleutils.callback.PlacesCallback;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.googleutils.utils.GeocoderUtils;
import pl.tpolgrabia.googleutils.utils.PlacesUtils;
import pl.tpolgrabia.panoramiobindings.callback.ProviderStatusCallback;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.PlacesAdapter;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesRequest;
import pl.tpolgrabia.urbanexplorer.worker.GooglePlacesWorker;
import pl.tpolgrabia.urbanexplorerutils.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

    private static final Logger lg = LoggerFactory.getLogger(PlacesFragment.class);
    public static final String TAG = PlacesFragment.class.getSimpleName();
    private PlacesUtils placesUtils;
    private GeocoderUtils geocoderUtils;
    private GooglePlacesWorker worker;

    public PlacesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View inflatedView = inflater.inflate(R.layout.fragment_places, container, false);

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
        worker = new GooglePlacesWorker(getActivity(), this);

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
        GooglePlacesRequest request = new GooglePlacesRequest();
        request.setLocation(location);
        request.setSearchRadius(AppConstants.DEF_PLACES_RADIUS);
        request.setSearchItemType("museum");
        worker.execute(request);

//        placesUtils.fetchNearbyPlaces(
//            location.getLatitude(),
//            location.getLongitude(),
//            AppConstants.DEF_PLACES_RADIUS,
//            "museum",
//            null,
//            new PlacesCallback() {
//                @Override
//                public void callback(Long statusCode, String statusMsg, List<GooglePlaceResult> googlePlaceResult) {
//                    lg.debug("Fetch nearby statusCode: {}, status message: {}, google result: {}",
//                        statusCode,
//                        statusMsg,
//                        googlePlaceResult);
//
//                    ListView googlePlacesWidget = (ListView) getView().findViewById(R.id.google_places);
//                    PlacesAdapter adapter = new PlacesAdapter(getActivity(), googlePlaceResult);
//                    googlePlacesWidget.setAdapter(adapter);
//                }
//            });
    }
}

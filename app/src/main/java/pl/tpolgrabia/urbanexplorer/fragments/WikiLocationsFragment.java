package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiResponseCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.WikiResponse;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.WikiUtils;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiLocationsFragment extends Fragment {


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
                WikiUtils.fetchNearPlaces(getActivity(), location.getLatitude(), location.getLongitude(), 10L, new WikiResponseCallback() {
                    @Override
                    public void callback(WikiStatus status, WikiResponse response) {
                        // handling here wiki locations
                        if (status != WikiStatus.SUCCESS) {
                            Toast.makeText(getActivity(), "Sorry, currently we have problem with interfacing wiki" +
                                ": " + status + ". Try again later", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // TODO on success

                        ListView locations = (ListView) inflatedView.findViewById(R.id.wiki_places);
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

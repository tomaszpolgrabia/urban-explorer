package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import pl.tpolgrabia.urbanexplorer.R;
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
                Location location = locationService.getLastKnownLocation(LocationUtils.getDefaultLocation(getActivity()));
                WikiUtils.fetchNearPlaces(this, location.getLatitude(), location.getLongitude());
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

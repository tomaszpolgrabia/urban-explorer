package pl.tpolgrabia.urbanexplorer.fragments;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import pl.tpolgrabia.urbanexplorer.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationListener {

    private static final String CLASS_TAG = HomeFragment.class.getSimpleName();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void setLocationData(double lat, double lng) {
        TextView locationInfo = (TextView) getActivity().findViewById(R.id.locationInfo);
        locationInfo.setText("Location: (" + lat + "," + lng + ")");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(CLASS_TAG, "Location provider changed: " + location);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        HomeFragment homeFrag = (HomeFragment) getActivity()
            .getSupportFragmentManager()
            .findFragmentById(R.id.home);
        homeFrag.setLocationData(lat, lng);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Log.i(CLASS_TAG, "Location provider status changed")
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(CLASS_TAG, "Provider " + provider + " enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(CLASS_TAG, "Provider " + provider + " disabled");
    }
}

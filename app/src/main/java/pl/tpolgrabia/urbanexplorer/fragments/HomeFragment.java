package pl.tpolgrabia.urbanexplorer.fragments;


import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;
import pl.tpolgrabia.urbanexplorer.R;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements LocationListener {

    private static final String CLASS_TAG = HomeFragment.class.getSimpleName();
    private static final long MIN_TIME = 60000;
    private static final float MIN_DISTANCE = 100;
    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private boolean gpsLocationEnabled;
    private boolean networkLocationEnabled;
    private boolean locationEnabled;
    private LocationManager locationService;
    private String locationProvider;
    private boolean locationServicesActivated = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationService = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        checkLocationSourceAvailability();

        if (!locationEnabled) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_ID);
            return;
        }

        Toast.makeText(getActivity(), "Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(CLASS_TAG, "Location provider changed: " + location);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        TextView locationInfo = (TextView) getActivity().findViewById(R.id.locationInfo);
        locationInfo.setText("Location: (" + lat + "," + lng + ")");
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

    @Override
    public void onResume() {
        super.onResume();

        if (locationProvider != null) {
            locationService.requestLocationUpdates(locationProvider,
                MIN_TIME,
                MIN_DISTANCE,
                this);
            locationServicesActivated = true;
            Toast.makeText(getActivity(), "Location resumed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationServicesActivated) {
            locationService.removeUpdates(this);
        }
    }

    private void checkLocationSourceAvailability() {
        gpsLocationEnabled = locationService.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkLocationEnabled = locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationEnabled = gpsLocationEnabled || networkLocationEnabled;
        if (gpsLocationEnabled) {
            locationProvider = LocationManager.GPS_PROVIDER;
            return;
        }

        if (networkLocationEnabled) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
            return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST_ID:
                checkLocationSourceAvailability();
                if (!locationEnabled) {
                    // sadly, nothing to do except from notifing user that program is not enable working
                    Toast.makeText(getActivity(), "Sorry location services are not working." +
                            " Program cannot work properly - check location settings to allow program working correctly",
                        Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

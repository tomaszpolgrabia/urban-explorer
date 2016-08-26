package pl.tpolgrabia.urbanexplorer;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

public class MainActivity extends ActionBarActivity  {

    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
    private static final long MIN_TIME = 60000;
    private static final float MIN_DISTANCE = 100;
    private boolean gpsLocationEnabled;
    private boolean networkLocationEnabled;
    private boolean locationEnabled;
    private LocationManager locationService;
    private String locationProvider;
    private HomeFragment homeFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.navbar);
        setSupportActionBar(toolbar);

        locationService = (LocationManager) getSystemService(LOCATION_SERVICE);
        checkLocationSourceAvailability();

        if (!locationEnabled) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_ID);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        homeFrag = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.home);
        locationService.requestLocationUpdates(locationProvider,
            MIN_TIME,
            MIN_DISTANCE,
            homeFrag);
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationService.removeUpdates(homeFrag);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST_ID:
                checkLocationSourceAvailability();
                if (!locationEnabled) {
                    // sadly, nothing to do except from notifing user that program is not enable working
                    Toast.makeText(this, "Sorry location services are not working." +
                        " Program cannot work properly - check location settings to allow program working correctly",
                        Toast.LENGTH_LONG);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // TODO show settings fragment
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

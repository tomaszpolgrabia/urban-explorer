package pl.tpolgrabia.urbanexplorer.callbacks.geocoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.callback.LocationGeoCoderCallback;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class GeocodedLocationCallback implements LocationGeoCoderCallback {
    private static final Logger lg = LoggerFactory.getLogger(GeocodedLocationCallback.class);
    private HomeFragment homeFragment;

    public GeocodedLocationCallback(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public void callback(int code, String message, String googleStatus, String geocodedLocation) {
        lg.debug("Geocoded result code {}, message {}, status: {}, value {}",
            code, message, googleStatus, geocodedLocation);

        homeFragment.setCurrentGeocodedLocation(geocodedLocation);
        homeFragment.updateLocationInfo();
    }
}

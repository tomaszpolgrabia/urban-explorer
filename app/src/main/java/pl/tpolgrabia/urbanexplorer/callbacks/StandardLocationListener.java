package pl.tpolgrabia.urbanexplorer.callbacks;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class StandardLocationListener implements LocationListener {
    private static final Logger lg = LoggerFactory.getLogger(StandardLocationListener.class);
    private static final String CLASS_TAG = StandardLocationListener.class.getSimpleName();
    private List<StandardLocationListenerCallback> locationChangedCallbacks = new ArrayList<>();

    @Override
    public void onLocationChanged(Location location) {
        lg.info("Location provider changed: {}", location);
        for (StandardLocationListenerCallback callback : locationChangedCallbacks) {
            callback.callback(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        lg.debug("Location provider status changed");
    }

    @Override
    public void onProviderEnabled(String provider) {
        lg.info("Provider {} enabled", provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        lg.info("Provider {} disabled", provider);
    }

    public void addCallback(StandardLocationListenerCallback callback) {
        locationChangedCallbacks.add(callback);
    }

    public boolean removeCallback(StandardLocationListenerCallback callback) {
        return locationChangedCallbacks.remove(callback);
    }
}

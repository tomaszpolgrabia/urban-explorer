package pl.tpolgrabia.urbanexplorer.callbacks;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class StandardLocationListener implements LocationListener {
    private static final String CLASS_TAG = StandardLocationListener.class.getSimpleName();
    private List<StandardLocationListenerCallback> locationChangedCallbacks = new ArrayList<>();

    @Override
    public void onLocationChanged(Location location) {
        Log.i(CLASS_TAG, "Location provider changed: " + location);
        for (StandardLocationListenerCallback callback : locationChangedCallbacks) {
            callback.callback(location);
        }
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

    public void addCallback(StandardLocationListenerCallback callback) {
        locationChangedCallbacks.add(callback);
    }

    public boolean removeCallback(StandardLocationListenerCallback callback) {
        return locationChangedCallbacks.remove(callback);
    }
}

package pl.tpolgrabia.urbanexplorer.callbacks;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.callback.ProviderStatusCallback;
import pl.tpolgrabia.urbanexplorerutils.callbacks.StandardLocationListenerCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class StandardLocationListener implements LocationListener {
    private static final Logger lg = LoggerFactory.getLogger(StandardLocationListener.class);
    private List<StandardLocationListenerCallback> locationChangedCallbacks = new ArrayList<>();
    private List<ProviderStatusCallback>
            providerStatusCallbacks = new ArrayList<>();

    @Override
    public void onLocationChanged(Location location) {
        lg.info("Location provider changed: {}", location);
        for (StandardLocationListenerCallback callback : locationChangedCallbacks) {
            callback.callback(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        lg.debug("Location provider {} status  has changed to {} with {}", provider, status, extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        lg.info("Provider {} enabled", provider);

        for (ProviderStatusCallback callback : providerStatusCallbacks){
            callback.callback(provider, true);
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        lg.info("Provider {} disabled", provider);

        for (ProviderStatusCallback callback : providerStatusCallbacks){
            callback.callback(provider, false);
        }
    }

    public void addCallback(StandardLocationListenerCallback callback) {
        lg.trace("Location added callback");
        locationChangedCallbacks.add(callback);
    }

    public boolean removeCallback(StandardLocationListenerCallback callback) {
        lg.trace("Location removed callback");
        return locationChangedCallbacks.remove(callback);
    }

    public void addProviderCallback(ProviderStatusCallback callback) {
        lg.trace("Provider added callback");
        providerStatusCallbacks.add(callback);
    }

    public void removeProviderCallback(ProviderStatusCallback callback) {
        lg.trace("Provider removed calback");
        providerStatusCallbacks.remove(callback);
    }
}

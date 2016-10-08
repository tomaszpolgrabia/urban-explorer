package pl.tpolgrabia.urbanexplorer.callbacks;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.events.LocationChangedEventBuilder;
import pl.tpolgrabia.urbanexplorer.events.ProviderStatusChangedEventBuilder;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class StandardLocationListener implements LocationListener {
    private static final Logger lg = LoggerFactory.getLogger(StandardLocationListener.class);
    private final Context ctx;

    public StandardLocationListener(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onLocationChanged(Location location) {
        lg.info("Location provider changed: {}", location);
        Toast.makeText(ctx, "Location changed " + location, Toast.LENGTH_LONG).show();
        LocationUtils.updateLastLocationUPdate(ctx);
        EventBus.getDefault().post(
            new LocationChangedEventBuilder()
                .setLocation(location)
                .build());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        lg.debug("Location provider {} status  has changed to {} with {}", provider, status, extras);
        Toast.makeText(ctx, "Location provider " + provider + " status changed to " + status, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        lg.info("Provider {} enabled", provider);

        EventBus.getDefault().post(
            new ProviderStatusChangedEventBuilder()
                .setProvider(provider)
                .setEnabled(true)
                .build()
        );
    }

    @Override
    public void onProviderDisabled(String provider) {
        lg.info("Provider {} disabled", provider);

        EventBus.getDefault().post(
            new ProviderStatusChangedEventBuilder()
                .setProvider(provider)
                .setEnabled(false)
                .build()
        );
    }

}

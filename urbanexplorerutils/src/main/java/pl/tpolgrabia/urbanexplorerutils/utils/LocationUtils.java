package pl.tpolgrabia.urbanexplorerutils.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class LocationUtils {

    private static final Logger lg = LoggerFactory.getLogger(LocationUtils.class);
    public static String getDefaultLocation(Context ctx) {

        if (ctx == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        LocationManager locationService = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (locationService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lg.debug("GPS location provider is enabled");
            return LocationManager.GPS_PROVIDER;
        }

        if (locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            lg.debug("Network location provider is enabled");
            return LocationManager.NETWORK_PROVIDER;
        }

        lg.trace("All provider: {}", locationService.getAllProviders());

        lg.debug("All location providers all disabled");

        return null;
    }

    public static Location getLastKnownLocation(Context ctx) {
        String locationProvider = getDefaultLocation(ctx);

        if (locationProvider == null) {
            lg.info("Location not available");
            return null;
        }

        return NetUtils.getSystemService(ctx).getLastKnownLocation(locationProvider);
    }
}

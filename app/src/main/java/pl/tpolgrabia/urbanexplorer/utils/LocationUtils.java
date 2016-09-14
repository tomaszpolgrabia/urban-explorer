package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class LocationUtils {
    public static String getDefaultLocation(Context ctx) {
        LocationManager locationService = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
        if (locationService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }

        if (locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        }

        return null;
    }
}
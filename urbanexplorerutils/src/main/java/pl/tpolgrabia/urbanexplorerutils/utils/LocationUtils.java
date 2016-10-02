package pl.tpolgrabia.urbanexplorerutils.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class LocationUtils {

    private static final Logger lg = LoggerFactory.getLogger(LocationUtils.class);
    public static final String LOCATION_UPDATE_TIME_CACHE = "location-cache.dat";

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

    public static void updateLastLocationUPdate(Context ctx) {
        File f = new File(ctx.getCacheDir(), LOCATION_UPDATE_TIME_CACHE);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, false)));
            bw.write("" + System.currentTimeMillis());
        } catch (FileNotFoundException e) {
            lg.error("File cannot be found", e);
        } catch (IOException e) {
            lg.error("Error during writing to file", e);
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    lg.error("Error during writer closing", e);
                }
            }
        }
    }

    public static Long getLastLocationUpdate(Context ctx) {
        File f = new File(ctx.getCacheDir(), LOCATION_UPDATE_TIME_CACHE);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return Long.parseLong(sb.toString());
        } catch (FileNotFoundException e) {
            return Long.MIN_VALUE;
        } catch (IOException e) {
            lg.error("I/O error during reading last location update timestamp", e);
            return Long.MIN_VALUE;
        } catch (NumberFormatException e) {
            lg.error("Wrong timestamp format", e);
            return Long.MIN_VALUE;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    lg.error("Cannot close reader", e);
                }
            }
        }
    }
}

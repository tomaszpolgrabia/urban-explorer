package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.callbacks.LocationGeoCoderCallback;

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

    public static void getGeoCodedLocation(Context ctx, Double latitude, Double longitude,
                                           final LocationGeoCoderCallback clbk) {
        if (ctx == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }

        if (latitude == null) {
            throw new IllegalArgumentException("Latitude cannot be null");
        }

        if (longitude == null) {
            throw new IllegalArgumentException("Longitude cannot be null");
        }

        AQuery aq = new AQuery(ctx);

        aq.ajax("https://maps.googleapis.com/maps/api/geocode/json" +
                "?latlng=" + latitude + "," + longitude +
                "&key=" + AppConstants.GOOGLE_API_KEY, JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                lg.debug("Got response from url {} with status {} - {}",
                        url,
                        status,
                        object);

                String googleStatus = object != null ? object.optString("status") : "(null)";
                lg.trace("Google status {}", googleStatus);

                if (status.getCode() != 200) {
                    lg.info("Got invalid response with error code {} and message {} and error {}",
                            status.getCode(), status.getMessage(), status.getError());
                    clbk.callback(status.getCode(), status.getMessage(), googleStatus, null);
                    return;
                }

                if (!"OK".equals(googleStatus)) {
                    lg.info("Got invalid google status {}", googleStatus);
                    clbk.callback(status.getCode(), status.getMessage(), googleStatus, null);
                    return;
                }

                JSONArray results = object.optJSONArray("results");
                if (results == null) {
                    clbk.callback(status.getCode(), status.getMessage(), googleStatus, null);
                    return;
                }

                int n = results.length();
                for (int i = 0; i < n; i++) {
                    result = results.optJSONObject(i);
                    if (result == null) {
                        continue;
                    }

                    JSONArray types = result.optJSONArray("types");
                    if (types == null) {
                        continue;
                    }

                    if (types.length() != 1){
                        continue;
                    }

                    String singleType = types.optString(0);
                    if (!"street_address".equals(singleType)) {
                        continue;
                    }
                    clbk.callback(status.getCode(),
                            status.getMessage(),
                            googleStatus,
                            result.optString("formatted_address"));
                    return;
                }

                clbk.callback(status.getCode(), status.getMessage(), googleStatus, "(not found)");

            }
        });
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

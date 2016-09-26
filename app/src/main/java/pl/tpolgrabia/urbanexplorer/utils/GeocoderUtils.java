package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.location.Location;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorerutils.callbacks.LocationGeoCoderCallback;
import pl.tpolgrabia.urbanexplorerutils.constants.UtilConstants;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;
import pl.tpolgrabia.wikibinding.utils.WikiUtils;

/**
 * Created by tpolgrabia on 26.09.16.
 */
public class GeocoderUtils {

    private static final Logger lg = LoggerFactory.getLogger(GeocoderUtils.class);
    private Context ctx;
    private final String googleApiKey;

    public GeocoderUtils(Context ctx, String googleApiKey) {
        this.ctx = ctx;
        this.googleApiKey = googleApiKey;
    }

    public void getGeoCodedLocation(LocationGeoCoderCallback clbk) {

        if (ctx == null) {
            lg.warn("Context is null - not available");
            clbk.callback(-1, "ERROR", "ERROR", "Not available");
            return;
        }

        Location location = LocationUtils.getLastKnownLocation(ctx);

        if (location == null) {
            lg.debug("Location is still not available");
            return;
        }

        getGeoCodedLocation(
            location.getLatitude(),
            location.getLongitude(),
            clbk);
    }

    public void getGeoCodedLocation(Double latitude, Double longitude,
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
                "&key=" + googleApiKey, JSONObject.class, new AjaxCallback<JSONObject>(){
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
}

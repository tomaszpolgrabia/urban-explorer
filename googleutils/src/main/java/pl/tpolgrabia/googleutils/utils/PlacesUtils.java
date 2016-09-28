package pl.tpolgrabia.googleutils.utils;

import android.content.Context;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tpolgrabia on 27.09.16.
 */
public class PlacesUtils {

    private static final Logger lg = LoggerFactory.getLogger(PlacesUtils.class);

    private final Context ctx;
    private final String apiKey;
    private final AQuery aq;

    public PlacesUtils(Context ctx, String apiKey) {
        this.ctx = ctx;
        this.apiKey = apiKey;
        this.aq = new AQuery(ctx);
    }

    public void fetchNearbyPlaces(Double latitude, Double longitude, Double searchRadius, String searchItemType, String pageToken) {

        if (latitude == null) {
            throw new IllegalArgumentException("Latitude cannot be null");
        }

        if (longitude == null) {
            throw new IllegalArgumentException("Longitude cannot be null");
        }

        if (searchRadius == null) {
            throw new IllegalArgumentException("Search radius cannot be null");
        }

        String queryUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
            "key=" + apiKey
            + "&location=" + latitude + "," + longitude
            + "&radius=" + searchRadius
            + "&type=" + searchItemType;

        if (pageToken != null) {
            queryUrl += "&pagetoken=" + pageToken;
        }

        aq.ajax(queryUrl,
            JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    lg.trace("Url: {}, object: {}, status: {}", url, object, status);

                    int statusCode = status.getCode();
                    String statusMessage = status.getMessage();
                    String statusError = status.getError();

                    if (statusCode != 200) {
                        lg.error("Invalid status code: {}, message: {}, error: {}",
                            statusCode,
                            statusMessage,
                            statusError);
                        return;
                    }

                    String googleStatus = object.optString("status");
                    if (!"OK".equals(googleStatus)) {
                        lg.error("Invalid google status: {}", googleStatus);
                        return;
                    }



                }
            });
    }
}

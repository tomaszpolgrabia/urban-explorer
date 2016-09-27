package pl.tpolgrabia.googleutils.utils;

import android.content.Context;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.JsonObject;

/**
 * Created by tpolgrabia on 27.09.16.
 */
public class PlacesUtils {

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
            JsonObject.class, new AjaxCallback<JsonObject>() {
                @Override
                public void callback(String url, JsonObject object, AjaxStatus status) {
                    super.callback(url, object, status);
                }
            });
    }
}

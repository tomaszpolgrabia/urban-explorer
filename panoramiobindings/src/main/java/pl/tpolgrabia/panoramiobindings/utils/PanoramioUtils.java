package pl.tpolgrabia.panoramiobindings.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.Display;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.callback.PanoramioResponseCallback;
import pl.tpolgrabia.panoramiobindings.callback.PanoramioResponseStatus;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioMapLocation;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioResponse;
import pl.tpolgrabia.panoramiobindings.exceptions.PanoramioResponseNotExpected;
import pl.tpolgrabia.urbanexplorerutils.utils.NetUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioUtils {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioUtils.class);
    private static final String CLASS_TAG = PanoramioUtils.class.getSimpleName();

    private static final String LOCATIONS_LIST_IMAGE_SIZE = "medium";
    private static final String LOCATIONS_ORDER = "popularity";

    public static void fetchPanoramioImages(
        Context ctx,
        Double lat,
        Double lon,
        Double radiusX,
        Double radiusY,
        Long offset,
        Long count,
        final PanoramioResponseCallback callback) {
        AQuery aq = NetUtils.createProxyAQueryInstance(ctx);
        final String aqQuery = "http://www.panoramio.com/map/get_panoramas.php?" +
            "set=public" +
            "&from=" + offset +
            "&to="   + (offset + count) +
            "&minx=" + (lon - radiusX) +
            "&miny=" + (lat - radiusY) +
            "&maxx=" + (lon + radiusX) +
            "&maxy=" + (lat + radiusX) +
            "&size=" + LOCATIONS_LIST_IMAGE_SIZE +
            "&order=" + LOCATIONS_ORDER +
            "&mapfilter=true";
        lg.debug("Query URL: {}", aqQuery);
        aq.ajax(aqQuery,
            JSONObject.class,
            new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    try {
                        lg.debug("Query code: {}, error: {}, message: {}, object: {}",
                            status.getCode(), status.getError(), status.getMessage(), object);
                        if (object == null) {
                            return;
                        }

                        List<PanoramioImageInfo> photosInfos;
                        try {
                            photosInfos = PanoramioUtils.fetchPanoramioImagesFromResponse(object.getJSONArray("photos"));
                        } catch (ParseException e) {
                            lg.warn("Parse exception", e);
                            photosInfos = new ArrayList<>();
                        }

                        Long photosCount = PanoramioUtils.fetchPanoramioImagesCountFromResponse(object);
                        callback.callback(PanoramioResponseStatus.SUCCESS,
                            photosInfos,
                            photosCount);

                    } catch (JSONException e) {
                        lg.warn("Json not supported format", e);
                    }
                }
            });
    }

    public static PanoramioImageInfo fetchPanoramioDto(JSONObject photo) throws JSONException, ParseException {
        PanoramioImageInfo info = new PanoramioImageInfo();
        info.setPhotoTitle(photo.getString("photo_title"));
        info.setPhotoFileUrl(photo.getString("photo_file_url"));
        info.setWidth(photo.getDouble("width"));
        info.setHeight(photo.getDouble("height"));
        info.setLatitude(photo.getDouble("latitude"));
        info.setLongitude(photo.getDouble("longitude"));
        info.setOwnerId(photo.getLong("owner_id"));
        info.setOwnerName(photo.getString("owner_name"));
        info.setOwnerUrl(photo.getString("owner_url"));
        info.setPhotoId(photo.getLong("photo_id"));
        info.setPhotoUrl(photo.getString("photo_url"));
        info.setUploadDate(photo.getString("upload_date"));
        return info;
    }

    public static Long fetchPanoramioImagesCountFromResponse(JSONObject object) {
        try {
            return object.getLong("count");
        } catch (JSONException e) {
            return null;
        }
    }

    public static List<PanoramioImageInfo> fetchPanoramioImagesFromResponse(JSONArray photos) throws JSONException, ParseException {

        if (photos == null) {
            throw new PanoramioResponseNotExpected("photos arg cannot be null");
        }

        List<PanoramioImageInfo> photosInfos = new ArrayList<>();
        int n = photos.length();
        for (int i = 0; i < n; i++) {
            photosInfos.add(
                fetchPanoramioDto(
                    photos.getJSONObject(i)));
        }

        return photosInfos;
    }

    public static PanoramioResponse fetchPanoramioResponse(JSONObject panoramioResponse) throws JSONException, ParseException {
        PanoramioResponse response = new PanoramioResponse();
        response.setCount(panoramioResponse.getLong("count"));
        response.setMoreAvailable(panoramioResponse.getBoolean("has_more"));
        response.setPhotos(fetchPanoramioImagesFromResponse(panoramioResponse.getJSONArray("photos")));
        response.setMapLocation(fetchPanoramioLocation(panoramioResponse.getJSONObject("map_location")));
        return response;
    }

    private static PanoramioMapLocation fetchPanoramioLocation(JSONObject mapLocation) throws JSONException {
        PanoramioMapLocation location = new PanoramioMapLocation();
        location.setLatitude(mapLocation.getDouble("lat"));
        location.setLongitude(mapLocation.getDouble("lon"));
        location.setZoom(mapLocation.getLong("panoramio_zoom"));
        return location;
    }


    public static PointF calcDims(Activity ctx) {
        DisplayMetrics metrics = calcMetrics(ctx);
        int w = metrics.widthPixels;
        int h = metrics.heightPixels;
        float wi = w / metrics.xdpi;
        float hi = h / metrics.ydpi;
        return new PointF(wi, hi);
    }

    public static DisplayMetrics calcMetrics(Activity ctx) {
        DisplayMetrics metrics = new DisplayMetrics();
        final Display display = ctx.getWindowManager().getDefaultDisplay();
        display.getMetrics(metrics);
        return metrics;
    }

    public static double calcDiag(Activity ctx) {
        PointF dims = calcDims(ctx);
        return Math.sqrt(dims.x*dims.x + dims.y*dims.y);
    }
}

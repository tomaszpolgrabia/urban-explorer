package pl.tpolgrabia.urbanexplorer.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.exceptions.PanoramioResponseNotExpected;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class PanoramioUtils {

    public static PanoramioImageInfo fetchPanoramioDto(JSONObject photo) throws JSONException {
        PanoramioImageInfo info = new PanoramioImageInfo();
        info.setPhotoTitle(photo.getString("photo_title"));
        info.setPhotoFileUrl(photo.getString("photo_file_url"));
        info.setWidth(photo.getDouble("width"));
        info.setHeight(photo.getDouble("height"));
        return info;
    }

    public static Long fetchPanoramioImagesCountFromResponse(JSONObject object) {
        try {
            return object.getLong("count");
        } catch (JSONException e) {
            return null;
        }
    }

    public static List<PanoramioImageInfo> fetchPanoramioImagesFromResponse(JSONObject object) throws JSONException {

        JSONArray photos = object.getJSONArray("photos");
        if (photos == null) {
            throw new PanoramioResponseNotExpected("Panoramio response doesn't contain phots");
        }

        List<PanoramioImageInfo> photosInfos = new ArrayList<PanoramioImageInfo>();
        int n = photos.length();
        for (int i = 0; i < n; i++) {
            photosInfos.add(
                fetchPanoramioDto(
                    photos.getJSONObject(i)));
        }

        return photosInfos;
    }
}

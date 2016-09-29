package pl.tpolgrabia.googleutils.converter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceConverter {

    private static final Logger lg = LoggerFactory.getLogger(GooglePlaceConverter.class);
    public static GooglePlaceResult convertToPlaceResult(JSONObject object) {

        lg.trace("Place result object: {}", object);

        if (object == null) {
            return null;
        }

        GooglePlaceResult dto = new GooglePlaceResult();
        dto.setGeometry(convertToPlaceGeometry(object.optJSONObject("geometry")));
        dto.setIcon(object.optString("icon"));
        dto.setId(object.optString("id"));
        dto.setName(object.optString("name"));
        dto.setPhotos(convertToPlacePhotos(object.optJSONArray("photos")));
        dto.setPlaceId(object.optString("place_id"));
        dto.setRating(object.optDouble("rating"));
        dto.setReference(object.optString("reference"));
        dto.setScope(object.optString("scope"));
        dto.setTypes(convertToStringList(object.optJSONArray("types")));
        dto.setVicinity(object.optString("vicinity"));
        return dto;
    }

    private static List<String> convertToStringList(JSONArray stringArray) {
        lg.trace("String array: {}", stringArray);

        if (stringArray == null) {
            return null;
        }

        int n = stringArray.length();
        List<String> ret = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ret.add(stringArray.optString(i));
        }

        return ret;
    }

    private static List<GooglePlacePhoto> convertToPlacePhotos(JSONArray jphotos) {
        lg.trace("Place photos: {}", jphotos);

        if (jphotos == null) {
            return null;
        }

        int n = jphotos.length();
        List<GooglePlacePhoto> photos = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            photos.add(convertToPlacePhoto(jphotos.optJSONObject(i)));
        }

        return photos;
    }

    private static GooglePlacePhoto convertToPlacePhoto(JSONObject jphoto) {
        lg.trace("Place photo: {}", jphoto);

        if (jphoto == null) {
            return null;
        }

        GooglePlacePhoto photo = new GooglePlacePhoto();
        photo.setHeight(jphoto.optLong("height"));
        photo.setWidth(jphoto.optLong("width"));
        photo.setPhotoReference(jphoto.optString("photo_reference"));
        photo.setHtmlAttributions(convertToStringList(jphoto.optJSONArray("html_attributions")));
        return photo;
    }

    private static GooglePlaceGeometry convertToPlaceGeometry(JSONObject jgeometry) {
        lg.trace("Place geometry: {}", jgeometry);

        if (jgeometry == null) {
            return null;
        }

        GooglePlaceGeometry geometry = new GooglePlaceGeometry();
        geometry.setLocation(convertToPlaceLocation(jgeometry.optJSONObject("location")));
        geometry.setViewport(convertToPlaceViewport(jgeometry.optJSONObject("viewport")));
        return geometry;
    }

    private static GooglePlaceViewport convertToPlaceViewport(JSONObject jviewport) {
        lg.trace("Place viewport: {}", jviewport);

        if (jviewport == null) {
            return null;
        }

        GooglePlaceViewport viewport = new GooglePlaceViewport();
        viewport.setNorthEast(convertToPlaceLocation(jviewport.optJSONObject("northeast")));
        viewport.setSouthWest(convertToPlaceLocation(jviewport.optJSONObject("southwest")));
        return viewport;
    }

    private static GooglePlaceLocation convertToPlaceLocation(JSONObject jlocation) {
        lg.trace("Place location: {}", jlocation);
        if (jlocation == null) {
            return null;
        }

        GooglePlaceLocation location = new GooglePlaceLocation();
        location.setLatitude(jlocation.optDouble("lat"));
        location.setLongitude(jlocation.optDouble("lng"));
        return location;
    }

    public static List<GooglePlaceResult> convertToPlaceResults(JSONArray jresults) {
        lg.trace("Place results: {}", jresults);

        if (jresults == null) {
            return null;
        }

        List<GooglePlaceResult> results = new ArrayList<>();
        int n = jresults.length();
        for (int i = 0; i < n; i++) {
            results.add(convertToPlaceResult(jresults.optJSONObject(i)));
        }
        return results;
    }
}

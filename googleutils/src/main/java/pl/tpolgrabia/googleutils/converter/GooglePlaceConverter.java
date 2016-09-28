package pl.tpolgrabia.googleutils.converter;

import org.json.JSONArray;
import org.json.JSONObject;
import pl.tpolgrabia.googleutils.dto.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceConverter {
    public static GooglePlaceResult convertToPlaceResult(JSONObject object) {
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

    private static List<String> convertToStringList(JSONArray types) {
        int n = types.length();
        List<String> ret = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            ret.add(types.optString(i));
        }

        return ret;
    }

    private static List<GooglePlacePhoto> convertToPlacePhotos(JSONArray jphotos) {
        int n = jphotos.length();
        List<GooglePlacePhoto> photos = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            photos.add(convertToPlacePhoto(jphotos.optJSONObject(i)));
        }

        return photos;
    }

    private static GooglePlacePhoto convertToPlacePhoto(JSONObject jphoto) {
        GooglePlacePhoto photo = new GooglePlacePhoto();
        photo.setHeight(jphoto.optLong("height"));
        photo.setWidth(jphoto.optLong("width"));
        photo.setPhotoReference(jphoto.optString("photo_reference"));
        photo.setHtmlAttributions(convertToStringList(jphoto.optJSONArray("html_attributions")));
        return photo;
    }

    private static GooglePlaceGeometry convertToPlaceGeometry(JSONObject jgeometry) {
        GooglePlaceGeometry geometry = new GooglePlaceGeometry();
        geometry.setLocation(convertToPlaceLocation(jgeometry.optJSONObject("location")));
        geometry.setViewport(convertToPlaceViewport(jgeometry.optJSONObject("viewport")));
        return geometry;
    }

    private static GooglePlaceViewport convertToPlaceViewport(JSONObject jviewport) {
        GooglePlaceViewport viewport = new GooglePlaceViewport();
        viewport.setNorthEast(convertToPlaceLocation(jviewport.optJSONObject("northeast")));
        viewport.setSouthWest(convertToPlaceLocation(jviewport.optJSONObject("southwest")));
        return viewport;
    }

    private static GooglePlaceLocation convertToPlaceLocation(JSONObject jlocation) {
        GooglePlaceLocation location = new GooglePlaceLocation();
        location.setLatitude(jlocation.optDouble("lat"));
        location.setLongitude(jlocation.optDouble("lng"));
        return location;
    }
}

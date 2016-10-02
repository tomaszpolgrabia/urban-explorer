package pl.tpolgrabia.googleutils.callback;

import pl.tpolgrabia.googleutils.dto.GooglePlacePhotoRefResult;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;

import java.util.List;

/**
 * Created by tpolgrabia on 02.10.16.
 */
public interface GooglePlacesPhotosCallback {
    void onResponse(int code, String message, List<GooglePlacePhotoRefResult> body);
    void onFailure(Throwable t);
}

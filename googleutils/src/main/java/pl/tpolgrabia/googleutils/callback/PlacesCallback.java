package pl.tpolgrabia.googleutils.callback;

import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;

import java.util.List;

/**
 * Created by tpolgrabia on 29.09.16.
 */
public interface PlacesCallback {
    void callback(Long statusCode, String statusMsg, List<GooglePlaceResult> googlePlaceResults);
}

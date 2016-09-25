package pl.tpolgrabia.wikibinding.dto.geosearch;

import pl.tpolgrabia.wikibinding.callback.WikiStatus;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public interface WikiGeoResponseCallback {
    void callback(WikiStatus status, WikiGeoResponse response);
}

package pl.tpolgrabia.urbanexplorer.dto.wiki.geosearch;

import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public interface WikiGeoResponseCallback {
    void callback(WikiStatus status, WikiGeoResponse response);
}

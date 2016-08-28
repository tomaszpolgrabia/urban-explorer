package pl.tpolgrabia.urbanexplorer.callbacks;

import pl.tpolgrabia.urbanexplorer.dto.WikiResponse;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public interface WikiResponseCallback {
    void callback(WikiStatus status, WikiResponse response);
}

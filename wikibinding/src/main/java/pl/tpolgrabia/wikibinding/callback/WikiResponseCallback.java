package pl.tpolgrabia.wikibinding.callback;

import pl.tpolgrabia.wikibinding.dto.generator.WikiResponse;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public interface WikiResponseCallback {
    void callback(WikiStatus status, WikiResponse response);
}

package pl.tpolgrabia.urbanexplorer.callbacks;

import org.json.JSONObject;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public interface PanoramioResponseCallback {
    void callback(PanoramioResponseStatus status, JSONObject response);
}

package pl.tpolgrabia.urbanexplorer.utils;

import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;

import java.util.List;

/**
 * Created by tpolgrabia on 11.09.16.
 */
public interface WikiAppResponseCallback {
    void callback(WikiStatus status, List<WikiAppObject> appObjects);
}

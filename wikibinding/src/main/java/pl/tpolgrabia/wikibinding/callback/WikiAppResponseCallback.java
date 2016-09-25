package pl.tpolgrabia.wikibinding.callback;

import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;

import java.util.List;

/**
 * Created by tpolgrabia on 11.09.16.
 */
public interface WikiAppResponseCallback {
    void callback(WikiStatus status, List<WikiAppObject> appObjects);
}

package pl.tpolgrabia.panoramiobindings.callback;

import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public interface PanoramioResponseCallback {
    void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount);
}

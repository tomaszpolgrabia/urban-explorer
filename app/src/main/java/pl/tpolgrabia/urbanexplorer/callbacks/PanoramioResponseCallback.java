package pl.tpolgrabia.urbanexplorer.callbacks;


import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;

import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public interface PanoramioResponseCallback {
    void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount);
}

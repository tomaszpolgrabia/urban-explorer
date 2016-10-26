package pl.tpolgrabia.urbanexplorer.events;

import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.handlers.PanoramioItemLongClickHandler;

/**
 * Created by tpolgrabia on 26.10.16.
 */
public class PhotoInfoUpdateEvent {
    private Object source;
    private PanoramioImageInfo photoInfo;

    public PhotoInfoUpdateEvent(Object source, PanoramioImageInfo photoInfo) {
        this.source = source;
        this.photoInfo = photoInfo;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public PanoramioImageInfo getPhotoInfo() {
        return photoInfo;
    }

    public void setPhotoInfo(PanoramioImageInfo photoInfo) {
        this.photoInfo = photoInfo;
    }

    @Override
    public String toString() {
        return "PhotoInfoUpdateEvent{" +
            "source=" + source +
            ", photoInfo=" + photoInfo +
            '}';
    }
}

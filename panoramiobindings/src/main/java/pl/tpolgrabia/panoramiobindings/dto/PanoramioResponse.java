package pl.tpolgrabia.panoramiobindings.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class PanoramioResponse implements Serializable{
    private static final long serialVersionUID = 8840731825651350777L;
    @SerializedName("photos")
    private List<PanoramioImageInfo> photos;
    @SerializedName("count")
    private Long count;
    @SerializedName("moreAvailable")
    private Boolean moreAvailable;
    @SerializedName("map_location")
    private PanoramioMapLocation mapLocation;

    public List<PanoramioImageInfo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PanoramioImageInfo> photos) {
        this.photos = photos;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Boolean getMoreAvailable() {
        return moreAvailable;
    }

    public void setMoreAvailable(Boolean moreAvailable) {
        this.moreAvailable = moreAvailable;
    }

    public PanoramioMapLocation getMapLocation() {
        return mapLocation;
    }

    public void setMapLocation(PanoramioMapLocation mapLocation) {
        this.mapLocation = mapLocation;
    }

    @Override
    public String toString() {
        return "PanoramioResponse{" +
            "photos=" + photos +
            ", count=" + count +
            ", moreAvailable=" + moreAvailable +
            ", mapLocation=" + mapLocation +
            '}';
    }
}

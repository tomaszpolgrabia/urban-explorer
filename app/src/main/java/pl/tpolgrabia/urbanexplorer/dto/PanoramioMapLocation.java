package pl.tpolgrabia.urbanexplorer.dto;

import java.io.Serializable;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class PanoramioMapLocation implements Serializable{
    private static final long serialVersionUID = -3048527017887972550L;
    private Double latitude;
    private Double longitude;
    private Long zoom;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Long getZoom() {
        return zoom;
    }

    public void setZoom(Long zoom) {
        this.zoom = zoom;
    }

    @Override
    public String toString() {
        return "PanoramioMapLocation{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            ", zoom=" + zoom +
            '}';
    }
}

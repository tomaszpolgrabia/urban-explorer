package pl.tpolgrabia.urbanexplorer.dto.panoramio;

import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by tpolgrabia on 18.09.16.
 */
public class PanoramioCacheDto implements Serializable{
    private static final long serialVersionUID = -8856222832500878380L;
    private List<PanoramioImageInfo> panoramioImages;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Date fetchedAt;

    public Date getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public List<PanoramioImageInfo> getPanoramioImages() {
        return panoramioImages;
    }

    public void setPanoramioImages(List<PanoramioImageInfo> panoramioImages) {
        this.panoramioImages = panoramioImages;
    }

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

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    @Override
    public String toString() {
        return "PanoramioCacheDto{" +
            "panoramioImages=" + panoramioImages +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", altitude=" + altitude +
            ", fetchedAt=" + fetchedAt +
            '}';
    }
}

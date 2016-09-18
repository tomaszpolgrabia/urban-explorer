package pl.tpolgrabia.urbanexplorer.dto.wiki;

import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by tpolgrabia on 18.09.16.
 */
public class WikiCacheDto implements Serializable{
    private static final long serialVersionUID = 3957902509576625035L;

    private List<WikiAppObject> appObject;
    private Double longitude;
    private Double latitude;
    private Double altitude;
    private Date fetchedAt;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<WikiAppObject> getAppObject() {
        return appObject;
    }

    public void setAppObject(List<WikiAppObject> appObject) {
        this.appObject = appObject;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Date getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(Date fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    @Override
    public String toString() {
        return "WikiCacheDto{" +
            "appObject=" + appObject +
            ", longitude=" + longitude +
            ", latitude=" + latitude +
            ", altitude=" + altitude +
            ", fetchedAt=" + fetchedAt +
            '}';
    }
}

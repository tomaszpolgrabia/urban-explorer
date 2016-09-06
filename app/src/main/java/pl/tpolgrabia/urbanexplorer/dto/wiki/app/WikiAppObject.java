package pl.tpolgrabia.urbanexplorer.dto.wiki.app;

import java.io.Serializable;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public class WikiAppObject implements Serializable {
    private String url;
    private String thumbnail;
    private String title;
    private String latitude;
    private String longitude;
    private String distance;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "WikiAppObject{" +
            "url='" + url + '\'' +
            ", thumbnail='" + thumbnail + '\'' +
            ", title='" + title + '\'' +
            ", latitude='" + latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            ", distance='" + distance + '\'' +
            '}';
    }
}

package pl.tpolgrabia.urbanexplorer.dto.wiki.app;

import java.io.Serializable;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public class WikiAppObject implements Serializable {
    private String url;
    private String thumbnail;
    private String title;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private Long pageId;

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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
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

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }
}

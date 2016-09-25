package pl.tpolgrabia.wikibinding.dto.geosearch;

import java.io.Serializable;

/**
 * Created by tpolgrabia on 05.09.16.
 */
public class WikiGeoObject implements Serializable {
    private static final long serialVersionUID = 4527861009683008530L;
    private Long pageId;
    private Long ns;
    private String title;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private String primary;

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Long getNs() {
        return ns;
    }

    public void setNs(Long ns) {
        this.ns = ns;
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

    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return "WikiGeoObject{" +
            "pageId=" + pageId +
            ", ns=" + ns +
            ", title='" + title + '\'' +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", distance=" + distance +
            ", primary='" + primary + '\'' +
            '}';
    }
}

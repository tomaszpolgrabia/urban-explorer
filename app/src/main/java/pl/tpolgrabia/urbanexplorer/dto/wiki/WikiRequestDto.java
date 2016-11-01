package pl.tpolgrabia.urbanexplorer.dto.wiki;

/**
 * Created by tpolgrabia on 01.11.16.
 */
public class WikiRequestDto {
    private Double latitude;
    private Double longitude;
    private Double radius;
    private Long limit;

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

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "WikiRequestDto{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            ", radius=" + radius +
            ", limit=" + limit +
            '}';
    }
}

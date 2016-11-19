package pl.tpolgrabia.urbanexplorer.dto;

/**
 * Created by tpolgrabia on 19.11.16.
 */
public class PanoramioRequest {
    private Long offset;
    private Long count;
    private Double latitude;
    private Double longitude;
    private Double radiusX;
    private Double radiusY;

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
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

    public Double getRadiusX() {
        return radiusX;
    }

    public void setRadiusX(Double radiusX) {
        this.radiusX = radiusX;
    }

    public Double getRadiusY() {
        return radiusY;
    }

    public void setRadiusY(Double radiusY) {
        this.radiusY = radiusY;
    }

    @Override
    public String toString() {
        return "PanoramioRequest{" +
            "offset=" + offset +
            ", count=" + count +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", radiusX=" + radiusX +
            ", radiusY=" + radiusY +
            '}';
    }
}

package pl.tpolgrabia.googleutils.dto;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceLocation {
    private Double latitude;
    private Double longitude;

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

    @Override
    public String toString() {
        return "GooglePlaceLocation{" +
            "latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}

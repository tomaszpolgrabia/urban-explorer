package pl.tpolgrabia.googleutils.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceLocation {
    @SerializedName("lat")
    private Double latitude;
    @SerializedName("lng")
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

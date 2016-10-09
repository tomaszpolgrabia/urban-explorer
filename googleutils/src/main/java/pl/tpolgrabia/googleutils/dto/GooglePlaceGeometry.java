package pl.tpolgrabia.googleutils.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceGeometry {
    @SerializedName("location")
    private GooglePlaceLocation location;
    @SerializedName("viewport")
    private GooglePlaceViewport viewport;

    public GooglePlaceLocation getLocation() {
        return location;
    }

    public void setLocation(GooglePlaceLocation location) {
        this.location = location;
    }

    public GooglePlaceViewport getViewport() {
        return viewport;
    }

    public void setViewport(GooglePlaceViewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public String toString() {
        return "GooglePlaceGeometry{" +
            "location=" + location +
            ", viewport=" + viewport +
            '}';
    }
}

package pl.tpolgrabia.googleutils.dto;

import com.google.gson.annotations.SerializedName;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceViewport {
    @SerializedName("northeast")
    private GooglePlaceLocation northEast;
    @SerializedName("southwest")
    private GooglePlaceLocation southWest;

    public GooglePlaceLocation getNorthEast() {
        return northEast;
    }

    public void setNorthEast(GooglePlaceLocation northEast) {
        this.northEast = northEast;
    }

    public GooglePlaceLocation getSouthWest() {
        return southWest;
    }

    public void setSouthWest(GooglePlaceLocation southWest) {
        this.southWest = southWest;
    }

    @Override
    public String toString() {
        return "GooglePlaceViewport{" +
            "northEast=" + northEast +
            ", southWest=" + southWest +
            '}';
    }
}

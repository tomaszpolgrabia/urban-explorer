package pl.tpolgrabia.googleutils.dto;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlaceViewport {
    private GooglePlaceLocation northEast;
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

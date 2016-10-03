package pl.tpolgrabia.urbanexplorer.dto;

import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;

import java.util.List;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlacesResponse {
    private List<GooglePlaceResult> places;

    public List<GooglePlaceResult> getPlaces() {
        return places;
    }

    public void setPlaces(List<GooglePlaceResult> places) {
        this.places = places;
    }

    @Override
    public String toString() {
        return "GooglePlacesResponse{" +
            "places=" + places +
            '}';
    }
}

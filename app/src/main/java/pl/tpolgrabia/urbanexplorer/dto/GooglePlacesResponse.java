package pl.tpolgrabia.urbanexplorer.dto;

import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;

import java.util.List;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlacesResponse {
    private List<GooglePlaceResult> places;

    private String nextPageToken;
    private String originalPageToken;
    private String status;

    public List<GooglePlaceResult> getPlaces() {
        return places;
    }

    public void setPlaces(List<GooglePlaceResult> places) {
        this.places = places;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public void setOriginalPageToken(String originalPageToken) {
        this.originalPageToken = originalPageToken;
    }

    public String getOriginalPageToken() {
        return originalPageToken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "GooglePlacesResponse{" +
            "places=" + places +
            ", nextPageToken='" + nextPageToken + '\'' +
            ", originalPageToken='" + originalPageToken + '\'' +
            ", status='" + status + '\'' +
            '}';
    }
}

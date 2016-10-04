package pl.tpolgrabia.urbanexplorer.dto;

import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;

import java.util.List;

/**
 * Created by tpolgrabia on 04.10.16.
 */
public class GooglePlacesState {

    private List<GooglePlaceResult> places;
    private String nextPageToken;
    private boolean noMoreResults;

    public void setPlaces(List<GooglePlaceResult> places) {
        this.places = places;
    }

    public List<GooglePlaceResult> getPlaces() {
        return places;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNoMoreResults(boolean noMoreResults) {
        this.noMoreResults = noMoreResults;
    }

    public boolean isNoMoreResults() {
        return noMoreResults;
    }

    @Override
    public String toString() {
        return "GooglePlacesState{" +
            "places=" + places +
            ", nextPageToken='" + nextPageToken + '\'' +
            ", noMoreResults=" + noMoreResults +
            '}';
    }
}

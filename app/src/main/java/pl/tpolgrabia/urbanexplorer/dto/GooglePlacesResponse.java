package pl.tpolgrabia.urbanexplorer.dto;

import pl.tpolgrabia.googleutils.dto.GooglePlacePhotoRefResult;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;

import java.util.List;
import java.util.Map;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlacesResponse {
    private List<GooglePlaceResult> places;
    private Map<String, List<GooglePlacePhotoRefResult>> photos;

    public List<GooglePlaceResult> getPlaces() {
        return places;
    }

    public void setPlaces(List<GooglePlaceResult> places) {
        this.places = places;
    }

    public Map<String, List<GooglePlacePhotoRefResult>> getPhotos() {
        return photos;
    }

    public void setPhotos(Map<String, List<GooglePlacePhotoRefResult>> photos) {
        this.photos = photos;
    }

    @Override
    public String toString() {
        return "GooglePlacesResponse{" +
            "places=" + places +
            ", photos=" + photos +
            '}';
    }
}

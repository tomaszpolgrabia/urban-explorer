package pl.tpolgrabia.googleutils;

import pl.tpolgrabia.googleutils.dto.GooglePlaceResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tpolgrabia on 02.10.16.
 */
public interface GooglePlacesService {
    @GET("nearbysearch/json")
    Call<GooglePlaceResponse> fetchNearbyPlacesFirst(
        @Query("key") String apiKey,
        @Query("location") String location,
        @Query("radius") Double radius,
        @Query("type") String type);

    @GET("nearbysearch/json")
    Call<GooglePlaceResponse> fetchNearbyPlacesNext(
        @Query("key") String apiKey,
        @Query("location") String location,
        @Query("radius") Double radius,
        @Query("type") String type,
        @Query("pagetoken") String pageToken);
}

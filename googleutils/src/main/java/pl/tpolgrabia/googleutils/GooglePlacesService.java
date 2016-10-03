package pl.tpolgrabia.googleutils;

import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * Created by tpolgrabia on 02.10.16.
 */
public interface GooglePlacesService {
    @GET("nearbysearch/json")
    Call<List<GooglePlaceResult>> fetchNearbyPlacesFirst(
        @Query("key") String apiKey,
        @Query("location") String location,
        @Query("radius") Double radius,
        @Query("type") String type);

    @GET("nearbysearch/json")
    Call<List<GooglePlaceResult>> fetchNearbyPlacesNext(
        @Query("key") String apiKey,
        @Query("location") String location,
        @Query("radius") Double radius,
        @Query("type") String type,
        @Query("pagetoken") String pageToken);
}

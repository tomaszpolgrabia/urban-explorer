package pl.tpolgrabia.googleutils;

import pl.tpolgrabia.googleutils.dto.GooglePlacePhotoRefResult;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

/**
 * Created by tpolgrabia on 02.10.16.
 */
public interface GooglePlacesService {
    @GET("photo")
    Call<List<GooglePlacePhotoRefResult>> fetchPhotosByRef(
        @Query("max_width") Long maxWidth,
        @Query("photoreference") String photoRef,
        @Query("key") String apiKey);
}

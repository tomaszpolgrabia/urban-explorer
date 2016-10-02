package pl.tpolgrabia.googleutils;

import pl.tpolgrabia.googleutils.dto.GooglePlacePhotoRefResult;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

/**
 * Created by tpolgrabia on 02.10.16.
 */
public interface GooglePlacesService {
    @GET("photo?maxwidth={maxWidth}" +
        "&photoreference={photoRef}" +
        "&key={apiKey}")
    Call<List<GooglePlacePhotoRefResult>> fetchPhotosByRef(
        @Path("maxWidth") Long maxWidth,
        @Path("photoRef") String photoRef,
        @Path("apiKey") String apiKey);
}

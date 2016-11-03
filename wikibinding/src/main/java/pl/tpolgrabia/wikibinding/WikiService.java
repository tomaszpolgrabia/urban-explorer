package pl.tpolgrabia.wikibinding;

import com.google.gson.JsonObject;
import pl.tpolgrabia.wikibinding.dto.generator.WikiResponse2;
import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoResponse2;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tpolgrabia on 27.10.16.
 */
public interface WikiService {
    @GET("api.php" +
        "?action=query" +
        "&list=geosearch" +
        "&format=json")
    Call<WikiGeoResponse2> fetchGeoSearch(
        @Query("gscoord") String gscoord,
        @Query("gsradius") Double radius,
        @Query("gslimit") Long limit);

    @GET("api.php" +
        "?action=query" +
        "&prop=coordinates%7Cpageimages%7Cpageterms" +
        "&colimit=50" +
        "&piprop=thumbnail" +
        "&pithumbsize=144" +
        "&pilimit=50" +
        "&wbptterms=description" +
        "&format=json")
    Call<WikiResponse2> fetchPageInfos(
        @Query("pageids") String pageIds);

    @GET("api.php" +
        "?action=query" +
        "&prop=info" +
        "&inprop=url" +
        "&format=json")
    Call<JsonObject> fetchPageInfo(
        @Query("pageids") Long pageId);
}

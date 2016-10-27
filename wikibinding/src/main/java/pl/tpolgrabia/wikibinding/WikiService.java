package pl.tpolgrabia.wikibinding;

import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by tpolgrabia on 27.10.16.
 */
public interface WikiService {
    @GET("api.php?action=query&list=geosearch&format=json")
    Call<WikiGeoResponse> fetchGeoSearch(
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
    Call<String> fetchPageInfos(
        @Query("pageids") String pageIds);

//        aq.ajax("https://" + countryCode + ".wikipedia.org/w/api.php" +
//        "?action=query" +
//        "&prop=coordinates%7Cpageimages%7Cpageterms" +
//        "&colimit=50" +
//        "&piprop=thumbnail" +
//        "&pithumbsize=144" +
//        "&pilimit=50" +
//        "&wbptterms=description" +
//        "&pageids=" + StringUtils.join(pageIds, "|") +
//        "&format=json", JSONObject.class, new AjaxCallback<JSONObject>() {
}

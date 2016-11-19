package pl.tpolgrabia.panoramiobindings.utils;

import pl.tpolgrabia.panoramiobindings.dto.PanoramioResponse;
import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by tpolgrabia on 19.11.16.
 */
public interface PanoramioService {
    Call<PanoramioResponse> fetch(
        @Query("set")       String set,
        @Query("from")      Long from,
        @Query("to")        Long to,
        @Query("minx")      Double minx,
        @Query("miny")      Double miny,
        @Query("maxx")      Double maxx,
        @Query("maxy")      Double maxy,
        @Query("size")      String size,
        @Query("order")     String order,
        @Query("mapfilter") Boolean mapFilter
    );
}

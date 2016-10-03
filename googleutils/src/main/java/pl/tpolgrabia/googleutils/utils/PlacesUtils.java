package pl.tpolgrabia.googleutils.utils;

import android.content.Context;
import com.androidquery.AQuery;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.GooglePlacesService;
import pl.tpolgrabia.googleutils.constants.GooglePlacesConstants;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResponse;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Created by tpolgrabia on 27.09.16.
 */
public class PlacesUtils {

    private static final Logger lg = LoggerFactory.getLogger(PlacesUtils.class);

    private final Context ctx;
    private final String apiKey;
    private final AQuery aq;

    public PlacesUtils(Context ctx, String apiKey) {
        this.ctx = ctx;
        this.apiKey = apiKey;
        this.aq = new AQuery(ctx);
    }

    public Response<GooglePlaceResponse> fetchNearbyPlaces(Double latitude,
                                                           Double longitude,
                                                           Double searchRadius,
                                                           String searchItemType,
                                                           String pageToken) throws IOException {

        if (latitude == null) {
            throw new IllegalArgumentException("Latitude cannot be null");
        }

        if (longitude == null) {
            throw new IllegalArgumentException("Longitude cannot be null");
        }

        if (searchRadius == null) {
            throw new IllegalArgumentException("Search radius cannot be null");
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(GooglePlacesConstants.GOOGLE_MAPS_PLACES_API_BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient.build())
            .build();

        return pageToken != null ?
            retrofit.create(GooglePlacesService.class).fetchNearbyPlacesNext(apiKey,
                latitude + "," + longitude,
                searchRadius,
                searchItemType,
                pageToken).execute()
            : retrofit.create(GooglePlacesService.class).fetchNearbyPlacesFirst(apiKey,
            latitude + "," + longitude,
            searchRadius,
            searchItemType).execute();

    }
}

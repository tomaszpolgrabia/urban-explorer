package pl.tpolgrabia.googleutils.utils;

import android.content.Context;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.GooglePlacesService;
import pl.tpolgrabia.googleutils.callback.GooglePlacesPhotosCallback;
import pl.tpolgrabia.googleutils.callback.PlacesCallback;
import pl.tpolgrabia.googleutils.constants.GooglePlacesConstants;
import pl.tpolgrabia.googleutils.converter.GooglePlaceConverter;
import pl.tpolgrabia.googleutils.dto.GooglePlacePhoto;
import pl.tpolgrabia.googleutils.dto.GooglePlacePhotoRefResult;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.*;

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

    public void fetchNearbyPlaces(Double latitude,
                                  Double longitude,
                                  Double searchRadius,
                                  String searchItemType,
                                  String pageToken,
                                  final PlacesCallback clbk) {

        if (latitude == null) {
            throw new IllegalArgumentException("Latitude cannot be null");
        }

        if (longitude == null) {
            throw new IllegalArgumentException("Longitude cannot be null");
        }

        if (searchRadius == null) {
            throw new IllegalArgumentException("Search radius cannot be null");
        }

        String queryUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
            "key=" + apiKey
            + "&location=" + latitude + "," + longitude
            + "&radius=" + searchRadius
            + "&type=" + searchItemType;

        if (pageToken != null) {
            queryUrl += "&pagetoken=" + pageToken;
        }

        aq.ajax(queryUrl,
            JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    lg.trace("Url: {}, object: {}, status: {}", url, object, status);

                    int statusCode = status.getCode();
                    String statusMessage = status.getMessage();
                    String statusError = status.getError();

                    if (statusCode != 200) {
                        lg.error("Invalid status code: {}, message: {}, error: {}",
                            statusCode,
                            statusMessage,
                            statusError);
                        clbk.callback((long)statusCode, statusMessage, null);
                        return;
                    }

                    String googleStatus = object.optString("status");
                    if (!"OK".equals(googleStatus)) {
                        lg.error("Invalid google status: {}", googleStatus);
                        clbk.callback((long)statusCode, googleStatus, null);
                        return;
                    }

                    try {
                        List<GooglePlaceResult> googleResults = GooglePlaceConverter.convertToPlaceResults(object.getJSONArray("results"));
                        clbk.callback((long) statusCode, googleStatus, googleResults);
                    } catch (Throwable t) {
                        lg.error("General error", t);
                        clbk.callback(-1L, "General error", null);
                    }

                }
            });
    }

    public List<GooglePlacePhotoRefResult> fetchPhotosByRefSync(String photosRef) throws IOException {
        Response<List<GooglePlacePhotoRefResult>> results = fetchPhotosByRefInvocation(photosRef).execute();
        return results.code() == HttpStatus.SC_OK ? results.body() : null;
    }

    public void fetchPhotosByRefAsync(String photosRef, final GooglePlacesPhotosCallback clbk) {
        fetchPhotosByRefInvocation(photosRef).enqueue(new Callback<List<GooglePlacePhotoRefResult>>() {
            @Override
            public void onResponse(Call<List<GooglePlacePhotoRefResult>> call, Response<List<GooglePlacePhotoRefResult>> response) {
                clbk.onResponse(response.code(), response.message(), response.body());
            }

            @Override
            public void onFailure(Call<List<GooglePlacePhotoRefResult>> call, Throwable t) {
                clbk.onFailure(t);
            }
        });
    }

    private Call<List<GooglePlacePhotoRefResult>> fetchPhotosByRefInvocation(String photosRef) {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(GooglePlacesConstants.GOOGLE_PLACES_BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        GooglePlacesService service = retrofit.create(GooglePlacesService.class);
        return service.fetchPhotosByRef(
            GooglePlacesConstants.PHOTO_MAX_WIDTH,
            photosRef,
            apiKey);
    }

    public Map<String, List<GooglePlacePhotoRefResult>> fetchPhotosSync(Set<String> photoRefs) throws IOException {
        HashMap<String, List<GooglePlacePhotoRefResult>> result = new HashMap<String, List<GooglePlacePhotoRefResult>>();
        for (String photoRef : photoRefs) {
            result.put(photoRef, fetchPhotosByRefSync(photoRef));
        }

        return result;
    }
}

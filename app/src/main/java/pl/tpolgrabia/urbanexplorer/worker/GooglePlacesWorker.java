package pl.tpolgrabia.urbanexplorer.worker;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.widget.Toast;
import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResponse;
import pl.tpolgrabia.googleutils.utils.PlacesUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesRequest;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesResponse;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlacesWorker extends AsyncTask<GooglePlacesRequest, Integer, List<GooglePlacesResponse>> {

    private static final Logger lg = LoggerFactory.getLogger(GooglePlacesWorker.class);

    private final PlacesUtils placesUtils;
    private final Context ctx;

    public GooglePlacesWorker(Context ctx) {
        this.placesUtils = new PlacesUtils(ctx, AppConstants.GOOGLE_API_KEY);
        this.ctx = ctx;
    }

    @Override
    protected List<GooglePlacesResponse> doInBackground(GooglePlacesRequest... params) {
        lg.trace("Doing processing in background");

        final List<GooglePlacesResponse> result = new ArrayList<>();

        for (final GooglePlacesRequest param : params) {
            lg.debug("Excuting param {}", param);
            lg.debug("Fetching page with token {}", param.getPageToken());
            Location location = param.getLocation();

            Response<GooglePlaceResponse> placesResponse = null;
            try {
                placesResponse = placesUtils.fetchNearbyPlaces(
                    location.getLatitude(),
                    location.getLongitude(),
                    param.getSearchRadius(),
                    param.getSearchItemType(),
                    param.getPageToken());

                if (placesResponse != null && placesResponse.code() == HttpStatus.SC_OK) {
                    GooglePlacesResponse response = new GooglePlacesResponse();
                    final GooglePlaceResponse responseBody = placesResponse.body();
                    lg.debug("Google response body: {}", responseBody);
                    response.setPlaces(responseBody.getResults());
                    response.setNextPageToken(responseBody.getNextPageToken());
                    response.setOriginalPageToken(param.getPageToken());
                    response.setStatus(responseBody.getStatus());
                    result.add(response);
                }

            } catch (IOException e) {
                lg.error("I/O error", e);
            }
        }

        lg.debug("Returning result: {}", result);

        return result;
    }

    @Override
    protected void onPostExecute(List<GooglePlacesResponse> googlePlacesResponses) {
        lg.debug("Post execute {}", googlePlacesResponses);

        for (GooglePlacesResponse response : googlePlacesResponses) {
            final String googleStatus = response.getStatus();
            if (!"OK".equals(googleStatus) && !"SUCCESS".equals(googleStatus)) {
                if (!"OVER_QUERY_LIMIT".equals(googleStatus)) {
                    Toast.makeText(ctx, "Google returned status {}", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ctx,
                        "This application has exceeded free google places api daily limit - 150k." +
                            " Sorry for that - I can nothing do more except from buying the premium plan which" +
                            " is nearly zero-probable - this is free app", Toast.LENGTH_LONG).show();
                }
            }
            EventBus.getDefault().post(response);
        }
    }
}

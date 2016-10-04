package pl.tpolgrabia.urbanexplorer.worker;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import org.apache.http.HttpStatus;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.googleutils.callback.PlacesCallback;
import pl.tpolgrabia.googleutils.dto.GooglePlacePhoto;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResponse;
import pl.tpolgrabia.googleutils.dto.GooglePlaceResult;
import pl.tpolgrabia.googleutils.utils.PlacesUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.PlacesAdapter;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesRequest;
import pl.tpolgrabia.urbanexplorer.dto.GooglePlacesResponse;
import pl.tpolgrabia.urbanexplorer.fragments.PlacesFragment;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by tpolgrabia on 03.10.16.
 */
public class GooglePlacesWorker extends AsyncTask<GooglePlacesRequest, Integer, List<GooglePlacesResponse>> {

    private static final Logger lg = LoggerFactory.getLogger(GooglePlacesWorker.class);

    private final Context ctx;
    private final PlacesUtils placesUtils;
    private final PlacesFragment placesFragment;

    public GooglePlacesWorker(Context ctx, PlacesFragment placesFragment) {
        this.ctx = ctx;
        this.placesFragment = placesFragment;
        this.placesUtils = new PlacesUtils(ctx, AppConstants.GOOGLE_API_KEY);
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
                    response.setPlaces(placesResponse.body().getResults());
                    response.setNextPageToken(placesResponse.body().getNextPageToken());
                    response.setOriginalPageToken(param.getPageToken());
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
//        final View view = placesFragment.getView();
//        if (view == null) {
//            lg.error("Fragment not attached to the view");
//            return;
//        }

        for (GooglePlacesResponse response : googlePlacesResponses) {
            // placesFragment.setNextPageToken(response.getNextPageToken());
            // ListView places = (ListView) view.findViewById(R.id.google_places);
            EventBus.getDefault().post(response);
            // places.setAdapter(new PlacesAdapter(ctx, response));
        }
    }
}

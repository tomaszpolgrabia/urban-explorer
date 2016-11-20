package pl.tpolgrabia.urbanexplorer.worker;

import android.os.AsyncTask;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioResponse;
import pl.tpolgrabia.panoramiobindings.utils.PanoramioUtils;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioRequest;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 19.11.16.
 */
public class PanoramioWorker extends AsyncTask<PanoramioRequest, Integer, List<PanoramioResponse>> {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioWorker.class);
    private final HomeFragment homeFragment;

    public PanoramioWorker(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    protected List<PanoramioResponse> doInBackground(PanoramioRequest... params) {
        List<PanoramioResponse> res = new ArrayList<>();

        for (PanoramioRequest param : params) {
            try {
                Response<PanoramioResponse> single = PanoramioUtils.fetchPanoramioImagesSync(
                    param.getLatitude(),
                    param.getLongitude(),
                    param.getRadiusX(),
                    param.getRadiusY(),
                    param.getOffset(),
                    param.getCount());

                if (single.code() != 200) {
                    final ResponseBody errorBody = single.errorBody();
                    lg.error("Fetching paranomio images failed with code: {}, message: {}, error: {}",
                        single.code(),
                        single.message(),
                        errorBody != null ? errorBody.toString() : "(null)");
                    continue;
                }

                res.add(single.body());
            } catch (IOException e) {
                lg.error("I/O error", e);
            }
        }

        return res;
    }

    @Override
    protected void onPostExecute(List<PanoramioResponse> panoramioResponses) {
        super.onPostExecute(panoramioResponses);
        lg.warn("NOT IMPLEMENTED");
        // TODO implement this


    }
}

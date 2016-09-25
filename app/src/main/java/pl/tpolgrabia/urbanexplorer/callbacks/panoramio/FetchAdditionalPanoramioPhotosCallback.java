package pl.tpolgrabia.urbanexplorer.callbacks.panoramio;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.callback.PanoramioResponseCallback;
import pl.tpolgrabia.panoramiobindings.callback.PanoramioResponseStatus;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioAdapter;

import java.util.List;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class FetchAdditionalPanoramioPhotosCallback implements PanoramioResponseCallback {
    private static final Logger lg = LoggerFactory.getLogger(FetchAdditionalPanoramioPhotosCallback.class);
    private HomeFragment homeFragment;
    private final FragmentActivity activity;

    public FetchAdditionalPanoramioPhotosCallback(HomeFragment homeFragment, FragmentActivity activity) {
        this.homeFragment = homeFragment;
        this.activity = activity;
    }

    @Override
    public void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount) {
        try {
            lg.debug("Fetched with status: {}, images: {}, count: {}", status, images, imagesCount);
            if (status != PanoramioResponseStatus.SUCCESS) {
                return;
            }

            final View view = homeFragment.getView();
            if (view == null) {
                lg.debug("View still not initialized");
                return;
            }

            ListView locations = (ListView) view.findViewById(R.id.locations);
            if (locations == null) {
                lg.trace("Empty locations");
                return;
            }
            ArrayAdapter<PanoramioImageInfo> adapter = (ArrayAdapter<PanoramioImageInfo>) locations.getAdapter();
            homeFragment.addPhotos(images);
            lg.debug("Additional Photos size {} loaded. There are {} photos", images.size(), homeFragment.getPhotosCount());

            if (homeFragment.getPhotosCount() <= 0) {
                Toast.makeText(homeFragment.getActivity(), "No results", Toast.LENGTH_SHORT).show();
            }

            homeFragment.setNoMorePhotos(images.isEmpty());
            if (adapter == null) {
                locations.setAdapter(new PanoramioAdapter(activity, R.id.list_item, images));
            } else {
                adapter.addAll(images);
            }

            // TODO we can think about removing first items also and last if the number
            // TODO of items exceeds the limit (to save the memory)

            lg.debug("Finished Fetching additional photos count: {}", homeFragment.getPhotosCount());

        } finally {
            lg.trace("Releasing fetching lock");
            homeFragment.getLoading().release();
        }

    }
}

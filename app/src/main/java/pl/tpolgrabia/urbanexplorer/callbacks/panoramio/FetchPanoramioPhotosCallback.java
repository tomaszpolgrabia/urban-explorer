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
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioAdapter;

import java.util.List;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class FetchPanoramioPhotosCallback implements PanoramioResponseCallback {
    private static final Logger lg = LoggerFactory.getLogger(FetchPanoramioPhotosCallback.class);

    private HomeFragment homeFragment;
    private final FragmentActivity activity;


    public FetchPanoramioPhotosCallback(HomeFragment homeFragment, FragmentActivity activity) {
        this.homeFragment = homeFragment;
        this.activity = activity;
    }

    @Override
    public void callback(PanoramioResponseStatus status, List<PanoramioImageInfo> images, Long imagesCount) {

        lg.trace("Panoramio response status {}, images: {}, imagesCount: {}",
            status,
            images,
            imagesCount);

        ArrayAdapter<PanoramioImageInfo> adapter = new PanoramioAdapter(activity,
            R.layout.location_item,
            images);

        if (images.isEmpty()) {
            Toast.makeText(homeFragment.getActivity(), "No results", Toast.LENGTH_SHORT).show();
        }
        final View view = homeFragment.getView();
        if (view == null) {
            lg.trace("Fragment's view is not initialized");
            return;
        }

        ListView locations = (ListView) view.findViewById(R.id.locations);
        locations.setAdapter(adapter);
        MainActivity mainActivity = (MainActivity) homeFragment.getActivity();
        if (mainActivity == null) {
            return;
        }

        mainActivity.setPhotos(images);

        lg.trace("Photos size: {}", homeFragment.getPhotosCount());
        mainActivity.hideProgress();
    }
}

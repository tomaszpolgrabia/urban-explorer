package pl.tpolgrabia.urbanexplorer.callbacks.panoramio;

import android.location.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorerutils.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

import java.util.ArrayList;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioLocationCallback implements StandardLocationListenerCallback {
    private static final Logger lg = LoggerFactory.getLogger(PanoramioLocationCallback.class);
    private HomeFragment homeFragment;

    public PanoramioLocationCallback(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public void callback(Location location) {
        homeFragment.setNoMorePhotos(false);
        homeFragment.setPhotos(new ArrayList<PanoramioImageInfo>());
    }
}

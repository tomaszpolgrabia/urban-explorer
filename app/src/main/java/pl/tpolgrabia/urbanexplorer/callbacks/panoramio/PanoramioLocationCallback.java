package pl.tpolgrabia.urbanexplorer.callbacks.panoramio;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.events.LocationChangedEvent;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;

import java.util.ArrayList;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioLocationCallback {
    private static final Logger lg = LoggerFactory.getLogger(PanoramioLocationCallback.class);
    private HomeFragment homeFragment;

    public PanoramioLocationCallback(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Subscribe
    public void handleLocationChanged(LocationChangedEvent event) {
        homeFragment.setNoMorePhotos(false);
        homeFragment.setPhotos(new ArrayList<PanoramioImageInfo>());
        EventBus.getDefault().post(new RefreshEvent(this));
    }
}

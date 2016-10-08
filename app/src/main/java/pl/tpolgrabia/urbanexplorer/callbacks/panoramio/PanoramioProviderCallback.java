package pl.tpolgrabia.urbanexplorer.callbacks.panoramio;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.events.ProviderStatusChangedEvent;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioProviderCallback {
    private static final Logger lg = LoggerFactory.getLogger(PanoramioProviderCallback.class);
    private HomeFragment homeFragment;

    public PanoramioProviderCallback(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Subscribe
    public void handleProviderStatusChanged(ProviderStatusChangedEvent event) {
        if (event.isEnabled()) {
            lg.trace("Handling provider enabling - refreshing panoramio listing");
            EventBus.getDefault().post(new RefreshEvent(this));
        }
    }
}

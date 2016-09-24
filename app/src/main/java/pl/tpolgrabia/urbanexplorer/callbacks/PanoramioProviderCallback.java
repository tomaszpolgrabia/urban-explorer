package pl.tpolgrabia.urbanexplorer.callbacks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioProviderCallback implements ProviderStatusCallback {
    private static final Logger lg = LoggerFactory.getLogger(PanoramioProviderCallback.class);
    private HomeFragment homeFragment;

    public PanoramioProviderCallback(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public void callback(String provider, boolean enabled) {
        if (enabled) {
            lg.trace("Handling provider enabling - refreshing panoramio listing");
            homeFragment.refresh();
        }
    }
}

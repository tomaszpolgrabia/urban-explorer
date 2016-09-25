package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.callback.ProviderStatusCallback;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiLocationProviderStatusCallback implements ProviderStatusCallback {
    private static final Logger lg = LoggerFactory.getLogger(WikiLocationProviderStatusCallback.class);
    private WikiLocationsFragment wikiLocationsFragment;

    public WikiLocationProviderStatusCallback(WikiLocationsFragment wikiLocationsFragment) {
        this.wikiLocationsFragment = wikiLocationsFragment;
    }

    @Override
    public void callback(String provider, boolean enabled) {
        if (enabled) {
            lg.trace("Handling provider enabling - refreshing wiki listing");
            wikiLocationsFragment.fetchWikiLocations();
        }
    }
}

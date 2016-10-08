package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.events.ProviderStatusChangedEvent;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiLocationProviderStatusCallback {
    private static final Logger lg = LoggerFactory.getLogger(WikiLocationProviderStatusCallback.class);
    private WikiLocationsFragment wikiLocationsFragment;

    public WikiLocationProviderStatusCallback(WikiLocationsFragment wikiLocationsFragment) {
        this.wikiLocationsFragment = wikiLocationsFragment;
    }

    @Subscribe
    public void handleProviderStatusChanged(ProviderStatusChangedEvent event) {
        if (event.isEnabled()) {
            lg.trace("Handling provider enabling - refreshing wiki listing");
            wikiLocationsFragment.fetchWikiLocations();
        }
    }
}

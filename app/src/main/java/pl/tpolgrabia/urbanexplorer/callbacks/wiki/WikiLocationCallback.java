package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

import org.greenrobot.eventbus.Subscribe;
import pl.tpolgrabia.urbanexplorer.events.LocationChangedEvent;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

import java.util.ArrayList;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiLocationCallback {
    private WikiLocationsFragment wikiLocationsFragment;

    public WikiLocationCallback(WikiLocationsFragment wikiLocationsFragment) {
        this.wikiLocationsFragment = wikiLocationsFragment;
    }

    @Subscribe
    public void handleLocationChanged(LocationChangedEvent event) {
        wikiLocationsFragment.setLastFetchSize(-1);
        wikiLocationsFragment.setAppObjects(new ArrayList<WikiAppObject>());
        wikiLocationsFragment.updateLocationInfo();
        wikiLocationsFragment.fetchWikiLocations();
    }
}

package pl.tpolgrabia.urbanexplorer.callbacks;

import android.location.Location;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

import java.util.ArrayList;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiLocationCallback implements StandardLocationListenerCallback {
    private WikiLocationsFragment wikiLocationsFragment;

    public WikiLocationCallback(WikiLocationsFragment wikiLocationsFragment) {
        this.wikiLocationsFragment = wikiLocationsFragment;
    }

    @Override
    public void callback(Location location) {
        wikiLocationsFragment.setLastFetchSize(-1);
        wikiLocationsFragment.setAppObjects(new ArrayList<WikiAppObject>());
        wikiLocationsFragment.updateLocationInfo();
        wikiLocationsFragment.fetchWikiLocations();
    }
}

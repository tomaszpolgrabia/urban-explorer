package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiLocationGeoCoderCallback {
    // implements LocationGeoCoderCallback {
    private static final Logger lg = LoggerFactory.getLogger(WikiLocationGeoCoderCallback.class);
    private WikiLocationsFragment wikiLocationsFragment;

    public WikiLocationGeoCoderCallback(WikiLocationsFragment wikiLocationsFragment) {
        this.wikiLocationsFragment = wikiLocationsFragment;
    }

    // @Override
    public void callback(int code, String message, String googleStatus, String geocodedLocation) {
        lg.debug("Geocoded result code {}, message {}, status: {}, value {}",
            code, message, googleStatus, geocodedLocation);

        wikiLocationsFragment.setCurrentGeocodedLocation(geocodedLocation);
        wikiLocationsFragment.updateLocationInfo();
    }
}

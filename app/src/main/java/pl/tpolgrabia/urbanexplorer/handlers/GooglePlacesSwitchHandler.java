package pl.tpolgrabia.urbanexplorer.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.fragments.PlacesFragment;

/**
 * Created by tpolgrabia on 28.09.16.
 */
public class GooglePlacesSwitchHandler implements Runnable {
    private static final Logger lg = LoggerFactory.getLogger(GooglePlacesSwitchHandler.class);
    private final MainActivity mainActivity;

    public GooglePlacesSwitchHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        lg.debug("Switching to google places fragment");
        mainActivity.switchFragment(new PlacesFragment(), PlacesFragment.TAG);
    }
}

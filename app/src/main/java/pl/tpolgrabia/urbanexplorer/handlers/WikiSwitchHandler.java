package pl.tpolgrabia.urbanexplorer.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class WikiSwitchHandler implements Runnable {
    private final MainActivity mainActivity;
    private static final Logger lg = LoggerFactory.getLogger(WikiSwitchHandler.class);

    public WikiSwitchHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        lg.debug("Switching to wiki fragment");
        mainActivity.switchFragment(new WikiLocationsFragment(), WikiLocationsFragment.TAG);
    }
}

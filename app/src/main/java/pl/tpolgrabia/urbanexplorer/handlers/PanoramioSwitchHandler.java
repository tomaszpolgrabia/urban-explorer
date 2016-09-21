package pl.tpolgrabia.urbanexplorer.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioSwitchHandler implements Runnable {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioSwitchHandler.class);
    private final MainActivity mainActivity;

    public PanoramioSwitchHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        lg.debug("Switching to home fragment");
        mainActivity.switchFragment(new HomeFragment(), HomeFragment.TAG);
    }
}

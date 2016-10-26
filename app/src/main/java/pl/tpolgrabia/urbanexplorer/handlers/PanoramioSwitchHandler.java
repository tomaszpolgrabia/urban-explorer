package pl.tpolgrabia.urbanexplorer.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.utils.PanoramioUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioShowerFragment;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

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
        double diagInches = PanoramioUtils.calcDiag(mainActivity);
        if (diagInches >= AppConstants.PANORAMIO_SHOWER_SIDEBAR_THRESHOLD) {
            mainActivity.addFragment(MainActivity.createShowerFragment(null), PanoramioShowerFragment.TAG);
            // mainActivity.addFragment(new WikiLocationsFragment(), WikiLocationsFragment.TAG);
        }
    }
}

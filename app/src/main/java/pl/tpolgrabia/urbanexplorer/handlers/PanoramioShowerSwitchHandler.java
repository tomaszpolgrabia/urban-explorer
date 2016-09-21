package pl.tpolgrabia.urbanexplorer.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.MainActivity;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioShowerSwitchHandler implements Runnable {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioShowerSwitchHandler.class);
    private final MainActivity mainActivity;

    public PanoramioShowerSwitchHandler(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        lg.debug("Switching to panoramio shower");
        mainActivity.switchToPhoto(mainActivity.getPhotoInfo());
    }
}

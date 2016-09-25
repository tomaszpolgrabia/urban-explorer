package pl.tpolgrabia.urbanexplorerutils.utils;

import android.support.v4.app.Fragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class DebugUtils {
    private static final Logger lg = LoggerFactory.getLogger(DebugUtils.class);
    public static void dumpFragments(List<Fragment> fragments) {
        for (Fragment frag : fragments) {
            if (frag == null) {
                lg.trace("Fragment is null");
            } else {
                lg.trace("Fragment TAG {}", frag.getTag());
            }
        }
    }
}

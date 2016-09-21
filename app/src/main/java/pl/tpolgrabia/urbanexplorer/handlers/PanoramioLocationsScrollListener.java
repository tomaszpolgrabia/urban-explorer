package pl.tpolgrabia.urbanexplorer.handlers;

import android.view.View;
import android.widget.AbsListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioLocationsScrollListener implements AbsListView.OnScrollListener {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioLocationsScrollListener.class);
    private HomeFragment homeFragment;

    public PanoramioLocationsScrollListener(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    @Override
    public void onScroll(AbsListView view,
                         int firstVisibleItem,
                         int visibleItemCount,
                         int totalItemCount) {

        try {

            if (firstVisibleItem <= 0) {
                // scrolled to the top
                lg.trace("Scrolled to the top");
            }

            if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                lg.trace("Scrolled to the bottom");
                // scrolled to the bottom
                final View fragView = homeFragment.getView();
                if (fragView == null) {
                    lg.trace("Frag still not initialized");
                    return;
                }

                homeFragment.fetchAdditionalPhotos();

            }

        } catch (InterruptedException e) {
            lg.error("Aquiring lock interrupted exception", e);
        }

    }
}

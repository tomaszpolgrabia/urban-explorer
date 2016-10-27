package pl.tpolgrabia.urbanexplorer.handlers;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.utils.PanoramioUtils;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.events.PhotoInfoUpdateEvent;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioAdapter;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioItemLongClickHandler implements AdapterView.OnItemLongClickListener {

    private static final Logger lg = LoggerFactory.getLogger(PanoramioItemLongClickHandler.class);

    private HomeFragment homeFragment;
    private final ListView finalLocations;

    public PanoramioItemLongClickHandler(HomeFragment homeFragment, ListView finalLocations) {
        this.homeFragment = homeFragment;
        this.finalLocations = finalLocations;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long rowId) {
        PanoramioAdapter panAdapter = (PanoramioAdapter) finalLocations.getAdapter();
        PanoramioImageInfo photoInfo = panAdapter.getItem(pos);
        MainActivity activity = (MainActivity) homeFragment.getActivity();
        if (PanoramioSwitchHandler.enoughLargeAndHorizontal(activity)) {
            lg.debug("Sending panoramio image event");
            EventBus.getDefault().post(new PhotoInfoUpdateEvent(this, photoInfo));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1);
            View layout = homeFragment.getView();
            layout.setLayoutParams(params);
        } else {
            activity.switchToPhoto(photoInfo);
        }
        return false;
    }
}

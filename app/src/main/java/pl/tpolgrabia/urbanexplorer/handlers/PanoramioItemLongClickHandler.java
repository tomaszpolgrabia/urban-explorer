package pl.tpolgrabia.urbanexplorer.handlers;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioAdapter;

/**
 * Created by tpolgrabia on 21.09.16.
 */
public class PanoramioItemLongClickHandler implements AdapterView.OnItemLongClickListener {
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
        activity.switchToPhoto(photoInfo);
        return false;
    }
}

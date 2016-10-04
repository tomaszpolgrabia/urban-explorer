package pl.tpolgrabia.urbanexplorer.handlers;

import android.widget.AbsListView;
import pl.tpolgrabia.urbanexplorer.fragments.PlacesFragment;


/**
 * Created by tpolgrabia on 04.10.16.
 */
public class GooglePlacesScrollListener implements AbsListView.OnScrollListener {
    private final PlacesFragment placesFragment;

    public GooglePlacesScrollListener(PlacesFragment placesFragment) {
        this.placesFragment = placesFragment;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount >= totalItemCount) {
            // scrolled to the bottom, loading new page
            placesFragment.loadNextPage();
        }
    }
}
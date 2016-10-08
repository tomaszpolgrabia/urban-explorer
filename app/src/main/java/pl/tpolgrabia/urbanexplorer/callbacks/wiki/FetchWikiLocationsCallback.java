package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.wikibinding.utils.WikiUtils;

import java.util.List;

/**
 * Created by tpolgrabia on 14.09.16.
 */
public class FetchWikiLocationsCallback implements AdapterView.OnItemLongClickListener {
    private final WikiUtils wikiUtils;
    private WikiLocationsFragment wikiLocationsFragment;

    public FetchWikiLocationsCallback(WikiLocationsFragment wikiLocationsFragment) {
        this.wikiLocationsFragment = wikiLocationsFragment;
        final FragmentActivity activity = wikiLocationsFragment.getActivity();
        this.wikiUtils = new WikiUtils(activity, wikiLocationsFragment.getWikiLocale(activity));
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final WikiAppObject item = wikiLocationsFragment.getAppObjects().get(position);
        wikiUtils.fetchSingleWikiInfoItemAndRunWikiPage(
            wikiLocationsFragment.getActivity(),
            item.getPageId(),
            new WikiInfoRunBrowserCallback(wikiLocationsFragment, item));
        return false;
    }
}

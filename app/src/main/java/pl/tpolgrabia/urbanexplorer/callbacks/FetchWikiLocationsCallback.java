package pl.tpolgrabia.urbanexplorer.callbacks;

import android.view.View;
import android.widget.AdapterView;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorer.utils.WikiUtils;

import java.util.List;

/**
 * Created by tpolgrabia on 14.09.16.
 */
public class FetchWikiLocationsCallback implements AdapterView.OnItemLongClickListener {
    private WikiLocationsFragment wikiLocationsFragment;
    private final List<WikiAppObject> appObjects;

    public FetchWikiLocationsCallback(WikiLocationsFragment wikiLocationsFragment, List<WikiAppObject> appObjects) {
        this.wikiLocationsFragment = wikiLocationsFragment;
        this.appObjects = appObjects;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final WikiAppObject item = appObjects.get(position);
        WikiUtils.fetchSingleWikiInfoItemAndRunWikiPage(
            wikiLocationsFragment.getActivity(),
            item.getPageId(),
            new WikiInfoRunBrowserCallback(wikiLocationsFragment, item));
        return false;
    }
}

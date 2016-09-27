package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

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
    private final List<WikiAppObject> appObjects;

    public FetchWikiLocationsCallback(WikiLocationsFragment wikiLocationsFragment, List<WikiAppObject> appObjects) {
        this.wikiLocationsFragment = wikiLocationsFragment;
        this.wikiUtils = new WikiUtils(wikiLocationsFragment.getActivity(), AppConstants.DEF_WIKI_COUNTRY_CODE);
        this.appObjects = appObjects;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final WikiAppObject item = appObjects.get(position);
        wikiUtils.fetchSingleWikiInfoItemAndRunWikiPage(
            wikiLocationsFragment.getActivity(),
            item.getPageId(),
            new WikiInfoRunBrowserCallback(wikiLocationsFragment, item));
        return false;
    }
}

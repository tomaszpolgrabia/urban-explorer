package pl.tpolgrabia.urbanexplorer.callbacks.wiki;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;
import pl.tpolgrabia.wikibinding.callback.WikiStatus;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.wikibinding.callback.WikiAppResponseCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiFetchAppDataCallback implements WikiAppResponseCallback {

    private WikiLocationsFragment wikiLocationsFragment;
    private final FragmentActivity activity;

    public WikiFetchAppDataCallback(WikiLocationsFragment wikiLocationsFragment, FragmentActivity activity) {
        this.wikiLocationsFragment = wikiLocationsFragment;
        this.activity = activity;
    }

    @Override
    public void callback(WikiStatus status, final List<WikiAppObject> objects) {
        ArrayList<WikiAppObject> nobjects = new ArrayList<WikiAppObject>(objects);
        wikiLocationsFragment.setAppObjects(nobjects);

        // handling here wiki locations
        if (status != WikiStatus.SUCCESS) {
            Toast.makeText(activity, "Sorry, currently we have problem with interfacing wiki" +
                ": " + status + ". Try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO on success

        final View view = wikiLocationsFragment.getView();

        if (view == null) {
            return;
        }

        ListView locations = (ListView) view.findViewById(R.id.wiki_places);
        locations.setOnItemLongClickListener(new FetchWikiLocationsCallback(wikiLocationsFragment, nobjects));
        locations.setAdapter(new WikiLocationsAdapter(activity, objects));
        if (objects.isEmpty()) {
            Toast.makeText(wikiLocationsFragment.getActivity(), "No results", Toast.LENGTH_SHORT).show();
        }

        MainActivity mainActivity = (MainActivity) wikiLocationsFragment.getActivity();
        if (mainActivity == null) {
            return;
        }

        EventBus.getDefault().post(new DataLoadingFinishEvent(this));
    }

}

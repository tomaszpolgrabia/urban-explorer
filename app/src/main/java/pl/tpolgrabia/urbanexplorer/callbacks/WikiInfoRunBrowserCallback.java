package pl.tpolgrabia.urbanexplorer.callbacks;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONException;
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

/**
 * Created by tpolgrabia on 14.09.16.
 */
public class WikiInfoRunBrowserCallback extends AjaxCallback<JSONObject> {
    private static final String CLASS_TAG = WikiInfoRunBrowserCallback.class.getSimpleName();
    private WikiLocationsFragment wikiLocationsFragment;
    private final WikiAppObject item;

    public WikiInfoRunBrowserCallback(WikiLocationsFragment wikiLocationsFragment, WikiAppObject item) {
        this.wikiLocationsFragment = wikiLocationsFragment;
        this.item = item;
    }

    @Override
    public void callback(String url, JSONObject object, AjaxStatus status) {
        if (status.getCode() != 200) {
            Toast.makeText(wikiLocationsFragment.getActivity(),
                "Sorry, network error code: " + status.getCode(),
                Toast.LENGTH_LONG)
                .show();
            return;
        }


        try {
            String wikiUrl = object.getJSONObject("query")
                .getJSONObject("pages")
                .getJSONObject(item.getPageId().toString())
                .getString("fullurl");
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(wikiUrl));
            wikiLocationsFragment.startActivity(intent);
        } catch (JSONException e) {
            Log.e(CLASS_TAG, "Error", e);
        }
    }
}

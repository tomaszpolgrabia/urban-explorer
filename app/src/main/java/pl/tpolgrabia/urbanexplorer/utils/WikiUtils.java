package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.view.View;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiResponseCallback;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiUtils {
    private static final String WIKI_FORMAT = "json";

    public static void fetchNearPlaces(Context ctx, final double latitude, final double longitude, final WikiResponseCallback callback) {
        AQuery aq = new AQuery(ctx);
        aq.ajax("TODO", JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                // TODO handle response
                String qurl = "https://en.wikipedia.org/w/api.php?" +
                    "action=query" +
                    "&prop=coordinates%7Cpageimages%7Cpageterms" +
                    "&colimit=50" +
                    "&piprop=thumbnail" +
                    "&pithumbsize=144" +
                    "&pilimit=50" +
                    "&wbptterms=description" +
                    "&generator=geosearch" +
                    "&ggscoord=" + latitude + "%7C" + longitude +
                    "&ggsradius=10000" +
                    "&ggslimit=50" +
                    "&format" + WIKI_FORMAT;
                callback.callback(null, null);
            }
        });
    }
}

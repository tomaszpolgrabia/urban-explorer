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
    public static void fetchNearPlaces(Context ctx, double latitude, double longitude, final WikiResponseCallback callback) {
        AQuery aq = new AQuery(ctx);
        aq.ajax("TODO", JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                // TODO handle response
                callback.callback(null, null);
            }
        });
    }
}

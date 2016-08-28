package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiResponseCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.WikiLocation;
import pl.tpolgrabia.urbanexplorer.dto.WikiPage;
import pl.tpolgrabia.urbanexplorer.dto.WikiResponse;
import pl.tpolgrabia.urbanexplorer.dto.WikiThumbnail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiUtils {
    private static final String CLASS_TAG = WikiUtils.class.getSimpleName();
    private static final String WIKI_FORMAT = "json";

    public static void fetchNearPlaces(Context ctx,
                                       final double latitude,
                                       final double longitude,
                                       final long resultsLimit,
                                       final WikiResponseCallback callback) {
        final AQuery aq = new AQuery(ctx);
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
                    "&ggslimit=" + resultsLimit +
                    "&format" + WIKI_FORMAT;
                aq.ajax(qurl, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        if (status.getCode() == 200) {
                            callback.callback(WikiStatus.SUCCESS, null);
                        } else {
                            try {
                                callback.callback(WikiStatus.NETWORK_ERROR, fetchWikiResponse(object));
                            } catch (JSONException e) {
                                Log.e(CLASS_TAG, "General error", e);
                                callback.callback(WikiStatus.GENERAL_ERROR, null);
                            }
                        }
                    }
                });

            }
        });
    }

    public static WikiResponse fetchWikiResponse(JSONObject object) throws JSONException {
        WikiResponse wikiResponse = new WikiResponse();
        wikiResponse.setBatchComplete(Boolean.valueOf(object.getString("batchcomplete")));
        wikiResponse.setPages(fetchPages(object.getJSONObject("query").getJSONObject("pages")));
        return wikiResponse;
    }

    public static List<WikiPage> fetchPages(JSONObject jpages) throws JSONException {
        List<WikiPage> pages = new ArrayList<>();
        Iterator<String> pagesIds = jpages.keys();
        while (pagesIds.hasNext()) {
            String pageId = pagesIds.next();
            pages.add(fetchPage(jpages.getJSONObject(pageId)));
        }
        return pages;
    }

    public static WikiPage fetchPage(JSONObject jpage) throws JSONException {
        WikiPage wikiPage = new WikiPage();
        wikiPage.setCoordinates(fetchCoordinates(jpage.getJSONArray("coordinates")));
        wikiPage.setIndex(jpage.getLong("index"));
        wikiPage.setNs(jpage.getLong("ns"));
        wikiPage.setPageId(jpage.getLong("pageId"));
        wikiPage.setThumbnail(fetchThumbnail(jpage.getJSONObject("thumbnail")));
        wikiPage.setTitle(jpage.getString("title"));
        return null;
    }

    public static WikiThumbnail fetchThumbnail(JSONObject jthumbnail) throws JSONException {
        WikiThumbnail wikiThumbnail = new WikiThumbnail();
        wikiThumbnail.setWidth(jthumbnail.getLong("width"));
        wikiThumbnail.setHeight(jthumbnail.getLong("height"));
        wikiThumbnail.setSource(jthumbnail.getString("source"));
        return wikiThumbnail;
    }

    public static List<WikiLocation> fetchCoordinates(JSONArray jcoordinates) throws JSONException {
        List<WikiLocation> wikiLocations = new ArrayList<WikiLocation>();

        int n = jcoordinates.length();
        for (int i = 0; i < n; i++) {
            wikiLocations.add(fetchCoordinate(jcoordinates.getJSONObject(i)))
        }

        return wikiLocations;
    }

    public static WikiLocation fetchCoordinate(JSONObject jlocation) throws JSONException {
        WikiLocation wikiLocation = new WikiLocation();
        wikiLocation.setLatitude(jlocation.getDouble("lat"));
        wikiLocation.setLongitude(jlocation.getDouble("lon"));
        wikiLocation.setPrimary(jlocation.getString("primary"));
        wikiLocation.setGlobe(jlocation.getString("globe"));
        return wikiLocation;
    }
}

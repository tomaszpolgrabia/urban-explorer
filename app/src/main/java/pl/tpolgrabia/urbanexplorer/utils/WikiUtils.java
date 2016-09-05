package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.util.Log;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiResponseCallback;
import pl.tpolgrabia.urbanexplorer.callbacks.WikiStatus;
import pl.tpolgrabia.urbanexplorer.dto.wiki.generator.WikiLocation;
import pl.tpolgrabia.urbanexplorer.dto.wiki.generator.WikiPage;
import pl.tpolgrabia.urbanexplorer.dto.wiki.generator.WikiResponse;
import pl.tpolgrabia.urbanexplorer.dto.wiki.generator.WikiThumbnail;
import pl.tpolgrabia.urbanexplorer.dto.wiki.geosearch.WikiGeoObject;
import pl.tpolgrabia.urbanexplorer.dto.wiki.geosearch.WikiGeoResponse;
import pl.tpolgrabia.urbanexplorer.dto.wiki.geosearch.WikiGeoResponseCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiUtils {
    private static final String CLASS_TAG = WikiUtils.class.getSimpleName();
    private static final String WIKI_FORMAT = "json";
    private static final long WIKI_MIN_RADIUS = 10L;
    private static final Long WIKI_MAX_RESULTS_LIMIT = 500L;
    private static final Long WIKI_MIN_RESULTS = 10L;

    public static void fetchNearPlaces(Context ctx,
                                       final double latitude,
                                       final double longitude,
                                       final Long resultsLimit,
                                       final Long radiusLimit,
                                       final WikiResponseCallback callback) {
        final AQuery aq = new AQuery(ctx);
        aq.ajax("TODO", JSONObject.class, new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                // TODO handle response
                final String qurl = "https://en.wikipedia.org/w/api.php?" +
                    "action=query" +
                    "&prop=coordinates%7Cpageimages%7Cpageterms" +
                    "&colimit=50" +
                    "&piprop=thumbnail" +
                    "&pithumbsize=144" +
                    "&pilimit=50" +
                    "&wbptterms=description" +
                    "&generator=geosearch" +
                    "&ggscoord=" + latitude + "%7C" + longitude +
                    "&ggsradius=" + Math.max(WIKI_MIN_RADIUS, ifNullSet(radiusLimit, 10000L)) +
                    "&ggslimit=" + Math.min(WIKI_MIN_RESULTS, Math.max(WIKI_MAX_RESULTS_LIMIT,
                        checkIfNullLong(resultsLimit))) +
                    "&format=" + WIKI_FORMAT;
                aq.ajax(qurl, JSONObject.class, new AjaxCallback<JSONObject>() {
                    @Override
                    public void callback(String url, JSONObject object, AjaxStatus status) {
                        if (status.getCode() == 200) {
                            try {
                                callback.callback(WikiStatus.SUCCESS, fetchWikiResponse(object));
                            } catch (JSONException e) {
                                Log.e(CLASS_TAG, "JSon error: " + object.toString(), e);
                            }
                        } else {
                            callback.callback(WikiStatus.NETWORK_ERROR, null);
                        }
                    }
                });

            }
        });
    }

    private static <T> T ifNullSet(T val, T def) {
        return val == null ? def : val;
    }

    private static Long checkIfNullLong(Long lval) {
        return lval != null ? lval : 0L;
    }

    public static WikiResponse fetchWikiResponse(JSONObject object) throws JSONException {
        if (object == null) {
            return null;
        }

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
        wikiPage.setCoordinates(fetchCoordinates(jpage.optJSONArray("coordinates")));
        wikiPage.setIndex(jpage.optLong("index"));
        wikiPage.setNs(jpage.optLong("ns"));
        wikiPage.setPageId(jpage.optLong("pageid"));
        wikiPage.setThumbnail(fetchThumbnail(jpage.optJSONObject("thumbnail")));
        wikiPage.setTitle(jpage.optString("title"));
        return wikiPage;
    }

    public static WikiThumbnail fetchThumbnail(JSONObject jthumbnail) throws JSONException {
        if (jthumbnail == null) {
            return null;
        }
        WikiThumbnail wikiThumbnail = new WikiThumbnail();
        wikiThumbnail.setWidth(jthumbnail.getLong("width"));
        wikiThumbnail.setHeight(jthumbnail.getLong("height"));
        wikiThumbnail.setSource(jthumbnail.getString("source"));
        return wikiThumbnail;
    }

    public static List<WikiLocation> fetchCoordinates(JSONArray jcoordinates) throws JSONException {
        if (jcoordinates == null) {
            return null;
        }

        List<WikiLocation> wikiLocations = new ArrayList<WikiLocation>();

        int n = jcoordinates.length();
        for (int i = 0; i < n; i++) {
            wikiLocations.add(fetchCoordinate(jcoordinates.getJSONObject(i)));
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

    public static void fetchGeoSearchWikiMetadata(Context ctx,
                                                  Double latitude,
                                                  Double longitude,
                                                  Double radius,
                                                  Long limit,
                                                  final WikiGeoResponseCallback callback) {
        AQuery aq = new AQuery(ctx);
        aq.ajax("https://en.wikipedia.org/w/api.php" +
            "?action=query" +
            "&list=geosearch" +
            "&gscoord=" + latitude + "%7C" + longitude +
            "gsradius=" + radius +
            "&gslimit=" + limit, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (status.getCode() == 200) {
                    try {
                        callback.callback(WikiStatus.SUCCESS, fetchWikiGeoResponse(object));
                    } catch (Throwable t) {
                        callback.callback(WikiStatus.GENERAL_ERROR, null);
                    }
                } else {
                    callback.callback(WikiStatus.NETWORK_ERROR, null);
                }
                super.callback(url, object, status);
            }
        });

    }

    public static WikiGeoResponse fetchWikiGeoResponse(JSONObject object) {
        WikiGeoResponse response = new WikiGeoResponse();
        response.setBatchComplete(object.optBoolean("batch_complete"));
        response.setQuery(fetchQueriesData(object.optJSONObject("query")));
        return response;
    }

    public static List<WikiGeoObject> fetchQueriesData(JSONObject object) {
        String val;
        Iterator<String> it = object.keys();
        List<WikiGeoObject> geoObjects = new ArrayList<>();
        while (it.hasNext()) {
            val = it.next();
            JSONObject geoPage = object.optJSONObject(val);
            geoObjects.add(fetchWikiGeoObject(geoPage));
        }
        return geoObjects;
    }

    public static WikiGeoObject fetchWikiGeoObject(JSONObject geoPage) {
        WikiGeoObject object = new WikiGeoObject();
        object.setPageId(geoPage.optLong("pageid"));
        object.setNs(geoPage.optLong("ns"));
        object.setTitle(geoPage.optString("title"));
        object.setLatitude(geoPage.optDouble("lat"));
        object.setLongitude(geoPage.optDouble("lon"));
        object.setDistance(geoPage.optDouble("dist"));
        object.setPrimary(geoPage.optString("primary"));
        return object;
    }
}

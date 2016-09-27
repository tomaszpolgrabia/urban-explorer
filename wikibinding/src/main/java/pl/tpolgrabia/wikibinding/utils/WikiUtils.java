package pl.tpolgrabia.wikibinding.utils;

import android.content.Context;
import android.location.Location;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.wikibinding.callback.WikiAppResponseCallback;
import pl.tpolgrabia.wikibinding.callback.WikiResponseCallback;
import pl.tpolgrabia.wikibinding.callback.WikiStatus;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.wikibinding.dto.generator.WikiLocation;
import pl.tpolgrabia.wikibinding.dto.generator.WikiPage;
import pl.tpolgrabia.wikibinding.dto.generator.WikiResponse;
import pl.tpolgrabia.wikibinding.dto.generator.WikiThumbnail;
import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoObject;
import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoResponse;
import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoResponseCallback;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.NetUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.SettingsUtils;

import java.util.*;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class WikiUtils {
    private static final Logger lg = LoggerFactory.getLogger(WikiUtils.class);
    private static final String CLASS_TAG = WikiUtils.class.getSimpleName();
    private static final String WIKI_FORMAT = "json";
    private static final long WIKI_MIN_RADIUS = 10L;
    private static final Long WIKI_MAX_RESULTS_LIMIT = 500L;
    private static final Long WIKI_MIN_RESULTS = 10L;
    private static final Double WIKI_STD_RADIUS = 10000.0;
    private static final Long WIKI_STD_LIMIT = 10L;

    public static void fetchNearPlaces(Context ctx,
                                       final double latitude,
                                       final double longitude,
                                       final Long resultsLimit,
                                       final Long radiusLimit,
                                       final WikiResponseCallback callback) {
        final AQuery aq = NetUtils.createProxyAQueryInstance(ctx);

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
                                lg.error("JSon error: {}", object, e);
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

        lg.debug("Latitude: {}, longitude: {}, radius: {}, limit: {}", latitude, longitude, radius, limit);

        if (radius == null) {
            radius = WIKI_STD_RADIUS;
        }

        if (limit ==  null) {
            limit = WIKI_STD_LIMIT;
        }

        AQuery aq = NetUtils.createProxyAQueryInstance(ctx);
        final String queryUrl = "https://en.wikipedia.org/w/api.php" +
            "?action=query" +
            "&list=geosearch" +
            "&gscoord=" + latitude + "%7C" + longitude +
            "&gsradius=" + String.format("%.2f", radius) +
            "&gslimit=" + limit +
            "&format=json";
        lg.trace("GeoSearch wiki API url: {}", queryUrl);
        aq.ajax(queryUrl, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                lg.trace("Finished waiting for {} with status {}:{} and response: {}",
                    url, status.getCode(), status.getMessage(), object);

                if (status.getCode() == 200) {
                    try {
                        callback.callback(WikiStatus.SUCCESS, fetchWikiGeoResponse(object));
                    } catch (Throwable t) {
                        lg.error("General error during fetching", t);
                        callback.callback(WikiStatus.GENERAL_ERROR, null);
                    }
                } else {
                    lg.error("Couldn't fetch wiki metadata {}, status: {}:{} from url: {}",
                        object, status.getCode(), status.getMessage(), url);
                    callback.callback(WikiStatus.NETWORK_ERROR, null);
                }
                super.callback(url, object, status);
            }
        });

    }

    public static WikiGeoResponse fetchWikiGeoResponse(JSONObject object) {
        WikiGeoResponse response = new WikiGeoResponse();
        response.setBatchComplete(object.optBoolean("batch_complete"));
        response.setQuery(fetchQueriesData(object.optJSONObject("query").optJSONArray("geosearch")));
        return response;
    }

    public static List<WikiGeoObject> fetchQueriesData(JSONArray object) {
        List<WikiGeoObject> geoObjects = new ArrayList<>();
        int n = object.length();
        int idx;
        for (idx = 0; idx < n; idx++)  {
            JSONObject geoPage = object.optJSONObject(idx);
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

    public static void fetchAppData(final Context ctx,
                                    final Double latitude,
                                    final Double longitude,
                                    final Double radius,
                                    final Long limit,
                                    final WikiAppResponseCallback callback) {

        lg.debug("Latitude: {}, longitude: {}, radius: {}, limit: {}",
            latitude, longitude, radius, limit);

        fetchGeoSearchWikiMetadata(ctx, latitude, longitude, radius, limit, new WikiGeoResponseCallback() {
            @Override
            public void callback(WikiStatus status, WikiGeoResponse response) {

                lg.trace("Fetching finished with status: {} and values: {}", status, response);

                if (status != WikiStatus.SUCCESS) {
                    Toast.makeText(ctx, "Sorry, couldn't fetch wiki metadata", Toast.LENGTH_SHORT).show();
                    return;
                }

                final List<WikiGeoObject> geoItems = response.getQuery();
                if (geoItems == null) {
                    return;
                }

                List<Long> pageIds = new ArrayList<Long>();
                for (WikiGeoObject wikiGeoObject : geoItems) {
                    pageIds.add(wikiGeoObject.getPageId());
                }

                final Map<Long, WikiGeoObject> geoItemsMap = new HashMap<>();
                for (WikiGeoObject geoItem : geoItems) {
                    geoItemsMap.put(geoItem.getPageId(), geoItem);
                }


                fetchPageInfos(ctx,
                    pageIds,
                    new WikiResponseCallback() {
                        @Override
                        public void callback(WikiStatus status, WikiResponse response) {
                            if (status != WikiStatus.SUCCESS) {
                                callback.callback(WikiStatus.NETWORK_ERROR, null);
                                return;
                            }

                            List<WikiAppObject> results = new ArrayList<WikiAppObject>();
                            List<WikiPage> pages = response.getPages();
                            for (WikiPage page : pages) {
                                WikiAppObject appObject = new WikiAppObject();
                                appObject.setTitle(page.getTitle());
                                appObject.setDistance(geoItemsMap.get(page.getPageId()).getDistance());
                                appObject.setLatitude(page.getCoordinates().get(0).getLatitude());
                                appObject.setLongitude(page.getCoordinates().get(0).getLongitude());
                                final WikiThumbnail thumbonail = page.getThumbnail();
                                final String thumSource = thumbonail != null ? thumbonail.getSource() : null;
                                appObject.setThumbnail(thumSource);
                                appObject.setUrl(thumSource);
                                appObject.setPageId(page.getPageId());
                                results.add(appObject);
                            }

                            // TODO here add callback invocation with result

                            callback.callback(WikiStatus.SUCCESS, results);

                        }
                    });
            }
        });

    }

    public static void fetchPageInfos(Context ctx, List<Long> pageIds, final WikiResponseCallback callback) {
        AQuery aq = NetUtils.createProxyAQueryInstance(ctx);
        aq.ajax("https://en.wikipedia.org/w/api.php" +
            "?action=query" +
            "&prop=coordinates%7Cpageimages%7Cpageterms" +
            "&colimit=50" +
            "&piprop=thumbnail" +
            "&pithumbsize=144" +
            "&pilimit=50" +
            "&wbptterms=description" +
            "&pageids=" + StringUtils.join(pageIds, "|") +
            "&format=json", JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                if (status.getCode() == 200) {
                    try {
                        callback.callback(WikiStatus.SUCCESS, fetchWikiResponse(object));
                    } catch (Throwable t) {
                        lg.error("General error", t);
                        callback.callback(WikiStatus.GENERAL_ERROR, null);
                    }
                } else {
                    callback.callback(WikiStatus.NETWORK_ERROR, null);
                }
            }
        });
    }


    public static void fetchSingleWikiInfoItemAndRunWikiPage(Context ctx,
                                                             Long pageId,
                                                             AjaxCallback<JSONObject> callback) {
        NetUtils.createProxyAQueryInstance(ctx).ajax(
            "https://en.wikipedia.org/w/api.php?action=query&prop=info&pageids="
                + pageId + "&inprop=url&format=json",
            JSONObject.class,
            callback
        );
    }

    public static void fetchAppData(Context ctx, WikiAppResponseCallback clbk) {
        final Location location = LocationUtils.getLastKnownLocation(ctx);

        if (location == null) {
            lg.info("Sorry, location is still not available");
            Toast.makeText(ctx, "Sorry, location is still not available", Toast.LENGTH_SHORT).show();
            EventBus.getDefault().post(new DataLoadingFinishEvent(ctx));
            return;
        }

        fetchAppData(ctx,
            location.getLatitude(),
            location.getLongitude(),
            SettingsUtils.fetchRadiusLimit(ctx),
            SettingsUtils.fetchSearchLimit(ctx),
            clbk
        );
    }
}
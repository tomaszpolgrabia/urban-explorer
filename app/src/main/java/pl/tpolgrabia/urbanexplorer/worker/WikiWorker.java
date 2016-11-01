package pl.tpolgrabia.urbanexplorer.worker;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.adapters.WikiLocationsAdapter;
import pl.tpolgrabia.urbanexplorer.callbacks.wiki.FetchWikiLocationsCallback;
import pl.tpolgrabia.urbanexplorer.dto.wiki.WikiRequestDto;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.wikibinding.dto.generator.WikiPage;
import pl.tpolgrabia.wikibinding.dto.generator.WikiResponse;
import pl.tpolgrabia.wikibinding.dto.generator.WikiResponse2;
import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoObject;
import pl.tpolgrabia.wikibinding.dto.geosearch.WikiGeoResponse2;
import pl.tpolgrabia.wikibinding.utils.WikiUtils;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tpolgrabia on 01.11.16.
 */
public class WikiWorker extends AsyncTask<WikiRequestDto, Integer, List<WikiAppObject>> {

    private static final Logger lg = LoggerFactory.getLogger(WikiWorker.class);
    private final WikiUtils wikiUtils;
    private final WikiLocationsFragment frag;
    private boolean success = false;
    private Context ctx;

    public WikiWorker(Context ctx,
                      WikiLocationsFragment wikiLocationsFragment,
                      String countryCode) {
        wikiUtils = new WikiUtils(ctx, countryCode);
        this.frag = wikiLocationsFragment;
        this.ctx = ctx;
    }

    @Override
    protected List<WikiAppObject> doInBackground(WikiRequestDto... params) {

        lg.info("Fetching {} wiki results", params.length);

        List<WikiAppObject> results = new ArrayList<>();
        for (WikiRequestDto param : params) {
            lg.debug("Fetching wiki results for {}", param);
            try {
                Response<WikiGeoResponse2> apiResult = wikiUtils.fetchGeoSearchWikiMetadata2(
                    param.getLatitude(),
                    param.getLongitude(),
                    param.getRadius(),
                    param.getLimit());

                final int apiResponseCode = apiResult.code();
                if (apiResponseCode != 200) {
                    lg.warn("Invalid error code {}. Response error message {}. Try it later again...",
                        apiResponseCode,
                        apiResult.message());
                    return results;
                }

                lg.debug("Fetched wiki response {}", apiResult.body());

                WikiGeoResponse2 apiGeoResponse = apiResult.body();
                List<Long> pageIds = new ArrayList<>();
                final List<WikiGeoObject> geoItems = apiGeoResponse.getQuery().getGeosearch();

                if (geoItems == null) {
                    return results;
                }

                for (WikiGeoObject geoObject : geoItems) {
                    pageIds.add(geoObject.getPageId());
                }

                final Map<Long, WikiGeoObject> geoItemsMap = new HashMap<>();
                for (WikiGeoObject geoItem : geoItems) {
                    geoItemsMap.put(geoItem.getPageId(), geoItem);
                }

                lg.debug("Fetching wiki page infos for {}", pageIds);
                Response<WikiResponse2> pageInfoResponse = wikiUtils.fetchPageInfos2(pageIds);
                int pageInfoResponseCode = pageInfoResponse.code();
                if (pageInfoResponseCode != 200) {
                    lg.warn("Invalid http code {}. Message: {}. Try it later again",
                        pageInfoResponseCode, pageInfoResponse.message());
                    return results;
                }

                WikiResponse2 wikiResponse = pageInfoResponse.body();

                lg.debug("Fetched page infos response: {}", wikiResponse);

                for (WikiPage page : wikiResponse.getQuery().getPages().values()) {
                    WikiAppObject appObject = WikiUtils.convertWikiAppObject(geoItemsMap, page);
                    results.add(appObject);
                }

            } catch (IOException e) {
                lg.error("I/O error", e);
            }
        }

        success = true;

        lg.info("Retrieved {} wiki results", results.size());
        return results;
    }

    @Override
    protected void onPostExecute(List<WikiAppObject> objects) {
        ArrayList<WikiAppObject> nobjects = new ArrayList<WikiAppObject>(objects);
        frag.setAppObjects(nobjects);

        // handling here wiki locations
        if (!success) {
            Toast.makeText(ctx, "Sorry, currently we have problem with interfacing wiki" +
                ": " + success + ". Try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO on success

        final View view = frag.getView();

        if (view == null) {
            return;
        }

        ListView locations = (ListView) view.findViewById(R.id.wiki_places);
        locations.setOnItemLongClickListener(new FetchWikiLocationsCallback(frag));
        locations.setAdapter(new WikiLocationsAdapter(frag.getActivity(), objects));
        if (objects.isEmpty()) {
            Toast.makeText(frag.getActivity(), "No results", Toast.LENGTH_SHORT).show();
        }

        MainActivity mainActivity = (MainActivity) frag.getActivity();
        if (mainActivity == null) {
            return;
        }

        EventBus.getDefault().post(new DataLoadingFinishEvent(this));
    }
}

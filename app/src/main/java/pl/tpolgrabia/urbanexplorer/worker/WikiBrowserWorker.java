package pl.tpolgrabia.urbanexplorer.worker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.wikibinding.utils.WikiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tpolgrabia on 03.11.16.
 */
public class WikiBrowserWorker extends AsyncTask<WikiAppObject, Integer, List<String>> {
    private static final Logger lg = LoggerFactory.getLogger(WikiBrowserWorker.class);

    private final Context ctx;
    private final WikiUtils wikiUtils;

    public WikiBrowserWorker(Context ctx, String countryCode) {
        this.ctx = ctx;
        wikiUtils = new WikiUtils(ctx, countryCode);
    }

    @Override
    protected List<String> doInBackground(WikiAppObject... params) {
        List<String> results = new ArrayList<>();
        for (WikiAppObject param : params) {
            try {
                final Long pageId = param.getPageId();
                lg.debug("Fetching page info url for {}", pageId);
                final String pageUrl = wikiUtils.fetchPageInfoUrl(pageId);
                lg.debug("Fetched page url {}", pageUrl);
                results.add(pageUrl);
            } catch (IOException e) {
                lg.error("I/O error during fetch page info", e);
            }
        }
        return results;
    }

    @Override
    protected void onPostExecute(List<String> strings) {
        if (strings == null || strings.isEmpty()) {
            return;
        }

        final int n = strings.size();
        if (n > 1) {
            lg.warn("Too many results to use - {}", n);
        }

        String wikiUrl = strings.get(0);
        Intent intent = new Intent(Intent.ACTION_VIEW,
            Uri.parse(wikiUrl));

        ctx.startActivity(intent);
    }
}

package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.dto.wiki.WikiCacheDto;
import pl.tpolgrabia.wikibinding.dto.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class WikiCacheUtils {
    private static final Logger lg = LoggerFactory.getLogger(WikiCacheUtils.class);

    private WikiCacheUtils() {
        /**
         * EMPTY
         */
    }

    public static void saveWikiObjectsToCache(Context ctx, List<WikiAppObject> appObjects) {
        try (BufferedWriter bw = new BufferedWriter(
            new OutputStreamWriter(
                new FileOutputStream(
                    new File(ctx.getCacheDir(),
                        AppConstants.WIKI_CACHE_FILENAME))))) {

            WikiCacheDto dto = new WikiCacheDto();
            dto.setAppObject(appObjects);
            if (ctx != null) {
                Location location = LocationUtils.getLastKnownLocation(ctx);
                if (location != null) {
                    dto.setLongitude(location.getLongitude());
                    dto.setLatitude(location.getLatitude());
                    dto.setAltitude(location.getAltitude());
                }
            }

            dto.setFetchedAt(new GregorianCalendar().getTime());
            // FIXME should be a fetched time, not persist time

            new Gson().toJson(bw);

        } catch (FileNotFoundException e) {
            lg.error("File not found", e);
        } catch (IOException e) {
            lg.error("I/O error", e);
        }
    }

    public static ArrayList<WikiAppObject> loadWikiObjectsFromCache(Context ctx, Bundle savedInstanceState) {
        ArrayList<WikiAppObject> appObjects = savedInstanceState == null ? new ArrayList<WikiAppObject>()
            : (ArrayList<WikiAppObject>)savedInstanceState.getSerializable(WikiLocationsFragment.WIKI_APP_OBJECTS);

        if (appObjects == null) {
            try (InputStreamReader ir = new InputStreamReader(
                new FileInputStream(
                    new File(ctx.getCacheDir(),
                        AppConstants.WIKI_CACHE_FILENAME)))) {

                WikiCacheDto dto = new Gson().fromJson(ir, WikiCacheDto.class);
                appObjects = new ArrayList<>(dto.getAppObject());

            } catch (FileNotFoundException e) {
                lg.error("File not found", e);
            } catch (IOException e) {
                lg.error("I/O error", e);
            }
        }
        return appObjects;
    }
}

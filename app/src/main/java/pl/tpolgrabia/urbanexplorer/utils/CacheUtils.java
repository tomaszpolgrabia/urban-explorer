package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioCacheDto;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.dto.wiki.WikiCacheDto;
import pl.tpolgrabia.urbanexplorer.dto.wiki.app.WikiAppObject;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class CacheUtils {
    private static final Logger lg = LoggerFactory.getLogger(CacheUtils.class);
    public static ArrayList<PanoramioImageInfo> restorePhotosFromCache(HomeFragment homeFragment, Bundle savedBundleSettings) {
        ArrayList<PanoramioImageInfo> photos;

        final Serializable serPhotos = savedBundleSettings.getSerializable(HomeFragment.PHOTO_LIST);
        lg.trace("Photo list serPhotos {}", serPhotos);
        photos = (ArrayList<PanoramioImageInfo>) serPhotos;

        if (photos != null && !photos.isEmpty()) {
            // we are using from serializable bundle photos
            return photos;
        } else {
            // maybe we find something in our cache file
            try (Reader reader =
                new InputStreamReader(
                    new FileInputStream(
                        new File(homeFragment.getActivity().getCacheDir(),
                            AppConstants.PANORAMIO_CACHE_FILENAME)))) {

                PanoramioCacheDto dto = new Gson().fromJson(new JsonReader(reader), PanoramioCacheDto.class);
                if (dto == null) {
                    lg.trace("Sorry, photos I/O cache is null");
                    return new ArrayList<>();
                }

                photos = new ArrayList<>(dto.getPanoramioImages());
                lg.trace("Photos size from I/O cache is {}", photos.size());
                lg.trace("I've read photos from I/O cache");
                return photos;

            } catch (FileNotFoundException e) {
                lg.error("File not found", e);
                return new ArrayList<>();
            } catch (IOException e) {
                lg.error("I/O error", e);
                return new ArrayList<>();
            } catch (Throwable t) {
                lg.error("Throwable", t);
                return new ArrayList<>();
            }
        }
    }

    public static void savePostsToCache(Context ctx, ArrayList<PanoramioImageInfo> photos) {
        File cacheDir = ctx.getCacheDir();
        BufferedWriter br = null;
        try {
            br = new BufferedWriter(
                new OutputStreamWriter(
                    new FileOutputStream(
                        new File(cacheDir, AppConstants.PANORAMIO_CACHE_FILENAME))));

            PanoramioCacheDto dto = new PanoramioCacheDto();
            dto.setPanoramioImages(photos);

            Location location = LocationUtils.getLastKnownLocation(ctx);
            if (location != null) {
                dto.setLongitude(location.getLongitude());
                dto.setLatitude(location.getLatitude());
                dto.setAltitude(location.getAltitude());
            }

            dto.setFetchedAt(new GregorianCalendar().getTime());
            // FIXME this should be a fetch time, not persist time

            new Gson().toJson(dto, br);

        } catch (FileNotFoundException e) {
            lg.error("File not found", e);
        } catch (IOException e) {
            lg.error("I/O Exception", e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    lg.error("I/O error during photos cache saving", e);
                }
            }
        }
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

    public static ArrayList<WikiAppObject> fetchAppObjectsFromCache(Context ctx, Bundle savedInstanceState) {
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

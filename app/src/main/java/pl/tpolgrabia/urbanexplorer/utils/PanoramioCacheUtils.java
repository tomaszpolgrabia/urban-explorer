package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioCacheDto;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * Created by tpolgrabia on 25.09.16.
 */
public class PanoramioCacheUtils {
    private static final Logger lg = LoggerFactory.getLogger(PanoramioCacheUtils.class);

    private PanoramioCacheUtils() {
        /**
         * EMPTY
         */
    }
    
    public static ArrayList<PanoramioImageInfo> loadPhotosFromCache(HomeFragment homeFragment, Bundle savedBundleSettings) {
        ArrayList<PanoramioImageInfo> photos;

        if (savedBundleSettings == null) {
            return new ArrayList<>();
        }

        final Serializable serPhotos = savedBundleSettings.getSerializable(HomeFragment.PHOTO_LIST);
        lg.trace("Photo list serPhotos {}", serPhotos);
        photos = (ArrayList<PanoramioImageInfo>) serPhotos;

        if (photos != null && !photos.isEmpty()) {
            // we are using from serializable bundle photos
            return photos;
        } else {
            // maybe we find something in our cache file
            Reader reader = null;
            try {
                reader =
                    new InputStreamReader(
                        new FileInputStream(
                            new File(homeFragment.getActivity().getCacheDir(),
                                AppConstants.PANORAMIO_CACHE_FILENAME)));

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
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        lg.error("Error closing reader - I/O error", e);
                    }
                }
            }
        }
    }

    public static void savePhotosToCache(Context ctx, ArrayList<PanoramioImageInfo> photos) {
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
}

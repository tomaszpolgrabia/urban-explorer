package pl.tpolgrabia.urbanexplorerutils.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorerutils.R;
import pl.tpolgrabia.urbanexplorerutils.constants.UtilConstants;

import java.util.HashSet;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class SettingsUtils {
    public static final double WIKI_DEF_RADIUS = 10.0;
    public static final long WIKI_DEF_LIMIT = 100;
    private static final Logger lg = LoggerFactory.getLogger(SettingsUtils.class);

    public static Double fetchRadiusY(Context ctx) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String pref_panoramio_radiusy = sharedPreferences.getString(
            "pref_panoramio_radiusy",
            String.valueOf(UtilConstants.PAMNORAMIO_DEF_RADIUSY));
        lg.debug("Panoramio radiusy pref equals {}", pref_panoramio_radiusy);
        return Double.parseDouble(
            pref_panoramio_radiusy);
    }

    public static Double fetchRadiusX(Context ctx) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String pref_panoramio_radiusx = sharedPreferences.getString(
            "pref_panoramio_radiusx",
            String.valueOf(UtilConstants.PAMNORAMIO_DEF_RADIUSX));
        lg.debug("Panoramio radiusx pref equals {}", pref_panoramio_radiusx);
        return Double.parseDouble(
            pref_panoramio_radiusx);
    }

    public static int getPanoramioBulkDataSize(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String sValue = sharedPrefs.getString(UtilConstants.PANORAMIO_BULK_SIZE_KEY,
            String.valueOf(UtilConstants.PANORAMIO_BULK_SIZE_DEF_VALUE));
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            lg.warn("Invalid panoramio bulk data size {}", sValue, e);
            return UtilConstants.PANORAMIO_BULK_SIZE_DEF_VALUE;
        }
    }

    public static Double fetchRadiusLimit(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String prefWikiRadius = sharedPreferences.getString("pref_wiki_radius", String.valueOf(WIKI_DEF_RADIUS));
        lg.debug("Pref wiki radius limit {}", prefWikiRadius);
        return NumberUtils.safeParseDouble(prefWikiRadius)*1000.0; // in m, settings are in km unit
    }

    public static Long fetchSearchLimit(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String prefWikiResultsLimit = sharedPreferences.getString("pref_wiki_limit", String.valueOf(WIKI_DEF_LIMIT));
        lg.debug("Pref wiki search results limit {}", prefWikiResultsLimit);
        return NumberUtils.safeParseLong(prefWikiResultsLimit);
    }

    public static Double getDefaultPlacesSearchRadius(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        try {
            return Double.parseDouble(sharedPrefs.getString(UtilConstants.PREF_GOOGLE_PLACES_RADIUS,
                UtilConstants.DEF_PLACES_RADIUS.toString())) * UtilConstants.GOOGLE_PLACES_STD_UNIT;
        } catch (NumberFormatException e) {
            lg.error("Invalid settings for google places search radius", e);
            return UtilConstants.DEF_PLACES_RADIUS;
        }
    }

    public static String getPlacesSearchCategories(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return StringUtils.join(sharedPrefs.getStringSet(
            UtilConstants.GOOGLE_PLACES_CATEGORIES_PREF, new HashSet<String>()),
            "|");
    }
}

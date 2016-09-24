package pl.tpolgrabia.urbanexplorer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 24.09.16.
 */
public class SettingsUtils {
    private static final Logger lg = LoggerFactory.getLogger(SettingsUtils.class);

    public static Double fetchRadiusY(Context ctx) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String pref_panoramio_radiusy = sharedPreferences.getString(
            "pref_panoramio_radiusy",
            String.valueOf(AppConstants.PAMNORAMIO_DEF_RADIUSY));
        lg.debug("Panoramio radiusy pref equals {}", pref_panoramio_radiusy);
        return Double.parseDouble(
            pref_panoramio_radiusy);
    }

    public static Double fetchRadiusX(Context ctx) {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        final String pref_panoramio_radiusx = sharedPreferences.getString(
            "pref_panoramio_radiusx",
            String.valueOf(AppConstants.PAMNORAMIO_DEF_RADIUSX));
        lg.debug("Panoramio radiusx pref equals {}", pref_panoramio_radiusx);
        return Double.parseDouble(
            pref_panoramio_radiusx);
    }

    public static int getPanoramioBulkDataSize(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx.getActivity());
        final String sValue = sharedPrefs.getString(AppConstants.PANORAMIO_BULK_SIZE_KEY,
            String.valueOf(AppConstants.PANORAMIO_BULK_SIZE_DEF_VALUE));
        try {
            return Integer.parseInt(sValue);
        } catch (NumberFormatException e) {
            lg.warn("Invalid panoramio bulk data size {}", sValue, e);
            return AppConstants.PANORAMIO_BULK_SIZE_DEF_VALUE;
        }
    }
}

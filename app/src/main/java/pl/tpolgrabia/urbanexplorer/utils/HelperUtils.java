package pl.tpolgrabia.urbanexplorer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;
import com.androidquery.util.AQUtility;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import io.fabric.sdk.android.Fabric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.AppConstants;
import pl.tpolgrabia.urbanexplorer.AppStage;
import pl.tpolgrabia.urbanexplorer.MainActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.dto.MainActivityState;
import pl.tpolgrabia.urbanexplorerutils.utils.ImageLoaderUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.NumberUtils;

import java.util.List;

/**
 * Created by tpolgrabia on 19.09.16.
 */
public class HelperUtils {
    private static final Logger lg = LoggerFactory.getLogger(HelperUtils.class);
    public static final String FIRST_TIME_LAUNCH = "FIRST_TIME_LAUNCH_KEY";
    public static final int LOCATION_SETTINGS_REQUEST_ID = 1;

    public static void firstTimeNotification(Context ctx) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        if (sharedPrefs.getBoolean(FIRST_TIME_LAUNCH, true)) {
            Toast.makeText(ctx, "To interact with any list itemm press long the item. When thgre is no results" +
                ", please, click refresh in the menu", Toast.LENGTH_LONG).show();
            Toast.makeText(ctx, "To change panoramio / wiki search views swipe left or right",
                Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(FIRST_TIME_LAUNCH, false);
            editor.apply();
        }
    }

    public static void initErrorAndDebugHanlers(Context ctx) {
        AQUtility.setDebug(AppConstants.RELEASE != AppStage.FINAL
            && AppConstants.RELEASE != AppStage.RELEASE_CANDIDATE);

        if (AppConstants.RELEASE == AppStage.FINAL
            || AppConstants.RELEASE == AppStage.RELEASE_CANDIDATE) {
            Fabric.with(ctx, new Crashlytics());
        }
    }

    public static void initUniversalDownloader(Context ctx) {
        MainActivity.options = ImageLoaderUtils.createRoundedOptions();
        MainActivity.rectOptions = ImageLoaderUtils.createRectangularOptions();
        initUniversalDownloader(ctx, MainActivity.options);
    }

    public static void initUniversalDownloader(Context ctx, DisplayImageOptions defaultOptions) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            ctx)
            .defaultDisplayImageOptions(defaultOptions)
            .memoryCache(new WeakMemoryCache())
            .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
    }

    public static void appendEffectToTransition(FragmentTransaction ctx, MainActivityState old, MainActivityState curr) {
        if (old.getOrder() == -1 || curr.getOrder() == -1) {
            return;
        }

        if (old != curr) {
            if (curr.getOrder() < old.getOrder()) {
                // slide left animation
                lg.trace("sliding left animation");
                ctx.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right);
            } else {
                // slide right animation
                lg.trace("sliding right animation");
                ctx.setCustomAnimations(
                    R.anim.slide_in_right,
                    R.anim.slide_out_right,
                    R.anim.slide_in_left,
                    R.anim.slide_out_left);
            }
        }
    }

    public static Long fetchGpsUpdateFreq(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String prefGpsUpdateFreq = sharedPreferences.getString(
            AppConstants.PREF_GPS_UPDATE_FREQ,
            String.valueOf(AppConstants.GPS_LOCATION_UPDATE_FREQ));

        lg.debug("Pref GPS location update frequency {}", prefGpsUpdateFreq);
        return Math.round(NumberUtils.safeParseDouble(prefGpsUpdateFreq)* 60.0 * 1000.0);
    }

    public static Float fetchGpsDistanceFreq(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String prefDistanceUpdateFreq = sharedPreferences.getString(
            AppConstants.PREF_GPS_DISTANCE_FREQ,
            String.valueOf(AppConstants.GPS_LOCATION_DISTANCE_FREQ));

        lg.debug("Pref GPS distance update frequency {}", prefDistanceUpdateFreq);
        return NumberUtils.safeParseFloat(prefDistanceUpdateFreq);
    }

    public static boolean checkForLocalicatonEnabled(Activity ctx) {

        lg.trace("Check for location enabled");
        final String locationProvider = LocationUtils.getDefaultLocation(ctx);
        lg.debug("Location provider {}", locationProvider);
        if (locationProvider == null) {
            lg.debug("Location provider is null. Prompting for enabling location services");
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            ctx.startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_ID);
            return true;
        }

        return false;
    }

    public static void traceAllAvailableFragments(FragmentManager fragmentManager) {
        final List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            lg.trace("Available fragments {}", fragments.size());
            for (Fragment frag : fragments) {
                if (frag != null) {
                    lg.trace("Available fragment with id: {}, tag: {}", frag.getId(), frag.getTag());
                } else {
                    lg.trace("Available null-fragment");
                }
            }
        } else {
            lg.trace("There are no fragments -> null");
        }
    }
}

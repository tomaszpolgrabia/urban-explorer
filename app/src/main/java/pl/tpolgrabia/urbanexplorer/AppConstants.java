package pl.tpolgrabia.urbanexplorer;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class AppConstants {
    public static final AppStage RELEASE = AppStage.DEVELOPMENT;
    public static final float MIN_DISTANCE = 100;
    public static final long GPS_LOCATION_UPDATE_FREQ = 15000;
    public static final float GPS_LOCATION_DISTANCE_FREQ = MIN_DISTANCE;

    public static final String PANORAMIO_CACHE_FILENAME = "panoramio-cache.dat";
    public static final String WIKI_CACHE_FILENAME = "wiki-cache.dat";
    public static final String PREF_GPS_UPDATE_FREQ = "pref_gps_update_freq";
    public static final String PREF_GPS_DISTANCE_FREQ = "pref_gps_distance_freq";
    public static final String PHOTO_BACKSTACK = "PHOTO_BACKSTACK";
    static final int HOME_FRAGMENT_ID = 0;
    static final int WIKI_FRAGMENT_ID = 1;
    static final String FRAG_ID = "FRAG_ID";
    static final int SETTINGS_ID_INTENT_REQUEST_ID = 2;
    static final String PHOTO_INFO = "PHOTO_INFO";
    static final String SAVED_CONFIG_KEY = "SAVED_CONFIG_KEY";
    public static final String GOOGLE_API_KEY = "AIzaSyBAJoK-pu_qnQ0U8EGjM1Zkz_g8oJV4w2g";
    public static final String DEF_WIKI_COUNTRY_CODE = "en";
}

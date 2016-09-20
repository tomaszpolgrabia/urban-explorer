package pl.tpolgrabia.urbanexplorer;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class AppConstants {
    public static final String GOOGLE_API_KEY = "AIzaSyBAJoK-pu_qnQ0U8EGjM1Zkz_g8oJV4w2g";
    public static final long MIN_TIME = 60000;
    public static final AppStage RELEASE = AppStage.DEVELOPMENT;
    public static final float MIN_DISTANCE = 100;
    public static final float PAMNORAMIO_DEF_RADIUSX = 0.05f;
    public static final float PAMNORAMIO_DEF_RADIUSY = 0.05f;
    public static final long GPS_LOCATION_UPDATE_FREQ = 15000;
    public static final float GPS_LOCATION_DISTANCE_FREQ = MIN_DISTANCE;
    public static final String PREF_HTTP_PROXY_HOST_KEY = "pref_proxy_host";
    public static final String DEF_HTTP_PROXY_HOST = "localhost";
    public static final String PREF_HTTP_PROXY_PORT_KEY = "pref_proxy_port";
    public static final String DEF_HTTP_PROXY_PORT = "8123";
    public static final String PREF_HTTP_PROXY_USER_KEY = "pref_proxy_user";
    public static final String DEF_HTTP_PROXY_USER = null;
    public static final String PREF_HTTP_PROXY_PASSWORD_KEY = "pref_proxy_pass";
    public static final String DEF_HTTP_PROXY_PASSWORD = null;
    public static final String PREF_HTTP_PROXY_ENABLED_KEY = "pref_proxy_enabled";
    public static final boolean DEF_HTTP_PROXY_ENABLED = false;

    public static final String PANORAMIO_BULK_SIZE_KEY = "pref_panoramio_bulk_size";
    public static final int PANORAMIO_BULK_SIZE_DEF_VALUE = 50;
    public static final String PANORAMIO_CACHE_FILENAME = "panoramio-cache.dat";
    public static final String WIKI_CACHE_FILENAME = "wiki-cache.dat";
    public static final String PREF_GPS_UPDATE_FREQ = "pref_gps_update_freq";
    public static final String PREF_GPS_DISTANCE_FREQ = "pref_gps_distance_freq";
    public static final String PHOTO_BACKSTACK = "PHOTO_BACKSTACK";
    static final int HOME_FRAGMENT_ID = 0;
    static final double MIN_FRAGMENT_ID = HOME_FRAGMENT_ID;
    static final int WIKI_FRAGMENT_ID = 1;
    static final double MAX_FRAGMENT_ID = WIKI_FRAGMENT_ID;
    static final String FRAG_ID = "FRAG_ID";
    static final int SETTINGS_ID_INTENT_REQUEST_ID = 2;
    static final String PHOTO_INFO = "PHOTO_INFO";
    static final String SAVED_CONFIG_KEY = "SAVED_CONFIG_KEY";
}

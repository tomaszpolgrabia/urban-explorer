package pl.tpolgrabia.urbanexplorer;

/**
 * Created by tpolgrabia on 27.08.16.
 */
public class AppConstants {
    public static final String GOOGLE_API_KEY = "AIzaSyDAnmEK6cgovRrefUuYojL1pxPEbIBLZUw";
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
}

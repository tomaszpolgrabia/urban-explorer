package pl.tpolgrabia.urbanexplorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
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
import pl.tpolgrabia.urbanexplorer.activities.SettingsActivity;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListener;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioShowerFragment;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorer.handlers.SwipeHandler;
import pl.tpolgrabia.urbanexplorer.utils.ImageLoaderUtils;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.NetUtils;
import pl.tpolgrabia.urbanexplorer.utils.NumberUtils;
import pl.tpolgrabia.urbanexplorer.views.CustomInterceptor;
import pl.tpolgrabia.urbanexplorer.views.SwipeFrameLayout;

public class MainActivity extends ActionBarActivity {

    private static final Logger lg = LoggerFactory.getLogger(MainActivity.class);

    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
    private static final String PHOTO_BACKSTACK = "PHOTO_BACKSTACK";
    private static final int HOME_FRAGMENT_ID = 0;
    private static final int WIKI_FRAGMENT_ID = 1;
    private static final double MAX_FRAGMENT_ID = WIKI_FRAGMENT_ID;
    private static final double MIN_FRAGMENT_ID = HOME_FRAGMENT_ID;
    private static final String FRAG_ID = "FRAG_ID";
    private static final int SETTINGS_ID_INTENT_REQUEST_ID = 2;
    private static final String PHOTO_INFO = "PHOTO_INFO";
    private static final String FIRST_TIME_LAUNCH = "FIRST_TIME_LAUNCH_KEY";
    private static final String MAIN_BACKSTACK = "MAIN_BACKSTACK_KEY";
    public static DisplayImageOptions options;
    private GestureDetectorCompat gestureDetector;
    private int currentFragmentId = 0;
    private StandardLocationListener locationCallback;

    private boolean locationServicesActivated = false;
    private GestureDetector.OnGestureListener swipeHandler;
    private PanoramioImageInfo photoInfo;
    private ProgressDialog progressDlg;
    private int oldFragmentId = 0;

    public StandardLocationListener getLocationCallback() {
        return locationCallback;
    }

    public void showProgress() {
        progressDlg.show();
    }

    public void hideProgress() {
        progressDlg.dismiss();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lg.trace("onCreate");
        setContentView(R.layout.activity_main);

        AQUtility.setDebug(AppConstants.RELEASE != AppStage.FINAL
            && AppConstants.RELEASE != AppStage.RELEASE_CANDIDATE);

        NetUtils.setGlobalProxyAuth(this);

        currentFragmentId = 0;
        progressDlg = new ProgressDialog(this);

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = ImageLoaderUtils.createDefaultOptions();

        options = defaultOptions;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            this)
            .defaultDisplayImageOptions(defaultOptions)
            .memoryCache(new WeakMemoryCache())
            .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        swipeHandler = new SwipeHandler(this);
        gestureDetector = new GestureDetectorCompat(this, swipeHandler);
        locationCallback = new StandardLocationListener();
        initLocalication();
        if (AppConstants.RELEASE == AppStage.FINAL
            || AppConstants.RELEASE == AppStage.RELEASE_CANDIDATE) {
            Fabric.with(this, new Crashlytics());
        }

        Integer fragId = savedInstanceState != null ? savedInstanceState.getInt(FRAG_ID) : null;
        lg.trace("Restored orig frag id:  {}", fragId);
        currentFragmentId = fragId == null ? 0 : fragId;
        lg.trace("Set final frag id: {}", fragId);
        photoInfo = savedInstanceState != null ? (PanoramioImageInfo) savedInstanceState.getSerializable(PHOTO_INFO) : null;
        switchFragment();

        updateSwipeHandler();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPrefs.getBoolean(FIRST_TIME_LAUNCH, true)) {
            Toast.makeText(this, "To interact with any list itemm press long the item. When thgre is no results" +
                ", please, click refresh in the menu", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "To change panoramio / wiki search views swipe left or right",
                Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(FIRST_TIME_LAUNCH, false);
            editor.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                final Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, MainActivity.SETTINGS_ID_INTENT_REQUEST_ID, new Bundle());
                return true;
            case R.id.refresh:
                progressDlg.setMessage("Refreshing results");
                progressDlg.show();
                switch (currentFragmentId) {
                    case HOME_FRAGMENT_ID:
                        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager()
                            .findFragmentByTag(HomeFragment.TAG);
                        homeFragment.fetchPanoramioPhotos();
                        break;
                    case WIKI_FRAGMENT_ID:
                        WikiLocationsFragment wikiLocationsFragment = (WikiLocationsFragment)
                            getSupportFragmentManager()
                            .findFragmentByTag(WikiLocationsFragment.TAG);
                        wikiLocationsFragment.fetchWikiLocations();
                        break;
                    default:
                        lg.warn("Unknown current fragment ID");
                        break;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void resetPhotoInfo() {
        this.photoInfo = null;
    }

    public void switchToPhoto(PanoramioImageInfo photoInfo) {
        this.photoInfo = photoInfo;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ctx = fragmentManager.beginTransaction();
        PanoramioShowerFragment panoramioShower = new PanoramioShowerFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(PanoramioShowerFragment.PANORAMIO_PHOTO_ARG_KEY, photoInfo);
        panoramioShower.setArguments(arguments);

        ctx.setCustomAnimations(R.anim.slide_in_down,
            R.anim.slide_out_down,
            R.anim.slide_in_up,
            R.anim.slide_out_up);
        Fragment frag = fragmentManager.findFragmentByTag(PanoramioShowerFragment.TAG);
        if (frag != null) {
            ctx.replace(R.id.fragments, frag);
        } else {
            ctx.replace(R.id.fragments, panoramioShower, PanoramioShowerFragment.TAG);
        }
        ctx.addToBackStack(PHOTO_BACKSTACK);

        ctx.commit();

    }

    private void switchFragment() {

        if (photoInfo != null) {
            switchToPhoto(photoInfo);
            return;
        }

        switch (currentFragmentId) {
            case HOME_FRAGMENT_ID:
                // switch to home fragment
                lg.debug("Switching to home fragment");
                final HomeFragment fragment = new HomeFragment();
                switchFragment(fragment, HomeFragment.TAG);
                break;
            case WIKI_FRAGMENT_ID:
                // switch to wiki fragment
                lg.debug("Switching to wiki fragment");
                switchFragment(new WikiLocationsFragment(), WikiLocationsFragment.TAG);
                break;
        }

    }

    private void switchFragment(Fragment fragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ctx = fragmentManager.beginTransaction();
        lg.trace("old fragment id: {}, current fragment id: {}", oldFragmentId, currentFragmentId);
        if (oldFragmentId != currentFragmentId) {
            if (currentFragmentId < oldFragmentId) {
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

        Fragment frag = fragmentManager.findFragmentByTag(tag);
        if (frag == null) {
            ctx.replace(R.id.fragments, fragment, tag);
        } else {
            ctx.replace(R.id.fragments, frag);
        }
        ctx.addToBackStack(MAIN_BACKSTACK);
        ctx.commit();
        updateSwipeHandler();
    }

    private void updateSwipeHandler() {
        SwipeFrameLayout swipeFragments = (SwipeFrameLayout) findViewById(R.id.fragments);
        swipeFragments.setCustomInterceptor(new CustomInterceptor() {
            @Override
            public void handle(MotionEvent ev) {
                gestureDetector.onTouchEvent(ev);
            }
        });
        swipeHandler = new SwipeHandler(this);
        gestureDetector = new GestureDetectorCompat(this, swipeHandler);
    }

    public void swipeLeft() {
        changeCurrentFragId((int)Math.max(MIN_FRAGMENT_ID, currentFragmentId-1));
        switchFragment();
    }

    private void changeCurrentFragId(int nextFragmentId) {
        oldFragmentId = currentFragmentId;
        currentFragmentId = nextFragmentId;
    }

    public void swipeRight() {
        changeCurrentFragId((int)Math.min(MAX_FRAGMENT_ID, currentFragmentId+1));
        switchFragment();
    }

    private void initLocalication() {
        if (checkForLocalicatonEnabled()) return;

        final Context ctx = this;

        locationCallback.addCallback(new StandardLocationListenerCallback() {
            @Override
            public void callback(Location location) {
                double lat = location.getLatitude();
                double lng = location.getLongitude();
                Toast.makeText(ctx, "Location: (" + lat + "," + lng + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkForLocalicatonEnabled() {

        final String locationProvider = LocationUtils.getDefaultLocation(this);
        if (locationProvider == null) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_ID);
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        lg.trace("onResume");
        String locationProvider = LocationUtils.getDefaultLocation(this);
        if (locationProvider != null) {
            LocationManager locationService = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationService.requestLocationUpdates(locationProvider,
                fetchGpsUpdateFreq(),
                fetchGpsDistanceFreq(),
                locationCallback);
            locationServicesActivated = true;
        }
    }

    private Float fetchGpsDistanceFreq() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefDistanceUpdateFreq = sharedPreferences.getString(
            "pref_gps_distance_freq",
            String.valueOf(AppConstants.GPS_LOCATION_DISTANCE_FREQ));

        lg.debug("Pref GPS distance update frequency {}", prefDistanceUpdateFreq);
        return NumberUtils.safeParseFloat(prefDistanceUpdateFreq);
    }

    private Long fetchGpsUpdateFreq() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String prefGpsUpdateFreq = sharedPreferences.getString(
            "pref_gps_update_freq",
            String.valueOf(AppConstants.GPS_LOCATION_UPDATE_FREQ));

        lg.debug("Pref GPS location update frequency {}", prefGpsUpdateFreq);
        return Math.round(NumberUtils.safeParseDouble(prefGpsUpdateFreq)* 60.0 * 1000.0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        lg.trace("onPause");
        if (locationServicesActivated) {
            LocationManager locationService = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationService.removeUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lg.trace("onDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST_ID:
                String locationProvider = LocationUtils.getDefaultLocation(this);
                if (locationProvider == null) {

                    // launching settings activity to allow the user switching on location service

                    Intent intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                }
                break;
            case SETTINGS_ID_INTENT_REQUEST_ID:
                NetUtils.setGlobalProxyAuth(this);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        lg.trace("1 Saving current fragment id: {}", currentFragmentId);
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAG_ID, currentFragmentId);
        outState.putSerializable(PHOTO_INFO, photoInfo);

//        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPrefs.edit();
//        editor.putInt(FRAG_ID, currentFragmentId);
//        editor.commit();

        lg.trace("2 Saving current fragment id: {}", currentFragmentId);
    }

}

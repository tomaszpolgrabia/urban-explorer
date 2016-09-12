package pl.tpolgrabia.urbanexplorer;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import io.fabric.sdk.android.Fabric;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListener;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListenerCallback;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioShowerFragment;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorer.utils.ImageLoaderUtils;

public class MainActivity extends ActionBarActivity implements GestureDetector.OnGestureListener {

    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
    private static final String PHOTO_BACKSTACK = "PHOTO_BACKSTACK";
    private static final float SWIPE_VELOCITY_THRESHOLD = 20;
    private static final int HOME_FRAGMENT_ID = 0;
    private static final int WIKI_FRAGMENT_ID = 1;
    private static final double MAX_FRAGMENT_ID = WIKI_FRAGMENT_ID;
    private static final double MIN_FRAGMENT_ID = HOME_FRAGMENT_ID;
    private static final String FRAG_ID = "FRAG_ID";
    public static DisplayImageOptions options;
    private GestureDetectorCompat gestureDetector;
    private float SWIPE_THRESHOLD = 50;
    private int currentFragmentId = 0;
    private LocationManager locationService;
    private StandardLocationListener locationCallback;

    private boolean gpsLocationEnabled;
    private boolean networkLocationEnabled;
    private boolean locationEnabled;
    private String locationProvider;
    private boolean locationServicesActivated = false;


    public StandardLocationListener getLocationCallback() {
        return locationCallback;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(CLASS_TAG, "onCreate");
        setContentView(R.layout.activity_main);

        currentFragmentId = 0;

//        Toolbar toolbar = (Toolbar) findViewById(R.id.navbar);
//        setSupportActionBar(toolbar);

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = ImageLoaderUtils.createDefaultOptions();

        options = defaultOptions;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            this)
            .defaultDisplayImageOptions(defaultOptions)
            .memoryCache(new WeakMemoryCache())
            .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

//        getSupportFragmentManager()
//            .beginTransaction()
//            .replace(R.id.fragments, new HomeFragment())
//            .commit();

        // lLinearLayout locations = (LinearLayout) findViewById(R.id.locations);
        // locations.setOnTouchListener(new OnSwipeTouchListener);
        gestureDetector = new GestureDetectorCompat(this, this);
        locationCallback = new StandardLocationListener();
        initLocalication();
        Fabric fabric = new Fabric.Builder(this).debuggable(true).kits(new Crashlytics()).build();
        Fabric.with(fabric);

        Integer fragId = savedInstanceState != null ? savedInstanceState.getInt(FRAG_ID) : null;
        Log.v(CLASS_TAG, "Restored orig frag id: " + fragId);
        currentFragmentId = fragId == null ? 0 : fragId;
        Log.v(CLASS_TAG, "Set final frag id: " + fragId);
        switchFragment();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // TODO show settings fragment
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void switchToPhoto(PanoramioImageInfo photoInfo) {
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
        ctx.replace(R.id.fragments, panoramioShower);
        ctx.addToBackStack(PHOTO_BACKSTACK);

        ctx.commit();

    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1 == null) {
            return false;
        }

        if (e2 == null) {
            return false;
        }

        float diffx = e2.getX() - e1.getX();
        float diffy = e2.getY() - e1.getY();
        Log.d(CLASS_TAG, "Flinging... diffx: " + diffx + " diffy" + diffy
        + ", velocityx: " + velocityX + ", velocityY: " + velocityY);

        if (Math.abs(diffx) > Math.abs(diffy)) {
            // horizontal moves
            if (Math.abs(diffx) < SWIPE_THRESHOLD) {
                return true;
            }

            if (Math.abs(velocityX) < SWIPE_VELOCITY_THRESHOLD) {
                return true;
            }

            if (diffx > 0) {
                // swipe right
                swipeRight();
            } else {
                // swipe left
                swipeLeft();
            }

        } else {
            // vertical moves

            if (Math.abs(diffy) < SWIPE_THRESHOLD) {
                return true;
            }

            if (Math.abs(velocityY) < SWIPE_VELOCITY_THRESHOLD) {
                return true;
            }

            if (diffy > 0) {
                // swipe down
                swipeDown();
            } else {
                // swipe up
                swipeUp();
            }
        }

        return true;
    }

    private void swipeDown() {

    }

    private void swipeUp() {

    }

    private void swipeLeft() {
        currentFragmentId = (int)Math.max(MIN_FRAGMENT_ID, currentFragmentId-1);
        switchFragment();
    }

    private void switchFragment() {
        switch (currentFragmentId) {
            case HOME_FRAGMENT_ID:
                // switch to home fragment
                Log.d(CLASS_TAG, "Switching to home fragment");
                switchFragment(new HomeFragment());
                break;
            case WIKI_FRAGMENT_ID:
                // switch to wiki fragment
                Log.d(CLASS_TAG, "Switching to wiki fragment");
                switchFragment(new WikiLocationsFragment());
                break;
        }

    }

    private void switchFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ctx = fragmentManager.beginTransaction();
        ctx.replace(R.id.fragments, fragment);
        ctx.addToBackStack(null);
        ctx.commit();
    }

    private void swipeRight() {
        currentFragmentId = (int)Math.min(MAX_FRAGMENT_ID, currentFragmentId+1);
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
                // getSupportFragmentManager().findFragmentById(R.id.wiki_)
                // TextView locationInfo = (TextView) findViewById(R.id.locationInfo);
                // locationInfo.setText("Location: (" + lat + "," + lng + ")");
                Toast.makeText(ctx, "Location: (" + lat + "," + lng + ")", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkForLocalicatonEnabled() {

        locationService = (LocationManager) getSystemService(LOCATION_SERVICE);

        checkLocationSourceAvailability();

        if (!locationEnabled) {
            Intent locationSettingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(locationSettingsIntent, LOCATION_SETTINGS_REQUEST_ID);
            return true;
        }
        return false;
    }

    private void checkLocationSourceAvailability() {
        gpsLocationEnabled = locationService.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkLocationEnabled = locationService.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        locationEnabled = gpsLocationEnabled || networkLocationEnabled;
        if (gpsLocationEnabled) {
            locationProvider = LocationManager.GPS_PROVIDER;
            return;
        }

        if (networkLocationEnabled) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
            return;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(CLASS_TAG, "onResume");
        if (locationProvider != null) {
            locationService.requestLocationUpdates(locationProvider,
                AppConstants.MIN_TIME,
                AppConstants.MIN_DISTANCE,
                locationCallback);
            locationServicesActivated = true;
            Toast.makeText(this, "Location resumed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(CLASS_TAG, "onPause");
        if (locationServicesActivated) {
            locationService.removeUpdates(locationCallback);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(CLASS_TAG, "onDestroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case LOCATION_SETTINGS_REQUEST_ID:
                checkLocationSourceAvailability();
                if (!locationEnabled) {
                    // sadly, nothing to do except from notifing user that program is not enable working
                    Toast.makeText(this, "Sorry location services are not working." +
                            " Program cannot work properly - check location settings to allow program working correctly",
                        Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(CLASS_TAG, "1 Saving current fragment id: " + currentFragmentId);
        super.onSaveInstanceState(outState);
        outState.putSerializable(FRAG_ID, currentFragmentId);
        Log.v(CLASS_TAG, "2 Saving current fragment id: " + currentFragmentId);
    }

}

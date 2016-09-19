package pl.tpolgrabia.urbanexplorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.activities.SettingsActivity;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListener;
import pl.tpolgrabia.urbanexplorer.dto.panoramio.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioShowerFragment;
import pl.tpolgrabia.urbanexplorer.fragments.Refreshable;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorer.handlers.SwipeHandler;
import pl.tpolgrabia.urbanexplorer.utils.HelperUtils;
import pl.tpolgrabia.urbanexplorer.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorer.utils.NetUtils;
import pl.tpolgrabia.urbanexplorer.views.CustomInterceptor;
import pl.tpolgrabia.urbanexplorer.views.SwipeFrameLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private static final Logger lg = LoggerFactory.getLogger(MainActivity.class);

    public static DisplayImageOptions options;
    private GestureDetectorCompat gestureDetector;
    private int currentFragmentId = 0;
    private StandardLocationListener locationCallback;
    private boolean locationServicesActivated = false;
    private GestureDetector.OnGestureListener swipeHandler;
    private PanoramioImageInfo photoInfo;
    private ProgressDialog progressDlg;
    private int oldFragmentId = 0;
    private boolean savedConfiguration;

    private static final Map<Integer, String> fragTags = new HashMap<>();

    static {
        fragTags.put(AppConstants.HOME_FRAGMENT_ID, HomeFragment.TAG);
        fragTags.put(AppConstants.WIKI_FRAGMENT_ID, WikiLocationsFragment.TAG);
    }

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

        HelperUtils.initErrorAndDebugHanlers();
        NetUtils.setGlobalProxyAuth(this);

        currentFragmentId = 0;
        progressDlg = new ProgressDialog(this);
        progressDlg.setCancelable(false);

        // UNIVERSAL IMAGE LOADER SETUP
        HelperUtils.initUniversalDownloader(this);

        swipeHandler = new SwipeHandler(this);
        gestureDetector = new GestureDetectorCompat(this, swipeHandler);
        locationCallback = new StandardLocationListener();

        // init fragments
        Integer fragId = savedInstanceState != null ? savedInstanceState.getInt(AppConstants.FRAG_ID) : null;
        lg.trace("Restored orig frag id:  {}", fragId);
        currentFragmentId = fragId == null ? 0 : fragId;
        lg.trace("Set final frag id: {}", fragId);
        photoInfo = savedInstanceState != null ? (PanoramioImageInfo) savedInstanceState.getSerializable(AppConstants.PHOTO_INFO) : null;
        savedConfiguration = savedInstanceState != null ? savedInstanceState.getBoolean(AppConstants.SAVED_CONFIG_KEY) : false;

        if (HelperUtils.checkForLocalicatonEnabled(this)) return;
        switchFragment();
        updateSwipeHandler();
        HelperUtils.firstTimeNotification(this);
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
                startActivityForResult(intent, AppConstants.SETTINGS_ID_INTENT_REQUEST_ID, new Bundle());
                return true;
            case R.id.refresh:
                progressDlg.setMessage("Refreshing results");
                showProgress();
                refreshFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshFragment() {
        final String tag = fragTags.get(currentFragmentId);
        if (tag == null) {
            lg.warn("Unknown fragment id");
            return;
        }

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            lg.warn("There is no fragment with the given tag");
            return;
        }

        Refreshable refreshable = (Refreshable) fragment;
        refreshable.refresh();

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
        if (!savedConfiguration) {
            ctx.addToBackStack(AppConstants.PHOTO_BACKSTACK);
        }

        ctx.commit();

    }

    private void switchFragment() {

        if (!savedConfiguration) {
            photoInfo = null;
        }

        if (photoInfo != null) {
            switchToPhoto(photoInfo);
            return;
        }

        switch (currentFragmentId) {
            case AppConstants.HOME_FRAGMENT_ID:
                // switch to home fragment
                lg.debug("Switching to home fragment");
                final HomeFragment fragment = new HomeFragment();
                switchFragment(fragment, HomeFragment.TAG);
                break;
            case AppConstants.WIKI_FRAGMENT_ID:
                // switch to wiki fragment
                lg.debug("Switching to wiki fragment");
                switchFragment(new WikiLocationsFragment(), WikiLocationsFragment.TAG);
                break;
        }

        savedConfiguration = false;

    }

    private void switchFragment(Fragment newFragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ctx = fragmentManager.beginTransaction();
        lg.trace("old newFragment id: {}, current newFragment id: {}", oldFragmentId, currentFragmentId);

        HelperUtils.appendEffectToTransition(ctx, oldFragmentId, currentFragmentId);
        HelperUtils.traceAllAvailableFragments(fragmentManager);

        lg.trace("Trying to search newFragment by tag {}", tag);
        Fragment currFragment = fragmentManager.findFragmentByTag(tag);
        if (currFragment == null) {
            lg.trace("Using new newFragment: {}", System.identityHashCode(newFragment));
            ctx.replace(R.id.fragments, newFragment, tag);
        } else {
            lg.trace("Reusing old newFragment: {}", System.identityHashCode(currFragment));
            ctx.replace(R.id.fragments, currFragment);
        }
        // ctx.addToBackStack(MAIN_BACKSTACK);
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
        lg.debug("Swiped left");
        changeCurrentFragId((int)Math.max(AppConstants.MIN_FRAGMENT_ID, currentFragmentId-1));
        switchFragment();
    }

    private void changeCurrentFragId(int nextFragmentId) {
        oldFragmentId = currentFragmentId;
        currentFragmentId = nextFragmentId;
    }

    public void swipeRight() {
        lg.debug("Swiped right");
        changeCurrentFragId((int)Math.min(AppConstants.MAX_FRAGMENT_ID, currentFragmentId+1));
        switchFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lg.trace("onResume");
        String locationProvider = LocationUtils.getDefaultLocation(this);

        if (locationProvider != null) {
            LocationManager locationService = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationService.requestLocationUpdates(locationProvider,
                HelperUtils.fetchGpsUpdateFreq(this),
                HelperUtils.fetchGpsDistanceFreq(this),
                locationCallback);
            locationServicesActivated = true;
        }

        savedConfiguration = false;
        photoInfo = null;
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
            case HelperUtils.LOCATION_SETTINGS_REQUEST_ID:
                refreshFragment();
                break;
            case AppConstants.SETTINGS_ID_INTENT_REQUEST_ID:
                NetUtils.setGlobalProxyAuth(this);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lg.trace("1 Saving current fragment id: {}", currentFragmentId);
        outState.putSerializable(AppConstants.FRAG_ID, currentFragmentId);
        outState.putSerializable(AppConstants.PHOTO_INFO, photoInfo);
        outState.putBoolean(AppConstants.SAVED_CONFIG_KEY, true);
        lg.trace("2 Saving current fragment id: {}", currentFragmentId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lg.trace("onStop {}", System.identityHashCode(this));
    }

    @Override
    protected void onStart() {
        super.onStart();
        lg.trace("onStart {}", System.identityHashCode(this));
    }
}

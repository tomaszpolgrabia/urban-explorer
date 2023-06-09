package pl.tpolgrabia.urbanexplorer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.activities.SettingsActivity;
import pl.tpolgrabia.urbanexplorer.callbacks.StandardLocationListener;
import pl.tpolgrabia.urbanexplorer.dto.MainActivityState;
import pl.tpolgrabia.urbanexplorer.events.RefreshSettingsEvent;
import pl.tpolgrabia.urbanexplorer.fragments.PlacesFragment;
import pl.tpolgrabia.urbanexplorer.fragments.WikiLocationsFragment;
import pl.tpolgrabia.urbanexplorer.handlers.*;
import pl.tpolgrabia.urbanexplorer.utils.HelperUtils;
import pl.tpolgrabia.urbanexplorer.views.CustomInterceptor;
import pl.tpolgrabia.urbanexplorer.views.SwipeFrameLayout;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingStartEvent;
import pl.tpolgrabia.urbanexplorerutils.events.RefreshEvent;
import pl.tpolgrabia.urbanexplorerutils.utils.LocationUtils;
import pl.tpolgrabia.urbanexplorerutils.utils.NetUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity {

    private static final Logger lg = LoggerFactory.getLogger(MainActivity.class);

    public static DisplayImageOptions options;
    public static DisplayImageOptions rectOptions;

    private GestureDetectorCompat gestureDetector;
    private MainActivityState currFrag = MainActivityState.WIKI;
    private StandardLocationListener locationCallback;
    private boolean locationServicesActivated = false;
    private GestureDetector.OnGestureListener swipeHandler;
    private ProgressDialog progressDlg;
    private MainActivityState oldFrag = MainActivityState.GOOGLE_PLACES;
    private boolean savedConfiguration;
    private static final Map<MainActivityState, Runnable> switchFragmentActions = new HashMap<>();

    private static final Map<Integer, String> fragTags = new HashMap<>();

    static {
        fragTags.put(MainActivityState.WIKI.getOrder(), WikiLocationsFragment.TAG);
        fragTags.put(MainActivityState.GOOGLE_PLACES.getOrder(), PlacesFragment.TAG);
    }

    public MainActivity() {
        switchFragmentActions.put(MainActivityState.WIKI, new WikiSwitchHandler(this));
        switchFragmentActions.put(MainActivityState.GOOGLE_PLACES, new GooglePlacesSwitchHandler(this));
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

        EventBus.getDefault().register(this);

        HelperUtils.initErrorAndDebugHanlers(this);
        NetUtils.setGlobalProxyAuth(this);

        currFrag = MainActivityState.WIKI;
        progressDlg = new ProgressDialog(this);
        progressDlg.setCancelable(false);

        // UNIVERSAL IMAGE LOADER SETUP
        HelperUtils.initUniversalDownloader(this);

        swipeHandler = new SwipeHandler(this);
        gestureDetector = new GestureDetectorCompat(this, swipeHandler);
        locationCallback = new StandardLocationListener(this);

        // init fragments
        MainActivityState fragId = savedInstanceState != null
            ? (MainActivityState)savedInstanceState.getSerializable(AppConstants.FRAG_ID)
            : MainActivityState.WIKI;

        lg.trace("Restored orig frag id:  {}", fragId);
        currFrag = fragId == null ? MainActivityState.WIKI : fragId;
        lg.trace("Set final frag id: {}", fragId);
        boolean copySavedConfiguration = savedConfiguration =
            savedInstanceState != null && savedInstanceState.getBoolean(AppConstants.SAVED_CONFIG_KEY);

        switchFragment();
        updateSwipeHandler();
        if (!copySavedConfiguration && HelperUtils.checkForLocalicatonEnabled(this)) return;
    }

    @Override
    public void onBackPressed() {
        lg.debug("Back pressed");

        super.onBackPressed();
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
        final String tag = fragTags.get(currFrag.getOrder());
        if (tag == null) {
            lg.warn("Unknown fragment id");
            hideProgress();
            return;
        }

        final Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            lg.warn("There is no fragment with the given tag");
            hideProgress();
            return;
        }

        EventBus.getDefault().post(new RefreshEvent(this));
    }


    private void switchFragment() {

        if (currFrag == oldFrag) {
            return;
        }

        Runnable switchAction = switchFragmentActions.get(currFrag);
        if (switchAction != null) {
            switchAction.run();
        } else {
            lg.warn("There is no valid switch action to the given fragment {}", currFrag);
        }

        savedConfiguration = false;

    }

    public void switchFragment(Fragment newFragment, String tag) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ctx = fragmentManager.beginTransaction();
        lg.trace("old newFragment id: {}, current newFragment id: {}", oldFrag, currFrag);

        HelperUtils.appendEffectToTransition(ctx, oldFrag, currFrag);
        HelperUtils.traceAllAvailableFragments(fragmentManager);

        lg.trace("Trying to search newFragment by tag {}", tag);
        // Fragment currFragment = fragmentManager.findFragmentByTag(tag);
        final List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment frag : fragments) {
                if (frag == null){
                    continue;
                }
                ctx.remove(frag);
            }
        }
        ctx.add(R.id.fragments, newFragment);
        ctx.commit();
        updateSwipeHandler();
    }

    private void updateSwipeHandler() {
        SwipeFrameLayout swipeFragments = (SwipeFrameLayout) findViewById(R.id.swipe_frag);
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
        changeCurrentFragId(currFrag.prev());
        switchFragment();
    }

    private void changeCurrentFragId(MainActivityState nextFragmentId) {
        if (nextFragmentId == null) {
            oldFrag = currFrag;
            return;
        }
        oldFrag = currFrag;
        currFrag = nextFragmentId;
    }

    public void swipeRight() {
        lg.debug("Swiped right");
        changeCurrentFragId(currFrag.next());
        switchFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        lg.trace("onResume");
        String locationProvider = LocationUtils.getDefaultLocation(this);

        lg.debug("Selected location provider {} is available", locationProvider);

        final Long updateTimeInMilliseconds = HelperUtils.fetchGpsUpdateFreq(this);
        lg.debug("Update time: {}", updateTimeInMilliseconds);
        if (locationProvider != null) {
            lg.debug("Requesting location updates");
            LocationManager locationService = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationService.requestLocationUpdates(locationProvider,
                updateTimeInMilliseconds,
                HelperUtils.fetchGpsDistanceFreq(this),
                locationCallback);
            locationServicesActivated = true;

            final Long lastLocationUpdateTime = LocationUtils.getLastLocationUpdate(this);
            lg.debug("Last location update time: {}", lastLocationUpdateTime);
            final long now = System.currentTimeMillis();
            lg.debug("Now: {}", now);
            final long lastLocationUpdateTimeAgo = now - lastLocationUpdateTime;
            lg.debug("Last location update was {} ms ago", lastLocationUpdateTimeAgo);
            if (lastLocationUpdateTime < 0 || lastLocationUpdateTimeAgo >= updateTimeInMilliseconds) {
                lg.info("Last location update time exceeded. Requesting single update...");
                locationService.requestSingleUpdate(locationProvider, locationCallback, Looper.getMainLooper());
            }
        }

        savedConfiguration = false;
        HelperUtils.firstTimeNotification(this);
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
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case HelperUtils.LOCATION_SETTINGS_REQUEST_ID:
                refreshFragment();
                break;
            case AppConstants.SETTINGS_ID_INTENT_REQUEST_ID:
                NetUtils.setGlobalProxyAuth(this);
                refreshAppSettings();
                refreshFragment();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void refreshAppSettings() {
        EventBus.getDefault().post(new RefreshSettingsEvent(this));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        lg.trace("1 Saving current fragment id: {}", currFrag);
        outState.putSerializable(AppConstants.FRAG_ID, currFrag);
        outState.putBoolean(AppConstants.SAVED_CONFIG_KEY, true);
        lg.trace("2 Saving current fragment id: {}", currFrag);
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

    @Subscribe
    public void handleLoadingStart(DataLoadingStartEvent event) {
        progressDlg.show();
    }

    @Subscribe
    public void handleLoadingFinish(DataLoadingFinishEvent event) {
        progressDlg.dismiss();
    }

    public void addFragment(Fragment fragment, String tag) {
        FragmentManager fragMgr = getSupportFragmentManager();
        FragmentTransaction tx = fragMgr.beginTransaction();
        tx.add(R.id.fragments, fragment, tag);
        tx.commit();
    }

}

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
import pl.tpolgrabia.panoramiobindings.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.events.RefreshSettingsEvent;
import pl.tpolgrabia.urbanexplorer.fragments.*;
import pl.tpolgrabia.urbanexplorer.handlers.*;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingFinishEvent;
import pl.tpolgrabia.urbanexplorerutils.events.DataLoadingStartEvent;
import pl.tpolgrabia.urbanexplorer.utils.HelperUtils;
import pl.tpolgrabia.urbanexplorer.views.CustomInterceptor;
import pl.tpolgrabia.urbanexplorer.views.SwipeFrameLayout;
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
    private MainActivityState currFrag = MainActivityState.PANORAMIO;
    private StandardLocationListener locationCallback;
    private boolean locationServicesActivated = false;
    private GestureDetector.OnGestureListener swipeHandler;
    private PanoramioImageInfo photoInfo;
    private ProgressDialog progressDlg;
    private MainActivityState oldFrag = MainActivityState.PANORAMIO_SHOWER;
    private boolean savedConfiguration;
    private static final Map<MainActivityState, Runnable> switchFragmentActions = new HashMap<>();

    private static final Map<Integer, String> fragTags = new HashMap<>();

    static {
        fragTags.put(MainActivityState.PANORAMIO.getOrder(), HomeFragment.TAG);
        fragTags.put(MainActivityState.WIKI.getOrder(), WikiLocationsFragment.TAG);
        fragTags.put(MainActivityState.GOOGLE_PLACES.getOrder(), PlacesFragment.TAG);
    }

    public MainActivity() {
        switchFragmentActions.put(MainActivityState.PANORAMIO_SHOWER, new PanoramioShowerSwitchHandler(this));
        switchFragmentActions.put(MainActivityState.PANORAMIO, new PanoramioSwitchHandler(this));
        switchFragmentActions.put(MainActivityState.WIKI, new WikiSwitchHandler(this));
        switchFragmentActions.put(MainActivityState.GOOGLE_PLACES, new GooglePlacesSwitchHandler(this));
    }

    private List<PanoramioImageInfo> photos;

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

        currFrag = MainActivityState.PANORAMIO;
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
            : MainActivityState.PANORAMIO;

        lg.trace("Restored orig frag id:  {}", fragId);
        currFrag = fragId == null ? MainActivityState.PANORAMIO : fragId;
        lg.trace("Set final frag id: {}", fragId);
        photoInfo = savedInstanceState != null ? (PanoramioImageInfo) savedInstanceState.getSerializable(AppConstants.PHOTO_INFO) : null;
        boolean copySavedConfiguration = savedConfiguration =
            savedInstanceState != null && savedInstanceState.getBoolean(AppConstants.SAVED_CONFIG_KEY);

        switchFragment();
        updateSwipeHandler();
        if (!copySavedConfiguration && HelperUtils.checkForLocalicatonEnabled(this)) return;
    }

    @Override
    public void onBackPressed() {
        lg.debug("Back pressed");

        switch(currFrag) {
            case PANORAMIO_SHOWER:
                photoInfo = null;
                currFrag = MainActivityState.PANORAMIO;
                break;
        }

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

    public void resetPhotoInfo() {
        this.photoInfo = null;
    }

    public void switchToPhoto(PanoramioImageInfo photoInfo) {
        this.photoInfo = photoInfo;
        this.currFrag = MainActivityState.PANORAMIO_SHOWER;
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

        if (currFrag == oldFrag) {
            return;
        }

        if (!savedConfiguration) {
            photoInfo = null;
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
        outState.putSerializable(AppConstants.PHOTO_INFO, photoInfo);
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

    public void setPhotos(List<PanoramioImageInfo> photos) {
        this.photos = photos;
    }

    public PanoramioImageInfo getPhotoInfo() {
        return photoInfo;
    }

    @Subscribe
    public void handleLoadingStart(DataLoadingStartEvent event) {
        progressDlg.show();
    }

    @Subscribe
    public void handleLoadingFinish(DataLoadingFinishEvent event) {
        progressDlg.dismiss();
    }

}

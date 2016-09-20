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
import pl.tpolgrabia.urbanexplorer.dto.MainActivityState;
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
    private MainActivityState currFrag = MainActivityState.PANORAMIO;
    private StandardLocationListener locationCallback;
    private boolean locationServicesActivated = false;
    private GestureDetector.OnGestureListener swipeHandler;
    private PanoramioImageInfo photoInfo;
    private ProgressDialog progressDlg;
    private MainActivityState oldFrag = MainActivityState.PANORAMIO_SHOWER;
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

        HelperUtils.initErrorAndDebugHanlers(this);
        NetUtils.setGlobalProxyAuth(this);

        currFrag = MainActivityState.PANORAMIO;
        progressDlg = new ProgressDialog(this);
        progressDlg.setCancelable(false);

        // UNIVERSAL IMAGE LOADER SETUP
        HelperUtils.initUniversalDownloader(this);

        swipeHandler = new SwipeHandler(this);
        gestureDetector = new GestureDetectorCompat(this, swipeHandler);
        locationCallback = new StandardLocationListener();

        // init fragments
        MainActivityState fragId = savedInstanceState != null
            ? (MainActivityState)savedInstanceState.getSerializable(AppConstants.FRAG_ID)
            : MainActivityState.PANORAMIO;

        lg.trace("Restored orig frag id:  {}", fragId);
        currFrag = fragId == null ? MainActivityState.PANORAMIO : fragId;
        lg.trace("Set final frag id: {}", fragId);
        photoInfo = savedInstanceState != null ? (PanoramioImageInfo) savedInstanceState.getSerializable(AppConstants.PHOTO_INFO) : null;
        savedConfiguration = savedInstanceState != null && savedInstanceState.getBoolean(AppConstants.SAVED_CONFIG_KEY);

        switchFragment();
        updateSwipeHandler();
        if (HelperUtils.checkForLocalicatonEnabled(this)) return;
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

        Refreshable refreshable = (Refreshable) fragment;
        refreshable.refresh();

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

        switch (currFrag) {
            case PANORAMIO_SHOWER:
                lg.debug("Switching to panoramio shower");
                switchToPhoto(photoInfo);
                break;
            case PANORAMIO:
                // switch to home fragment
                lg.debug("Switching to home fragment");
                switchFragment(new HomeFragment(), HomeFragment.TAG);
                break;
            case WIKI:
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

        if (locationProvider != null) {
            LocationManager locationService = (LocationManager)getSystemService(LOCATION_SERVICE);
            locationService.requestLocationUpdates(locationProvider,
                HelperUtils.fetchGpsUpdateFreq(this),
                HelperUtils.fetchGpsDistanceFreq(this),
                locationCallback);
            locationServicesActivated = true;
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
}

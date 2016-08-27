package pl.tpolgrabia.urbanexplorer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import pl.tpolgrabia.urbanexplorer.dto.PanoramioImageInfo;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;
import pl.tpolgrabia.urbanexplorer.fragments.PanoramioShowerFragment;

public class MainActivity extends ActionBarActivity  {

    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
    private static final String PHOTO_BACKSTACK = "PHOTO_BACKSTACK";
    public static DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.navbar);
//        setSupportActionBar(toolbar);

        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
            .cacheOnDisc(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new FadeInBitmapDisplayer(300)).build();

        options = defaultOptions;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
            this)
            .defaultDisplayImageOptions(defaultOptions)
            .memoryCache(new WeakMemoryCache())
            .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);

        getSupportFragmentManager()
            .beginTransaction()
            .add(R.id.fragments, new HomeFragment())
            .commit();

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
        //HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.home_frag);
        FragmentTransaction ctx = fragmentManager.beginTransaction();
//        ctx.remove(homeFragment);

        // TODO add inserting photo showing fragment

        PanoramioShowerFragment panoramioShower = new PanoramioShowerFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(PanoramioShowerFragment.PANORAMIO_PHOTO_ARG_KEY, photoInfo);
        panoramioShower.setArguments(arguments);

        // ctx.add(R.id.fragments, panoramioShower);
        ctx.replace(R.id.fragments, panoramioShower);
        ctx.addToBackStack(PHOTO_BACKSTACK);

        ctx.commit();

    }
}

package pl.tpolgrabia.urbanexplorer;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

public class MainActivity extends ActionBarActivity  {

    private static final int LOCATION_SETTINGS_REQUEST_ID = 1;
    private static final String CLASS_TAG = MainActivity.class.getSimpleName();
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
}

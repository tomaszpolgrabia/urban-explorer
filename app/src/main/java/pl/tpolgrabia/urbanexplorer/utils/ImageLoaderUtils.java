package pl.tpolgrabia.urbanexplorer.utils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class ImageLoaderUtils {
    public static DisplayImageOptions createRoundedOptions() {
        return new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new CircleBitmapDisplayer()).build();
    }

    public static DisplayImageOptions createRectangularOptions() {
        return new DisplayImageOptions.Builder()
            .cacheOnDisc(true).cacheInMemory(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .displayer(new FadeInBitmapDisplayer(300)).build();

    }
}

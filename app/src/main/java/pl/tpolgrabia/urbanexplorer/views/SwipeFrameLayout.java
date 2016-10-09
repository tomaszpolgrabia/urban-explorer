package pl.tpolgrabia.urbanexplorer.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by tpolgrabia on 28.08.16.
 */
public class SwipeFrameLayout extends FrameLayout {
    private CustomInterceptor customInterceptor;

    public SwipeFrameLayout(Context context) {
        super(context);
    }

    public SwipeFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (customInterceptor != null) {
            customInterceptor.handle(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setCustomInterceptor(CustomInterceptor customInterceptor) {
        this.customInterceptor = customInterceptor;
    }
}

package pl.tpolgrabia.urbanexplorer.handlers;

import android.view.GestureDetector;
import android.view.MotionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.MainActivity;

/**
 * Created by tpolgrabia on 13.09.16.
 */
public class SwipeHandler implements GestureDetector.OnGestureListener {
    private static final Logger lg = LoggerFactory.getLogger(SwipeHandler.class);
    private static final String CLASS_TAG = SwipeHandler.class.getSimpleName();
    private final MainActivity activity;
    private static final float SWIPE_THRESHOLD = 50;
    private static final float SWIPE_VELOCITY_THRESHOLD = 10;

    public SwipeHandler(MainActivity activity) {
        this.activity = activity;
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
        lg.debug(CLASS_TAG, "Flinging... diffx: {} diffy: {}, velocityx: {}, velocityY: {}",
            diffx, diffy, velocityX, velocityY);

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
                activity.swipeRight();
            } else {
                // swipe left
                activity.swipeLeft();
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
            } else {
                // swipe up
            }
        }

        return true;
    }

}

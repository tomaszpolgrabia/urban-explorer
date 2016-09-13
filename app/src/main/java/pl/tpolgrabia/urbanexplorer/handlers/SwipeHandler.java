package pl.tpolgrabia.urbanexplorer.handlers;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import pl.tpolgrabia.urbanexplorer.MainActivity;

/**
 * Created by tpolgrabia on 13.09.16.
 */
public class SwipeHandler implements GestureDetector.OnGestureListener {
    private static final String CLASS_TAG = SwipeHandler.class.getSimpleName();
    private final MainActivity activity;
    private static final float SWIPE_THRESHOLD = 50;
    private static final float SWIPE_VELOCITY_THRESHOLD = 20;

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

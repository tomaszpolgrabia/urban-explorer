package pl.tpolgrabia.urbanexplorer.workers;

import android.os.AsyncTask;
import pl.tpolgrabia.urbanexplorer.fragments.HomeFragment;

/**
 * Created by tpolgrabia on 11.09.16.
 */
public class FetchingPhotosWorker extends AsyncTask<Boolean, Integer, Boolean> {

    private HomeFragment homeFragment;

    public FetchingPhotosWorker(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
    }

    @Override
    protected Boolean doInBackground(Boolean... params) {
        for (Boolean arg : params) {

        }

        return null;
    }
}

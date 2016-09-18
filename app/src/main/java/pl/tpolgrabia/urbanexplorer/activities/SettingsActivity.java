package pl.tpolgrabia.urbanexplorer.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.fragments.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    private static final Logger lg = LoggerFactory.getLogger(SettingsActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        setTitle("Urban explorer settings");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
            .replace(R.id.settings_fragments, new SettingsFragment())
            .commit();

        lg.trace("onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
        lg.trace("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();

        lg.trace("onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lg.trace("onDestroy");
    }
}

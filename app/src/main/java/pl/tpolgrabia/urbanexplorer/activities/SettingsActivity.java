package pl.tpolgrabia.urbanexplorer.activities;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import pl.tpolgrabia.urbanexplorer.R;
import pl.tpolgrabia.urbanexplorer.fragments.SettingsFragment;

public class SettingsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        setTitle("Urban explorer settings");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
            .replace(R.id.settings_fragments, new SettingsFragment())
            .commit();
    }
}

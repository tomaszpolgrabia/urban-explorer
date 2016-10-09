package pl.tpolgrabia.urbanexplorer.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.tpolgrabia.urbanexplorer.R;

public class SettingsFragment extends PreferenceFragment {

    private Logger lg = LoggerFactory.getLogger(SettingsFragment.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.urban_expl_settings);
        getPreferenceScreen().setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                // TODO handle changing preference
                lg.info("Preference {} has changed its value to {}",
                    preference,
                    newValue);
                return true;
            }
        });
    }
}

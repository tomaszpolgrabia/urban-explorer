package pl.tpolgrabia.urbanexplorer.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import pl.tpolgrabia.urbanexplorer.R;

public class SettingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}

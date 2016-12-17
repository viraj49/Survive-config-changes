package tank.viraj.config.change.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import tank.viraj.config.change.R;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangeActivity extends AppCompatActivity {
    private static final String FLAG_COMMIT_FRAGMENT = "configChangeFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_change_activity);
        setActionBarTitle();

        /* create/find the fragment and load it in frame layout */
        ConfigChangeFragment configChangeFragment = (ConfigChangeFragment) getSupportFragmentManager().findFragmentByTag(FLAG_COMMIT_FRAGMENT);
        if (configChangeFragment == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.config_change_frame, new ConfigChangeFragment(), FLAG_COMMIT_FRAGMENT)
                    .commit();
        }
    }

    private void setActionBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getResources().getString(R.string.app_name));
        }
    }
}
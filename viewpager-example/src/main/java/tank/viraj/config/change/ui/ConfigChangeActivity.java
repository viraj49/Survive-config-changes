package tank.viraj.config.change.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;

import butterknife.BindView;
import butterknife.ButterKnife;
import tank.viraj.config.change.R;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangeActivity extends AppCompatActivity {

    @BindView(R.id.config_change_view_pager)
    ViewPager viewPager;

    @BindView(R.id.config_change_tabs)
    PagerTitleStrip tabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_change_activity);
        ButterKnife.bind(this);

        setActionBarTitle();

        tabs.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    private void setActionBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getResources().getString(R.string.app_name));
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] TITLES = {"Fragment 1", "Fragment 2", "Fragment 3"};

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ConfigChangeFragment();
                case 1:
                    return new ConfigChangeFragment();
                case 2:
                    return new ConfigChangeFragment();
            }
            return null;
        }
    }
}
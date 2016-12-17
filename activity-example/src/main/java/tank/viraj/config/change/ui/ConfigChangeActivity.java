package tank.viraj.config.change.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tank.viraj.config.change.R;
import tank.viraj.config.change.base.BasePresenterActivity;
import tank.viraj.config.change.base.PresenterFactory;
import tank.viraj.config.change.presenter.ConfigChangePresenter;
import tank.viraj.config.change.presenter.ConfigChangePresenterFactory;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangeActivity extends BasePresenterActivity<ConfigChangePresenter, ConfigChangeActivity>
        implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout pullToRefreshLayout;

    @BindView(R.id.data_message)
    TextView dataMessage;

    @BindView(R.id.error_message)
    TextView errorMessage;

    private Unbinder unbinder;
    private ConfigChangePresenter configChangePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_change_activity);

        unbinder = ButterKnife.bind(this, this);

        setActionBarTitle();
        setUpPullToRefresh();
    }

    @NonNull
    @Override
    protected PresenterFactory<ConfigChangePresenter> getPresenterFactory() {
        return new ConfigChangePresenterFactory();
    }

    @Override
    protected void onPresenterPrepared(@NonNull ConfigChangePresenter configChangePresenter) {
        this.configChangePresenter = configChangePresenter;
    }

    public void setViewData(String data) {
        dataMessage.setVisibility(VISIBLE);
        errorMessage.setVisibility(GONE);

        dataMessage.setText(data);
    }

    public void showError() {
        dataMessage.setVisibility(GONE);
        errorMessage.setVisibility(VISIBLE);
    }

    private void setActionBarTitle() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setTitle(getResources().getString(R.string.app_name));
        }
    }

    /* PullToRefresh */
    private void setUpPullToRefresh() {
        pullToRefreshLayout.setOnRefreshListener(this);
        pullToRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        pullToRefreshLayout.canChildScrollUp();
    }

    public void startRefreshAnimation() {
        pullToRefreshLayout.post(() -> pullToRefreshLayout.setRefreshing(true));
    }

    public void stopRefreshAnimation() {
        pullToRefreshLayout.post(() -> pullToRefreshLayout.setRefreshing(false));
    }

    @Override
    public void onRefresh() {
        /* load fresh data, when pullToRefresh is called */
        configChangePresenter.getDataForView();
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }
}
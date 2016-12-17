package tank.viraj.config.change.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import tank.viraj.config.change.R;
import tank.viraj.config.change.dataSource.ConfigChangeDataSource;
import tank.viraj.config.change.presenter.ConfigChangePresenter;
import tank.viraj.config.change.util.InternetConnection;
import tank.viraj.config.change.util.RxSchedulerConfiguration;
import tank.viraj.config.change.util.ViewState;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.refresh_view)
    SwipeRefreshLayout pullToRefreshLayout;

    @BindView(R.id.data_message)
    TextView dataMessage;

    @BindView(R.id.error_message)
    TextView errorMessage;

    private Unbinder unbinder;
    private ConfigChangePresenter configChangePresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        ConfigChangeDataSource configChangeDataSource = new ConfigChangeDataSource();
        RxSchedulerConfiguration rxSchedulerConfiguration = new RxSchedulerConfiguration();
        InternetConnection internetConnection = new InternetConnection();
        ViewState viewState = new ViewState();

        configChangePresenter = new ConfigChangePresenter(configChangeDataSource, rxSchedulerConfiguration, internetConnection, viewState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.config_change_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        unbinder = ButterKnife.bind(this, view);

        setUpPullToRefresh();

        /* bind the view and load data from Realm or Retrofit2 */
        configChangePresenter.bind(this);
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

    public void showSnackBar() {
        Snackbar.make(pullToRefreshLayout, "Error loading data!", Snackbar.LENGTH_LONG)
                .setAction("RETRY", view -> {
                    startRefreshAnimation();
                    configChangePresenter.getDataForView();
                }).show();
    }

    /* PullToRefresh */
    private void setUpPullToRefresh() {
        pullToRefreshLayout.setOnRefreshListener(this);
        pullToRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        pullToRefreshLayout.canChildScrollUp();
    }

    public void startRefreshAnimation() {
        if (pullToRefreshLayout != null) {
            pullToRefreshLayout.post(() -> pullToRefreshLayout.setRefreshing(true));
        }
    }

    public void stopRefreshAnimation() {
        if (pullToRefreshLayout != null) {
            pullToRefreshLayout.post(() -> pullToRefreshLayout.setRefreshing(false));
        }
    }

    @Override
    public void onRefresh() {
        /* load fresh data, when pullToRefresh is called */
        configChangePresenter.getDataForView();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        configChangePresenter.unBind();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        configChangePresenter.unSubscribe();
        super.onDestroy();
    }
}
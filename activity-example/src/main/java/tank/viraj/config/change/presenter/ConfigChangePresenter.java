package tank.viraj.config.change.presenter;

import io.reactivex.disposables.Disposable;
import tank.viraj.config.change.base.Presenter;
import tank.viraj.config.change.dataSource.ConfigChangeDataSource;
import tank.viraj.config.change.ui.ConfigChangeActivity;
import tank.viraj.config.change.util.RxSchedulerConfiguration;
import tank.viraj.config.change.util.ViewState;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangePresenter implements Presenter<ConfigChangeActivity> {
    private ConfigChangeActivity view;
    private ConfigChangeDataSource configChangeDataSource;
    private RxSchedulerConfiguration rxSchedulerConfiguration;
    private ViewState viewState;
    private Disposable viewSubscription;

    ConfigChangePresenter(ConfigChangeDataSource configChangeDataSource,
                          RxSchedulerConfiguration rxSchedulerConfiguration,
                          ViewState viewState) {
        this.configChangeDataSource = configChangeDataSource;
        this.rxSchedulerConfiguration = rxSchedulerConfiguration;
        this.viewState = viewState;
    }

    /* Bind View, Set VIEW & DATA subscriptions */
    @Override
    public void onViewAttached(ConfigChangeActivity configChangeActivity) {
        view = configChangeActivity;

        if (!viewState.isViewLoadedAtLeastOnceWithValidData() ||
                configChangeDataSource.getDataLoadingState().isLoadingData()) {
            view.startRefreshAnimation();
        }

        setUpViewSubscription();

        if (!viewState.isViewLoadedAtLeastOnceWithValidData() &&
                !configChangeDataSource.getDataLoadingState().isLoadingData()) {
            getDataForView();
        }
    }

    /* UnBind View, Dispose VIEW subscription */
    @Override
    public void onViewDetached() {
        if (viewSubscription != null && !viewSubscription.isDisposed()) {
            viewSubscription.dispose();
        }
    }

    /* Dispose DATA subscription */
    /* isChangingConfigurations returns true if Activity is restarting because of config changes */
    @Override
    public void onDestroyed() {
        if (view != null) {
            if (!view.isChangingConfigurations()) {
                configChangeDataSource.unSubscribeDataSubscription();
            }
        } else {
            configChangeDataSource.unSubscribeDataSubscription();
        }
    }

    public void getDataForView() {
        configChangeDataSource.getDataForView();
    }

    private void setUpViewSubscription() {
        if (viewSubscription == null || viewSubscription.isDisposed()) {
            viewSubscription = configChangeDataSource.getDataSubscription()
                    .subscribeOn(rxSchedulerConfiguration.getComputationThread())
                    .observeOn(rxSchedulerConfiguration.getMainThread())
                    .subscribe(dataString -> {
                        if (dataString.length() <= 0) {
                            if (!viewState.isViewLoadedAtLeastOnceWithValidData()) {
                                view.showError();
                            }
                        } else {
                            view.setViewData(dataString);
                            viewState.setViewLoadedAtLeastOnceWithValidData(true);
                        }

                        if (!configChangeDataSource.getDataLoadingState().isLoadingData()) {
                            view.stopRefreshAnimation();
                        }
                    }, error -> {
                        if (!configChangeDataSource.getDataLoadingState().isLoadingData()) {
                            view.stopRefreshAnimation();
                        }
                    });
        }
    }
}
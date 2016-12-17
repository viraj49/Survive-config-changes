package tank.viraj.config.change.presenter;

import io.reactivex.disposables.Disposable;
import tank.viraj.config.change.dataSource.ConfigChangeDataSource;
import tank.viraj.config.change.ui.ConfigChangeFragment;
import tank.viraj.config.change.util.InternetConnection;
import tank.viraj.config.change.util.RxSchedulerConfiguration;
import tank.viraj.config.change.util.ViewState;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangePresenter {
    private ConfigChangeFragment view;
    private ConfigChangeDataSource configChangeDataSource;
    private RxSchedulerConfiguration rxSchedulerConfiguration;
    private ViewState viewState;
    private InternetConnection internetConnection;
    private Disposable viewSubscription;
    private Disposable internetStatusSubscription;

    public ConfigChangePresenter(ConfigChangeDataSource configChangeDataSource,
                                 RxSchedulerConfiguration rxSchedulerConfiguration,
                                 InternetConnection internetConnection,
                                 ViewState viewState) {
        this.configChangeDataSource = configChangeDataSource;
        this.rxSchedulerConfiguration = rxSchedulerConfiguration;
        this.internetConnection = internetConnection;
        this.viewState = viewState;
    }

    /* Bind View, Set VIEW & DATA subscriptions */
    public void bind(ConfigChangeFragment configChangeFragment) {
        view = configChangeFragment;

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
    public void unBind() {
        if (viewSubscription != null && !viewSubscription.isDisposed()) {
            viewSubscription.dispose();
        }
    }

    /* Dispose DATA subscription */
    /* isChangingConfigurations returns true if Activity is restarting because of config changes */
    public void unSubscribe() {
        stopWaitForInternetToComeBack();
        configChangeDataSource.unSubscribeDataSubscription();
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
                                waitForInternetToComeBack();
                            }

                            view.showSnackBar();
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

    private void waitForInternetToComeBack() {
        if (internetConnection.isInternetOn()) {
            return;
        }

        if (internetStatusSubscription == null || internetStatusSubscription.isDisposed()) {
            internetStatusSubscription = internetConnection.getInternetStatusHotObservable()
                    .filter(internetConnectionStatus -> internetConnectionStatus)
                    .subscribeOn(rxSchedulerConfiguration.getComputationThread())
                    .observeOn(rxSchedulerConfiguration.getComputationThread())
                    .subscribe(internetConnectionStatus -> {
                        view.startRefreshAnimation();
                        setUpViewSubscription();
                        getDataForView();
                        stopWaitForInternetToComeBack();
                    });
        }

        internetConnection.registerBroadCastReceiver(view.getActivity().getApplicationContext());
    }

    private void stopWaitForInternetToComeBack() {
        if (internetStatusSubscription != null && !internetStatusSubscription.isDisposed()) {
            internetStatusSubscription.dispose();
            internetConnection.unRegisterBroadCastReceiver(view.getActivity().getApplicationContext());
        }
    }
}
package tank.viraj.config.change.presenter;


import tank.viraj.config.change.base.PresenterFactory;
import tank.viraj.config.change.dataSource.ConfigChangeDataSource;
import tank.viraj.config.change.util.RxSchedulerConfiguration;
import tank.viraj.config.change.util.ViewState;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangePresenterFactory implements PresenterFactory<ConfigChangePresenter> {

    private ConfigChangeDataSource configChangeDataSource;
    private RxSchedulerConfiguration rxSchedulerConfiguration;
    private ViewState viewState;

    public ConfigChangePresenterFactory() {
        configChangeDataSource = new ConfigChangeDataSource();
        rxSchedulerConfiguration = new RxSchedulerConfiguration();
        viewState = new ViewState();
    }

    @Override
    public ConfigChangePresenter create() {
        return new ConfigChangePresenter(configChangeDataSource, rxSchedulerConfiguration, viewState);
    }
}

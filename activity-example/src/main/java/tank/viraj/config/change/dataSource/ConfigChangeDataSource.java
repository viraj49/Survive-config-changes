package tank.viraj.config.change.dataSource;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import lombok.Getter;
import tank.viraj.config.change.util.DataLoadingState;
import tank.viraj.config.change.util.RxSchedulerConfiguration;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class ConfigChangeDataSource {

    @Getter
    private DataLoadingState dataLoadingState;

    private BehaviorSubject<String> dataSubject;
    private Disposable dataSubscription;
    private RxSchedulerConfiguration rxSchedulerConfiguration;
    private int numberOfNetworkCallsMade;

    public ConfigChangeDataSource() {
        rxSchedulerConfiguration = new RxSchedulerConfiguration();
        dataSubject = BehaviorSubject.create();
        dataLoadingState = new DataLoadingState();
        numberOfNetworkCallsMade = 0;
    }

    public Observable<String> getDataSubscription() {
        return dataSubject.serialize();
    }

    public void getDataForView() {
        if (dataLoadingState.isLoadingData()) {
            return;
        }

        dataLoadingState.setLoadingData(true);
        if (dataSubscription != null && !dataSubscription.isDisposed()) {
            dataSubscription.dispose();
        }

        dataSubscription = getNetworkObservable()
                .filter(dataString -> dataString != null && dataString.length() > 0)
                .first(getDefaultResponse())
                .subscribeOn(rxSchedulerConfiguration.getComputationThread())
                .observeOn(rxSchedulerConfiguration.getComputationThread())
                .subscribe(dataString -> {
                            dataLoadingState.setLoadingData(false);
                            dataSubject.onNext(dataString);
                        },
                        error -> {
                            dataLoadingState.setLoadingData(false);
                            dataSubject.onNext("");
                        }
                );
    }

    /**
     * A fake network call
     * Increments number of network call counter
     * Returns the results with DELAY of 2 seconds
     */
    private Observable<String> getNetworkObservable() {
        return Observable.fromCallable(this::getDataFromFakeNetwork)
                .delay(2, TimeUnit.SECONDS);
    }

    private String getDataFromFakeNetwork() {
        return "Number of Network Calls Made = " + (++numberOfNetworkCallsMade);
    }

    private String getDefaultResponse() {
        return "";
    }

    public void unSubscribeDataSubscription() {
        if (dataSubscription != null && !dataSubscription.isDisposed()) {
            dataSubscription.dispose();
        }
    }
}
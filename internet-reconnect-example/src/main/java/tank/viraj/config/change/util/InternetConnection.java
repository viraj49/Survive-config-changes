package tank.viraj.config.change.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Viraj Tank, 01-12-2016.
 */
public class InternetConnection {
    private BroadcastReceiver broadcastReceiver;
    private PublishSubject<Boolean> internetStatusHotObservable;
    private final int maxRetryLimit = 5;
    private int delayBetweenRetry = 100;
    private int currentRepeatCount = 1;

    public InternetConnection() {
    }

    private Observable<Boolean> isInternetOnObservable() {
        return Observable.fromCallable(this::isInternetOn);
    }

    public boolean isInternetOn() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public Observable<Boolean> getInternetStatusHotObservable() {
        internetStatusHotObservable = PublishSubject.create();
        return internetStatusHotObservable.serialize();
    }

    /* Register for Internet connection change broadcast receiver */
    public void registerBroadCastReceiver(Context context) {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Bundle extras = intent.getExtras();
                NetworkInfo info = extras.getParcelable("networkInfo");
                assert info != null;
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    checkForWorkingInternetConnection();
                } else {
                    internetStatusHotObservable.onNext(isInternetOn());
                }
            }
        };

        context.registerReceiver(broadcastReceiver, filter);
    }

    /* It takes a few milliseconds, from the connection is on
     * to we get an active working internet connection. */
    private void checkForWorkingInternetConnection() {
        currentRepeatCount = 1;
        delayBetweenRetry = 100;

        isInternetOnObservable()
                .repeatWhen(objectObservable -> {
                    if (currentRepeatCount >= maxRetryLimit) {
                        return Observable.empty();
                    }
                    currentRepeatCount++;
                    delayBetweenRetry += 300;
                    return Observable.timer(delayBetweenRetry, TimeUnit.MILLISECONDS);
                })
                .filter(connectionStatus -> connectionStatus)
                .subscribe(connectionStatus -> {
                    currentRepeatCount = maxRetryLimit;
                    internetStatusHotObservable.onNext(isInternetOn());
                });
    }

    /* unRegister for Internet connection change broadcast receiver */
    public void unRegisterBroadCastReceiver(Context context) {
        assert broadcastReceiver != null;
        context.unregisterReceiver(broadcastReceiver);
    }
}
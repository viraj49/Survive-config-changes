# Survive-config-changes
Android MVP that survives **life cycle**, **configuration** &amp; **internet connectivity** changes.

A detailed description is availabe on following Medium post series:</br>
Part1 - https://medium.com/@Viraj.Tank/art-of-subscribe-unsubscribe-in-rxjava-12c30d315777</br>
Part2 - https://medium.com/@Viraj.Tank/android-mvp-that-survives-view-life-cycle-configuration-internet-changes-part-2-6b1e2b5c5294

## activity-example
Survive life cycle and config changes when using **Activity as VIEW**.</br>

Presenter survives using **Loader**.

Subscribe VIEW and DATA subscription on ```onAttachedToWindow```. </br>
Unsubscribe VIEW subscription on ```onDetachedFromWindow```. </br>
Unsubscribe DATA subscription on ```onDestroy``` + ```if(!activity.isChangingConfigurations())```.

## fragment-example
Survive life cycle and config changes when using **Fragment as VIEW**.

Presenter survives using **setRetainInstance(true)**.

Subscribe VIEW and DATA subscription on ```onViewCreated``` </br>
Unsubscribe VIEW subscription on ```onDestroyView``` </br>
Unsubscribe DATA subscription on ```onDestroy```

## viewpager-example
Survive life cycle and config changes when using **Fragment+ViewPager as VIEW**.

Presenter survives using **setRetainInstance(true)**.

Subscribe VIEW and DATA subscription on ```onViewCreated``` </br>
Unsubscribe VIEW subscription on ```onDestroyView``` </br>
Unsubscribe DATA subscription on ```onDestroy```

## internet-reconnect-example
This example show cases, how to reconnect with internet connection.

Subscribe Connectivity Broadcast receiver on ``BIND``.</br>
Unsubscribe Connectivity Broadcast receiver on ``onDestroy`` or ``UNBIND``.

## Auto Reconnect Example
![Auto Reconnect GIF](https://github.com/viraj49/Survive-config-changes/blob/master/auto-reconnect.gif)

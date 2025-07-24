package com.ganeshji.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.ads.MobileAds;
import com.ganeshji.app.utils.AppOpenAdManager;   // adjust if you placed the manager elsewhere

import java.lang.ref.WeakReference;

/**
 * Custom Application that:
 *  1) initializes Google Mobile Ads,
 *  2) pre‑loads an App‑Open Ad,
 *  3) shows that ad whenever the whole app returns to foreground,
 *  4) keeps track of the currently visible Activity.
 */
public class MyApp extends Application implements LifecycleObserver {

    /** Weak reference so we don’t leak the Activity. */
    private WeakReference<Activity> currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        /* 1. Initialise Google Mobile Ads */
        MobileAds.initialize(this);

        /* 2. Pre‑load the first App‑Open Ad ASAP */
        AppOpenAdManager.loadAd(this);

        /* 3. Listen for process‑level lifecycle events (foreground/background) */
        ProcessLifecycleOwner.get()
                .getLifecycle()
                .addObserver(this);

        /* 4. Watch every Activity to know which one is on screen */
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override public void onActivityResumed(Activity activity) {
                currentActivity = new WeakReference<>(activity);
            }
            @Override public void onActivityPaused(Activity activity) { }
            @Override public void onActivityCreated(Activity activity, Bundle bundle) { }
            @Override public void onActivityStarted(Activity activity) { }
            @Override public void onActivityStopped(Activity activity) { }
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle bundle) { }
            @Override public void onActivityDestroyed(Activity activity) { }
        });
    }

    /** Runs every time the whole app enters foreground (from background). */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onMoveToForeground() {
        Activity activity = currentActivity != null ? currentActivity.get() : null;

        if (activity != null) {
            AppOpenAdManager.showIfAvailable(activity);
        } else {
            // No activity ready yet (very cold start) – just ensure an ad is loading.
            AppOpenAdManager.loadAd(this);
        }
    }
}

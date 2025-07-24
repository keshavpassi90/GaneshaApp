package com.ganeshji.app.utils;

import android.app.Activity;
import android.content.Context;

import com.ganeshji.app.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

import java.lang.ref.WeakReference;

/**
 * Singleton that loads, caches, and shows App‑Open Ads.
 */
public final class AppOpenAdManager {

    private static AppOpenAd appOpenAd;
    private static boolean   isLoading  = false;
    private static long      loadTime   = 0L;

    /** Optional one‑shot callback executed when the very first ad finishes loading. */
    private static WeakReference<Runnable> pendingWhenLoaded;

    private AppOpenAdManager() { /* no instances */ }

    /* ---------- helpers ---------- */

    private static boolean isAdAvailable() {
        return appOpenAd != null &&
                (System.currentTimeMillis() - loadTime) < 4 * 60 * 60 * 1_000L; // < 4 h
    }

    /* ---------- public API ---------- */

    /** Warm‑up an App‑Open Ad (safe to call repeatedly). */
    public static void loadAd(Context ctx) {
        if (isLoading || isAdAvailable()) return;
        isLoading = true;

        String adUnitId = ctx.getString(R.string.app_open_id); // TODO replace with real ID

        AppOpenAd.load(
                ctx,
                adUnitId,
                new AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override public void onAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                        loadTime  = System.currentTimeMillis();
                        isLoading = false;

                        Runnable first = pendingWhenLoaded != null ? pendingWhenLoaded.get() : null;
                        if (first != null) first.run();
                        pendingWhenLoaded = null;
                    }
                    @Override public void onAdFailedToLoad(LoadAdError err) {
                        isLoading = false;

                    }
                });
    }

    /** Load an ad (if needed) and run `whenLoaded` once the ad is cached. */
    public static void loadAdAndThen(Context ctx, Runnable whenLoaded) {
        if (isAdAvailable()) {
            whenLoaded.run();
        } else {
            pendingWhenLoaded = new WeakReference<>(whenLoaded);
            loadAd(ctx);
        }
    }

    /** Show immediately if cached; otherwise just warm‑up. */
    public static void showIfAvailable(Activity activity) {
        showIfAvailable(activity, null);
    }

    /** Show now, and call `afterDismiss` only when the ad is closed (or fails). */
    public static void showIfAvailable(Activity activity, Runnable afterDismiss) {
        if (!isAdAvailable()) {
            if (afterDismiss != null) afterDismiss.run();
            loadAd(activity);
            return;
        }

        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override public void onAdDismissedFullScreenContent() {
                appOpenAd = null;
                loadAd(activity);
                if (afterDismiss != null) afterDismiss.run();
            }
            @Override public void onAdFailedToShowFullScreenContent(AdError err) {
                appOpenAd = null;
                loadAd(activity);
                if (afterDismiss != null) afterDismiss.run();
            }
        });

        appOpenAd.show(activity);
    }
}

package com.royal.attitude.status.utils;

import android.content.Context;
import android.os.Bundle;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.royal.attitude.status.MainActivity;

import androidx.annotation.NonNull;

public class AdManagerInter {
    static InterstitialAd interAd;
    private final Context ctx;
    Methods methods;

    public AdManagerInter(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        AdRequest adRequest;
       /* if (ConsentInformation.getInstance(ctx).getConsentStatus() == ConsentStatus.PERSONALIZED) {
            adRequest = new AdRequest.Builder()
                    .build();
        } else {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
        }
        InterstitialAd.load(ctx, Constants.interstitialAdID, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                interAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            }
        });*/
        methods = new Methods(ctx);
        methods.showFacdebookAd(ctx);

    }

    public InterstitialAd getAd() {
        return interAd;
    }

    public static void setAd(InterstitialAd interstitialAd) {
        interAd = interstitialAd;
    }

}
package com.royal.attitude.status;

import android.app.Application;
import android.os.StrictMode;

import com.royal.attitude.status.utils.Constants;
import com.royal.attitude.status.utils.SharedPref;
import com.facebook.FacebookSdk;
import com.onesignal.OneSignal;
import com.royal.attitude.status.utils.DBHelper;

import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        DBHelper dbHelper = new DBHelper(getApplicationContext());
        dbHelper.onCreate(dbHelper.getWritableDatabase());

        FacebookSdk.sdkInitialize(getApplicationContext());

        SharedPref sharedPref = new SharedPref(this);
        String mode = sharedPref.getDarkMode();
        switch (mode) {
            case Constants.DARK_MODE_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case Constants.DARK_MODE_OFF:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case Constants.DARK_MODE_ON:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}
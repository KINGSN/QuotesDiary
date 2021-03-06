package com.royal.attitude.status.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.royal.attitude.status.item.ItemUser;

public class SharedPref {

    private Methods methods;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static String TAG_UID = "uid" ,TAG_USERNAME = "name",TAG_PROFILE_IMAGE = "proimage", TAG_EMAIL = "email", TAG_MOBILE = "mobile", TAG_REMEMBER = "rem",
            TAG_PASSWORD = "pass", SHARED_PREF_AUTOLOGIN = "autologin", TAG_LOGIN_TYPE = "loginType", TAG_AUTH_ID = "auth_id", TAG_NIGHT_MODE = "nightmode";

    public SharedPref(Context context) {
        methods = new Methods(context);
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public Boolean getIsNotification() {
        return sharedPreferences.getBoolean("noti", true);
    }

    public void setIsNotification(Boolean isNotification) {
        editor.putBoolean("noti", isNotification);
        editor.apply();
    }

    public void setIsFirst(Boolean flag) {
        editor.putBoolean("firstopen", flag);
        editor.apply();
    }

    public Boolean getIsFirst() {
        return sharedPreferences.getBoolean("firstopen", true);
    }

    public void setLoginDetails(ItemUser itemUser, Boolean isRemember, String password, String loginType) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_UID, methods.encrypt(itemUser.getId()));
        editor.putString(TAG_USERNAME, methods.encrypt(itemUser.getName()));
        editor.putString(TAG_MOBILE, methods.encrypt(itemUser.getMobile()));
        editor.putString(TAG_EMAIL, methods.encrypt(itemUser.getEmail()));
        editor.putString(TAG_PROFILE_IMAGE, methods.encrypt(itemUser.getImage()));
        editor.putString(TAG_PASSWORD, methods.encrypt(password));
        editor.putString(TAG_LOGIN_TYPE, methods.encrypt(loginType));
        editor.putString(TAG_AUTH_ID, methods.encrypt(itemUser.getAuthID()));
        editor.apply();
    }

    public void setRemeber(Boolean isRemember) {
        editor.putBoolean(TAG_REMEMBER, isRemember);
        editor.putString(TAG_PASSWORD, "");
        editor.apply();
    }

    public void getUserDetails() {
        Constants.itemUser = new ItemUser(methods.decrypt(sharedPreferences.getString(TAG_UID,"")),
                                            methods.decrypt(sharedPreferences.getString(TAG_USERNAME,"")),
                                            methods.decrypt(sharedPreferences.getString(TAG_EMAIL,"")),
                                            sharedPreferences.getString(TAG_MOBILE,""),
                                            sharedPreferences.getString(TAG_PROFILE_IMAGE,""),
                                            methods.decrypt(sharedPreferences.getString(TAG_AUTH_ID,"")),
                                            methods.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,"")));
    }

    public String getEmail() {
        return methods.decrypt(sharedPreferences.getString(TAG_EMAIL,""));
    }

    public String getPassword() {
        return methods.decrypt(sharedPreferences.getString(TAG_PASSWORD,""));
    }

    public String getAuthID() {
        return methods.decrypt(sharedPreferences.getString(TAG_AUTH_ID,""));
    }

    public Boolean getIsAutoLogin() {
        return sharedPreferences.getBoolean(SHARED_PREF_AUTOLOGIN, false);
    }

    public String getLoginType() {
        return methods.decrypt(sharedPreferences.getString(TAG_LOGIN_TYPE,""));
    }

    public void setLoginType(String loginType) {
        editor.putString(TAG_LOGIN_TYPE, methods.encrypt(loginType));
        editor.apply();
    }

    public void setIsAutoLogin(Boolean isAutoLogin) {
        editor.putBoolean(SHARED_PREF_AUTOLOGIN, isAutoLogin);
        editor.apply();
    }

    public Boolean getIsRemember() {
        return sharedPreferences.getBoolean(TAG_REMEMBER, false);
    }

    public String getDarkMode() {
        return sharedPreferences.getString(TAG_NIGHT_MODE, Constants.DARK_MODE_SYSTEM);
    }

    public void setDarkMode(String nightMode) {
        editor.putString(TAG_NIGHT_MODE, nightMode);
        editor.apply();
    }
}
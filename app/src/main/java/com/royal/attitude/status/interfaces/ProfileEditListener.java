package com.royal.attitude.status.interfaces;

public interface ProfileEditListener {
    void onStart();
    void onEnd(String profileImage, String success, String registerSuccess, String message);
}
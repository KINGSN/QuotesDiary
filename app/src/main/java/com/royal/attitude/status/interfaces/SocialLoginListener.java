package com.royal.attitude.status.interfaces;

public interface SocialLoginListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message, String user_id);
}
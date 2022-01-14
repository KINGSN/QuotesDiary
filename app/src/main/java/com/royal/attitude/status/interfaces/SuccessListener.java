package com.royal.attitude.status.interfaces;

public interface SuccessListener {
    void onStart();
    void onEnd(String success, String registerSuccess, String message);
}
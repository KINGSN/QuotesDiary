package com.royal.attitude.status.interfaces;

import com.royal.attitude.status.item.ItemCat;

import java.util.ArrayList;

public interface CategoryListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListImageCat, ArrayList<ItemCat> arrayListTextCat);
}
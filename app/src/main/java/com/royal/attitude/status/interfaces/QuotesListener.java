package com.royal.attitude.status.interfaces;

import com.royal.attitude.status.item.ItemQuotes;

import java.util.ArrayList;

public interface QuotesListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<ItemQuotes> arrayListQuotes, int total_records);
}
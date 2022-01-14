package com.royal.attitude.status.interfaces;

import com.royal.attitude.status.item.ItemQuotes;

import java.util.ArrayList;

public interface HomeListener {
    void onStart();
    void onEnd(String success, String message, ArrayList<ItemQuotes> arrayListFeatured, ArrayList<ItemQuotes> arrayListLatest, ArrayList<ItemQuotes> arrayListPopular, ArrayList<ItemQuotes> arrayListTop, ArrayList<ItemQuotes> arrayListQuoteOfDay, ArrayList<ItemQuotes> arrayListText);
}
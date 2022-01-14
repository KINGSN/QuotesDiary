package com.royal.attitude.status.utils;

import com.royal.attitude.status.item.ItemQuotes;

import java.util.ArrayList;

public class MySingleton {
    private static final MySingleton SELF = new MySingleton();

    private ArrayList<ItemQuotes> quotesTemp = new ArrayList<>();
    private ArrayList<ItemQuotes> quotes = new ArrayList<>();
    private boolean isQoutes;

    private MySingleton() {
        // Don't want anyone else constructing the singleton.
    }

    public static MySingleton getInstance() {
        return SELF;
    }

    public ArrayList<ItemQuotes> getQuotesTemp() {
        return quotesTemp;
    }

    public ArrayList<ItemQuotes> getQuotes() {
        return quotes;
    }
}
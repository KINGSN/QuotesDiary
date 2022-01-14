package com.royal.attitude.status.eventbus;

import com.royal.attitude.status.item.ItemQuotes;

public class EventDelete {
    private int position;
    private ItemQuotes itemQuotes;
    private String type;

    public EventDelete(ItemQuotes itemQuotes, int position, String type) {
        this.itemQuotes = itemQuotes;
        this.position = position;
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public ItemQuotes getItemQuotes() {
        return itemQuotes;
    }

    public String getType() {
        return type;
    }
}
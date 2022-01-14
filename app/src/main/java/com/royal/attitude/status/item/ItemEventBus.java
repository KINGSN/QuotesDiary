package com.royal.attitude.status.item;

import android.graphics.Bitmap;

import java.io.Serializable;

public class ItemEventBus implements Serializable {

    private String type, font;
    private int bgColor, textSize, brightness;
    private Bitmap bitmap;

    public String getType() {
        return type;
    }

    public int getBgColor() {
        return bgColor;
    }

    public String getFont() {
        return font;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }
}

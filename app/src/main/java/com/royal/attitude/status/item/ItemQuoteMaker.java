package com.royal.attitude.status.item;

import android.widget.TextView;

import com.royal.attitude.status.utils.OnDragTouchListener;

import java.io.Serializable;

public class ItemQuoteMaker implements Serializable {

    private TextView textView;
    private boolean isTextBold = false;
    private int textGravity = 1, seekProgress = 25, fontPos = 0;
    private OnDragTouchListener.OnDragActionListener onDragActionListener;

    public ItemQuoteMaker(TextView textView, OnDragTouchListener.OnDragActionListener onDragActionListener) {
        this.textView = textView;
        this.onDragActionListener = onDragActionListener;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public boolean getIsTextBold() {
        return isTextBold;
    }

    public void setIsTextBold(boolean textBold) {
        isTextBold = textBold;
    }

    public int getTextGravity() {
        return textGravity;
    }

    public void setTextGravity(int textGravity) {
        this.textGravity = textGravity;
    }

    public int getSeekProgress() {
        return seekProgress;
    }

    public void setSeekProgress(int seekProgress) {
        this.seekProgress = seekProgress;
    }

    public int getFontPos() {
        return fontPos;
    }

    public void setFontPos(int fontPos) {
        this.fontPos = fontPos;
    }

    public OnDragTouchListener.OnDragActionListener getOnDragTouchListener() {
        return onDragActionListener;
    }

    public void setOnDragTouchListener(OnDragTouchListener.OnDragActionListener onDragActionListener) {
        this.onDragActionListener = onDragActionListener;
    }
}
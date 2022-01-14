package com.royal.attitude.status.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class OnDragTouchListener implements View.OnTouchListener {

    /**
     * Callback used to indicate when the drag is finished
     */
    public interface OnDragActionListener {
        /**
         * Called when drag event is started
         *
         * @param view The view dragged
         */
        void onDragStart(View view);

        /**
         * Called when drag event is completed
         *
         * @param view The view dragged
         */
        void onDragEnd(View view, Boolean delete);
    }

    private View mView;
    private View mParent;
    private boolean isDragging;
    private boolean isInitialized = false;

    private int width;
    private float xWhenAttached;
    private float maxLeft;
    private float maxRight;
    private float dX;

    private int height;
    private float yWhenAttached;
    private float maxTop;
    private float maxBottom;
    private float dY;
    private final float SCROLL_THRESHOLD = 10;
    private float mDownX;
    private float mDownY;
    private boolean isOnClick = false;
    private View viewCancel;
    private Boolean isCancelInflated = false, isCancelShrinked = true, delete = false;
    private int statusBar, bottomMargin;

    private OnDragActionListener mOnDragActionListener;

    public OnDragTouchListener(View view, View viewCancel, int statusBar, int bottomMargin) {
        this(view, (View) view.getParent(), viewCancel, statusBar, bottomMargin, null);
    }

    public OnDragTouchListener(View view, View parent, View viewCancel, int statusBar, int bottomMargin) {
        this(view, parent, viewCancel, statusBar, bottomMargin, null);
    }

    public OnDragTouchListener(View view, View viewCancel, int statusBar, int bottomMargin, OnDragActionListener onDragActionListener) {
        this(view, (View) view.getParent(), viewCancel, statusBar, bottomMargin, onDragActionListener);
    }

    public OnDragTouchListener(View view, View parent, View viewCancel, int statusBar, int bottomMargin, OnDragActionListener onDragActionListener) {
        initListener(view, parent, viewCancel, statusBar, bottomMargin);
        setOnDragActionListener(onDragActionListener);
    }

    public void setOnDragActionListener(OnDragActionListener onDragActionListener) {
        mOnDragActionListener = onDragActionListener;
    }

    public void initListener(View view, View parent, View view_cancel, int statusBar, int bottomMar) {
        mView = view;
        mParent = parent;
        viewCancel = view_cancel;
        this.statusBar = statusBar;
        isDragging = false;
        isInitialized = false;
        bottomMargin = bottomMar;
    }

    public void updateBounds() {
        updateViewBounds();
        updateParentBounds();
        isInitialized = true;
    }

    public void updateViewBounds() {
        width = mView.getWidth();
        xWhenAttached = mView.getX();
        dX = 0;

        height = mView.getHeight();
        yWhenAttached = mView.getY();
        dY = 0;
    }

    public void updateParentBounds() {
        maxLeft = 0;
        maxRight = maxLeft + mParent.getWidth();

        maxTop = 0;
        maxBottom = maxTop + mParent.getHeight();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isDragging) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    onDragFinish(delete);
                    break;
                case MotionEvent.ACTION_MOVE:

                    float[] bounds = new float[4];
                    // LEFT
                    bounds[0] = event.getRawX() + dX;
                    if (bounds[0] < maxLeft) {
                        bounds[0] = maxLeft;
                    }
                    // RIGHT
                    bounds[2] = bounds[0] + width;
                    if (bounds[2] > maxRight) {
                        bounds[2] = maxRight;
                        bounds[0] = bounds[2] - width;
                    }
                    // TOP
                    bounds[1] = event.getRawY() + dY;
                    if (bounds[1] < maxTop) {
                        bounds[1] = maxTop;
                    }
                    // BOTTOM
                    bounds[3] = bounds[1] + height;
                    if (bounds[3] > maxBottom) {
                        bounds[3] = maxBottom;
                        bounds[1] = bounds[3] - height;
                    }
                    mView.animate().x(bounds[0]).y(bounds[1]).setDuration(0).start();

                    float x = event.getRawX();
                    float y = event.getRawY();

                    if ((viewCancel.getLeft() <= x && x <= viewCancel.getRight()) && ((mParent.getBottom()-(viewCancel.getBottom() - viewCancel.getTop())-bottomMargin) <= y && y <= (mParent.getBottom()-bottomMargin))) {
                        if(!isCancelInflated) {
                            delete = true;
                            viewCancel.animate().scaleX(1.5f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
                            viewCancel.animate().scaleY(1.5f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();

                            mView.animate().scaleX(0.3f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
                            mView.animate().scaleY(0.3f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();

//                            mView.animate().x(viewCancel.getX()).y(viewCancel.getY()).start();

                            isCancelInflated = true;
                            isCancelShrinked = false;
                        }

                    } else {
                        if(!isCancelShrinked) {
                            delete = false;
                            viewCancel.animate().scaleX(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
                            viewCancel.animate().scaleY(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();

                            mView.animate().scaleX(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
                            mView.animate().scaleY(1f).setDuration(200).setInterpolator(new OvershootInterpolator()).start();

                            isCancelInflated = false;
                            isCancelShrinked = true;
                        }
                    }
                    break;
            }
            return true;
        } else {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    mDownX = event.getX();
                    mDownY = event.getY();
                    isOnClick = true;

                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (isOnClick && (Math.abs(mDownX - event.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - event.getY()) > SCROLL_THRESHOLD)) {
                        isOnClick = false;
                        isDragging = true;

                        if (!isInitialized) {
                            updateBounds();
                        }
                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();
                        if (mOnDragActionListener != null) {
                            mOnDragActionListener.onDragStart(mView);
                        }
                    }
                    return true;

                case MotionEvent.ACTION_UP:
                    if (isOnClick) {
                        v.performClick();
                        isOnClick = false;
                    }
                    return true;
            }
        }
        return false;
    }

    private void onDragFinish(Boolean delete) {
        if (mOnDragActionListener != null) {
            mOnDragActionListener.onDragEnd(mView, delete);
        }

        dX = 0;
        dY = 0;
        isDragging = false;
    }
}
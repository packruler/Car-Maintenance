/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.packruler.carmaintenance.ui.adapters;

import android.animation.ValueAnimator;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.packruler.carmaintenance.R;

/**
 * Created by packr on 7/21/2015.
 */
public abstract class ExpandableViewHolder extends RecyclerView.ViewHolder {
    String TAG = getClass().getSimpleName();
    FrameLayout layout;
    RelativeLayout expandedMenu;
    CardView card;
    ValueAnimator valueAnimator;
    final static Interpolator outInterpolator = new DecelerateInterpolator();
    final static Interpolator inInterpolator = new AccelerateInterpolator();
    final static int aDuration = 300;
    int minHeight;
    boolean setup = false;

    public ExpandableViewHolder(View itemView) {
        super(itemView);
        layout = (FrameLayout) itemView.findViewById(R.id.expandable_layout);
        layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                Log.v(TAG, "LAYOUT");
                close(true);
                if (setup) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    else
                        layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        card = (CardView) layout.findViewById(R.id.card);
        expandedMenu = (RelativeLayout) itemView.findViewById(R.id.expanded_menu);
    }

    public void expand() {
                if (minHeight <= (layout.getPaddingTop() * 2))
                    minHeight = card.getHeight() + (layout.getPaddingTop() * 2);

                valueAnimator = ValueAnimator.ofInt(0, card.getHeight() + layout.getPaddingTop());
                valueAnimator.setDuration(aDuration);

                valueAnimator.setInterpolator(outInterpolator);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int currentValue = (int) valueAnimator.getAnimatedValue();
                        expandedMenu.setY(currentValue);
//                    Log.v(TAG, "Current Value:" + currentValue);

                        float height = currentValue + expandedMenu.getHeight();
//                    Log.v(TAG, "Height: " + height + " | " + minHeight);
                        final int outHeight;
                        if (height < minHeight)
                            outHeight = minHeight;
                        else outHeight = (int) height;

                        if (height != 0)
                                    expandedMenu.requestLayout();
                                    layout.getLayoutParams().height = outHeight;
                    }
                });
                valueAnimator.start();
    }

    public void close(boolean initialLoad) {
        if (initialLoad) {
            layout.requestLayout();
            card.requestLayout();
            expandedMenu.requestLayout();
        }
//            Log.v(TAG, "Card height: " + card.getHeight());
        minHeight = card.getHeight() + (layout.getPaddingTop() * 2);
        int hideValue = card.getHeight() - expandedMenu.getHeight();

        if (initialLoad) {
            expandedMenu.setY(hideValue);
            if (minHeight > (layout.getPaddingTop() * 2)) {
                ((RecyclerView.LayoutParams) layout.getLayoutParams()).height = card.getHeight() + (layout.getPaddingTop() * 2);
//                    Log.d(TAG, "SUCCESS");
                setup = true;
            } else {
//                    Log.d(TAG, "FAILED");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                            Log.d(TAG, "RERUN");
                        close(true);
                    }
                }, 100);
            }

            layout.requestLayout();
//                Log.v(TAG, "Current Value:" + hideValue);
        } else {
            if (valueAnimator != null && valueAnimator.isRunning())
                valueAnimator.cancel();

            valueAnimator = ValueAnimator.ofInt((int) expandedMenu.getY(), hideValue);

            valueAnimator.setDuration(aDuration);
            valueAnimator.setInterpolator(inInterpolator);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentValue = (int) valueAnimator.getAnimatedValue();
                    expandedMenu.setY(currentValue);

                    int height = currentValue + expandedMenu.getHeight();
                    if (height < minHeight)
                        height = minHeight;
//                        Log.v(TAG, "Height: " + height + " | " + minHeight);
                    if (height != 0) {
                        ((RecyclerView.LayoutParams) layout.getLayoutParams()).height = height;
                    }
                    layout.requestLayout();
                }
            });
            valueAnimator.start();
        }
    }

    abstract void setDisplay(Cursor cursor);

    public void onBind(Cursor cursor) {
        TAG = "Position: " + cursor.getInt(0);
        close(true);
        setDisplay(cursor);
    }

    public boolean equals(Object o) {
        if (o instanceof Long)
            return (Long) o == getItemId();
        else
            return o instanceof ExpandableViewHolder && ((ExpandableViewHolder) o).getItemId() == getItemId();
    }
}

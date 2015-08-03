/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.packruler.carmaintenance.ui.adapters;

import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.packruler.carmaintenance.R;

/**
 * Created by packr on 8/3/2015.
 */
public class VehicleStatisticsHandler {
    String TAG = getClass().getSimpleName();
    private FrameLayout layout;
    private ImageView expandedArrow;
    LinearLayout extraDetails;
    CardView card;
    ValueAnimator valueAnimator;
    final static Interpolator outInterpolator = new DecelerateInterpolator();
    final static Interpolator inInterpolator = outInterpolator;
    final static int aDuration = 2;
    int minHeight;
    boolean setup = false;
    boolean expanded = false;

    public VehicleStatisticsHandler(final FrameLayout layout) {
        this.layout = layout;
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
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expanded)
                    close(false);
                else expand();
            }
        });
        extraDetails = (LinearLayout) layout.findViewById(R.id.extra_details);
        expandedArrow = (ImageView) layout.findViewById(R.id.expandable_arrow);
//        expandedArrow.rota
    }

    public void expand() {
        if (minHeight <= (layout.getPaddingTop() * 2))
            minHeight = card.getHeight() + (layout.getPaddingTop() * 2);

        valueAnimator = ValueAnimator.ofInt(0, card.getHeight() + layout.getPaddingTop());
        valueAnimator.setDuration(aDuration * extraDetails.getHeight());

        valueAnimator.setInterpolator(outInterpolator);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) valueAnimator.getAnimatedValue();
                extraDetails.setY(currentValue);
//                    Log.v(TAG, "Current Value:" + currentValue);

                float height = currentValue + extraDetails.getHeight();
//                    Log.v(TAG, "Height: " + height + " | " + minHeight);
                final int outHeight;
                if (height < minHeight)
                    outHeight = minHeight;
                else outHeight = (int) height;

                if (height != 0)
                    extraDetails.requestLayout();
                layout.getLayoutParams().height = outHeight;
            }
        });
        valueAnimator.start();
        expanded = true;
    }

    public void close(boolean initialLoad) {
        if (initialLoad) {
            layout.requestLayout();
            card.requestLayout();
            extraDetails.requestLayout();
        }
//            Log.v(TAG, "Card height: " + card.getHeight());
        minHeight = card.getHeight() + (layout.getPaddingTop() * 2);
        int hideValue = card.getHeight() - extraDetails.getHeight();

        if (initialLoad) {
            extraDetails.setY(hideValue);
            if (minHeight > (layout.getPaddingTop() * 2)) {
                ((RelativeLayout.LayoutParams) layout.getLayoutParams()).height = card.getHeight() + (layout.getPaddingTop() * 2);
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

            valueAnimator = ValueAnimator.ofInt((int) extraDetails.getY(), hideValue);

            valueAnimator.setDuration(aDuration * extraDetails.getHeight());
            valueAnimator.setInterpolator(inInterpolator);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentValue = (int) valueAnimator.getAnimatedValue();
                    extraDetails.setY(currentValue);

                    int height = currentValue + extraDetails.getHeight();
                    if (height < minHeight)
                        height = minHeight;
//                        Log.v(TAG, "Height: " + height + " | " + minHeight);
                    if (height != 0) {
                        ((RelativeLayout.LayoutParams) layout.getLayoutParams()).height = height;
                    }
                    layout.requestLayout();
                }
            });
            valueAnimator.start();
            expanded = false;
        }
    }
}

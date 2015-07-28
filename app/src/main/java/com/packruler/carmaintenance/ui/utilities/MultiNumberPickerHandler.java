/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.packruler.carmaintenance.ui.utilities;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;

import java.util.LinkedList;

/**
 * Created by packr on 7/24/2015.
 */
public class MultiNumberPickerHandler extends LinearLayout {
    private LinkedList<MaterialPicker> numbers = new LinkedList<>();
    private MaterialPicker.OnValueChangeListener onValueChangeListener;
    private int primaryColor = 0;

    public MultiNumberPickerHandler(final Context context) {
        super(context);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        Log.v(getClass().getSimpleName(), "Padding: " + padding);
        setGravity(Gravity.END);
        setPadding(padding, 0, 0, padding);
        setOrientation(LinearLayout.HORIZONTAL);
        numbers.add(new MaterialPicker(context));
        onValueChangeListener = new MaterialPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(MaterialPicker picker, byte value) {
                Log.v(getClass().getSimpleName(), "Last: " + (numbers.peekLast() == picker));
                if (value != 0 && (numbers.peekLast() == picker)) {
                    MaterialPicker newPicker = new MaterialPicker(context);
                    newPicker.setOnValueChangeListener(onValueChangeListener);
                    if (primaryColor != 0)
                        newPicker.setPrimaryColor(primaryColor);
                    numbers.add(newPicker);
                    addView(newPicker, 0);
                }
            }
        };
        numbers.get(0).setOnValueChangeListener(onValueChangeListener);
        addView(numbers.get(0));
    }

    public void setPrimaryColor(int color) {
        primaryColor = color;
        for (MaterialPicker cur : numbers) {
            cur.setPrimaryColor(color);
        }
    }
}

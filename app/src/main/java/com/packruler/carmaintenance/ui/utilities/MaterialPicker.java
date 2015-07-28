/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.packruler.carmaintenance.ui.utilities;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.packruler.carmaintenance.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by packr on 7/24/2015.
 */
public class MaterialPicker extends LinearLayout {
    final View root;
    final ImageButton up;
    final ImageButton down;
    final MaterialEditText display;
    private byte value;
    private OnValueChangeListener onValueChangeListener;

    public MaterialPicker(Context context) {
        super(context);

        root = LayoutInflater.from(context).inflate(R.layout.picker_layout, null);
        up = (ImageButton) root.findViewById(R.id.up_arrow);
        up.setOnClickListener(onUpClick);
        down = (ImageButton) root.findViewById(R.id.down_arrow);
        down.setOnClickListener(onDownClick);
        display = (MaterialEditText) root.findViewById(R.id.value);
        this.addView(root);
    }

    private void setArrowColor(int arrowColor) {
        Drawable arrow;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            arrow = getContext().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp);
            if (arrow != null) {
                arrow.setTint(arrowColor);
                up.setImageDrawable(arrow);
            }

            arrow = getContext().getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp);
            if (arrow != null) {
                arrow.setTint(arrowColor);
                down.setImageDrawable(arrow);
            }
        } else {
            arrow = getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_white_24dp);
            if (arrow != null) {
                arrow.setColorFilter(arrowColor, PorterDuff.Mode.SRC_IN);
                up.setImageDrawable(arrow);
            }

            arrow = getContext().getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_white_24dp);
            if (arrow != null) {
                arrow.setColorFilter(arrowColor, PorterDuff.Mode.SRC_IN);
                down.setImageDrawable(arrow);
            }
        }
    }

    public void setEditable(boolean editable) {
        if (editable) {
            up.setOnClickListener(onUpClick);
            down.setOnClickListener(onDownClick);
        } else {
            up.setOnClickListener(null);
            down.setOnClickListener(null);
        }
    }

    public byte getValue() {
        return value;
    }

    private final OnClickListener onUpClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (value == 9)
                value = 0;
            else value++;

            display.setText("" + value);
            onValueChanged();
        }
    };

    private final OnClickListener onDownClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (value == 0)
                value = 9;
            else value--;

            display.setText("" + value);
            onValueChanged();
        }
    };

    private void onValueChanged() {
        if (onValueChangeListener != null)
            onValueChangeListener.onValueChange(this, value);
    }

    public void setOnValueChangeListener(@Nullable OnValueChangeListener listener) {
        onValueChangeListener = listener;
    }

    public interface OnValueChangeListener {
        void onValueChange(MaterialPicker picker, byte value);
    }

    public void setPrimaryColor(int color) {
        display.setPrimaryColor(color);
        display.setBaseColor(color);
        setArrowColor(color);
    }
}

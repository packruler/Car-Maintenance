/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.example.materialpicker;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/**
 * Created by packr on 7/24/2015.
 */
public class MaterialPicker extends LinearLayout {
    final View root;
    final ImageButton up;
    final ImageButton down;
    final EditText display;
    private byte value;
    private OnValueChangeListener onValueChangeListener;

    public MaterialPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

        root = LayoutInflater.from(context).inflate(R.layout.picker_layout, null);
        up = (ImageButton) root.findViewById(R.id.up_arrow);
        up.setOnClickListener(onUpClick);
        down = (ImageButton) root.findViewById(R.id.down_arrow);
        down.setOnClickListener(onDownClick);
        display = (EditText) root.findViewById(R.id.value);
        display.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                value = Byte.decode(s.toString());
                onValueChanged();
            }
        });
        this.addView(root);
    }

    public void setArrowColor(int arrowColor) {
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
            onValueChangeListener.onValueChange(value);
    }

    public void setOnValueChangeListener(@Nullable OnValueChangeListener listener) {
        onValueChangeListener = listener;
    }

    public abstract class OnValueChangeListener {
        public abstract void onValueChange(byte value);
    }
}

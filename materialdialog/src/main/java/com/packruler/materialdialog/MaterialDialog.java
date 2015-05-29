package com.packruler.materialdialog;

import android.content.Context;
import android.util.AttributeSet;

import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Packruler on 5/28/15.
 */
public class MaterialDialog extends MaterialEditText {
    private final String TAG = getClass().getName();



    public MaterialDialog(Context context) {
        super(context);
    }

    public MaterialDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaterialDialog(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    private void init(Context context, AttributeSet attrs) {

    }
}

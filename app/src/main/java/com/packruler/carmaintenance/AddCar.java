package com.packruler.carmaintenance;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by packr_000 on 11/30/2014.
 */
public class AddCar extends AlertDialog {
    protected AddCar(Context context) {
        super(context);
    }

    protected AddCar(Context context, int theme) {
        super(context, theme);
    }

    protected AddCar(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}

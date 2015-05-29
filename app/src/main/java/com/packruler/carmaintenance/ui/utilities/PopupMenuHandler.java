package com.packruler.carmaintenance.ui.utilities;

import android.support.v7.internal.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.packruler.carmaintenance.R;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by Packruler on 5/29/15.
 */
public class PopupMenuHandler {
    private final String TAG = getClass().getName();
    private PopupMenu menu;

    public PopupMenuHandler(RelativeLayout parent, MaterialEditText materialEditText) {
        Log.v(TAG, "Load");
        View clickCatch = LayoutInflater.from(materialEditText.getContext()).inflate(R.layout.dialog_button_overlay, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, materialEditText.getId());
        params.addRule(RelativeLayout.ALIGN_RIGHT, materialEditText.getId());
        params.addRule(RelativeLayout.ALIGN_LEFT, materialEditText.getId());
        params.addRule(RelativeLayout.ALIGN_TOP, materialEditText.getId());
        parent.addView(clickCatch, params);
        MenuBuilder builder = new MenuBuilder(materialEditText.getContext());
//        builder.
        menu = new PopupMenu(materialEditText.getContext(), clickCatch);
//        menu.get
    }
}

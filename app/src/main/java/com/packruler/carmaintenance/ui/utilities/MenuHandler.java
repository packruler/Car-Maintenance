/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.packruler.carmaintenance.ui.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.packruler.carmaintenance.R;

/**
 * Created by packr on 7/7/2015.
 */
public class MenuHandler {
    private static final String TAG = "MenuHandler";
    public static String SAVE;
    public static String DISCARD;
    public static String DELETE;
    public static final Drawable[] icons = new Drawable[3];


    public static void setResources(Context context) {
        Resources res = context.getResources();
        icons[0] = res.getDrawable(R.drawable.ic_save);
        icons[1] = res.getDrawable(R.drawable.ic_cancel);
        icons[2] = res.getDrawable(R.drawable.ic_delete);

        SAVE = res.getString(R.string.save);
        DISCARD = res.getString(R.string.discard);
        DELETE = res.getString(R.string.delete);
    }

    public static Menu setupEditMenu(Menu menu) {
        menu.clear();

        menu.add(SAVE);
        menu.getItem(0).setIcon(icons[0]).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(DISCARD);
        menu.getItem(1).setIcon(icons[1]).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(DELETE);
        menu.getItem(2).setIcon(icons[2]);

//        activity.getToolbar().setOnMenuItemClickListener(this);
        Log.v(TAG, "onCreateOptionsMenu");
        Log.v(TAG, "Menu size: " + menu.size());
        return menu;
    }

    public static void setUIColor() {
        for (Drawable icon : icons)
            icon.setColorFilter(Swatch.getForegroundColor(), PorterDuff.Mode.MULTIPLY);
    }

}

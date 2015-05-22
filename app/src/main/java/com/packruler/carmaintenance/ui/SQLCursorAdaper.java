package com.packruler.carmaintenance.ui;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packruler.carmaintenance.R;

/**
 * Created by Packruler on 5/21/15.
 */
public class SQLCursorAdaper extends CursorAdapter {
    public SQLCursorAdaper(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.cursor_adapter_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(android.R.id.text1)).setText(cursor.getString(0));
    }
}

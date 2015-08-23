/*
 * Copyright (c) 2015.  Ethan Leisinger
 */

package com.packruler.carmaintenance.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.packruler.carmaintenance.sql.AvailableCarsSQL;

import java.io.IOException;

/**
 * Created by packr on 8/5/2015.
 */
public class SQLiteAutoCompleteAdapter extends CursorAdapter {
    private String TAG = getClass().getSimpleName();
    private Cursor cursor;
    private SQLiteDatabase database;
    private String table;
    private String columnName;
    private String conditions;
    private Context context;
    private String where;

    public SQLiteAutoCompleteAdapter(Context context, String table, String columnName) {
        super(context, null, false);
        try {
            Log.v(TAG, "Open database");
            database = new AvailableCarsSQL(context).getReadableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Is open " + database.isOpen());
        this.context = context;
        this.table = table;
        this.columnName = columnName;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        database.close();
        return super.clone();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.v(TAG, "newView");
        return LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view).setText(convertToString(cursor));
        Log.v(TAG, "Col: " + cursor.getColumnName(0) + " | Val: " + cursor.getString(0));
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        Log.v(TAG, "Query: " + constraint);
        where = null;
        if (constraint != null)
            where = columnName + " LIKE '%" + constraint + "%'";

        if (database == null || !database.isOpen())
            try {
                Log.v(TAG, "Reopen database");
                database = new AvailableCarsSQL(context).getReadableDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        Cursor cursor = database.query(true, table, new String[]{columnName}, where, null, null, conditions, columnName + " ASC", null);
        int x = 0;
        if (cursor.moveToFirst())
            while (!cursor.isAfterLast()) {
                Log.v(TAG, x++ + ": " + convertToString(cursor));
                cursor.moveToNext();
            }
        Log.v(TAG, "Size: " + cursor.getCount());
        return database.query(true, table, new String[]{columnName}, where, null, null, conditions, columnName + " ASC", null);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {
        if (cursor == null)
            return "";
        return cursor.getString(0);
    }

    @Override
    public int getCount() {
        Cursor cursor = database.query(true, table, new String[]{columnName}, where, null, null, conditions, columnName + " ASC", null);
        Log.v(TAG, "Count: " + cursor.getCount());
        return cursor.getCount();
    }
}

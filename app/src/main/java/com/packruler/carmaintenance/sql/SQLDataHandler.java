package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Packruler on 5/7/15.
 */
public class SQLDataHandler {
    private final String TAG = getClass().getSimpleName();
    private SQLiteDatabase database;
    private String tableName;
    private long row;
    private SQLDataOberservable observable;
    private String selection;
    private ContentValues contentValues;
    private Handler backgroundHandler;

    public SQLDataHandler(SQLiteDatabase database, String tableName, long row, SQLDataOberservable observable) {
        this.database = database;
        this.tableName = tableName;
        this.row = row;
        this.observable = observable;
        loadSelection();
    }

    public void storeValuesInBackground(final Runnable runnable) {
        if (backgroundHandler == null) {
            Log.v(TAG, "Initialize Thread");
            HandlerThread thread = new HandlerThread(getClass().getSimpleName() + ".BACKGROUND");
            thread.start();
            backgroundHandler = new Handler(thread.getLooper());
        }
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {
                SQLDataHandler.this.beginTransaction();
                runnable.run();
                SQLDataHandler.this.endTransaction();
            }
        });

    }

    public boolean beginTransaction() {
        Log.v(TAG, "beginTransaction");
        boolean wasNull = contentValues != null;
        contentValues = new ContentValues();
        return wasNull;
    }

    public boolean endTransaction() {
        if (contentValues == null)
            return false;
        putContentValues(contentValues);
        return true;
    }

    public String getString(String column) {
        Cursor cursor = database.query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        String output = cursor.getString(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void put(String column, String value) {
        boolean inTransaction = contentValues != null;

        ContentValues contentValues;
        if (inTransaction)
            contentValues = this.contentValues;
        else
            contentValues = new ContentValues();

        contentValues.put(column, value);

        if (!inTransaction)
            putContentValues(contentValues);
    }

    public Integer getInt(String column) {
        Cursor cursor = database.query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        int output = cursor.getInt(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void put(String column, Integer value) {
        boolean inTransaction = contentValues != null;

        ContentValues contentValues;
        if (inTransaction)
            contentValues = this.contentValues;
        else
            contentValues = new ContentValues();

        contentValues.put(column, value);

        if (!inTransaction)
            putContentValues(contentValues);
    }

    public Long getLong(String column) {
        Cursor cursor = database.query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        long output = cursor.getLong(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void put(String column, Long value) {
        boolean inTransaction = contentValues != null;

        ContentValues contentValues;
        if (inTransaction)
            contentValues = this.contentValues;
        else
            contentValues = new ContentValues();

        contentValues.put(column, value);

        if (!inTransaction)
            putContentValues(contentValues);
    }

    public Float getFloat(String column) {
        Cursor cursor = database.query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        Float output = cursor.getFloat(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void put(String column, Float value) {
        boolean inTransaction = contentValues != null;

        ContentValues contentValues;
        if (inTransaction)
            contentValues = this.contentValues;
        else
            contentValues = new ContentValues();

        contentValues.put(column, value);

        if (!inTransaction)
            putContentValues(contentValues);
    }

    public Double getDouble(String column) {
        Cursor cursor = database.query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        double output = cursor.getDouble(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void put(String column, Double value) {
        boolean inTransaction = contentValues != null;

        ContentValues contentValues;
        if (inTransaction)
            contentValues = this.contentValues;
        else
            contentValues = new ContentValues();

        contentValues.put(column, value);

        if (!inTransaction)
            putContentValues(contentValues);
    }

    public boolean getBoolean(String column) {
        Cursor cursor = database.query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        boolean output = cursor.getInt(cursor.getColumnIndex(column)) == 1;
        cursor.close();
        return output;
    }

    public void putBoolean(String column, boolean value) {
        boolean inTransaction = contentValues != null;

        ContentValues contentValues;
        if (inTransaction)
            contentValues = this.contentValues;
        else
            contentValues = new ContentValues();

        contentValues.put(column, value);

        if (!inTransaction)
            putContentValues(contentValues);

    }

    public void putContentValues(ContentValues contentValues) {
        Log.v(TAG, "Content Values: " + contentValues.toString());
        database.update(tableName, contentValues, selection, null);
        observable.notifiyChanged();
    }

    public void removeRow() {
        database.delete(tableName, selection, null);
    }

    public void loadSelection() {
        selection = "_id= " + row;
    }
}

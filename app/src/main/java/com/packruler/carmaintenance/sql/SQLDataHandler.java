package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DataSetObservable;

/**
 * Created by Packruler on 5/7/15.
 */
public class SQLDataHandler {
    private final String TAG = getClass().getName();
    private CarSQL carSQL;
    private String tableName;
    private String selection;
    private DataSetObservable observable;

    public SQLDataHandler(CarSQL carSQL, String tableName, String selection, DataSetObservable observable) {
        this.carSQL = carSQL;
        this.tableName = tableName;
        this.selection = selection;
        this.observable = observable;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getString(String column) {
        Cursor cursor = carSQL.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        String output = cursor.getString(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void putString(String column, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        setContentValues(contentValues);
    }

    public float getFloat(String column) {
        Cursor cursor = carSQL.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        float output = cursor.getFloat(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void putFloat(String column, float value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        setContentValues(contentValues);
    }

    public int getInt(String column) {
        Cursor cursor = carSQL.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        int output = cursor.getInt(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void putInt(String column, int value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        setContentValues(contentValues);
    }

    public double getDouble(String column) {
        Cursor cursor = carSQL.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        double output = cursor.getDouble(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void putDouble(String column, double value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        setContentValues(contentValues);
    }

    public long getLong(String column) {
        Cursor cursor = carSQL.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        long output = cursor.getLong(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void putLong(String column, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        setContentValues(contentValues);
    }

    public boolean getBoolean(String column) {
        Cursor cursor = carSQL.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        cursor.moveToFirst();
        boolean output = cursor.getInt(cursor.getColumnIndex(column)) == 1;
        cursor.close();
        return output;
    }

    public void putBoolean(String column, boolean value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        setContentValues(contentValues);
    }

    public void setContentValues(ContentValues contentValues) {
//        Log.v(TAG, "Content Values: " + contentValues.toString());
        carSQL.getWritableDatabase().update(tableName, contentValues, selection, null);
        observable.notifyChanged();
    }

    public void removeRow() {
        carSQL.getWritableDatabase().delete(tableName, selection, null);
    }
}

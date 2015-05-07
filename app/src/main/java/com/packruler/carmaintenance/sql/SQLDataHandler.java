package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.sql.SQLDataException;

/**
 * Created by Packruler on 5/7/15.
 */
public class SQLDataHandler {
    private final String TAG = getClass().getName();
    private CarSql carSql;
    private String tableName;
    private String selection;

    public SQLDataHandler(CarSql carSql, String tableName, String selection) {
        this.carSql = carSql;
        this.tableName = tableName;
        this.selection = selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public String getString(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
        String output = cursor.getString(cursor.getColumnIndex(column));
        cursor.close();
        return output;
    }

    public void putString(String column, String value, boolean skipCheck) throws SQLDataException {
        if (skipCheck) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(column, value);
            setContentValues(contentValues);
        } else
            putString(column, value, 0);
    }

    public void putString(String column, String value) throws SQLDataException {
        putString(column, value, 0);
    }

    public void putString(String column, String value, int minimumLength) throws SQLDataException {
        if (carSql.checkString(value, minimumLength)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(column, value);
            setContentValues(contentValues);
        }
    }

    public float getFloat(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
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
        Cursor cursor = carSql.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
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
        Cursor cursor = carSql.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
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
        Cursor cursor = carSql.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
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
        Cursor cursor = carSql.getReadableDatabase().query(tableName, new String[]{column},
                selection, null, null, null, null);
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
        Log.i(TAG, "Content Values: " + contentValues.toString());
        carSql.getWritableDatabase().update(tableName, contentValues, selection, null);
    }
}

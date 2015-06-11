package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by Packruler on 5/7/15.
 */
public class SQLDataHandler {
    private final String TAG = getClass().getSimpleName();
    private CarSQL carSQL;
    private String tableName;
    private long row;
    private SQLDataOberservable observable;
    private String selection;

    public SQLDataHandler(CarSQL carSQL, String tableName, long row, SQLDataOberservable observable) {
        this.carSQL = carSQL;
        this.tableName = tableName;
        this.row = row;
        this.observable = observable;
        loadSelection();
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setRow(long row) {
        this.row = row;
        loadSelection();
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
        observable.notifiyChanged();
    }

    public void removeRow() {
        carSQL.getWritableDatabase().delete(tableName, selection, null);
    }

    public void loadSelection() {
        selection = "_id= " + row;
    }
}

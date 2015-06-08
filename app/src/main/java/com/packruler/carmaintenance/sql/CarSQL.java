package com.packruler.carmaintenance.sql;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.packruler.carmaintenance.ui.utilities.VehicleMap;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSQL {
    private final String TAG = getClass().getSimpleName();

    private SQLHelper sqlHelper;
//    private Activity activity;

    public CarSQL(Activity activity) {
//        this.activity = activity;
        sqlHelper = new SQLHelper(activity);
    }

    private class SQLHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Cars.db";

        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, "Create new database");
            db.execSQL(Vehicle.SQL_CREATE);
            db.execSQL(ServiceTask.SQL_CREATE);
            db.execSQL(FuelStop.SQL_CREATE);
            db.execSQL(PartReplacement.SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public VehicleMap getCars() {
        VehicleMap map = new VehicleMap();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.ROW_ID}, null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return map;
        }

        while (!cursor.isAfterLast()) {
            map.put(new Vehicle(this, cursor.getLong(0)));
            cursor.moveToNext();
        }
        cursor.close();
        return map;
    }

    public int getCarCount() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.ROW_ID}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private SQLiteDatabase database;

    public void beginTransaction() {
        if (database == null)
            database = sqlHelper.getWritableDatabase();
        database.beginTransaction();
    }

    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    public void endTransaction() {
        database.endTransaction();
    }

    public boolean close() {
        if (database != null) {
            if (!database.inTransaction()) {
                database.close();
                return true;
            }
            Log.e(TAG, "Database open");
        }
        return false;
    }

    public SQLiteDatabase getWritableDatabase() {
        if (database == null) {
            Log.v(TAG, "Database in transation");
            database = sqlHelper.getWritableDatabase();
        }
        return database;
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqlHelper.getReadableDatabase();
    }

    public boolean canUseCarName(String name) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.VEHICLE_NAME},
                Vehicle.VEHICLE_NAME + "= \"" + name + "\"",
                null, null, null, null);
        boolean canUse = cursor.getCount() == 0;
        cursor.close();
        return canUse;
    }
}

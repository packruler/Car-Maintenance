package com.packruler.carmaintenance.sql;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSQL {
    private final String TAG = getClass().getName();

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


    public List<String> getCarNames() {
        LinkedList<String> list = new LinkedList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.VEHICLE_NAME}, null, null, null, null, null);
        if (!cursor.moveToFirst())
            return list;

        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(cursor.getColumnIndex(Vehicle.VEHICLE_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public SQLiteDatabase getWritableDatabase() {
        return sqlHelper.getWritableDatabase();
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

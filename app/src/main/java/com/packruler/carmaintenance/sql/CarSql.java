package com.packruler.carmaintenance.sql;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSql {
    private final String TAG = getClass().getName();

    private SQLHelper sqlHelper;
    private VehicleContainer activity;

    public CarSql(Activity activity) {
        this.activity = (VehicleContainer) activity;
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

    public void putCar(Vehicle vehicle) {
        ContentValues contentValues = vehicle.getContentValues();
        Log.i(TAG, "Put: " + contentValues.toString());

        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        database.insertOrThrow(Vehicle.TABLE_NAME, null, contentValues);
    }

    public void updateCar(Vehicle vehicle) {
        ContentValues contentValues = vehicle.getContentValues();
        Log.i(TAG, "Put: " + contentValues.toString());

        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        database.update(Vehicle.TABLE_NAME, contentValues, null, null);
    }

    public List<ServiceTask> getServiceTasks(String name) {
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(ServiceTask.TABLE_NAME, null, ServiceTask.CAR_NAME + "= " + "\"" + name + "\"", null, null, null, null);
        cursor.moveToFirst();
        LinkedList<ServiceTask> list = new LinkedList<>();
        Log.i(TAG, "Count: " + cursor.getCount());
        while (!cursor.isAfterLast()) {
            ServiceTask task;
            switch (cursor.getString(cursor.getColumnIndex(ServiceTask.TYPE))) {
                case FuelStop.FUEL_STOP:
                    task = new FuelStop(cursor);
                    break;
                default:
                    task = new ServiceTask(cursor);
                    break;
            }
            list.add(task);
            cursor.moveToNext();
        }
        return list;
    }

    public void putServiceTask(ServiceTask serviceTask) {
        ContentValues contentValues = serviceTask.getContentValues();
        Log.i(TAG, "Service Task: " + contentValues.toString());
        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        database.insertOrThrow(ServiceTask.TABLE_NAME, null, contentValues);
    }

    public void loadCars() {
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Vehicle vehicle = new Vehicle(cursor);
            vehicle.setServiceTasks(getServiceTasks(vehicle.getName()));
//            Cursor serviceCursor = database.rawQuery("SELECT * FROM " + ServiceTask.TABLE_NAME + " WHERE " + ServiceTask.CAR_NAME + " = " + vehicle.getName(), null);
//            while (!serviceCursor.isAfterLast()) {
//                ServiceTask task;
//                switch (cursor.getString(cursor.getColumnIndex(ServiceTask.TYPE))) {
//                    case FuelStop.FUEL_STOP:
//                        task = new FuelStop(serviceCursor);
//                        break;
//                    default:
//                        task = new ServiceTask(serviceCursor);
//                        break;
//                }
//                vehicle.addServiceTask(task);
//            }
            Log.i(TAG, "Loaded " + vehicle.getName());
            activity.loadCar(vehicle);
            cursor.moveToNext();
        }
    }

    public interface VehicleContainer {
        void loadCar(Vehicle vehicle);
    }
}

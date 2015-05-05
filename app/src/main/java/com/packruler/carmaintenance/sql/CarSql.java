package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.packruler.carmaintenance.ui.MainActivity;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSql {
    private final String TAG = getClass().getName();

    private SQLHelper sqlHelper;
    private MainActivity activity;

    public CarSql(MainActivity activity) {
        this.activity = activity;
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
            db.execSQL(CarTable.SQL_CREATE);
            db.execSQL(ServiceTable.SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 1:
                    db.execSQL(ServiceTable.SQL_CREATE);
                case 2:
                    db.execSQL("ALTER TABLE " + ServiceTable.TABLE_NAME + " ADD COLUMN " + ServiceTable.COLUMN_NAME_OCTANE + " INT");
            }
        }

    }

    public void putCar(Vehicle vehicle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CarTable.COLUMN_NAME_CAR_NAME, vehicle.getName());
        contentValues.put(CarTable.COLUMN_NAME_MAKE, vehicle.getMake());
        contentValues.put(CarTable.COLUMN_NAME_MODEL, vehicle.getModel());
        contentValues.put(CarTable.COLUMN_NAME_SUBMODEL, vehicle.getSubmodel());
        contentValues.put(CarTable.COLUMN_NAME_YEAR, vehicle.getYear());
        contentValues.put(CarTable.COLUMN_NAME_VIN, vehicle.getVin());
        contentValues.put(CarTable.COLUMN_NAME_WEIGHT, vehicle.getModel());
        contentValues.put(CarTable.COLUMN_NAME_COLOR, vehicle.getColor());
        contentValues.put(CarTable.COLUMN_NAME_BOUGHT_FROM, vehicle.getBoughtFrom());
        contentValues.put(CarTable.COLUMN_NAME_PURCHASE_COST, vehicle.getPurchaseCost());
        contentValues.put(CarTable.COLUMN_NAME_PURCHASE_DATE, vehicle.getPurchaseDate().getTime());
        Log.i(TAG, "Put: " + contentValues.toString());

        SQLiteDatabase database = sqlHelper.getWritableDatabase();
        database.beginTransaction();
        database.insert(CarTable.TABLE_NAME, null, contentValues);
        database.endTransaction();
    }

    public List<ServiceTask> getServiceTasks(String name) {
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(ServiceTable.TABLE_NAME, null, null, null, null, null, null);
        LinkedList<ServiceTask> list = new LinkedList<>();
        while (!cursor.isAfterLast()) {
            if (cursor.getString(0).equals(name)) {
                ServiceTask task;
                switch (cursor.getString(1)) {
                    case FuelStop.FUEL_STOP:
                        task = new FuelStop();
                        ((FuelStop) task).setCostPerVolume(cursor.getFloat(7));
                        ((FuelStop) task).setVolume(cursor.getFloat(8));
                        ((FuelStop) task).setOctane(cursor.getInt(9));
                        break;
                    default:
                        task = new ServiceTask();
                        break;
                }
                task.setCost(cursor.getFloat(2));
                task.setMileage(cursor.getFloat(3));
                task.setDate(new Date(cursor.getLong(4)));
                task.setPlace(cursor.getString(5));
                task.setPlaceName(cursor.getString(6));
                list.add(task);
            }
        }
        return list;
    }

    public void loadCars() {
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(CarTable.TABLE_NAME, null, null, null, null, null, null);
        while (!cursor.isAfterLast()) {
            Vehicle vehicle = new Vehicle(cursor);
            vehicle.setServiceTasks(getServiceTasks(vehicle.getName()));
            activity.loadCar(vehicle);
            cursor.moveToNext();
        }
    }
}

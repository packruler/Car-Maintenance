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

import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSql {
    private final String TAG = getClass().getName();

    private SQLHelper sqlHelper;
    private VehicleContainer activity;

    public static final String[] RESERVED_WORDS = new String[]{"ABORT", "ACTION", "ADD", "AFTER",
            "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ATTACH", "AUTOINCREMENT", "BEFORE",
            "BEGIN", "BETWEEN", "BY", "CASCADE", "CASE", "CAST", "CHECK", "COLLATE", "COLUMN",
            "COMMIT", "CONFLICT", "CONSTRAINT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME",
            "CURRENT_TIMESTAMP", "DATABASE", "DEFAULT", "DEFERRABLE", "DEFERRED", "DELETE", "DESC",
            "DETACH", "DISTINCT", "DROP", "EACH", "ELSE", "END", "ESCAPE", "EXCEPT", "EXCLUSIVE",
            "EXISTS", "EXPLAIN", "FAIL", "FOR", "FOREIGN", "FROM", "FULL", "GLOB", "GROUP", "HAVING",
            "IF", "IGNORE", "IMMEDIATE", "IN", "INDEX", "INDEXED", "INITIALLY", "INNER", "INSERT",
            "INSTEAD", "INTERSECT", "INTO", "IS", "ISNULL", "JOIN", "KEY", "LEFT", "LIKE", "LIMIT",
            "MATCH", "NATURAL", "NO", "NOT", "NOTNULL", "NULL", "OF", "OFFSET", "ON", "OR", "ORDER",
            "OUTER", "PLAN", "PRAGMA", "PRIMARY", "QUERY", "RAISE", "RECURSIVE", "REFERENCES",
            "REGEXP", "REINDEX", "RELEASE", "RENAME", "REPLACE", "RESTRICT", "RIGHT", "ROLLBACK",
            "ROW", "SAVEPOINT", "SELECT", "SET", "TABLE", "TEMP", "TEMPORARY", "THEN", "TO",
            "TRANSACTION", "TRIGGER", "UNION", "UNIQUE", "UPDATE", "USING", "VACUUM", "VALUES",
            "VIEW", "VIRTUAL", "WHEN", "WHERE", "WITH", "WITHOUT"
    };

    public Set<String> RESERVED_STRINGS = new TreeSet<>();

    public CarSql(Activity activity) {
        this.activity = (VehicleContainer) activity;
        sqlHelper = new SQLHelper(activity);
        RESERVED_STRINGS.addAll(Arrays.asList(ServiceTask.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(Vehicle.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(FuelStop.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(PartReplacement.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(RESERVED_WORDS));
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

    }

//    public void putCar(Vehicle vehicle) {
//        ContentValues contentValues = vehicle.getContentValues();
//        Log.i(TAG, "Put: " + contentValues.toString());
//
//        SQLiteDatabase database = sqlHelper.getWritableDatabase();
//        database.insertOrThrow(Vehicle.TABLE_NAME, null, contentValues);
//    }
//
//    public void updateCar(Vehicle vehicle) {
//        ContentValues contentValues = vehicle.getContentValues();
//        Log.i(TAG, "Put: " + contentValues.toString());
//
//        SQLiteDatabase database = sqlHelper.getWritableDatabase();
//        database.update(Vehicle.TABLE_NAME, contentValues, null, null);
//    }
//
//    public List<ServiceTask> getServiceTasks(String name) {
//        SQLiteDatabase database = sqlHelper.getReadableDatabase();
//        Cursor cursor = database.query(ServiceTask.TABLE_NAME, null, ServiceTask.VEHICLE_NAME + "= " + "\"" + name + "\"", null, null, null, null);
//        cursor.moveToFirst();
//        LinkedList<ServiceTask> list = new LinkedList<>();
//        Log.i(TAG, "Count: " + cursor.getCount());
//        while (!cursor.isAfterLast()) {
//            ServiceTask task;
//            switch (cursor.getString(cursor.getColumnIndex(ServiceTask.TYPE))) {
//                case FuelStop.FUEL_STOP:
//                    task = new FuelStop(cursor);
//                    break;
//                default:
//                    task = new ServiceTask(cursor);
//                    break;
//            }
//            list.add(task);
//            cursor.moveToNext();
//        }
//        return list;
//    }
//
//    public void putServiceTask(ServiceTask serviceTask) {
//        ContentValues contentValues = serviceTask.getContentValues();
//        Log.i(TAG, "Service Task: " + contentValues.toString());
//        SQLiteDatabase database = sqlHelper.getWritableDatabase();
//        database.insertOrThrow(ServiceTask.TABLE_NAME, null, contentValues);
//    }
//
//    public void loadCars() {
//        SQLiteDatabase database = sqlHelper.getReadableDatabase();
//        Cursor cursor = database.query(Vehicle.TABLE_NAME, null, null, null, null, null, null);
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            Vehicle vehicle = new Vehicle(cursor);
//            vehicle.setServiceTasks(getServiceTasks(vehicle.getName()));
////            Cursor serviceCursor = database.rawQuery("SELECT * FROM " + ServiceTask.TABLE_NAME + " WHERE " + ServiceTask.VEHICLE_NAME + " = " + vehicle.getName(), null);
////            while (!serviceCursor.isAfterLast()) {
////                ServiceTask task;
////                switch (cursor.getString(cursor.getColumnIndex(ServiceTask.TYPE))) {
////                    case FuelStop.FUEL_STOP:
////                        task = new FuelStop(serviceCursor);
////                        break;
////                    default:
////                        task = new ServiceTask(serviceCursor);
////                        break;
////                }
////                vehicle.addServiceTask(task);
////            }
//            Log.i(TAG, "Loaded " + vehicle.getName());
//            activity.loadCar(vehicle);
//            cursor.moveToNext();
//        }
//    }
//
//    public void updateVehicleData(Vehicle vehicle) {
//        SQLiteDatabase database = sqlHelper.getWritableDatabase();
//        database.update(Vehicle.TABLE_NAME, vehicle.getContentValues(), Vehicle.VEHICLE_NAME + "= " + vehicle.getName(), null);
//    }
//
//    public void updateVehicleName(String oldName, Vehicle vehicle) {
//        SQLiteDatabase database = sqlHelper.getWritableDatabase();
//        database.update(Vehicle.TABLE_NAME, vehicle.getContentValues(), Vehicle.VEHICLE_NAME + "= " + "\"" + oldName + "\"", null);
//        database.delete(ServiceTask.TABLE_NAME, ServiceTask.VEHICLE_NAME + "= " + "\"" + oldName + "\"", null);
//        for (ServiceTask task : vehicle.getServiceTasks()) {
//            database.insert(ServiceTask.TABLE_NAME, null, task.getContentValues());
//        }
//    }

    public interface VehicleContainer {
        void loadCar(Vehicle vehicle);
    }

    public List<Vehicle> getCars() {
        LinkedList<Vehicle> list = new LinkedList<>();
        Cursor cursor = getReadableDatabase().query(Vehicle.TABLE_NAME, new String[]{Vehicle.VEHICLE_NAME}, null, null, null, null, null);
        if (!cursor.moveToFirst())
            return list;

        while (!cursor.isAfterLast()) {
            list.add(new Vehicle(this, cursor.getString(cursor.getColumnIndex(Vehicle.VEHICLE_NAME))));
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

    public boolean checkString(String string) throws SQLDataException {
        return checkString(string, false);
    }

    public boolean checkString(String string, int minimumLength) throws SQLDataException {
        if (string.length() < minimumLength)
            throw new SQLDataException("\"" + string + "\"" + " is too short. Must be at least " + minimumLength + "characters long");
        return checkString(string);
    }

    public boolean checkString(String string, boolean skipCheck) throws SQLDataException {
        long start = System.currentTimeMillis();
        for (String check : RESERVED_STRINGS) {
            if (string.contains(" " + check + " "))
                throw new SQLDataException("Values cannot contain: \"" + check + "\"");
        }
//        Log.i(TAG, "Check string took: " + (System.currentTimeMillis() - start));
        return true;
    }
}

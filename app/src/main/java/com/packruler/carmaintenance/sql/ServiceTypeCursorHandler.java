package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

/**
 * Created by Packruler on 6/5/15.
 */
public class ServiceTypeCursorHandler {
    private static final String TAG = "ServiceTypeCursorHandler";

    public static final String TABLE_NAME = "service_types";
    public static final String TYPE = "type";


    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + TYPE + " STRING PRIMARY KEY" + ")";

    public static Cursor getAvailableTypes(CarSQL carSQL) {
        return carSQL.getReadableDatabase().query(true, TABLE_NAME, null, null, null, null, null, TYPE + " ASC", null);
    }

    public static void addType(CarSQL carSQL, String type) {
        ContentValues values = new ContentValues();
        values.put(TYPE, type);
        carSQL.getWritableDatabase().insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static boolean removeType(CarSQL carSQL, String type) {
        Cursor cursor = carSQL.getReadableDatabase().query(false, ServiceTask.TABLE_NAME, new String[]{ServiceTask.TYPE},
                ServiceTask.TYPE + "= \"" + type + "\"", null, null, null, null, null);
        if (cursor.getCount() == 0) {
            carSQL.getWritableDatabase().delete(TABLE_NAME, TYPE + "= \"" + type + "\"", null);
            return true;
        }
        return false;
    }
}

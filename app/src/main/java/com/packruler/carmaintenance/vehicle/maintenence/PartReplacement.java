package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Packruler on 5/7/15.
 */
public class PartReplacement extends ServiceTask {
    private final String TAG = getClass().getName();

    public static final String TABLE_NAME = "parts";

    public static final String PART_NAME = "part_name";
    public static final String MANUFACTURER = "manufacturer";
    public static final String EXPECTED_LIFE_DISTANCE = "expected_life_distance";
    public static final String EXPECTED_LIFE_TIME = "expected_life_distance";
    public static final String WARRANTY_LIFE_DISTANCE = "warranty_life_distance";
    public static final String WARRANTY_LIFE_TIME = "warranty_life_distance";
    public static final String SERVICE_TASK_ROW = "service_task_row";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + VEHICLE_NAME + " STRING," +
                    DATE + " LONG," + TYPE + " STRING," + COST + " FLOAT," +
                    COST_UNITS + " STRING," + MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," +
                    DETAILS + " STRING," + LOCATION_ID + " STRING," + LOCATION_NAME + " STRING," +
                    SERVICE_TASK_ROW + " LONG" + ")";

    public PartReplacement(CarSQL carSQL, String carName) {
        this.carSQL = carSQL;

        SQLiteDatabase database = carSQL.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_NAME, carName);
        row = database.insert(TABLE_NAME, null, contentValues);
//        Log.v(TAG, "Row: " + row);

        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                ID + "= " + row);
    }

    public PartReplacement(CarSQL carSQL, String carName, ServiceTask task) {
        this(carSQL, carName);
        sqlDataHandler.putLong(SERVICE_TASK_ROW, task.getRow());
    }

    public PartReplacement(CarSQL carSQL, long row) {
        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                ID + "= " + row);
    }

    public String getPartName() {
        return sqlDataHandler.getString(PART_NAME);
    }

    public void setPartName(String partName) throws SQLDataException {
        sqlDataHandler.putString(PART_NAME, partName);
    }

    public String getManufacturer() {
        return sqlDataHandler.getString(MANUFACTURER);
    }

    public void setManufacturer(String manufacturer) {
        sqlDataHandler.putString(MANUFACTURER, manufacturer);
    }

    public int getExpectedLifeDistance() {
        return sqlDataHandler.getInt(EXPECTED_LIFE_DISTANCE);
    }

    public void setExpectedLifeDistance(int expectedLifeDistance) {
        sqlDataHandler.putInt(EXPECTED_LIFE_DISTANCE, expectedLifeDistance);
    }

    public long getExpectedLifeTime() {
        return sqlDataHandler.getLong(EXPECTED_LIFE_TIME);
    }

    public void setExpectedLifeTime(long expectedLifeTime) {
        sqlDataHandler.putLong(EXPECTED_LIFE_TIME, expectedLifeTime);
    }

    public int getWarrantyLifeDistance() {
        return sqlDataHandler.getInt(WARRANTY_LIFE_DISTANCE);
    }

    public void setWarrantyLifeDistance(int warrantyLifeDistance) {
        sqlDataHandler.putInt(WARRANTY_LIFE_DISTANCE, warrantyLifeDistance);
    }

    public long getWarrantyLifeTime() {
        return sqlDataHandler.getLong(WARRANTY_LIFE_DISTANCE);
    }

    public void setWarrantyLifeTime(long warrantyLifeTime) {
        sqlDataHandler.putLong(WARRANTY_LIFE_TIME, warrantyLifeTime);
    }

    public static Cursor getPartReplacementCursorForCar(CarSQL carSQL, String carName) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, null,
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
    }

    public static List<PartReplacement> getPartReplacementsForCar(CarSQL carSQL, String carName) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);

        if (!cursor.moveToFirst())
            return new ArrayList<>(0);

        ArrayList<PartReplacement> list = new ArrayList<>(cursor.getCount());

        while (!cursor.isAfterLast()) {
            list.add(new PartReplacement(carSQL, cursor.getLong(0)));
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("PartReplacement", "PartReplacement size: " + list.size());
        return list;
    }

    public static int getPartReplacementCountForCar(CarSQL carSQL, String carName) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, DATE},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

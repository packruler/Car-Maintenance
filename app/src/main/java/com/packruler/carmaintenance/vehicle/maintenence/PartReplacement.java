package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import java.sql.SQLDataException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 5/7/15.
 */
public class PartReplacement extends ServiceTask {
    private final String TAG = getClass().getName();

    public static final String TABLE_NAME = "parts";

    public static final String PART_NAME = "part_name";
    public static final String MANUFACTURER = "manufacturer";
    public static final String NUMBER = "number";
    public static final String EXPECTED_LIFE_DISTANCE = "expected_life_distance";
    public static final String EXPECTED_LIFE_TIME = "expected_life_distance";
    public static final String WARRANTY_LIFE_DISTANCE = "warranty_life_distance";
    public static final String WARRANTY_LIFE_TIME = "warranty_life_distance";


    public static final String[] RESERVED_WORDS = new String[]{TABLE_NAME, PART_NAME,
            MANUFACTURER, NUMBER, EXPECTED_LIFE_DISTANCE, EXPECTED_LIFE_TIME, WARRANTY_LIFE_DISTANCE,
            WARRANTY_LIFE_TIME
    };

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + VEHICLE_NAME + " STRING," +
                    TASK_NUM + " INTEGER," + TYPE + " STRING," + COST + " FLOAT," + COST_UNITS + " STRING," +
                    MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," + DATE + " STRING," +
                    DETAILS + " STRING," + LOCATION_ID + " STRING," + LOCATION_NAME + " STRING" +
                    ")";

    public PartReplacement(CarSQL carSQL, String carName, int taskNum, boolean skipCheck) {
        this.taskNum = taskNum;
        this.carName = carName;
        this.carSQL = carSQL;

        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                VEHICLE_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum);

        if (!skipCheck) {
            SQLiteDatabase database = carSQL.getWritableDatabase();
            Cursor cursor = database.query(true, TABLE_NAME, new String[]{VEHICLE_NAME},
                    VEHICLE_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null, null, null, null, null);

            if (!cursor.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(VEHICLE_NAME, carName);
                contentValues.put(TASK_NUM, taskNum);
                database.insert(TABLE_NAME, null, contentValues);
            }
            cursor.close();
        }
    }

    public PartReplacement(CarSQL carSQL, String carName, int taskNum) {
        this(carSQL, carName, taskNum, false);
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

    public void setManufacturer(String manufacturer) throws SQLDataException {
        sqlDataHandler.putString(MANUFACTURER, manufacturer);
    }

    public String getNumber() {
        return sqlDataHandler.getString(NUMBER);
    }

    public void setNumber(String number) throws SQLDataException {
        sqlDataHandler.putString(NUMBER, number);
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

    public static List<PartReplacement> getPartReplacementsForCar(CarSQL carSQL, String carName) {
        LinkedList<PartReplacement> list = new LinkedList<>();
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, TASK_NUM},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);

        if (!cursor.moveToFirst())
            return list;

        int taskNum = 0;
        while (!cursor.isAfterLast()) {
            list.add(new PartReplacement(carSQL, carName, ++taskNum));
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("PartReplacement", "PartReplacement size: " + list.size());
        return list;
    }

    public static int getPartReplacementCountForCar(CarSQL carSQL, String carName) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{TASK_NUM},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

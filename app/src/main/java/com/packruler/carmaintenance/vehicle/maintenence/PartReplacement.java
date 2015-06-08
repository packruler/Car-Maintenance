package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Packruler on 5/7/15.
 */
public class PartReplacement extends ServiceTask {
    private final String TAG = getClass().getName();

    public static final String TABLE_NAME = "parts";

    public static final String PART_NAME = "part_name";
    public static final String BRAND = "brand";
    public static final String PRODUCT_NUMBER = "product_number";
    public static final String EXPECTED_LIFE_DISTANCE = "expected_life_distance";
    public static final String EXPECTED_LIFE_TIME = "expected_life_time";
    public static final String WARRANTY_LIFE_DISTANCE = "warranty_life_distance";
    public static final String WARRANTY_LIFE_TIME = "warranty_life_time";
    public static final String SERVICE_TASK_ROW = "service_task_row";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VEHICLE_ROW + " LONG," + DATE + " LONG," + TYPE + " STRING," +
                    COST + " FLOAT," + COST_UNITS + " STRING," + MILEAGE + " LONG," +
                    MILEAGE_UNITS + " STRING," + DETAILS + " STRING," + LOCATION_ID + " STRING," +
                    LOCATION_NAME + " STRING," + SERVICE_TASK_ROW + " LONG," +
                    BRAND + " STRING," + PRODUCT_NUMBER + " STRING," +
                    EXPECTED_LIFE_DISTANCE + " LONG," + EXPECTED_LIFE_TIME + " LONG," +
                    WARRANTY_LIFE_DISTANCE + " LONG," + WARRANTY_LIFE_TIME + " LONG" + ")";

    public PartReplacement(CarSQL carSQL, long row, boolean taskRow) {
        super(carSQL);
        if (!taskRow) {
            sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                    ID + "= " + row);
            this.row = row;
        } else {
            SQLiteDatabase database = carSQL.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_ROW, new ServiceTask(carSQL, row).getVehicleRow());
            contentValues.put(SERVICE_TASK_ROW, row);
            this.row = database.insert(TABLE_NAME, null, contentValues);
//        Log.v(TAG, "Row: " + row);

            sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                    ID + "= " + this.row);
        }
    }

    public PartReplacement(CarSQL carSQL, ServiceTask task) {
        this(carSQL, task.getRow());
        sqlDataHandler.putLong(SERVICE_TASK_ROW, task.getRow());
    }

    public PartReplacement(CarSQL carSQL, long row) {
        this(carSQL, row, false);
    }

    public String getPartName() {
        return sqlDataHandler.getString(PART_NAME);
    }

    public void setPartName(String partName) throws SQLDataException {
        sqlDataHandler.putString(PART_NAME, partName);
    }

    public String getManufacturer() {
        return sqlDataHandler.getString(BRAND);
    }

    public void setManufacturer(String manufacturer) {
        sqlDataHandler.putString(BRAND, manufacturer);
    }

    public String getProductNumber() {
        return sqlDataHandler.getString(PRODUCT_NUMBER);
    }

    public void setProductNumber(String productNumber) {
        sqlDataHandler.putString(PRODUCT_NUMBER, productNumber);
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

    public static Cursor getPartReplacementCursorForCar(CarSQL carSQL, long vehicleRow) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, null,
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);
    }

    public static List<PartReplacement> getPartReplacementsForCar(CarSQL carSQL, long vehicleRow) {
        long start = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);

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

    public static int getPartReplacementCountForCar(CarSQL carSQL, long vehicleRow) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    @Override
    public boolean equals(Object o) {
        return o.getClass().equals(PartReplacement.class) && row == ((PartReplacement) o).getRow();
    }
}

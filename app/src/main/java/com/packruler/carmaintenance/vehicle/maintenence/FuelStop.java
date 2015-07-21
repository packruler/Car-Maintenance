package com.packruler.carmaintenance.vehicle.maintenence;

import android.database.Cursor;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class FuelStop extends ServiceTask {
    private final String TAG = getClass().getSimpleName();

    public static final String TABLE_NAME = "fuel";

    public static final String COST_PER_VOLUME = "cost_per_volume";
    public static final String VOLUME = "volume";
    public static final String OCTANE = "octane";
    public static final String MISSED_FILL_UP = "missed_fill_up";
    public static final String COMPLETE_FILL_UP = "complete_fill_up";
    public static final String DISTANCE_TRAVELED = "distance_traveled";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VEHICLE_ROW + " TEXT," + DATE + " LONG," + DATE_TIME_ZONE + " TEXT," +
                    TYPE + " TEXT," + COST + " FLOAT," + MILEAGE + " LONG," +
                    DETAILS + " TEXT," + LOCATION_ID + " TEXT," +
                    LOCATION_NAME + " TEXT," + COST_PER_VOLUME + " FLOAT," +
                    VOLUME + " FLOAT," + OCTANE + " INT," + MISSED_FILL_UP + " INTEGER," +
                    COMPLETE_FILL_UP + " INTEGER," + DISTANCE_TRAVELED + " LONG" + ")";


    public FuelStop(CarSQL carSQL, long rowId, boolean carRow) {
        super(carSQL, rowId, carRow, TABLE_NAME);
    }

    public FuelStop(CarSQL carSQL, long row) {
        this(carSQL, row, false);
    }

    @Override
    public void delete() {
        delete(TABLE_NAME);
    }

    public float getVolume() {
        return sqlDataHandler.getFloat(VOLUME);
    }

    public void setVolume(float volume) {
        sqlDataHandler.put(VOLUME, volume);
    }

    public void setVolume(String volume) {
        setVolume(Float.parseFloat(cleanNumberString(volume)));
    }

    public boolean isCompleteFillUp() {
        return sqlDataHandler.getBoolean(COMPLETE_FILL_UP);
    }

    public void setCompleteFillUp(boolean completeFillUp) {
        sqlDataHandler.put(COMPLETE_FILL_UP, completeFillUp);
    }

    public boolean missedFillup() {
        return sqlDataHandler.getBoolean(MISSED_FILL_UP);
    }

    public void setMissedFillup(boolean missedFillup) {
        sqlDataHandler.put(MISSED_FILL_UP, missedFillup);
    }

    public int getOctane() {
        return sqlDataHandler.getInt(OCTANE);
    }

    public void setOctane(int octane) {
        sqlDataHandler.put(OCTANE, octane);
    }

    public float getCostPerVolume() {
        return sqlDataHandler.getFloat(COST_PER_VOLUME);
    }

    public void setCostPerVolume(float costPerVolume) {
        sqlDataHandler.put(COST_PER_VOLUME, costPerVolume);
    }

    public void setCostPerVolume(String costPerVolume) {
        setCostPerVolume(Float.parseFloat(cleanNumberString(costPerVolume)));
    }

    public void updateDistanceTraveled() {
        long distance = -1;
        if (isCompleteFillUp() && !missedFillup() && getDate() > 0) {
            Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{MILEAGE},
                    DATE + "< " + getDate(), null, null, null, DATE + " DESC", String.valueOf(1));
            if (cursor.moveToFirst())
                distance = getMileage() - cursor.getInt(cursor.getColumnIndex(MILEAGE));
        }
        if (distance < -1)
            distance = -1;
        sqlDataHandler.put(DISTANCE_TRAVELED, distance);
    }

    public static Cursor getFuelStopCursorForCar(CarSQL carSQL, long vehicleRow) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, null,
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);
    }

    public static Cursor getFuelStopCursorForCar(CarSQL carSQL, long vehicleRow, String orderBy) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, null,
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, orderBy);
    }

    public static List<FuelStop> getFuelStopsForCar(CarSQL carSQL, long vehicleRow) {
        long start = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);

        if (!cursor.moveToFirst())
            return new ArrayList<>(0);

        ArrayList<FuelStop> list = new ArrayList<>(cursor.getCount());
        while (!cursor.isAfterLast()) {
            list.add(new FuelStop(carSQL, cursor.getLong(0)));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("FuelStop", "FuelStop size: " + list.size());

        Log.d("FuelStop", "Task took " + (Calendar.getInstance().getTimeInMillis() - start));
        return list;
    }

    public static int getFuelStopCountForCar(CarSQL carSQL, long vehicleRow) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

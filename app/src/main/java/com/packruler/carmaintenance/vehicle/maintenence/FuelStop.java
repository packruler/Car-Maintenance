package com.packruler.carmaintenance.vehicle.maintenence;

import android.database.Cursor;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class FuelStop extends ServiceTask {
    private final String TAG = getClass().getSimpleName();

    public static final String TABLE_NAME = "fuel";
    public static final String FUEL_STOP = "FUEL_STOP";

    public static final String COST_PER_VOLUME = "cost_per_volume";
    public static final String VOLUME = "volume";
    public static final String VOLUME_UNITS = "volume_units";
    public static final String OCTANE = "octane";
    public static final String OCTANE_UNITS = "octane_units";
    public static final String MISSED_FILL_UP = "missed_fill_up";
    public static final String COMPLETE_FILL_UP = "complete_fill_up";
    public static final String DISTANCE_PER_VOLUME = "distance_per_volume";
    public static final String DISTANCE_PER_VOLUME_UNIT = "distance_per_volume_unit";

    public static final String[] RESERVED_WORDS = new String[]{TABLE_NAME, FUEL_STOP,
            COST_PER_VOLUME, VOLUME, VOLUME_UNITS, OCTANE, OCTANE_UNITS, MISSED_FILL_UP,
            COMPLETE_FILL_UP, DISTANCE_PER_VOLUME, DISTANCE_PER_VOLUME_UNIT
    };

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VEHICLE_ROW + " TEXT," + DATE + " LONG," + TYPE + " TEXT," +
                    COST_UNITS + " TEXT," + COST + " FLOAT," + MILEAGE + " LONG," +
                    MILEAGE_UNITS + " TEXT," + DETAILS + " TEXT," + LOCATION_ID + " TEXT," +
                    LOCATION_NAME + " TEXT," + COST_PER_VOLUME + " FLOAT," +
                    VOLUME + " FLOAT," + VOLUME_UNITS + " TEXT," +
                    OCTANE + " INT," + OCTANE_UNITS + " TEXT," + MISSED_FILL_UP + " INTEGER," +
                    COMPLETE_FILL_UP + " INTEGER," + DISTANCE_PER_VOLUME + " FLOAT," +
                    DISTANCE_PER_VOLUME_UNIT + " TEXT" + ")";


    public FuelStop(CarSQL carSQL, long rowId, boolean carRow) {
        super(carSQL, rowId, carRow, TABLE_NAME);
    }

    public FuelStop(CarSQL carSQL, long row) {
        this(carSQL, row, false);
    }

    public float getVolume() {
        return sqlDataHandler.getFloat(VOLUME);
    }

    public void setVolume(float volume) {
        sqlDataHandler.putFloat(VOLUME, volume);
    }

    public String getVolumeUnit() {
        return sqlDataHandler.getString(VOLUME_UNITS);
    }

    public void setVolumeUnits(String volumeUnits) throws SQLDataException {
        sqlDataHandler.putString(VOLUME_UNITS, volumeUnits);
    }

    public boolean isCompleteFillUp() {
        return sqlDataHandler.getBoolean(COMPLETE_FILL_UP);
    }

    public void setCompleteFillUp(boolean completeFillUp) {
        sqlDataHandler.putBoolean(COMPLETE_FILL_UP, completeFillUp);
    }

    public boolean isMissedFillup() {
        return sqlDataHandler.getBoolean(MISSED_FILL_UP);
    }

    public void setMissedFillup(boolean missedFillup) {
        sqlDataHandler.putBoolean(MISSED_FILL_UP, missedFillup);
    }

    public int getOctane() {
        return sqlDataHandler.getInt(OCTANE);
    }

    public void setOctane(int octane) {
        sqlDataHandler.putInt(OCTANE, octane);
    }

    public String getOctaneUnits() {
        return sqlDataHandler.getString(OCTANE_UNITS);
    }

    public void setOctaneUnits(String octaneUnits) throws SQLDataException {
        sqlDataHandler.putString(OCTANE_UNITS, octaneUnits);
    }

    public float getCostPerVolume() {
        return sqlDataHandler.getFloat(COST_PER_VOLUME);
    }

    public void setCostPerVolume(float costPerVolume) {
        sqlDataHandler.putFloat(COST_PER_VOLUME, costPerVolume);
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

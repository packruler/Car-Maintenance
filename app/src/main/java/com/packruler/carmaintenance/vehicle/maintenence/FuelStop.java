package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class FuelStop extends ServiceTask {
    private final String TAG = getClass().getName();

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
                    VEHICLE_NAME + " STRING," + DATE + " LONG," + TYPE + " STRING," +
                    COST_UNITS + " STRING," + COST + " FLOAT," + MILEAGE + " LONG," +
                    MILEAGE_UNITS + " STRING," + DETAILS + " STRING," + LOCATION_ID + " STRING," +
                    LOCATION_NAME + " STRING," + COST_PER_VOLUME + " FLOAT," +
                    VOLUME + " FLOAT," + VOLUME_UNITS + " STRING," +
                    OCTANE + " INT," + OCTANE_UNITS + " STRING," + MISSED_FILL_UP + " INTEGER," +
                    COMPLETE_FILL_UP + " INTEGER," + DISTANCE_PER_VOLUME + " FLOAT," +
                    DISTANCE_PER_VOLUME_UNIT + " STRING" + ")";


    public FuelStop(CarSQL carSQL, String carName) {
        this.carSQL = carSQL;

        SQLiteDatabase database = carSQL.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_NAME, carName);
        row = database.insert(TABLE_NAME, null, contentValues);
//        Log.v(TAG, "Row: " + row);

        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                ID + "= " + row);
    }

    public FuelStop(CarSQL carSQL, long row) {
        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                ID + "= " + row);
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

    public static Cursor getFuelStopCursorForCar(CarSQL carSQL, String carName) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, null,
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
    }

    public static List<FuelStop> getFuelStopsForCar(CarSQL carSQL, String carName) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);


        if (!cursor.moveToFirst())
            return new ArrayList<>(0);

        ArrayList<FuelStop> list = new ArrayList<>(cursor.getCount());

        while (!cursor.isAfterLast()) {
            list.add(new FuelStop(carSQL, cursor.getLong(0)));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public static int getFuelStopCountForCar(CarSQL carSQL, String carName) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, DATE},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

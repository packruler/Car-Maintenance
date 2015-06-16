package com.packruler.carmaintenance.vehicle.utilities;

import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;

/**
 * Created by Packruler on 6/15/15.
 */
public class UpdateAverageEfficiency extends AsyncTask<Vehicle, Long, Double> {
    private String TAG = getClass().getSimpleName();

    @Override
    protected Double doInBackground(Vehicle... params) {
        double volume = 0;
        long currentMileage = -1, lastMileage = -1, netMileage = 0;
        Cursor cursor;
        boolean cont = true;
        for (Vehicle vehicle : params) {
            cursor = vehicle.getFuelStopCursor();
            if (cursor.moveToFirst()) {
                Log.v(TAG, "Date: " + cursor.getString(cursor.getColumnIndex(FuelStop.DATE)));
                while (!cursor.isLast() && !cursor.isAfterLast()) {
                    if (cursor.getInt(cursor.getColumnIndex(FuelStop.COMPLETE_FILL_UP)) == 1 &&
                            cursor.getInt(cursor.getColumnIndex(FuelStop.MISSED_FILL_UP)) == 0) {
                        volume += cursor.getDouble(cursor.getColumnIndex(FuelStop.VOLUME));
                        if (currentMileage == -1)
                            currentMileage = cursor.getLong(cursor.getColumnIndex(FuelStop.MILEAGE));

                        cursor.moveToNext();
                        lastMileage = cursor.getLong(cursor.getColumnIndex(FuelStop.MILEAGE));

                        netMileage = currentMileage - lastMileage;
                        currentMileage = lastMileage;
                    } else
                        cursor.moveToNext();
                }
            }
        }
        return null;
    }

    private double getVolume(Cursor cursor) {
        return cursor.getDouble(cursor.getColumnIndex(FuelStop.VOLUME));
    }

    private int getMileage(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(FuelStop.MILEAGE));
    }
}

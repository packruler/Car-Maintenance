package com.packruler.carmaintenance.vehicle.maintenence;

import android.database.Cursor;

/**
 * Created by Packruler on 4/27/2015.
 */
public class FuelStop extends ServiceTask {
    public static final String FUEL_STOP = "FUEL_STOP";

    private float volume;
    private boolean completeFillUp;
    private boolean missedFillup;
    private int octane;
    private float costPerVolume;

    public FuelStop(int taskNum) {
        super(taskNum);
        super.setType(FUEL_STOP);
    }

    public FuelStop(Cursor cursor) {
        super(cursor);
        setVolume(cursor.getFloat(cursor.getColumnIndex(VOLUME)));
        setCompleteFillUp(cursor.getInt(cursor.getColumnIndex(COMPLETE_FILL_UP)));
        setMissedFillup(cursor.getInt(cursor.getColumnIndex(MISSED_FILL_UP)));
        setOctane(cursor.getInt(cursor.getColumnIndex(OCTANE)));
        setCostPerVolume(cursor.getFloat(cursor.getColumnIndex(COST_PER_VOLUME)));
    }

    public void setVolume(float in) {
        volume = in;
    }

    public float getVolume() {
        return volume;
    }

    public void setCompleteFillUp(boolean completeFillUp) {
        this.completeFillUp = completeFillUp;
        super.contentValues.put(COMPLETE_FILL_UP, completeFillUp);
    }

    public void setCompleteFillUp(int completeFillUp) {
        if (completeFillUp == 1)
            setCompleteFillUp(true);
        else
            setCompleteFillUp(false);
    }

    public boolean isCompleteFillUp() {
        return completeFillUp;
    }

    public void setMissedFillup(boolean in) {
        missedFillup = in;
        super.contentValues.put(MISSED_FILL_UP, missedFillup);
    }

    public void setMissedFillup(int missedFillup) {
        if (missedFillup == 1)
            setMissedFillup(true);
        else
            setMissedFillup(false);
    }

    public boolean isMissedFillup() {
        return missedFillup;
    }

    public int getOctane() {
        return octane;
    }

    public void setOctane(int octane) {
        this.octane = octane;
        super.contentValues.put(OCTANE, octane);
    }

    public float getCostPerVolume() {
        return costPerVolume;
    }

    public void setCostPerVolume(float costPerVolume) {
        this.costPerVolume = costPerVolume;
        super.contentValues.put(COST_PER_VOLUME, costPerVolume);
    }
}

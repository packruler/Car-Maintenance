package com.packruler.carmaintenance.vehicle.maintenence;

import org.json.JSONException;

/**
 * Created by Packruler on 4/27/2015.
 */
public class FuelStop extends ServiceTask {
    public static final String FUEL_STOP = "FUEL_STOP";
    public static final String VOLUME = "VOLUME";
    public static final String FILLED = "FILLED";
    public static final String MISSED_FILLUP = "MISSED_FILLUP";
    public static final String OCTANE = "OCTANE";

    private float volume;
    private boolean filled;
    private boolean missedFillup;
    private int octane;
    private float costPerVolume;

    public FuelStop() {
        super.setTask(FUEL_STOP);
    }

//    public FuelStop(JSONObject jsonObject) throws JSONException {
//        super(jsonObject);
//        if (!super.getTask().equals(FUEL_STOP))
//            throw new RuntimeException("NOT FUEL STOP");
//
//        volume = jsonObject.getDouble(VOLUME);
//        filled = jsonObject.getBoolean(FILLED);
//    }

    public void setVolume(float in) {
        volume = in;
    }

    public float getVolume() {
        return volume;
    }

    public void setFilled(boolean in) throws JSONException {
        filled = in;
    }

    public boolean getFilled() {
        return filled;
    }

    public void setMissedFillup(boolean in) {
        missedFillup = in;
    }

    public boolean getMissedFillup() {
        return missedFillup;
    }

    public int getOctane() {
        return octane;
    }

    public void setOctane(int octane) {
        this.octane = octane;
    }

    public float getCostPerVolume() {
        return costPerVolume;
    }

    public void setCostPerVolume(float costPerVolume) {
        this.costPerVolume = costPerVolume;
    }
}

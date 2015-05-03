package com.packruler.carmaintenance.vehicle.maintenence;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Packruler on 4/27/2015.
 */
public class FuelStop extends ServiceTask {
    public static final String FUEL_STOP = "FUEL_STOP";
    public static final String VOLUME = "VOLUME";
    public static final String FILLED = "FILLED";
    public static final String MISSED_FILLUP = "MISSED_FILLUP";
    public static final String OCTANE = "OCTANE";

    private double volume;
    private boolean filled;
    private boolean missedFillup;
    private int octane;

    public FuelStop() {
        super.setTask("Fuel");
    }

    public FuelStop(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
        if (!super.getTask().equals(FUEL_STOP))
            throw new RuntimeException("NOT FUEL STOP");

        volume = jsonObject.getDouble(VOLUME);
        filled = jsonObject.getBoolean(FILLED);
    }

    public void setVolume(long in) {
        volume = in;
    }

    public double getVolume() {
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

    @Override
    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = super.getJSONObject();

        jsonObject.put(VOLUME, volume);
        jsonObject.put(FILLED, filled);
        jsonObject.put(MISSED_FILLUP, missedFillup);

        return super.getJSONObject();
    }

    public int getOctane() {
        return octane;
    }

    public void setOctane(int octane) {
        this.octane = octane;
    }
}

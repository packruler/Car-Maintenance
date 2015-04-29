package com.packruler.carmaintenance;

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

    private long volume;
    private boolean filled;
    private boolean missedFillup;

    public FuelStop() throws JSONException {
        super();
        super.setTask("Fuel");

    }

    public FuelStop(JSONObject in) throws JSONException {
        super(in);
        if (!super.getTask().equals(FUEL_STOP))
            throw new RuntimeException("NOT FUEL STOP");

        volume = super.jsonObject.getLong(VOLUME);
        filled = super.jsonObject.getBoolean(FILLED);
    }

    public void setVolume(long in) throws JSONException {
        volume = in;
        super.jsonObject.put(VOLUME, volume);
    }

    public long getVolume() {
        return volume;
    }

    public void setFilled(boolean in) throws JSONException {
        filled = in;
        super.jsonObject.put(FILLED, filled);
    }

    public boolean getFilled() {
        return filled;
    }

    public void setMissedFillup(boolean in) throws JSONException {
        missedFillup = in;
        super.jsonObject.put(MISSED_FILLUP, missedFillup);
    }

    public boolean getMissedFillup() {
        return missedFillup;
    }
}

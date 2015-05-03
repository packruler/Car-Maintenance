package com.packruler.carmaintenance.vehicle.race;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Packruler on 4/29/15.
 */
public class Lap {
    public static final String START_POSITION = "START_POSITION";
    public static final String END_POSITION = "END_POSITION";
    public static final String DURATION = "DURATION";

    private int startPosition;
    private int endPosition;
    private double duration;

    public Lap() {

    }

    public Lap(JSONObject jsonObject) throws JSONException {
        startPosition = jsonObject.getInt(START_POSITION);
        endPosition = jsonObject.getInt(END_POSITION);
        duration = jsonObject.getDouble(DURATION);
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(START_POSITION, startPosition);
        jsonObject.put(END_POSITION, endPosition);
        jsonObject.put(DURATION, duration);

        return jsonObject;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public int getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(int endPosition) {
        this.endPosition = endPosition;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}

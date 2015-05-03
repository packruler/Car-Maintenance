package com.packruler.carmaintenance.vehicle.race;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by Packruler on 4/29/15.
 */
public class RoadCourse extends Race {
    public static final String LAPS = "LAPS";
    public static final String START_POSITION= "START_POSITION";
    public static final String END_POSITION= "END_POSITION";

    private LinkedList<Lap> laps= new LinkedList<>();
    private int startPosition;
    private int endPosition;

    public RoadCourse(){
    }

    public RoadCourse(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray(LAPS);
        for (int x = 0; x < jsonArray.length(); x++){
            laps.add(new Lap(jsonArray.getJSONObject(x)));
        }

        setStartPosition(jsonObject.getInt(START_POSITION));
        setEndPosition(jsonObject.getInt(END_POSITION));
    }

    @Override
    public JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = super.getJsonObject();
        jsonObject.put(START_POSITION,startPosition);
        jsonObject.put(END_POSITION,endPosition);

        JSONArray jsonArray = new JSONArray();
        for (Lap lap : laps){
            jsonArray.put(lap.getJsonObject());
        }

        return jsonObject;
    }

    public LinkedList<Lap> getLaps() {
        return laps;
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

    public void addLap(Lap lap){
        laps.add(lap);
    }

    public boolean removeLap(Lap lap){
        return laps.remove(lap);
    }
}

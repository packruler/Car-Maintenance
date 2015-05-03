package com.packruler.carmaintenance.vehicle.race;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Packruler on 4/29/15.
 */
public class Race {
    public static final String STYLE = "STYLE";
    public static final String TIME = "TIME";
    public static final String DISTANCE = "DISTANCE";
    public static final String TRACK = "TRACK";
    public static final String CIRCUIT = "CIRCUIT";
    public static final String DATE = "DATE";
    public static final String PLACE = "PLACE";

    private String style;
    private long time;
    private double distance;
    private String track;
    private String circuit;
    private Date date;
    private Place place;

    public Race() {

    }

    public Race(String style) {
        setStyle(style);
    }

    public Race(JSONObject jsonObject) throws JSONException {
        style = jsonObject.getString(STYLE);
        time = jsonObject.getLong(TIME);
        distance = jsonObject.getLong(DISTANCE);


    }


    public JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(STYLE, style);
        jsonObject.put(TIME, time);
        jsonObject.put(DISTANCE, distance);
        jsonObject.put(TRACK, track);
        jsonObject.put(CIRCUIT, circuit);
        jsonObject.put(DATE, date.getTime());

        if (place != null)
            jsonObject.put(PLACE, place.getId());

        return jsonObject;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time){
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getCircuit() {
        return circuit;
    }

    public void setCircuit(String circuit) {
        this.circuit = circuit;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }
}

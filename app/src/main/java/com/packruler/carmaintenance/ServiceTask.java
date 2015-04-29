package com.packruler.carmaintenance;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Packruler on 4/27/2015.
 */
public class ServiceTask {
    public static final String TASK = "TASK";
    public static final String DETAILS = "DETAILS";
    public static final String PLACE_NAME = "PLACE_NAME";
    public static final String PLACE = "PLACE";
    public static final String DATE = "DATE";
    public static final String COST = "COST";
    public static final String MILEAGE = "MILEAGE";

//    GeoDataApi geoDataApi = new G

    private String task = "";
    private String details = "";
    private String placeName = "";
    private Place place;
    private Date date = new Date();
    private long cost;
    private long mileage;
    protected JSONObject jsonObject = new JSONObject();

    public ServiceTask() throws JSONException {
        jsonObject.put(TASK, task);
        jsonObject.put(DETAILS, details);
        jsonObject.put(PLACE, Long.MIN_VALUE);
        jsonObject.put(DATE, date.getTime());
        jsonObject.put(COST, cost);
        jsonObject.put(MILEAGE, mileage);
    }

    public ServiceTask(JSONObject in) throws JSONException {
        jsonObject = in;
        task = jsonObject.getString(TASK);
        details = jsonObject.getString(DETAILS);
        placeName = jsonObject.getString(PLACE_NAME);
//        place = GeoDataApi.
        date = new Date(jsonObject.getLong(DATE));

    }

    public void setTask(String name) throws JSONException {
        task = name;
        jsonObject.put(TASK, task);
    }

    public String getTask() {
        return task;
    }

    public void setDetails(String in) throws JSONException {
        details = in;
        jsonObject.put(DETAILS, details);
    }

    public String getDetails() {
        return details;
    }

    public void setPlaceName(String in) throws JSONException {
        placeName = in;
        jsonObject.put(PLACE_NAME, placeName);
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlace(Place in) throws JSONException {
        place = in;
        setPlaceName(place.getName().toString());
        jsonObject.put(PLACE, place.getId());
    }

    public Place getPlace() {
        return place;
    }

    public void setDate(Date in) throws JSONException {
        date = in;
        jsonObject.put(DATE, date.getTime());
    }

    public Date getDate() {
        return date;
    }

    public void setCost(long in) throws JSONException {
        cost = in;
        jsonObject.put(COST, cost);
    }

    public long getCost() {
        return cost;
    }

    public void setMileage(long in) throws JSONException {
        mileage = in;
        jsonObject.put(MILEAGE, mileage);
    }

    public long getMileage() {
        return mileage;
    }

    public JSONObject getJSONObject() throws JSONException {
        jsonObject.put("task", task);
        jsonObject.put("details", details);
        jsonObject.put("place", place);
        jsonObject.put("date", date.getTime());
        jsonObject.put("cost", cost);
        jsonObject.put("mileage", mileage);
        return jsonObject;
    }
}

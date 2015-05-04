package com.packruler.carmaintenance.vehicle.maintenence;

import com.google.android.gms.location.places.Place;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by Packruler on 4/27/2015.
 */
public class ServiceTask {
    public static final String TYPE = "TYPE";
    public static final String DETAILS = "DETAILS";
    public static final String PLACE_NAME = "PLACE_NAME";
    public static final String PLACE = "PLACE";
    public static final String DATE = "DATE";
    public static final String COST = "PURCHASE_COST";
    public static final String MILEAGE = "MILEAGE";

//    GeoDataApi geoDataApi = new G

    private String task = "";
    private String details = "";
    private String placeName = "";
    private Place place;
    private Date date = new Date();
    private float cost;
    private float mileage;

    public ServiceTask() {
    }

//    public ServiceTask(JSONObject jsonObject) throws JSONException {
//        task = jsonObject.getString(TYPE);
//        details = jsonObject.getString(DETAILS);
//        placeName = jsonObject.getString(PLACE_NAME);
////        place = googleApiClient.getPlaceById(googleApiClient, jsonObject.getString(PLACE));
//        date = new Date(jsonObject.getLong(DATE));
//        cost = jsonObject.getDouble(COST);
//        mileage = jsonObject.getDouble(MILEAGE);
//
//    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getTask() {
        return task;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceName() {
        return placeName;
    }

    //TODO: setPlace by id
    public void setPlace(String id){

    }
    public void setPlace(Place place) {
        this.place = place;
        setPlaceName(this.place.getName().toString());
    }

    public Place getPlace() {
        return place;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public float getCost() {
        return cost;
    }

    public void setMileage(float mileage){
        this.mileage = mileage;
    }

    public float getMileage() {
        return mileage;
    }

    public JSONObject getJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(TYPE, task);
        jsonObject.put(DETAILS, details);
        jsonObject.put(PLACE_NAME, placeName);
        jsonObject.put(DATE, date.getTime());
        jsonObject.put(COST, cost);
        jsonObject.put(MILEAGE, mileage);

        if (place != null)
            jsonObject.put(PLACE, place.getId());

        return jsonObject;
    }
}

package com.packruler.carmaintenance.vehicle;

import android.util.Log;

import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Packruler on 4/27/2015.
 */
public class Vehicle {
    private final String TAG = getClass().getName();
    public static final String NAME = "NAME";
    public static final String MAKE = "MAKE";
    public static final String MODEL = "MODEL";
    public static final String SUBMODEL = "SUBMODEL";
    public static final String YEAR = "YEAR";
    public static final String SERVICE_TASKS = "SERVICE_TASKS";
    public static final String MILEAGE = "MILEAGE";
    public static final String PURCHASE_DATE = "PURCHASE_DATE";
    public static final String PURCHASE_COST = "PURCHASE_COST";

    private String name="";
    private String make="";
    private String model="";
    private String submodel="";
    private int year;
    private LinkedList<ServiceTask> serviceTasks;
    private double mileage;
    private Date purchase_date;
    private double purchaseCost;

    public Vehicle() {
        serviceTasks = new LinkedList<>();
        purchase_date = Calendar.getInstance().getTime();
    }

    public Vehicle(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString(NAME);
        mileage = jsonObject.getLong(MILEAGE);
        purchase_date = new Date(jsonObject.getLong(PURCHASE_DATE));
        make = jsonObject.getString(MAKE);
        model = jsonObject.getString(MODEL);
        submodel = jsonObject.getString(SUBMODEL);
        year = jsonObject.getInt(YEAR);
        purchaseCost = jsonObject.getDouble(PURCHASE_COST);

        serviceTasks = new LinkedList<>();
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(SERVICE_TASKS);
            for (int x = 0; x < jsonObject.length(); x++) {
                JSONObject current = jsonArray.getJSONObject(x);
                if (current.getString(ServiceTask.TASK).equals(FuelStop.FUEL_STOP))
                    serviceTasks.add(new FuelStop(current));
                else
                    serviceTasks.add(new ServiceTask(current));
            }
        }catch (JSONException e){
            Log.i(TAG, "JSONException JSONArray");
        }
    }

    public JSONObject getJsonObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(NAME, name);
        jsonObject.put(MAKE, make);
        jsonObject.put(MODEL, model);
        jsonObject.put(SUBMODEL, submodel);
        jsonObject.put(YEAR, year);
        jsonObject.put(SERVICE_TASKS, serviceTasks);
        jsonObject.put(MILEAGE, mileage);
        jsonObject.put(PURCHASE_DATE, purchase_date.getTime());
        jsonObject.put(PURCHASE_COST, purchaseCost);

        return jsonObject;
    }

    public void setName(String in){
        name = in;
    }

    public String getName() {
        return name;
    }

    public void setMake(String in){
        make = in;
    }

    public String getMake() {
        return make;
    }

    public void setModel(String in){
        model = in;
    }

    public String getModel() {
        return model;
    }

    public void setSubmodel(String in){
        this.submodel = in;
    }

    public String getSubmodel() {
        return submodel;
    }

    public void setYear(int in)  {
        year = in;
    }

    public int getYear() {
        return year;
    }

    public void setMileage(long in)  {
        mileage = in;
    }

    public double getMileage() {
        return mileage;
    }

    public void setPurchaseDate(Date in)  {
        purchase_date = in;
    }

    public Date getPurchase_date() {
        return purchase_date;
    }

    public void addServiceTask(ServiceTask in)  {
        serviceTasks.add(in);
    }

    public double getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(double purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public List<ServiceTask> getServiceTasks() {
        return serviceTasks;
    }

    public Map<String, String> getDetailValues(){
        TreeMap<String, String> map = new TreeMap<>();
        map.put(NAME, name);
        map.put(YEAR, ""+year);
        map.put(MAKE, make);
        map.put(MODEL, model);
        map.put(SUBMODEL, submodel);
        map.put(MILEAGE, ""+mileage);
        map.put(PURCHASE_COST, ""+purchaseCost);
        map.put(PURCHASE_DATE, "" + purchase_date.getTime());
        return map;
    }

    public Comparator<ServiceTask> dateComparator = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
    };

    private Comparator<ServiceTask> mileageComparator = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return (int) (lhs.getMileage() - rhs.getMileage());
        }
    };

    private Comparator<ServiceTask> costComparator = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return (int) (lhs.getCost() - rhs.getCost());
        }
    };
}

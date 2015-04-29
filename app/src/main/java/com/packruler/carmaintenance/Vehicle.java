package com.packruler.carmaintenance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class Vehicle {
    public static final String NAME = "NAME";
    public static final String MAKE = "MAKE";
    public static final String MODEL = "MODEL";
    public static final String SUBMODEL = "SUBMODEL";
    public static final String YEAR = "YEAR";
    public static final String SERVICE_TASKS = "SERVICE_TASKS";
    public static final String MILEAGE = "MILEAGE";
    public static final String PURCHASED = "PURCHASED";

    private String name;
    private String make;
    private String model;
    private String submodel;
    private int year;
    private LinkedList<ServiceTask> serviceTasks;
    private long mileage;
    private Date purchased;

    public Vehicle() {
        serviceTasks = new LinkedList<>();
    }

    public Vehicle(JSONObject jsonObject) throws JSONException {
        name = jsonObject.getString(NAME);
        mileage = jsonObject.getLong(MILEAGE);
        purchased = new Date(jsonObject.getLong(PURCHASED));
        make = jsonObject.getString(MAKE);
        model = jsonObject.getString(MODEL);
        submodel = jsonObject.getString(SUBMODEL);
        year = jsonObject.getInt(YEAR);

        serviceTasks = new LinkedList<>();
        if (jsonObject.getJSONArray(SERVICE_TASKS).length() > 0) {
            JSONArray jsonArray = jsonObject.getJSONArray(SERVICE_TASKS);
            for (int x = 0; x < jsonObject.length(); x++) {
                JSONObject current = jsonArray.getJSONObject(x);
                if (current.getString(ServiceTask.TASK).equals(FuelStop.FUEL_STOP))
                    serviceTasks.add(new FuelStop(current));
                else
                    serviceTasks.add(new ServiceTask(current));
            }
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
        jsonObject.put(PURCHASED, purchased);

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

    public long getMileage() {
        return mileage;
    }

    public void setPurchased(Date in)  {
        purchased = in;
    }

    public Date getPurchased() {
        return purchased;
    }

    public void addServiceTask(ServiceTask in)  {
        serviceTasks.add(in);
    }

    public List<ServiceTask> getServiceTasks() {
        return serviceTasks;
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

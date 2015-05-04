package com.packruler.carmaintenance.vehicle;

import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
    public static final String VIN = "VIN";

    private String name="";
    private String make="";
    private String model="";
    private String submodel="";
    private int year;
    private String vin="";
    private LinkedList<ServiceTask> serviceTasks = new LinkedList<>();
    private long weight;
    private double mileage;
    private Date purchase_date = new Date();
    private double purchaseCost;
    private String color="";
    private String boughtFrom="";

    public Vehicle() {
        serviceTasks = new LinkedList<>();
        purchase_date = Calendar.getInstance().getTime();
    }

//    public Vehicle(JSONObject jsonObject) throws JSONException {
//        name = jsonObject.getString(NAME);
//        mileage = jsonObject.getLong(MILEAGE);
//        purchase_date = new Date(jsonObject.getLong(PURCHASE_DATE));
//        make = jsonObject.getString(MAKE);
//        model = jsonObject.getString(MODEL);
//        submodel = jsonObject.getString(SUBMODEL);
//        year = jsonObject.getInt(YEAR);
//        purchaseCost = jsonObject.getDouble(PURCHASE_COST);
//        vin = jsonObject.getString(VIN);
//
//        serviceTasks = new LinkedList<>();
//        try {
//            JSONArray jsonArray = jsonObject.getJSONArray(SERVICE_TASKS);
//            for (int x = 0; x < jsonObject.length(); x++) {
//                JSONObject current = jsonArray.getJSONObject(x);
//                if (current.getString(ServiceTask.TASK).equals(FuelStop.FUEL_STOP))
//                    serviceTasks.add(new FuelStop(current));
//                else
//                    serviceTasks.add(new ServiceTask(current));
//            }
//        } catch (JSONException e) {
//            Log.i(TAG, "JSONException JSONArray");
//        }
//    }

//    public JSONObject getJsonObject() throws JSONException {
//        JSONObject jsonObject = new JSONObject();
//
//        jsonObject.put(NAME, name);
//        jsonObject.put(MAKE, make);
//        jsonObject.put(MODEL, model);
//        jsonObject.put(SUBMODEL, submodel);
//        jsonObject.put(YEAR, year);
//        jsonObject.put(SERVICE_TASKS, serviceTasks);
//        jsonObject.put(MILEAGE, mileage);
//        jsonObject.put(PURCHASE_DATE, purchase_date.getTime());
//        jsonObject.put(PURCHASE_COST, purchaseCost);
//
//        return jsonObject;
//    }

    public void setName(String in) {
        name = in;
    }

    public String getName() {
        return name;
    }

    public void setMake(String in) {
        make = in;
    }

    public String getMake() {
        return make;
    }

    public void setModel(String in) {
        model = in;
    }

    public String getModel() {
        return model;
    }

    public void setSubmodel(String in) {
        this.submodel = in;
    }

    public String getSubmodel() {
        return submodel;
    }

    public void setYear(int in) {
        year = in;
    }

    public int getYear() {
        return year;
    }

    public void setMileage(long in) {
        mileage = in;
    }

    public double getMileage() {
        return mileage;
    }

    public void setPurchaseDate(Date in) {
        purchase_date = in;
    }

    public Date getPurchaseDate() {
        return purchase_date;
    }

    public void addServiceTask(ServiceTask in) {
        serviceTasks.add(in);
    }

    public double getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(double purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
    }

    public String getBoughtFrom() {
        return boughtFrom;
    }

    public void setBoughtFrom(String boughtFrom) {
        this.boughtFrom = boughtFrom;
    }
}

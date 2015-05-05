package com.packruler.carmaintenance.vehicle;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

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

    public static final String TABLE_NAME = "vehicles";
    public static final String CAR_NAME = "car_name";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String SUBMODEL = "submodel";
    public static final String YEAR = "year";
    public static final String VIN = "vin";
    public static final String WEIGHT = "weight";
    public static final String MILEAGE = "mileage";
    public static final String COLOR = "color";
    public static final String PURCHASE_DATE = "purchase_date";
    public static final String BOUGHT_FROM = "bought_from";
    public static final String PURCHASE_COST = "purchase_cost";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    CAR_NAME + " STRING PRIMARY KEY," +
                    MAKE + " STRING," + MODEL + " STRING," +
                    SUBMODEL + " STRING," + YEAR + " INTEGER," +
                    VIN + " STRING," + WEIGHT + " LONG," +
                    MILEAGE + " FLOAT," + COLOR + " STRING," +
                    PURCHASE_DATE + " LONG," + BOUGHT_FROM + " STRING," +
                    PURCHASE_COST + " FLOAT" + ")";

    private String name = "";
    private String make = "";
    private String model = "";
    private String submodel = "";
    private int year;
    private String vin = "";
    private List<ServiceTask> serviceTasks = new LinkedList<>();
    private long weight;
    private float mileage;
    private Date purchaseDate = new Date();
    private float purchaseCost;
    private String color = "";
    private String boughtFrom = "";

    private ContentValues contentValues = new ContentValues();

    public Vehicle() {
        serviceTasks = new LinkedList<>();
        setPurchaseDate(Calendar.getInstance().getTime());
    }

    public Vehicle(Cursor cursor) {
        name = cursor.getString(cursor.getColumnIndex(CAR_NAME));
        make = cursor.getString(cursor.getColumnIndex(MAKE));
        model = cursor.getString(cursor.getColumnIndex(MODEL));
        submodel = cursor.getString(cursor.getColumnIndex(SUBMODEL));
        year = cursor.getInt(cursor.getColumnIndex(YEAR));
        vin = cursor.getString(cursor.getColumnIndex(VIN));
        weight = cursor.getLong(cursor.getColumnIndex(WEIGHT));
        mileage = cursor.getFloat(cursor.getColumnIndex(MILEAGE));
        purchaseDate = new Date(cursor.getLong(cursor.getColumnIndex(PURCHASE_DATE)));
        purchaseCost = cursor.getFloat(cursor.getColumnIndex(PURCHASE_COST));
        color = cursor.getString(cursor.getColumnIndex(COLOR));
        boughtFrom = cursor.getString(cursor.getColumnIndex(BOUGHT_FROM));
    }

    public void setName(String name) {
        this.name = name;
        contentValues.put(CAR_NAME, this.name);
    }

    public String getName() {
        return name;
    }

    public void setMake(String make) {
        this.make = make;
        contentValues.put(MAKE, make);
    }

    public String getMake() {
        return make;
    }

    public void setModel(String model) {
        this.model = model;
        contentValues.put(MODEL, model);
    }

    public String getModel() {
        return model;
    }

    public void setSubmodel(String submodel) {
        this.submodel = submodel;
        contentValues.put(SUBMODEL, submodel);
    }

    public String getSubmodel() {
        return submodel;
    }

    public void setYear(int year) {
        this.year = year;
        contentValues.put(YEAR, year);
    }

    public int getYear() {
        return year;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
        contentValues.put(MILEAGE, mileage);
    }

    public float getMileage() {
        return mileage;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
        contentValues.put(PURCHASE_DATE, purchaseDate.getTime());
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void addServiceTask(ServiceTask serviceTask) {
        serviceTasks.add(serviceTask);
    }

    public void setServiceTasks(List<ServiceTask> serviceTasks) {
        this.serviceTasks = serviceTasks;
    }

    public List<ServiceTask> getServiceTasks() {
        return serviceTasks;
    }

    public float getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(float purchaseCost) {
        this.purchaseCost = purchaseCost;
        contentValues.put(PURCHASE_COST, purchaseCost);
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
        contentValues.put(VIN, vin);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        contentValues.put(COLOR, color);
    }

    public long getWeight() {
        return weight;
    }

    public void setWeight(long weight) {
        this.weight = weight;
        contentValues.put(WEIGHT, weight);
    }

    public String getBoughtFrom() {
        return boughtFrom;
    }

    public void setBoughtFrom(String boughtFrom) {
        this.boughtFrom = boughtFrom;
        contentValues.put(BOUGHT_FROM, boughtFrom);
    }

    public ContentValues getContentValues() {
        Log.i(TAG, "Content Values: " + contentValues.toString());

        return contentValues;
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

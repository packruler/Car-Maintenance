package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.location.places.Place;
import com.packruler.carmaintenance.sql.CarSql;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.sql.SQLDataException;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Packruler on 4/27/2015.
 */
public class ServiceTask {
    public static final String TABLE_NAME = "service";
    public static final String GENERAL_TYPE = "GENERAL";
    public static final String CAR_NAME = "car_name";
    public static final String TASK_NUM = "task_num";
    public static final String TYPE = "type";
    public static final String DETAILS = "details";
    public static final String COST = "cost";
    public static final String COST_UNITS = "cost_units";
    public static final String MILEAGE = "mileage";
    public static final String MILEAGE_UNITS = "mileage_units";
    public static final String DATE = "date";
    public static final String LOCATION_ID = "location_id";
    public static final String LOCATION_NAME = "location_name";

    //For Gas
    public static final String COST_PER_VOLUME = "cost_per_volume";
    public static final String VOLUME = "volume";
    public static final String VOLUME_UNITS = "volume_units";
    public static final String OCTANE = "octane";
    public static final String OCTANE_UNITS = "octane_units";
    public static final String MISSED_FILL_UP = "MISSED_FILL_UP";
    public static final String COMPLETE_FILL_UP = "COMPLETE_FILL_UP";

    public static final String[] RESERVED_WORDS = new String[]{TABLE_NAME, GENERAL_TYPE, CAR_NAME};

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + CAR_NAME + " STRING," +
                    TASK_NUM + " INTEGER," + TYPE + " STRING," + COST + " FLOAT," +
                    MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," + DATE + " STRING," +
                    DETAILS + " STRING," + LOCATION_ID + " STRING," + LOCATION_NAME + " STRING," +
                    COST_PER_VOLUME + " FLOAT," + COST_UNITS + " STRING," +
                    VOLUME + " FLOAT," + VOLUME_UNITS + " STRING," +
                    OCTANE + " INT," + OCTANE_UNITS + " STRING," + MISSED_FILL_UP + " INTEGER," +
                    COMPLETE_FILL_UP + " INTEGER" + ")";

//    GeoDataApi geoDataApi = new G

    protected ContentValues contentValues = new ContentValues();
    private int taskNum;
    private String carName;
    private String type = "";
    private String details = "";
    private String locationName = "";
    private Place location;
    private Date date = new Date();
    private float cost;
    private float mileage;
    private SQLiteDatabase database;
    private Set<String> RESERVED_STRINGS = new TreeSet<>(Arrays.asList(RESERVED_WORDS));

    public ServiceTask(int taskNum) {
        RESERVED_STRINGS.addAll(Arrays.asList(CarSql.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(Vehicle.RESERVED_WORDS));

        this.taskNum = taskNum;
        contentValues.put(TASK_NUM, taskNum);
        setType(GENERAL_TYPE);
    }

    public ServiceTask(Cursor cursor) {
        RESERVED_STRINGS.addAll(Arrays.asList(CarSql.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(Vehicle.RESERVED_WORDS));

        setCarName(cursor.getString(cursor.getColumnIndex(CAR_NAME)));
        setTaskNum(cursor.getInt(cursor.getColumnIndex(TASK_NUM)));
        setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        setDetails(cursor.getString(cursor.getColumnIndex(DETAILS)));
        setLocationName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)));
//        TODO: set location from placeId
//        setLocation();
        setDate(new Date(cursor.getLong(cursor.getColumnIndex(DATE))));
        setCost(cursor.getFloat(cursor.getColumnIndex(COST)));
        setMileage(cursor.getFloat(cursor.getColumnIndex(MILEAGE)));
    }

    public String getCarName() {
        return carName;
    }

    public boolean setCarName(String carName) {
        this.carName = carName;
        contentValues.put(CAR_NAME, carName);
        return true;
    }

    public boolean canUseCarName(String carName) throws SQLDataException {
        if (carName.length() < 3)
            throw new SQLDataException("Car name must be more than 3 characters long");

        Cursor nameCursor = database.query(TABLE_NAME, new String[]{CAR_NAME}, null, null, null, null, null);
        nameCursor.moveToFirst();
        while (!nameCursor.isAfterLast()) {
            if (nameCursor.getString(nameCursor.getColumnIndex(CAR_NAME)).equalsIgnoreCase(carName))
                throw new SQLDataException(carName + " already in use");
        }

        if (RESERVED_STRINGS.contains(carName))
            throw new SQLDataException(carName + " cannot be used");

        return true;
    }

    public int getTaskNum() {
        return taskNum;
    }

    private void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
        contentValues.put(TASK_NUM, taskNum);
    }

    public void setType(String type) {
        this.type = type;
        contentValues.put(ServiceTask.TYPE, type);
    }

    public String getType() {
        return type;
    }

    public void setDetails(String details) {
        this.details = details;
        contentValues.put(DETAILS, details);
    }

    public String getDetails() {
        return details;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
        contentValues.put(LOCATION_NAME, locationName);
    }

    public String getLocationName() {
        return locationName;
    }

    //TODO: setPlace by id
    public void setLocation(String id) {

    }

    public void setPlace(Place location) {
        this.location = location;
        setLocationName(this.location.getName().toString());
        contentValues.put(LOCATION_ID, this.location.getId());
    }

    public Place getLocation() {
        return location;
    }

    public void setDate(Date date) {
        this.date = date;
        contentValues.put(DATE, date.getTime());
    }

    public Date getDate() {
        return date;
    }

    public void setCost(float cost) {
        this.cost = cost;
        contentValues.put(COST, cost);
    }

    public float getCost() {
        return cost;
    }

    public void setMileage(float mileage) {
        this.mileage = mileage;
        contentValues.put(MILEAGE, mileage);
    }

    public float getMileage() {
        return mileage;
    }

    public ContentValues getContentValues() {
        return contentValues;
    }
}

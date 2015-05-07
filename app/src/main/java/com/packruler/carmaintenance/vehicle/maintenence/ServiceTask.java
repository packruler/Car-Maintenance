package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;

import com.packruler.carmaintenance.sql.CarSql;
import com.packruler.carmaintenance.vehicle.Vehicle;

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
    public static final String MISSED_FILL_UP = "missed_fill_up";
    public static final String COMPLETE_FILL_UP = "complete_fill_up";
    public static final String DISTANCE_PER_VOLUME = "distance_per_volume";
    public static final String DISTANCE_PER_VOLUME_UNIT = "distance_per_volume_unit";

    public static final String[] RESERVED_WORDS = new String[]{TABLE_NAME, GENERAL_TYPE, CAR_NAME};

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + CAR_NAME + " STRING," +
                    TASK_NUM + " INTEGER," + TYPE + " STRING," + COST + " FLOAT," +
                    MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," + DATE + " STRING," +
                    DETAILS + " STRING," + LOCATION_ID + " STRING," + LOCATION_NAME + " STRING," +
                    COST_PER_VOLUME + " FLOAT," + COST_UNITS + " STRING," +
                    VOLUME + " FLOAT," + VOLUME_UNITS + " STRING," +
                    OCTANE + " INT," + OCTANE_UNITS + " STRING," + MISSED_FILL_UP + " INTEGER," +
                    COMPLETE_FILL_UP + " INTEGER," + DISTANCE_PER_VOLUME + " FLOAT," +
                    DISTANCE_PER_VOLUME_UNIT + " STRING," + ")";

//    GeoDataApi geoDataApi = new G

    protected ContentValues contentValues = new ContentValues();
    private int taskNum;
    private String carName;
    //    private String type = "";
//    private String details = "";
//    private String locationName = "";
//    private String location;
//    private Date date = new Date();
//    private float cost;
//    private float mileage;
    private CarSql carSql;
    private Set<String> RESERVED_STRINGS = new TreeSet<>(Arrays.asList(RESERVED_WORDS));

    public ServiceTask(CarSql carSql, String carName, int taskNum) {
        RESERVED_STRINGS.addAll(Arrays.asList(CarSql.RESERVED_WORDS));
        RESERVED_STRINGS.addAll(Arrays.asList(Vehicle.RESERVED_WORDS));

        this.taskNum = taskNum;
        this.carName = carName;
        this.carSql = carSql;
    }

//    public ServiceTask(Cursor cursor) {
//        RESERVED_STRINGS.addAll(Arrays.asList(CarSql.RESERVED_WORDS));
//        RESERVED_STRINGS.addAll(Arrays.asList(Vehicle.RESERVED_WORDS));
//
//        setCarName(cursor.getString(cursor.getColumnIndex(CAR_NAME)));
//        setTaskNum(cursor.getInt(cursor.getColumnIndex(TASK_NUM)));
//        setType(cursor.getString(cursor.getColumnIndex(TYPE)));
//        setDetails(cursor.getString(cursor.getColumnIndex(DETAILS)));
//        setLocationName(cursor.getString(cursor.getColumnIndex(LOCATION_NAME)));
////        TODO: set location from placeId
////        setLocation();
//        setDate(new Date(cursor.getLong(cursor.getColumnIndex(DATE))));
//        setCost(cursor.getFloat(cursor.getColumnIndex(COST)));
//        setMileage(cursor.getFloat(cursor.getColumnIndex(MILEAGE)));
//    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        setString(CAR_NAME, carName);
        this.carName = carName;
    }

    public int getTaskNum() {
        return taskNum;
    }

    private void setTaskNum(int taskNum) {
        setInt(TASK_NUM, taskNum);
        this.taskNum = taskNum;
    }

    public String getString(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{DETAILS},
                CAR_NAME + "= \"" + carName + "\"" + " AND " + TASK_NUM + "= \"" + taskNum + "\"",
                null, null, null, null);
        return cursor.getString(cursor.getColumnIndex(column));
    }

    public void setString(String column, String value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        carSql.getWritableDatabase().update(TABLE_NAME, contentValues,
                CAR_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null);
    }

    public float getFloat(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{DETAILS},
                CAR_NAME + "= \"" + carName + "\"" + " AND " + TASK_NUM + "= \"" + taskNum + "\"",
                null, null, null, null);
        return cursor.getFloat(cursor.getColumnIndex(column));
    }

    public void setFloat(String column, float value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        carSql.getWritableDatabase().update(TABLE_NAME, contentValues,
                CAR_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null);
    }

    public int getInt(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{DETAILS},
                CAR_NAME + "= \"" + carName + "\"" + " AND " + TASK_NUM + "= \"" + taskNum + "\"",
                null, null, null, null);
        return cursor.getInt(cursor.getColumnIndex(column));
    }

    public void setInt(String column, int value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        carSql.getWritableDatabase().update(TABLE_NAME, contentValues,
                CAR_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null);
    }

    public double getDouble(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{DETAILS},
                CAR_NAME + "= \"" + carName + "\"" + " AND " + TASK_NUM + "= \"" + taskNum + "\"",
                null, null, null, null);
        return cursor.getDouble(cursor.getColumnIndex(column));
    }

    public void setDouble(String column, double value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        carSql.getWritableDatabase().update(TABLE_NAME, contentValues,
                CAR_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null);
    }

    public long getLong(String column) {
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{DETAILS},
                CAR_NAME + "= \"" + carName + "\"" + " AND " + TASK_NUM + "= \"" + taskNum + "\"",
                null, null, null, null);
        return cursor.getLong(cursor.getColumnIndex(column));
    }

    public void setLong(String column, long value) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, value);
        carSql.getWritableDatabase().update(TABLE_NAME, contentValues,
                CAR_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null);
    }

    public void setType(String type) {
        setString(TYPE, type);
    }

    public String getType() {
        return getString(TYPE);
    }

    public void setDetails(String details) {
        setString(DETAILS, details);
    }

    public String getDetails() {
        return getString(DETAILS);
    }

    public void setLocationName(String locationName) {
        setString(LOCATION_NAME, locationName);
    }

    public String getLocationName() {
        return getString(LOCATION_NAME);
    }

    public void setLocationId(String locationId) {
        setString(LOCATION_ID, locationId);
    }

    public String getLocationId() {
        return getString(LOCATION_ID);
    }

    public void setDate(long date) {
        setLong(DATE, date);
    }

    public Date getDate() {
        return new Date(getLong(DATE));
    }

    public void setCost(float cost) {
        setFloat(COST, cost);
    }

    public float getCost() {
        return getFloat(COST);
    }

    public void setCostUnits(String units) {
        setString(COST_UNITS, units);
    }

    public String getCostUnits() {
        return getString(COST_UNITS);
    }

    public void setMileage(float mileage) {
        setFloat(MILEAGE, mileage);
    }

    public float getMileage() {
        return getFloat(MILEAGE);
    }
}

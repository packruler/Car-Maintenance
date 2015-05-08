package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSql;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import java.sql.SQLDataException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class ServiceTask {
    private final String TAG = getClass().getName();
    public static final String TABLE_NAME = "service";
    public static final String GENERAL_TYPE = "GENERAL";
    public static final String VEHICLE_NAME = "vehicle_name";
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


    public static final String[] RESERVED_WORDS = new String[]{TABLE_NAME, GENERAL_TYPE, VEHICLE_NAME,
            TASK_NUM, TYPE, DETAILS, COST, COST_UNITS, MILEAGE, MILEAGE_UNITS, DATE, LOCATION_ID};

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + VEHICLE_NAME + " STRING," +
                    TASK_NUM + " INTEGER," + TYPE + " STRING," + COST + " FLOAT," +
                    MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," + DATE + " STRING," +
                    DETAILS + " STRING," + LOCATION_ID + " STRING," + LOCATION_NAME + " STRING," +
                    COST_UNITS + " STRING" + ")";

//    GeoDataApi geoDataApi = new G

    //    protected ContentValues contentValues = new ContentValues();
    protected int taskNum;
    protected String carName;
    protected CarSql carSql;
    protected SQLDataHandler sqlDataHandler;

    protected ServiceTask() {

    }

    public ServiceTask(CarSql carSql, String carName, int taskNum) {

        this.taskNum = taskNum;
        this.carName = carName;
        this.carSql = carSql;

        sqlDataHandler = new SQLDataHandler(carSql, TABLE_NAME,
                VEHICLE_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum);

        SQLiteDatabase database = carSql.getWritableDatabase();
        Cursor cursor = database.query(true, TABLE_NAME, new String[]{VEHICLE_NAME},
                VEHICLE_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum, null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_NAME, carName);
            contentValues.put(TASK_NUM, taskNum);
            database.insert(TABLE_NAME, null, contentValues);
        }
        cursor.close();
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) throws SQLDataException {
        sqlDataHandler.putString(VEHICLE_NAME, carName, 3);
        this.carName = carName;
        sqlDataHandler.setSelection(VEHICLE_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum);
    }

    public void setCarName(String carName, boolean skipCheck) throws SQLDataException {
        if (skipCheck)
            sqlDataHandler.putString(VEHICLE_NAME, carName, skipCheck);
        else
            setCarName(carName);
    }

    public int getTaskNum() {
        return taskNum;
    }

    private void setTaskNum(int taskNum) {
        sqlDataHandler.putInt(TASK_NUM, taskNum);
        this.taskNum = taskNum;
        sqlDataHandler.setSelection(VEHICLE_NAME + "= \"" + carName + "\" AND " + TASK_NUM + "= " + taskNum);
    }

    public void setType(String type) throws SQLDataException {
        sqlDataHandler.putString(TYPE, type);
    }

    public String getType() {
        return sqlDataHandler.getString(TYPE);
    }

    public void setDetails(String details) throws SQLDataException {
        sqlDataHandler.putString(DETAILS, details);
    }

    public String getDetails() {
        return sqlDataHandler.getString(DETAILS);
    }

    public void setLocationName(String locationName) throws SQLDataException {
        sqlDataHandler.putString(LOCATION_NAME, locationName);
    }

    public String getLocationName() {
        return sqlDataHandler.getString(LOCATION_NAME);
    }

    public void setLocationId(String locationId) throws SQLDataException {
        sqlDataHandler.putString(LOCATION_ID, locationId);
    }

    public String getLocationId() {
        return sqlDataHandler.getString(LOCATION_ID);
    }

    public void setDate(long date) {
        sqlDataHandler.putLong(DATE, date);
    }

    public Date getDate() {
        return new Date(sqlDataHandler.getLong(DATE));
    }

    public void setCost(float cost) {
        sqlDataHandler.putFloat(COST, cost);
    }

    public float getCost() {
        return sqlDataHandler.getFloat(COST);
    }

    public void setCostUnits(String units) throws SQLDataException {
        sqlDataHandler.putString(COST_UNITS, units);
    }

    public String getCostUnits() {
        return sqlDataHandler.getString(COST_UNITS);
    }

    public void setMileage(float mileage) {
        sqlDataHandler.putFloat(MILEAGE, mileage);
    }

    public float getMileage() {
        return sqlDataHandler.getFloat(MILEAGE);
    }

    public void setContentValues(ContentValues contentValues) {
        sqlDataHandler.setContentValues(contentValues);
    }

    public static List<ServiceTask> getServiceTasksForCar(CarSql carSql, String carName) {
        LinkedList<ServiceTask> list = new LinkedList<>();
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, TASK_NUM},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);

        if (!cursor.moveToFirst())
            return list;

        int taskNum = 0;
        while (!cursor.isAfterLast()) {
            list.add(new ServiceTask(carSql, carName, ++taskNum));
            cursor.moveToNext();
        }
        cursor.close();
        Log.i("ServiceTasks", "ServiceTask size: " + list.size());
        return list;
    }

    public static int getServiceTasksCountForCar(CarSql carSql, String carName) {
        Cursor cursor = carSql.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, TASK_NUM},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
}

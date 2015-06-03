package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.sql.SQLDataException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class ServiceTask {
    private final String TAG = getClass().getName();
    public static final String TABLE_NAME = "service";
    public static final String ID = "_id";
    public static final String GENERAL_TYPE = "GENERAL";
    public static final String VEHICLE_NAME = "vehicle_name";
    public static final String TYPE = "type";
    public static final String DETAILS = "details";
    public static final String COST = "cost";
    public static final String COST_UNITS = "cost_units";
    public static final String MILEAGE = "mileage";
    public static final String MILEAGE_UNITS = "mileage_units";
    public static final String DATE = "date";
    public static final String LOCATION_ID = "location_id";
    public static final String LOCATION_NAME = "location_name";
    public static final String PARTS_REPLACED = "parts_replaced";


    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VEHICLE_NAME + " STRING," + DATE + " LONG," + TYPE + " STRING," +
                    COST + " FLOAT," + MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," +
                    DETAILS + " STRING," + LOCATION_ID + " STRING," + LOCATION_NAME + " STRING," +
                    COST_UNITS + " STRING," + PARTS_REPLACED + " STRING" + ")";

    protected long row;
    protected CarSQL carSQL;
    protected SQLDataHandler sqlDataHandler;

    protected ServiceTask() {

    }

    public ServiceTask(CarSQL carSQL, String carName) {
        this.carSQL = carSQL;

        SQLiteDatabase database = carSQL.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(VEHICLE_NAME, carName);
        row = database.insert(TABLE_NAME, null, contentValues);
//        Log.v(TAG, "Row: " + row);

        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                ID + "= " + row);
    }

    public ServiceTask(CarSQL carSQL, long row) {
        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                ID + "= " + row);
    }

    public long getRow() {
        return row;
    }

    public String getCarName() {
        return sqlDataHandler.getString(VEHICLE_NAME);
    }

    public void setCarName(String carName) {
        sqlDataHandler.putString(VEHICLE_NAME, carName);
    }

    public void setType(String type) {
        sqlDataHandler.putString(TYPE, type);
    }

    public String getType() {
        return sqlDataHandler.getString(TYPE);
    }

    public void setDetails(String details) {
        sqlDataHandler.putString(DETAILS, details);
    }

    public String getDetails() {
        return sqlDataHandler.getString(DETAILS);
    }

    public void setLocationName(String locationName) {
        sqlDataHandler.putString(LOCATION_NAME, locationName);
    }

    public String getLocationName() {
        return sqlDataHandler.getString(LOCATION_NAME);
    }

    public void setLocationId(String locationId) {
        sqlDataHandler.putString(LOCATION_ID, locationId);
    }

    public String getLocationId() {
        return sqlDataHandler.getString(LOCATION_ID);
    }

    public void setDate(long date) {
        sqlDataHandler.putLong(DATE, date);
    }

    public long getDateLong() {
        return sqlDataHandler.getLong(DATE);
    }

    public Date getDate() {
        return new Date(getDateLong());
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

    public void setMileage(long mileage) {
        sqlDataHandler.putLong(MILEAGE, mileage);
    }

    public long getMileage() {
        return sqlDataHandler.getLong(MILEAGE);
    }

    public void setPartsReplaced(List<PartReplacement> partsReplaced) {
        JSONArray jsonArray = new JSONArray();
        for (PartReplacement part : partsReplaced) {
            jsonArray.put(part.getRow());
        }
        sqlDataHandler.putString(PARTS_REPLACED, jsonArray.toString());
    }

    public List<PartReplacement> getPartsReplaced() {
        if (sqlDataHandler.getString(PARTS_REPLACED) != null) {
            try {
                JSONArray jsonArray = new JSONArray(sqlDataHandler.getString(PARTS_REPLACED));
                ArrayList<PartReplacement> parts = new ArrayList<>(jsonArray.length());
                for (int x = 0; x < jsonArray.length(); x++) {
                    parts.add(new PartReplacement(carSQL, jsonArray.getLong(x)));
                }
                return parts;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new LinkedList<>();
    }

    public void setContentValues(ContentValues contentValues) {
        sqlDataHandler.setContentValues(contentValues);
    }

    public static Cursor getServiceTaskCursorForCar(CarSQL carSQL, String carName) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, null,
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
    }

    public static List<ServiceTask> getServiceTasksForCar(CarSQL carSQL, String carName) {
        long start = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);

        if (!cursor.moveToFirst())
            return new ArrayList<>(0);

        ArrayList<ServiceTask> list = new ArrayList<>(cursor.getCount());
        while (!cursor.isAfterLast()) {
            list.add(new ServiceTask(carSQL, cursor.getLong(0)));
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("ServiceTasks", "ServiceTask size: " + list.size());

        Log.d("ServiceTasks", "Task took " + (Calendar.getInstance().getTimeInMillis() - start));
        return list;
    }

    public static int getServiceTasksCountForCar(CarSQL carSQL, String carName) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, DATE},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
        int count = cursor.getCount();
        Log.v("ServiceTaskCount", "Name: " + carName + " Count: " + count);
        cursor.close();
        return count;
    }

    public static Comparator<ServiceTask> compareByDate = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return Float.compare(lhs.getDateLong(), rhs.getDateLong());
        }
    };

    public static Comparator<ServiceTask> compareByCost = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return Float.compare(lhs.getCost(), rhs.getCost());
        }
    };
}

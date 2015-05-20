package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;

import java.sql.SQLDataException;
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


    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + VEHICLE_NAME + " STRING," +
                    DATE + " LONG," + TYPE + " STRING," + COST + " FLOAT," +
                    MILEAGE + " LONG," + MILEAGE_UNITS + " STRING," + DETAILS + " STRING," +
                    LOCATION_ID + " STRING," + LOCATION_NAME + " STRING," +
                    COST_UNITS + " STRING" + ")";

//    GeoDataApi geoDataApi = new G

    //    protected ContentValues contentValues = new ContentValues();
    protected long date;
    protected String carName;
    protected CarSQL carSQL;
    protected SQLDataHandler sqlDataHandler;

    protected ServiceTask() {

    }

    public ServiceTask(CarSQL carSQL, String carName, long date, boolean isNew) {
        this.carName = carName;
        this.carSQL = carSQL;

        if (isNew) {
            SQLiteDatabase database = carSQL.getWritableDatabase();
            date = checkDate(date);
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_NAME, carName);
            contentValues.put(DATE, date);
            database.insert(TABLE_NAME, null, contentValues);
        }

        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                VEHICLE_NAME + "= \"" + carName + "\" AND " + DATE + "= " + date);
        this.date = date;
    }

    public ServiceTask(CarSQL carSQL, String carName, long date) {
        this(carSQL, carName, date, false);
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        sqlDataHandler.putString(VEHICLE_NAME, carName);
        this.carName = carName;
        sqlDataHandler.setSelection(VEHICLE_NAME + "= \"" + carName + "\" AND " + DATE + "= " + date);
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

    /**
     * Check date for collisions and return value that is not colliding
     *
     * @param date
     *         Requested date to set value to
     *
     * @return value that can be used with the same minute.
     *
     * @throws RuntimeException
     *         if >60,000 values at the same minute have been added to database
     */
    public long checkDate(long date) {
        return checkDate(date, carSQL.getReadableDatabase());
    }

    private long checkDate(long date, SQLiteDatabase database) {
        Cursor cursor = database.query(true, TABLE_NAME, new String[]{VEHICLE_NAME, DATE},
                VEHICLE_NAME + "= \"" + carName + "\" AND " +
                        DATE + ">= " + date + " AND " + DATE + "< " + (date + 60000), null, null, null, null, null);

        if (!cursor.moveToLast())
            Log.v(TAG, "Date input with no collisions");
        else if (cursor.getLong(cursor.getColumnIndex(DATE)) == date + 60000)
            //TODO: Develop method to go back through all values trying to find first open time
            throw new RuntimeException("Attempted to store >60,000 service tasks on the same date");
        else {
            date = cursor.getLong(cursor.getColumnIndex(DATE)) + 1;
            Log.v(TAG, "Collision at date moved to " + date);
        }

        cursor.close();
        return date;
    }

    public void setDate(long date) {
        sqlDataHandler.putLong(DATE, date);
    }

    public long getDateLong() {
        return date;
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

    public void setMileage(long mileage) {
        sqlDataHandler.putLong(MILEAGE, mileage);
    }

    public long getMileage() {
        return sqlDataHandler.getLong(MILEAGE);
    }

    public void setContentValues(ContentValues contentValues) {
        sqlDataHandler.setContentValues(contentValues);
    }

    public static Cursor getServiceTaskCursorForCar(CarSQL carSQL, String carName) {
        return carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{VEHICLE_NAME, DATE},
                VEHICLE_NAME + "= \"" + carName + "\"", null, null, null, null);
    }

    public static List<ServiceTask> getServiceTasksForCar(CarSQL carSQL, String carName) {
        LinkedList<ServiceTask> list = new LinkedList<>();
        long start = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = getServiceTaskCursorForCar(carSQL, carName);

        if (!cursor.moveToFirst())
            return list;

        while (!cursor.isAfterLast()) {
            list.add(new ServiceTask(carSQL, carName, cursor.getLong(cursor.getColumnIndex(DATE)), false));
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

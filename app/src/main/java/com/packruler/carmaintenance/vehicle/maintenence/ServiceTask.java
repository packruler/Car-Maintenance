package com.packruler.carmaintenance.vehicle.maintenence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;
import com.packruler.carmaintenance.sql.SQLDataOberservable;
import com.packruler.carmaintenance.vehicle.Vehicle;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class ServiceTask extends SQLDataOberservable {
    private final String TAG = getClass().getSimpleName();
    public static final String TABLE_NAME = "service";
    public static final String ID = "_id";
    public static final String GENERAL_TYPE = "GENERAL";
    public static final String VEHICLE_ROW = "vehicle_row";
    public static final String TYPE = "type";
    public static final String DETAILS = "details";
    public static final String COST = "cost";
    public static final String MILEAGE = "mileage";
    public static final String DATE = "date";
    public static final String LOCATION_ID = "location_id";
    public static final String LOCATION_NAME = "location_name";
    public static final String PARTS_REPLACED = "parts_replaced";


    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VEHICLE_ROW + " LONG," + DATE + " LONG," + TYPE + " TEXT," +
                    COST + " FLOAT," + MILEAGE + " LONG," + DETAILS + " TEXT," +
                    LOCATION_ID + " TEXT," + LOCATION_NAME + " TEXT," +
                    PARTS_REPLACED + " TEXT" + ")";

    protected CarSQL carSQL;
    protected SQLDataHandler sqlDataHandler;

    protected ServiceTask(CarSQL carSQL) {
        this.carSQL = carSQL;
    }

    protected ServiceTask(CarSQL carSQL, long row, boolean carRow, String TABLE_NAME) {
        this(carSQL);
        if (!carRow) {
            init(row, TABLE_NAME);
        } else {
            SQLiteDatabase database = carSQL.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_ROW, row);
            init(database.insert(TABLE_NAME, null, contentValues), TABLE_NAME);
        }
    }

    public ServiceTask(CarSQL carSQL, long row, boolean carRow) {
        this(carSQL, row, carRow, TABLE_NAME);
    }

    public ServiceTask(CarSQL carSQL, long row) {
        this(carSQL, row, false);
    }

    protected void init(long rowId, String TABLE_NAME) {
        table = TABLE_NAME;
        row = rowId;
        sqlDataHandler = new SQLDataHandler(carSQL, table, row, this);
    }

    public long getRow() {
        return row;
    }

    public boolean beginTransaction() {
        return sqlDataHandler.beginTransaction();
    }

    public boolean endTransaction() {
        return sqlDataHandler.endTransaction();
    }

    public long getVehicleRow() {
        return sqlDataHandler.getLong(VEHICLE_ROW);
    }

    public Vehicle getVehicle() {
        return new Vehicle(carSQL, getVehicleRow());
    }

    public void setType(String type) {
        sqlDataHandler.put(TYPE, type);
    }

    public String getType() {
        return sqlDataHandler.getString(TYPE);
    }

    public void setDetails(String details) {
        sqlDataHandler.put(DETAILS, details);
    }

    public String getDetails() {
        return sqlDataHandler.getString(DETAILS);
    }

    public void setLocationName(String locationName) {
        sqlDataHandler.put(LOCATION_NAME, locationName);
    }

    public String getLocationName() {
        return sqlDataHandler.getString(LOCATION_NAME);
    }

    public void setLocationId(String locationId) {
        sqlDataHandler.put(LOCATION_ID, locationId);
    }

    public String getLocationId() {
        return sqlDataHandler.getString(LOCATION_ID);
    }

    public void setDate(long date) {
        sqlDataHandler.put(DATE, date);
    }

    public long getDate() {
        return sqlDataHandler.getLong(DATE);
    }


    public void setCost(float cost) {
        sqlDataHandler.put(COST, cost);
    }

    public float getCost() {
        return sqlDataHandler.getFloat(COST);
    }

    public void setMileage(long mileage) {
        sqlDataHandler.put(MILEAGE, mileage);
    }

    public long getMileage() {
        return sqlDataHandler.getLong(MILEAGE);
    }

    public void setPartsReplaced(List<PartReplacement> partsReplaced) {
        JSONArray jsonArray = new JSONArray();
        for (PartReplacement part : partsReplaced) {
            jsonArray.put(part.getRow());
        }
        sqlDataHandler.put(PARTS_REPLACED, jsonArray.toString());
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
        sqlDataHandler.putContentValues(contentValues);
    }

    public void delete() {
//        String type = getType();
        boolean success = carSQL.getWritableDatabase().delete(TABLE_NAME, ID + "= " + row, null) == 1;
        Log.v(TAG, "Delete row " + row + ": " + (success ? "SUCCESS" : "FAILED"));
//        if (success && type != null)
//            ServiceTypeCursorHandler.removeType(carSQL, type);
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(ServiceTask.class))
            return row == ((ServiceTask) o).getRow();
        return false;
    }

    public static Cursor getServiceTaskCursorForCar(CarSQL carSQL, long vehicleRow) {
        Log.v("ServiceTaskCursorForCar", "" + (carSQL != null));
        return carSQL.getReadableDatabase().query(false, TABLE_NAME, null,
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null, null);
    }

    public static List<ServiceTask> getServiceTasksForCar(CarSQL carSQL, long vehicleRow) {
        long start = Calendar.getInstance().getTimeInMillis();

        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);

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

    public static int getServiceTasksCountForCar(CarSQL carSQL, long vehicleRow) {
        Cursor cursor = carSQL.getReadableDatabase().query(TABLE_NAME, new String[]{ID},
                VEHICLE_ROW + "= " + vehicleRow, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public static Comparator<ServiceTask> compareByDate = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return Float.compare(lhs.getDate(), rhs.getDate());
        }
    };

    public static Comparator<ServiceTask> compareByCost = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return Float.compare(lhs.getCost(), rhs.getCost());
        }
    };
}

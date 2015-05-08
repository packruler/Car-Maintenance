package com.packruler.carmaintenance.vehicle;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.sql.SQLDataException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class Vehicle {
    private final String TAG = getClass().getName();

    public static final String TABLE_NAME = "vehicles";
    public static final String VEHICLE_NAME = "vehicle_name";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String SUBMODEL = "submodel";
    public static final String YEAR = "year";
    public static final String VIN = "vin";
    public static final String WEIGHT = "weight";
    public static final String WEIGHT_UNITS = "weight_units";
    public static final String MILEAGE = "mileage";
    public static final String MILEAGE_UNITS = "mileage_units";
    public static final String COLOR = "color";
    public static final String PURCHASE_DATE = "purchase_date";
    public static final String BOUGHT_FROM = "bought_from";
    public static final String PURCHASE_COST = "purchase_cost";
    public static final String COST_UNITS = "cost_units";
    public static final String DISPLACEMENT = "displacement";
    public static final String DISPLACEMENT_UNITS = "displacement_units";
    public static final String HORSEPOWER = "horsepower";
    public static final String HORSEPOWER_UNITS = "horsepower_units";
    public static final String TORQUE = "torque";
    public static final String TORQUE_UNITS = "torque_units";

    public static final String[] RESERVED_WORDS = new String[]{
            TABLE_NAME, VEHICLE_NAME, MAKE, MODEL, SUBMODEL, YEAR, VIN, WEIGHT, WEIGHT_UNITS, MILEAGE,
            MILEAGE_UNITS, COLOR, PURCHASE_DATE, BOUGHT_FROM, PURCHASE_COST, COST_UNITS,
            DISPLACEMENT, DISPLACEMENT_UNITS, HORSEPOWER, HORSEPOWER_UNITS, TORQUE, TORQUE_UNITS
    };

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    VEHICLE_NAME + " STRING PRIMARY KEY," +
                    MAKE + " STRING," + MODEL + " STRING," +
                    SUBMODEL + " STRING," + YEAR + " INTEGER," +
                    VIN + " STRING," + WEIGHT + " LONG," + WEIGHT_UNITS + " STRING," +
                    DISPLACEMENT + " FLOAT," + DISPLACEMENT_UNITS + " STRING," +
                    HORSEPOWER + " FLOAT," + HORSEPOWER_UNITS + " STRING," +
                    TORQUE + " FLOAT," + TORQUE_UNITS + " STRING," +
                    MILEAGE + " FLOAT," + MILEAGE_UNITS + " STRING," + COLOR + " STRING," +
                    PURCHASE_DATE + " LONG," + BOUGHT_FROM + " STRING," +
                    PURCHASE_COST + " FLOAT," + COST_UNITS + " STRING" + ")";

    protected CarSQL carSQL;
    private String name = "";
    private String make = "";
    private String model = "";
    private String submodel = "";
    private int year;
    private String vin = "";
    private long weight;
    private float mileage;
    private Date purchaseDate = new Date();
    private float purchaseCost;
    private String color = "";
    private String boughtFrom = "";

    private SQLDataHandler sqlDataHandler;
    private ContentValues contentValues = new ContentValues();
    private Cursor cursor;
    private int serviceTaskCount = 0;
    private int fuelStopCount = 0;
    private int partCount = 0;

    public Vehicle(CarSQL carSQL, String name) {
        this.name = name;
        this.carSQL = carSQL;
        sqlDataHandler = new SQLDataHandler(carSQL, TABLE_NAME,
                VEHICLE_NAME + "= \"" + this.name + "\"");

        SQLiteDatabase database = carSQL.getWritableDatabase();
        Cursor cursor = database.query(true, TABLE_NAME, new String[]{VEHICLE_NAME},
                VEHICLE_NAME + "= \"" + name + "\"", null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_NAME, name);
            database.insert(TABLE_NAME, null, contentValues);
        }

        cursor.close();
        serviceTaskCount = ServiceTask.getServiceTasksCountForCar(carSQL, name);
        fuelStopCount = FuelStop.getServiceTasksCountForCar(carSQL, name);
        partCount = PartReplacement.getPartReplacementCountForCar(carSQL, name);
    }

    public void setName(String name) throws SQLDataException {
        if (canUseCarName(name)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_NAME, name);
            sqlDataHandler.setContentValues(contentValues);

            sqlDataHandler.setSelection(VEHICLE_NAME + "= \"" + this.name + "\"");

            long start = System.currentTimeMillis();

            carSQL.getWritableDatabase().update(ServiceTask.TABLE_NAME, contentValues,
                    VEHICLE_NAME + "= \"" + this.name + "\"", null);

            long doneService = System.currentTimeMillis();
            carSQL.getWritableDatabase().update(ServiceTask.TABLE_NAME, contentValues,
                    VEHICLE_NAME + "= \"" + this.name + "\"", null);

            long doneFuel = System.currentTimeMillis();
            carSQL.getWritableDatabase().update(ServiceTask.TABLE_NAME, contentValues,
                    VEHICLE_NAME + "= \"" + this.name + "\"", null);

            long done = System.currentTimeMillis();
            this.name = name;

            Log.i(TAG, "Service task: " + (doneService - start));
            Log.i(TAG, "Fuel task: " + (doneFuel - doneService));
            Log.i(TAG, "Part task: " + (done - doneFuel));
            Log.i(TAG, "All task: " + (done - start));
        }
    }

    public boolean canUseCarName(String carName) throws SQLDataException {
        return carSQL.checkString(carName, 3);
    }

    public String getName() {
        return name;
    }

    public void setMake(String make) throws SQLDataException {
        if (canUseMake(make)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MAKE, make);
            sqlDataHandler.setContentValues(contentValues);
        }
    }

    public String getMake() {
        return sqlDataHandler.getString(MAKE);
    }

    public boolean canUseMake(String make) throws SQLDataException {
        return carSQL.checkString(make, 3);
    }

    public void setModel(String model) throws SQLDataException {
        sqlDataHandler.putString(MODEL, model);
    }

    public String getModel() {
        return sqlDataHandler.getString(MODEL);
    }

    public void setSubmodel(String submodel) throws SQLDataException {
        sqlDataHandler.putString(SUBMODEL, submodel);
    }

    public String getSubmodel() {
        return sqlDataHandler.getString(SUBMODEL);
    }

    public void setYear(int year) {
        sqlDataHandler.putInt(YEAR, year);
    }

    public int getYear() {
        return sqlDataHandler.getInt(YEAR);
    }

    public void setMileage(float mileage) {
        sqlDataHandler.putFloat(MILEAGE, mileage);
    }

    public float getMileage() {
        return sqlDataHandler.getFloat(MILEAGE);
    }

    public void setMileageUnits(String units) throws SQLDataException {
        sqlDataHandler.putString(MILEAGE_UNITS, units);
    }

    public void setPurchaseDate(Date purchaseDate) {
        sqlDataHandler.putLong(PURCHASE_DATE, purchaseDate.getTime());
    }

    public Date getPurchaseDate() {
        return new Date(sqlDataHandler.getLong(PURCHASE_DATE));
    }

    public synchronized ServiceTask getNewServiceTask() {
        return new ServiceTask(carSQL, name, ++serviceTaskCount);
    }

    public synchronized FuelStop getNewFuelStop() {
        return new FuelStop(carSQL, name, ++fuelStopCount);
    }

    public synchronized PartReplacement getNewPartReplacement() {
        return new PartReplacement(carSQL, name, ++partCount);
    }

    public List<ServiceTask> getServiceTasks() {
        return ServiceTask.getServiceTasksForCar(carSQL, name);
    }

    public float getPurchaseCost() {
        return sqlDataHandler.getFloat(PURCHASE_COST);
    }

    public void setPurchaseCost(float purchaseCost) {
        sqlDataHandler.putFloat(PURCHASE_COST, purchaseCost);
    }

    public String getCostUnits() {
        return sqlDataHandler.getString(COST_UNITS);
    }

    public void setCostUnits(String costUnits) throws SQLDataException {
        sqlDataHandler.putString(COST_UNITS, costUnits);
    }

    public String getVin() {
        return sqlDataHandler.getString(VIN);
    }

    public void setVin(String vin) throws SQLDataException {
        sqlDataHandler.putString(VIN, vin);
    }

    public String getColor() {
        return sqlDataHandler.getString(COLOR);
    }

    public void setColor(String color) throws SQLDataException {
        sqlDataHandler.putString(COLOR, color);
    }

    public long getWeight() {
        return sqlDataHandler.getLong(WEIGHT);
    }

    public void setWeight(long weight) {
        sqlDataHandler.putLong(WEIGHT, weight);
    }

    public String getWeightUnits() {
        return sqlDataHandler.getString(WEIGHT);
    }

    public void setWeightUnits(String weightUnits) throws SQLDataException {
        sqlDataHandler.putString(WEIGHT_UNITS, weightUnits);
    }

    public String getBoughtFrom() {
        return sqlDataHandler.getString(BOUGHT_FROM);
    }

    public void setBoughtFrom(String boughtFrom) throws SQLDataException {
        sqlDataHandler.putString(BOUGHT_FROM, boughtFrom);
    }

    public void setContentValues(ContentValues contentValues) {
        sqlDataHandler.setContentValues(contentValues);
    }

    public static Comparator<ServiceTask> dateComparator = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return lhs.getDate().compareTo(rhs.getDate());
        }
    };

    private static Comparator<ServiceTask> mileageComparator = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return (int) (lhs.getMileage() - rhs.getMileage());
        }
    };

    private static Comparator<ServiceTask> costComparator = new Comparator<ServiceTask>() {
        @Override
        public int compare(ServiceTask lhs, ServiceTask rhs) {
            return (int) (lhs.getCost() - rhs.getCost());
        }
    };
}

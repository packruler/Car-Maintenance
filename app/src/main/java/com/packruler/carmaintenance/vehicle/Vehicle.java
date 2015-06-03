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
    public static final String CURRENT_MILEAGE = "current_mileage";
    public static final String CURRENT_MILEAGE_UNITS = "current_mileage_units";
    public static final String COLOR = "color";
    public static final String PURCHASE_MILEAGE = "purchase_mileage";
    public static final String PURCHASE_MILEAGE_UNITS = "purchase_mileage_units";
    public static final String PURCHASE_DATE = "purchase_date";
    public static final String BOUGHT_FROM = "bought_from";
    public static final String PURCHASE_COST = "purchase_cost";
    public static final String PURCHASE_COST_UNITS = "purchase_cost_units";
    public static final String DISPLACEMENT = "displacement";
    public static final String DISPLACEMENT_UNITS = "displacement_units";
    public static final String POWER = "power";
    public static final String POWER_UNITS = "power_units";
    public static final String TORQUE = "torque";
    public static final String TORQUE_UNITS = "torque_units";
    public static final String DISPLAY_COLOR = "display_color";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    VEHICLE_NAME + " STRING PRIMARY KEY," +
                    MAKE + " STRING," + MODEL + " STRING," +
                    SUBMODEL + " STRING," + YEAR + " INTEGER," +
                    VIN + " STRING," + WEIGHT + " LONG," + WEIGHT_UNITS + " STRING," +
                    DISPLACEMENT + " FLOAT," + DISPLACEMENT_UNITS + " STRING," +
                    POWER + " FLOAT," + POWER_UNITS + " STRING," +
                    TORQUE + " FLOAT," + TORQUE_UNITS + " STRING," +
                    CURRENT_MILEAGE + " LONG," + CURRENT_MILEAGE_UNITS + " STRING," + COLOR + " STRING," +
                    PURCHASE_DATE + " LONG," + BOUGHT_FROM + " STRING," +
                    PURCHASE_COST + " FLOAT," + PURCHASE_COST_UNITS + " STRING," +
                    PURCHASE_MILEAGE + " LONG," + PURCHASE_MILEAGE_UNITS + " STRING," +
                    DISPLAY_COLOR + " INTEGER" + ")";

    protected CarSQL carSQL;
    private String name = "";

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
            Log.i(TAG, "New Vehicle Name: " + name);
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_NAME, name);
            database.insert(TABLE_NAME, null, contentValues);
        }

        cursor.close();
        serviceTaskCount = ServiceTask.getServiceTasksCountForCar(carSQL, name);
        fuelStopCount = FuelStop.getServiceTasksCountForCar(carSQL, name);
        partCount = PartReplacement.getPartReplacementCountForCar(carSQL, name);
    }

    public void setName(String name) {
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
        } else
            Log.i(TAG, "Name already used");
    }

    public boolean canUseCarName(String carName) {
        if (name.equals(carName))
            return false;

        SQLiteDatabase database = carSQL.getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.VEHICLE_NAME},
                Vehicle.VEHICLE_NAME + "= \"" + carName + "\"",
                null, null, null, null);
        boolean canUse = cursor.getCount() == 0;
        cursor.close();
        return canUse;
    }

    public String getName() {
        return name;
    }

    public void setMake(String make) {
        if (!make.equals(getMake()))
            sqlDataHandler.putString(MAKE, make);
    }

    public String getMake() {
        return sqlDataHandler.getString(MAKE);
    }

    public void setModel(String model) {
        if (!model.equals(getModel()))
            sqlDataHandler.putString(MODEL, model);
    }

    public String getModel() {
        return sqlDataHandler.getString(MODEL);
    }

    public void setSubmodel(String submodel) {
        if (!submodel.equals(getSubmodel()))
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

    public void setCurrentMileage(long mileage) {
        sqlDataHandler.putLong(CURRENT_MILEAGE, mileage);
    }

    public long getCurrentMileage() {
        return sqlDataHandler.getLong(CURRENT_MILEAGE);
    }

    public void setCurrentMileageUnits(String units) {
        sqlDataHandler.putString(CURRENT_MILEAGE_UNITS, units);
    }

    public String getCurrentMileageUnits() {
        return sqlDataHandler.getString(CURRENT_MILEAGE_UNITS);
    }

    public void setPurchaseMileage(long mileage) {
        sqlDataHandler.putLong(PURCHASE_MILEAGE, mileage);
    }

    public long getPurchaseMileage() {
        return sqlDataHandler.getLong(PURCHASE_MILEAGE);
    }

    public void setPurchaseMileageUnits(String units) {
        sqlDataHandler.putString(PURCHASE_MILEAGE_UNITS, units);
    }

    public String getPurchaseMileageUnits() {
        return sqlDataHandler.getString(PURCHASE_MILEAGE_UNITS);
    }

    public void setPurchaseDate(Date purchaseDate) {
        sqlDataHandler.putLong(PURCHASE_DATE, purchaseDate.getTime());
    }

    public long getPurchaseDate() {
        return sqlDataHandler.getLong(PURCHASE_DATE);
    }

    public synchronized ServiceTask getNewServiceTask() {
        return new ServiceTask(carSQL, name);
    }

    public synchronized FuelStop getNewFuelStop() {
        return new FuelStop(carSQL, name);
    }

    public synchronized PartReplacement getNewPartReplacement() {
        return new PartReplacement(carSQL, name);
    }

    public List<ServiceTask> getServiceTasks() {
        return ServiceTask.getServiceTasksForCar(carSQL, name);
    }

    public Cursor getServiceTaskCursor(String orderBy) {
        return carSQL.getReadableDatabase().query(ServiceTask.TABLE_NAME, null, VEHICLE_NAME + "= \"" + name + "\"", null, null, null, orderBy);
    }

    public Cursor getServiceTaskCursor(String orderBy, boolean inverse) {
        if (inverse)
            orderBy += " DESC";
        else
            orderBy += " ASC";
        return getServiceTaskCursor(orderBy);
    }

    public Cursor getServiceTaskCursor() {
        return ServiceTask.getServiceTaskCursorForCar(carSQL, name);
    }

    public int getServiceTaskCount() {
        return ServiceTask.getServiceTasksCountForCar(carSQL, name);
    }

    public int getFuelStopCount() {
        return FuelStop.getFuelStopCountForCar(carSQL, name);
    }

    public int getPartCount() {
        return PartReplacement.getPartReplacementCountForCar(carSQL, name);
    }

    public float getPurchaseCost() {
        return sqlDataHandler.getFloat(PURCHASE_COST);
    }

    public void setPurchaseCost(float purchaseCost) {
        sqlDataHandler.putFloat(PURCHASE_COST, purchaseCost);
    }

    public String getPurchaseCostUnits() {
        return sqlDataHandler.getString(PURCHASE_COST_UNITS);
    }

    public void setPurchaseCostUnits(String costUnits) {
        sqlDataHandler.putString(PURCHASE_COST_UNITS, costUnits);
    }

    public String getVin() {
        return sqlDataHandler.getString(VIN);
    }

    public void setVin(String vin) {
        sqlDataHandler.putString(VIN, vin);
    }

    public String getColor() {
        return sqlDataHandler.getString(COLOR);
    }

    public void setColor(String color) {
        sqlDataHandler.putString(COLOR, color);
    }

    public long getWeight() {
        return sqlDataHandler.getLong(WEIGHT);
    }

    public void setWeight(long weight) {
        sqlDataHandler.putLong(WEIGHT, weight);
    }


    public String getWeightUnits() {
        return sqlDataHandler.getString(WEIGHT_UNITS);
    }

    public void setWeightUnits(String weightUnits) {
        sqlDataHandler.putString(WEIGHT_UNITS, weightUnits);
    }

    public long getPower() {
        return sqlDataHandler.getLong(POWER);
    }

    public void setPower(long power) {
        sqlDataHandler.putLong(POWER, power);
    }

    public String getPowerUnits() {
        return sqlDataHandler.getString(POWER_UNITS);
    }

    public void setPowerUnits(String units) {
        sqlDataHandler.putString(POWER_UNITS, units);
    }

    public long getTorque() {
        return sqlDataHandler.getLong(TORQUE);
    }

    public void setTorque(long torque) {
        sqlDataHandler.putLong(TORQUE, torque);
    }

    public String getTorqueUnits() {
        return sqlDataHandler.getString(TORQUE_UNITS);
    }

    public void setTorqueUnits(String units) {
        sqlDataHandler.putString(TORQUE_UNITS, units);
    }

    public String getBoughtFrom() {
        return sqlDataHandler.getString(BOUGHT_FROM);
    }

    public void setBoughtFrom(String boughtFrom) {
        sqlDataHandler.putString(BOUGHT_FROM, boughtFrom);
    }

    public void setDisplayColor(int color) {
        Log.v(TAG, "Store: " + color);
        sqlDataHandler.putInt(DISPLAY_COLOR, color);
    }

    public int getDisplayColor() {
        int out = sqlDataHandler.getInt(DISPLAY_COLOR);
        Log.v(TAG, "Current palette: " + out);
        return out;
    }

    public void putContentValues(ContentValues contentValues) {
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

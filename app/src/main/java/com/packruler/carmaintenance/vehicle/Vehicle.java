package com.packruler.carmaintenance.vehicle;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.sql.SQLDataHandler;
import com.packruler.carmaintenance.sql.SQLDataOberservable;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Packruler on 4/27/2015.
 */
public class Vehicle extends SQLDataOberservable {
    private final String TAG = getClass().getSimpleName();

    public static final String TABLE_NAME = "vehicles";
    public static final String VEHICLE_NAME = "vehicle_name";
    public static final String ROW_ID = "_id";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String SUBMODEL = "submodel";
    public static final String YEAR = "year";
    public static final String VIN = "vin";
    public static final String WEIGHT = "weight";
    public static final String WEIGHT_UNITS = "weight_units";
    public static final String CURRENT_MILEAGE = "current_mileage";
    public static final String DISTANCE_UNITS = "distance_units";
    public static final String COLOR = "color";
    public static final String PURCHASE_MILEAGE = "purchase_mileage";
    public static final String PURCHASE_DATE = "purchase_date";
    public static final String BOUGHT_FROM = "bought_from";
    public static final String PURCHASE_COST = "purchase_cost";
    public static final String COST_UNITS = "cost_units";
    public static final String DISPLACEMENT = "displacement";
    public static final String DISPLACEMENT_UNITS = "displacement_units";
    public static final String POWER = "power";
    public static final String POWER_UNITS = "power_units";
    public static final String TORQUE = "torque";
    public static final String TORQUE_UNITS = "torque_units";
    public static final String PRIMARY_COLOR = "display_color";
    public static final String TEXT_COLOR = "display_text_color";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" + ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    VEHICLE_NAME + " TEXT," + MAKE + " TEXT," + MODEL + " TEXT," +
                    SUBMODEL + " TEXT," + YEAR + " INTEGER," +
                    VIN + " TEXT," + WEIGHT + " LONG," + WEIGHT_UNITS + " TEXT," +
                    DISPLACEMENT + " FLOAT," + DISPLACEMENT_UNITS + " TEXT," +
                    POWER + " FLOAT," + POWER_UNITS + " TEXT," +
                    TORQUE + " FLOAT," + TORQUE_UNITS + " TEXT," +
                    CURRENT_MILEAGE + " LONG," + DISTANCE_UNITS + " TEXT," + COLOR + " TEXT," +
                    PURCHASE_DATE + " LONG," + BOUGHT_FROM + " TEXT," +
                    PURCHASE_COST + " FLOAT," + COST_UNITS + " TEXT," +
                    PURCHASE_MILEAGE + " LONG," + PRIMARY_COLOR + " INTEGER" + ")";

    protected CarSQL carSQL;

    private SQLDataHandler sqlDataHandler;

    private Vehicle(CarSQL carSQL) {
        this.carSQL = carSQL;
    }

    public Vehicle(CarSQL carSQL, String name) {
        this(carSQL);

        SQLiteDatabase database = carSQL.getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, VEHICLE_NAME + "= \"" + name + "\"",
                null, null, null, null);

        if (!cursor.moveToFirst()) {
            Log.i(TAG, "New Vehicle Name: " + name);
            ContentValues contentValues = new ContentValues();
            contentValues.put(VEHICLE_NAME, name);
            init(database.insert(TABLE_NAME, null, contentValues));
        } else
            init(cursor.getLong(0));

        cursor.close();
    }

    public Vehicle(CarSQL carSQL, long rowId) {
        this(carSQL);
        init(rowId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vehicle))
            return false;
        return row == ((Vehicle) o).getRow();
    }

    private void init(long rowId) {
        table = TABLE_NAME;
        row = rowId;
        sqlDataHandler = new SQLDataHandler(carSQL, table, row, this);
    }

    public long getRow() {
        return row;
    }

    public boolean setName(String name) {
        if (getName().equals(name))
            return true;

        if (canUseCarName(name)) {
            sqlDataHandler.putString(VEHICLE_NAME, name);
//            ContentValues contentValues = new ContentValues();
//            contentValues.put(VEHICLE_NAME, name);
//            sqlDataHandler.setContentValues(contentValues);
//
//            sqlDataHandler.setRow(VEHICLE_NAME + "= \"" + this.name + "\"");
//
//            long start = System.currentTimeMillis();
//
//            carSQL.getWritableDatabase().update(ServiceTask.TABLE_NAME, contentValues,
//                    VEHICLE_NAME + "= \"" + this.name + "\"", null);
//
//            long doneService = System.currentTimeMillis();
//            carSQL.getWritableDatabase().update(ServiceTask.TABLE_NAME, contentValues,
//                    VEHICLE_NAME + "= \"" + this.name + "\"", null);
//
//            long doneFuel = System.currentTimeMillis();
//            carSQL.getWritableDatabase().update(ServiceTask.TABLE_NAME, contentValues,
//                    VEHICLE_NAME + "= \"" + this.name + "\"", null);
//
//            long done = System.currentTimeMillis();
//            this.name = name;
//
//            Log.i(TAG, "Service task: " + (doneService - start));
//            Log.i(TAG, "Fuel task: " + (doneFuel - doneService));
//            Log.i(TAG, "Part task: " + (done - doneFuel));
//            Log.i(TAG, "All task: " + (done - start));
            return true;
        }
        Log.i(TAG, "Name already used");
        return false;
    }

    public boolean canUseCarName(String carName) {
        return getName().equals(carName) || carSQL.canUseCarName(carName);
    }

    public String getName() {
        return sqlDataHandler.getString(VEHICLE_NAME);
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
        sqlDataHandler.putString(DISTANCE_UNITS, units);
    }

    public String getCurrentMileageUnits() {
        return sqlDataHandler.getString(DISTANCE_UNITS);
    }

    public void setPurchaseMileage(long mileage) {
        sqlDataHandler.putLong(PURCHASE_MILEAGE, mileage);
    }

    public long getPurchaseMileage() {
        return sqlDataHandler.getLong(PURCHASE_MILEAGE);
    }

    public void setPurchaseDate(Date purchaseDate) {
        sqlDataHandler.putLong(PURCHASE_DATE, purchaseDate.getTime());
    }

    public long getPurchaseDate() {
        return sqlDataHandler.getLong(PURCHASE_DATE);
    }

    public synchronized ServiceTask getNewServiceTask() {
        return new ServiceTask(carSQL, row, true);
    }

    public synchronized FuelStop getNewFuelStop() {
        return new FuelStop(carSQL, row, true);
    }

    public synchronized PartReplacement getNewPartReplacement() {
        return new PartReplacement(carSQL, row, true);
    }

    public List<ServiceTask> getServiceTasks() {
        return ServiceTask.getServiceTasksForCar(carSQL, row);
    }

    public Cursor getServiceTaskCursor(String orderBy) {
        return carSQL.getReadableDatabase().query(ServiceTask.TABLE_NAME, null, ServiceTask.VEHICLE_ROW + "= " + row, null, null, null, orderBy);
    }

    public Cursor getServiceTaskCursor(String orderBy, boolean inverse) {
        if (inverse)
            orderBy += " DESC";
        else
            orderBy += " ASC";
        return getServiceTaskCursor(orderBy);
    }

    public Cursor getServiceTaskCursor() {
        return ServiceTask.getServiceTaskCursorForCar(carSQL, row);
    }

    public int getServiceTaskCount() {
        return ServiceTask.getServiceTasksCountForCar(carSQL, row);
    }

    public int getFuelStopCount() {
        return FuelStop.getFuelStopCountForCar(carSQL, row);
    }

    public Cursor getFuelStopCursor() {
        return FuelStop.getFuelStopCursorForCar(carSQL, row, FuelStop.DATE + " DSC");
    }

    public int getPartCount() {
        return PartReplacement.getPartReplacementCountForCar(carSQL, row);
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

    public void setPurchaseCostUnits(String costUnits) {
        sqlDataHandler.putString(COST_UNITS, costUnits);
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
        sqlDataHandler.putInt(PRIMARY_COLOR, color);
    }

    public int getDisplayColor() {
        return sqlDataHandler.getInt(PRIMARY_COLOR);
    }

    public void setTextColor(int color) {
        sqlDataHandler.putInt(TEXT_COLOR, color);
    }

    public int getTextColor() {
        return sqlDataHandler.getInt(TEXT_COLOR);
    }

    public File getImage() {
        return new File(carSQL.getMainFilePath() + "/" + row + "/" + "/vehicle.jpg");
    }

    public void putContentValues(ContentValues contentValues) {
        sqlDataHandler.setContentValues(contentValues);
    }
}

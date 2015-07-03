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
import java.util.Calendar;
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
    public static final String DATE_TIME_ZONE = "date_time_zone";
    public static final String BOUGHT_FROM = "bought_from";
    public static final String PURCHASE_COST = "purchase_cost";
    public static final String CURRENCY = "currency";
    public static final String DISPLACEMENT = "displacement";
    public static final String DISPLACEMENT_UNITS = "displacement_units";
    public static final String POWER = "power";
    public static final String POWER_UNITS = "power_units";
    public static final String TORQUE = "torque";
    public static final String TORQUE_UNITS = "torque_units";
    public static final String FUEL_EFFICIENCY_UNITS = "fuel_efficiency_unit";
    public static final String VOLUME_UNITS = "volume_units";
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
                    PURCHASE_DATE + " LONG," + DATE_TIME_ZONE + " TEXT," + BOUGHT_FROM + " TEXT," +
                    PURCHASE_COST + " FLOAT," + CURRENCY + " TEXT," +
                    PURCHASE_MILEAGE + " LONG," + FUEL_EFFICIENCY_UNITS + " TEXT," +
                    VOLUME_UNITS + " TEXT," + PRIMARY_COLOR + " INTEGER" + ")";

    protected CarSQL carSQL;

    private SQLDataHandler sqlDataHandler;

    private Vehicle(CarSQL carSQL) {
        this.carSQL = carSQL;
    }

    public Vehicle(CarSQL carSQL, String name) {
        this(carSQL);

        SQLiteDatabase database = carSQL.getWritableDatabase();
        long start = Calendar.getInstance().getTimeInMillis();
        Log.v(TAG, "Start query");
        Cursor cursor = database.query(TABLE_NAME, new String[]{VEHICLE_NAME}, VEHICLE_NAME + "= \"" + name + "\"",
                null, null, null, null);

        Log.v(TAG, "query took " + (Calendar.getInstance().getTimeInMillis() - start));
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
        sqlDataHandler = new SQLDataHandler(carSQL.getWritableDatabase(), table, row, this);
    }

    public void delete() {
        if (getImage().exists())
            getImage().delete();
        SQLiteDatabase database = carSQL.getWritableDatabase();
        try {
            long start = Calendar.getInstance().getTimeInMillis();
            database.beginTransaction();
            database.delete(TABLE_NAME, ROW_ID + "= " + row, null);
            database.delete(ServiceTask.TABLE_NAME, ServiceTask.VEHICLE_ROW + "= " + row, null);
            database.delete(FuelStop.TABLE_NAME, FuelStop.VEHICLE_ROW + "= " + row, null);
            database.delete(PartReplacement.TABLE_NAME, PartReplacement.VEHICLE_ROW + "= " + row, null);
            database.setTransactionSuccessful();
            Log.v(TAG, "Delete took: " + (Calendar.getInstance().getTimeInMillis() - start));
        } finally {
            database.endTransaction();
        }
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

    public boolean setName(String name) {
        sqlDataHandler.put(VEHICLE_NAME, name);
        return true;
    }

    public String getName() {
        return sqlDataHandler.getString(VEHICLE_NAME);
    }

    public void setMake(String make) {
        sqlDataHandler.put(MAKE, make);
    }

    public String getMake() {
        return sqlDataHandler.getString(MAKE);
    }

    public void setModel(String model) {
        sqlDataHandler.put(MODEL, model);
    }

    public String getModel() {
        return sqlDataHandler.getString(MODEL);
    }

    public void setSubmodel(String submodel) {
        sqlDataHandler.put(SUBMODEL, submodel);
    }

    public String getSubmodel() {
        return sqlDataHandler.getString(SUBMODEL);
    }

    public void setYear(int year) {
        sqlDataHandler.put(YEAR, year);
    }

    public int getYear() {
        return sqlDataHandler.getInt(YEAR);
    }

    public void setCurrentMileage(long mileage) {
        sqlDataHandler.put(CURRENT_MILEAGE, mileage);
    }

    public long getCurrentMileage() {
        return sqlDataHandler.getLong(CURRENT_MILEAGE);
    }

    public void setMileageUnits(String units) {
        sqlDataHandler.put(DISTANCE_UNITS, units);
    }

    public String getMileageUnits() {
        return sqlDataHandler.getString(DISTANCE_UNITS);
    }

    public void setPurchaseMileage(long mileage) {
        sqlDataHandler.put(PURCHASE_MILEAGE, mileage);
    }

    public long getPurchaseMileage() {
        return sqlDataHandler.getLong(PURCHASE_MILEAGE);
    }

    public void setPurchaseDate(long purchaseDate) {
        sqlDataHandler.put(PURCHASE_DATE, purchaseDate);
    }

    public long getPurchaseDate() {
        return sqlDataHandler.getLong(PURCHASE_DATE);
    }

    public void setDateTimeZone(String timeZone) {
        sqlDataHandler.put(DATE_TIME_ZONE, timeZone);
    }

    public String getDateTimeZone() {
        return sqlDataHandler.getString(DATE_TIME_ZONE);
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
        return FuelStop.getFuelStopCursorForCar(carSQL, row, FuelStop.DATE + " DESC");
    }

    public int getPartCount() {
        return PartReplacement.getPartReplacementCountForCar(carSQL, row);
    }

    public float getPurchaseCost() {
        return sqlDataHandler.getFloat(PURCHASE_COST);
    }

    public void setPurchaseCost(float purchaseCost) {
        sqlDataHandler.put(PURCHASE_COST, purchaseCost);
    }

    public String getCurrency() {
        return sqlDataHandler.getString(CURRENCY);
    }

    public void setCurrency(String currency) {
        sqlDataHandler.put(CURRENCY, currency);
    }

    public String getVin() {
        return sqlDataHandler.getString(VIN);
    }

    public void setVin(String vin) {
        sqlDataHandler.put(VIN, vin);
    }

    public String getColor() {
        return sqlDataHandler.getString(COLOR);
    }

    public void setColor(String color) {
        sqlDataHandler.put(COLOR, color);
    }

    public long getWeight() {
        return sqlDataHandler.getLong(WEIGHT);
    }

    public void setWeight(long weight) {
        sqlDataHandler.put(WEIGHT, weight);
    }


    public String getWeightUnits() {
        return sqlDataHandler.getString(WEIGHT_UNITS);
    }

    public void setWeightUnits(String weightUnits) {
        sqlDataHandler.put(WEIGHT_UNITS, weightUnits);
    }

    public long getPower() {
        return sqlDataHandler.getLong(POWER);
    }

    public void setPower(long power) {
        sqlDataHandler.put(POWER, power);
    }

    public String getPowerUnits() {
        return sqlDataHandler.getString(POWER_UNITS);
    }

    public void setPowerUnits(String units) {
        sqlDataHandler.put(POWER_UNITS, units);
    }

    public long getTorque() {
        return sqlDataHandler.getLong(TORQUE);
    }

    public void setTorque(long torque) {
        sqlDataHandler.put(TORQUE, torque);
    }

    public String getTorqueUnits() {
        return sqlDataHandler.getString(TORQUE_UNITS);
    }

    public void setTorqueUnits(String units) {
        sqlDataHandler.put(TORQUE_UNITS, units);
    }

    public String getBoughtFrom() {
        return sqlDataHandler.getString(BOUGHT_FROM);
    }

    public void setBoughtFrom(String boughtFrom) {
        sqlDataHandler.put(BOUGHT_FROM, boughtFrom);
    }

    public void setUiColor(int color) {
        sqlDataHandler.put(PRIMARY_COLOR, color);
    }

    public int getUiColor() {
        return sqlDataHandler.getInt(PRIMARY_COLOR);
    }

    public void setTextColor(int color) {
        sqlDataHandler.put(TEXT_COLOR, color);
    }

    public int getTextColor() {
        return sqlDataHandler.getInt(TEXT_COLOR);
    }

    public File getImage() {
        return new File(carSQL.getMainFilePath() + "/" + row + "/" + "/vehicle.webp");
    }

    public float getFuelEfficiency() {
        Cursor volume = carSQL.getReadableDatabase()
                .rawQuery("select sum(" + FuelStop.VOLUME + ") " +
                        "from " + FuelStop.TABLE_NAME +
                        " where " + FuelStop.COMPLETE_FILL_UP + "= 1", null);
//        if (volume.moveToFirst()){
//            Cursor distance= carSQL.getReadableDatabase()
//                    .query(FuelStop.TABLE_NAME,new String[]{FuelStop.})
//        }
        return 0f;
    }

    public void setFuelEfficiencyUnits(String units) {
        sqlDataHandler.put(FUEL_EFFICIENCY_UNITS, units);
    }

    public String getFuelEfficiencyUnits() {
        return sqlDataHandler.getString(FUEL_EFFICIENCY_UNITS);
    }

    public void setVolumeUnits(String units) {
        sqlDataHandler.put(VOLUME_UNITS, units);
    }

    public String getVolumeUnits() {
        return sqlDataHandler.getString(VOLUME_UNITS);
    }

    public void putContentValues(ContentValues contentValues) {
        sqlDataHandler.putContentValues(contentValues);
    }
}

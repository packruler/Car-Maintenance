package com.packruler.carmaintenance.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.packruler.carmaintenance.vehicle.Vehicle;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSql {
    private final String TAG = getClass().getName();

    public static final class CarTable implements BaseColumns {
        public static final String TABLE_NAME = "cars";
        public static final String COLUMN_NAME_CAR_NAME = "car_name";
        public static final String COLUMN_NAME_MAKE = "make";
        public static final String COLUMN_NAME_MODEL = "model";
        public static final String COLUMN_NAME_SUBMODEL = "submodel";
        public static final String COLUMN_NAME_YEAR = "year";
        public static final String COLUMN_NAME_VIN = "vin";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_COLOR = "color";
        public static final String COLUMN_NAME_PURCHASE_DATE = "purchase_date";
        public static final String COLUMN_NAME_BOUGHT_FROM = "bought_from";
        public static final String COLUMN_NAME_PURCHASE_COST = "purchase_cost";

        private static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_NAME_CAR_NAME + " STRING PRIMARY KEY," +
                        COLUMN_NAME_MAKE + " STRING," + COLUMN_NAME_MODEL + " STRING," +
                        COLUMN_NAME_SUBMODEL + " STRING," + COLUMN_NAME_YEAR + " INTEGER," +
                        COLUMN_NAME_VIN + " STRING," +
                        COLUMN_NAME_WEIGHT + " LONG," + COLUMN_NAME_COLOR + " STRING," +
                        COLUMN_NAME_PURCHASE_DATE + " LONG," + COLUMN_NAME_BOUGHT_FROM + " STRING," +
                        COLUMN_NAME_PURCHASE_COST + " FLOAT" + ")";
    }

    public static final class ServiceTable implements BaseColumns {
        public static final String TABLE_NAME = "service";
        public static final String COLUMN_NAME_CAR_NAME = "car_name";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_COST = "cost";
        public static final String COLUMN_NAME_MILEAGE = "mileage";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LOCATION_ID = "location_id";
        public static final String COLUMN_NAME_LOCATION_NAME = "location_name";

        //For Gas
        public static final String COLUMN_NAME_COST_PER_VOLUME = "cost_per_volume";
        public static final String COLUMN_NAME_VOLUME = "volume";
        public static final String COLUMN_NAME_OCTANE = "octane";

        public static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_NAME_CAR_NAME + " STRING," +
                        COLUMN_NAME_TYPE + " STRING," + COLUMN_NAME_COST + " FLOAT," +
                        COLUMN_NAME_MILEAGE + " LONG," + COLUMN_NAME_DATE + " STRING," +
                        COLUMN_NAME_LOCATION_ID + " STRING," + COLUMN_NAME_LOCATION_NAME +
                        COLUMN_NAME_COST_PER_VOLUME + " FLOAT," + COLUMN_NAME_VOLUME + " FLOAT," +
                        COLUMN_NAME_OCTANE + " INT" + ")";
    }

    private SQLiteDatabase database;
    private SQLHelper sqlHelper;
    private Context context;

    public CarSql(Context context) {
        this.context = context;
        sqlHelper = new SQLHelper(context);
        database = sqlHelper.getWritableDatabase();
    }

    private class SQLHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 3;
        public static final String DATABASE_NAME = "Cars.db";

        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CarTable.SQL_CREATE);
            db.execSQL(ServiceTable.SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 1:
                    db.execSQL(ServiceTable.SQL_CREATE);
                case 2:
                    db.execSQL("ALTER TABLE " + ServiceTable.TABLE_NAME + " ADD COLUMN " + ServiceTable.COLUMN_NAME_OCTANE + " INT");
            }
        }

    }

    public void putCar(Vehicle vehicle) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CarTable.COLUMN_NAME_CAR_NAME, vehicle.getName());
        contentValues.put(CarTable.COLUMN_NAME_MAKE, vehicle.getMake());
        contentValues.put(CarTable.COLUMN_NAME_MODEL, vehicle.getModel());
        contentValues.put(CarTable.COLUMN_NAME_SUBMODEL, vehicle.getSubmodel());
        contentValues.put(CarTable.COLUMN_NAME_YEAR, vehicle.getYear());
        contentValues.put(CarTable.COLUMN_NAME_VIN, vehicle.getVin());
        contentValues.put(CarTable.COLUMN_NAME_WEIGHT, vehicle.getModel());
        contentValues.put(CarTable.COLUMN_NAME_COLOR, vehicle.getColor());
        contentValues.put(CarTable.COLUMN_NAME_BOUGHT_FROM, vehicle.getBoughtFrom());
        contentValues.put(CarTable.COLUMN_NAME_PURCHASE_COST, vehicle.getPurchaseCost());
        contentValues.put(CarTable.COLUMN_NAME_PURCHASE_DATE, vehicle.getPurchaseDate().getTime());
        Log.i(TAG, "Put: " + contentValues.toString());

        database.beginTransaction();
        database.insert(CarTable.TABLE_NAME, null, contentValues);
        database.endTransaction();
    }


}

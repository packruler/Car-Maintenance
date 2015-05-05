package com.packruler.carmaintenance.sql;

import android.provider.BaseColumns;

/**
 * Created by Packruler on 5/5/15.
 */
public final class CarTable implements BaseColumns {
    public static final String TABLE_NAME = "cars";
    public static final String COLUMN_NAME_CAR_NAME = "car_name";
    public static final String COLUMN_NAME_MAKE = "make";
    public static final String COLUMN_NAME_MODEL = "model";
    public static final String COLUMN_NAME_SUBMODEL = "submodel";
    public static final String COLUMN_NAME_YEAR = "year";
    public static final String COLUMN_NAME_VIN = "vin";
    public static final String COLUMN_NAME_WEIGHT = "weight";
    public static final String COLUMN_NAME_MILEAGE = "mileage";
    public static final String COLUMN_NAME_COLOR = "color";
    public static final String COLUMN_NAME_PURCHASE_DATE = "purchase_date";
    public static final String COLUMN_NAME_BOUGHT_FROM = "bought_from";
    public static final String COLUMN_NAME_PURCHASE_COST = "purchase_cost";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_NAME_CAR_NAME + " STRING PRIMARY KEY," +
                    COLUMN_NAME_MAKE + " STRING," + COLUMN_NAME_MODEL + " STRING," +
                    COLUMN_NAME_SUBMODEL + " STRING," + COLUMN_NAME_YEAR + " INTEGER," +
                    COLUMN_NAME_VIN + " STRING," + COLUMN_NAME_WEIGHT + " LONG," +
                    COLUMN_NAME_MILEAGE + " FLOAT," + COLUMN_NAME_COLOR + " STRING," +
                    COLUMN_NAME_PURCHASE_DATE + " LONG," + COLUMN_NAME_BOUGHT_FROM + " STRING," +
                    COLUMN_NAME_PURCHASE_COST + " FLOAT" + ")";
}
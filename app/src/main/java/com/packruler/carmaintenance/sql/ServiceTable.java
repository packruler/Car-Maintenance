package com.packruler.carmaintenance.sql;

import android.provider.BaseColumns;

/**
 * Created by Packruler on 5/5/15.
 */
public final class ServiceTable implements BaseColumns {
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
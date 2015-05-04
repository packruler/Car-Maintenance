package com.packruler.carmaintenance.vehicle;

import android.content.SharedPreferences;

/**
 * Created by Packruler on 5/2/15.
 */
public class UnitHandler {
    private final String TAG = getClass().getName();

    public static final String MILE = "MILE";
    public static final String FEET = "FEET";
    public static final String METER = "METER";
    public static final String KILOMETER = "KILOMETER";
    public static final String INCH = "INCH";
    public static final String CENTIMETER = "CENTIMETER";
    public static final String MILIMETER = "MILIMETER";

    public static final String CUBIC_INCH = "CUBIC_INCH";
    public static final String LITER = "LITER";
    public static final String CUBIC_CENTIMETER = "CUBIC_CENTIMETER";

    public static final String HORSEPOWER = "HORSEPOWER";
    public static final String KILOWATT = "KILOWATT";

    public static final String FOOT_POUND = "FOOT_POUND";
    public static final String NEWTON_METER = "NEWTON_METER";

    public static final String POUND = "POUND";
    public static final String OUNCE = "OUNCE";
    public static final String KILOGRAM = "KILOGRAM";
    public static final String GRAM = "GRAM";

    public static final String GALLON_US = "GALLON_US";
    public static final String GALLON_IMPERIAL = "GALLON_IMPERIAL";

    public static final String KILOMETER_PER_LITER = "KILOMETER_PER_LITER";
    public static final String LITER_PER_100_KILOMETER = "LITER_PER_100_KILOMETER";
    public static final String MILES_PER_GALLON_US = "MILES_PER_GALLON_US";
    public static final String MILES_PER_GALLON_IMPERIAL = "MILES_PER_GALLON_IMPERIAL";

    private SharedPreferences sharedPreferences;

    //Distance stored in meter
    public static double getDistance(double in, String unit) {
        switch (unit) {
            case KILOMETER:
                return in * 0.001;
            case METER:
                return in;
            case CENTIMETER:
                return in * 100;
            case MILIMETER:
                return in * 1000;
            case MILE:
                return in * 0.000621371;
            case FEET:
                return in * 3.28084;
            case INCH:
                return in * 39.3701;
        }
        return Double.MIN_VALUE;
    }

    public static double setDistance(double in, String unit) {
        switch (unit) {
            case KILOMETER:
                return in / 0.001;
            case METER:
                return in;
            case CENTIMETER:
                return in / 100;
            case MILIMETER:
                return in / 1000;
            case MILE:
                return in / 0.000621371;
            case FEET:
                return in / 3.28084;
            case INCH:
                return in / 39.3701;
        }
        return Double.MIN_VALUE;
    }

    //Displacement stored in cubic centimeter
    public static double getDisplacement(double in, String unit) {
        switch (unit) {
            case CUBIC_CENTIMETER:
                return in;
            case LITER:
                return in * 0.001;
            case CUBIC_INCH:
                return in * 0.0610237441;
        }
        return Double.MIN_VALUE;
    }

    public static double setDisplacemet(double in, String unit) {
        switch (unit) {
            case CUBIC_CENTIMETER:
                return in;
            case LITER:
                return in / 0.001;
            case CUBIC_INCH:
                return in / 0.0610237441;
        }
        return Double.MIN_VALUE;
    }

    //Power stored in horsepower
    public static double getPower(double in, String unit) {
        switch (unit) {
            case HORSEPOWER:
                return in;
            case KILOWATT:
                return in * 0.745699872;
        }
        return Double.MIN_VALUE;
    }

    public static double storePower(double in, String unit) {
        switch (unit) {
            case HORSEPOWER:
                return in;
            case KILOWATT:
                return in / 0.745699872;
        }
        return Double.MIN_VALUE;
    }

    //Torque stored in newton meter
    public static double getTorque(double in, String unit) {
        switch (unit) {
            case NEWTON_METER:
                return in;
            case FOOT_POUND:
                return in * 0.7375621483695506;
        }
        return Double.MIN_VALUE;
    }

    public static double setTorque(double in, String unit) {
        switch (unit) {
            case NEWTON_METER:
                return in;
            case FOOT_POUND:
                return in / 0.7375621483695506;
        }
        return Double.MIN_VALUE;
    }

    //Weight stored in pound
    public static double getWeight(double in, String unit) {
        switch (unit) {
            case POUND:
                return in;
            case OUNCE:
                return in * 16;
            case KILOGRAM:
                return in * 0.453592;
            case GRAM:
                return in * 453.592;
        }
        return Double.MIN_VALUE;
    }

    public static double setWeight(double in, String unit) {
        switch (unit) {
            case POUND:
                return in;
            case OUNCE:
                return in / 16;
            case KILOGRAM:
                return in / 0.453592;
            case GRAM:
                return in / 453.592;
        }
        return Double.MIN_VALUE;
    }

    //Volume stored in liter
    public static double getVolume(double in, String unit) {
        switch (unit) {
            case LITER:
                return in;
            case GALLON_US:
                return in * 0.264172;
            case GALLON_IMPERIAL:
                return in * 0.219969;
        }
        return Double.MIN_VALUE;
    }

    public static double setVolume(double in, String unit) {
        switch (unit) {
            case LITER:
                return in;
            case GALLON_US:
                return in / 0.264172;
            case GALLON_IMPERIAL:
                return in / 0.219969;
        }
        return Double.MIN_VALUE;
    }

    //Efficency calculated from liter and meter
    public static double getEffieciency(double volume, double distance, String unit) {
        double output;
        switch (unit) {
            case LITER_PER_100_KILOMETER:
                output = volume / (distance / 100000);
                break;
            case KILOMETER_PER_LITER:
                output = (distance / 1000) / volume;
                break;
            case MILES_PER_GALLON_US:
                output = getDistance(distance, MILE) / getVolume(volume, GALLON_US);
                break;
            case MILES_PER_GALLON_IMPERIAL:
                output = getDistance(distance, MILE) / getVolume(volume, GALLON_IMPERIAL);
                break;
            default:
                output = Double.MIN_VALUE;
                break;
        }
        return output;
    }

}

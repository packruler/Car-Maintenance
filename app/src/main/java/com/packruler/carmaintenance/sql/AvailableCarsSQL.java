package com.packruler.carmaintenance.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Packruler on 5/8/15.
 */
public class AvailableCarsSQL {
    private final String TAG = getClass().getName();

    public static final String TABLE_NAME = "full_data";
    public static final String MAKE = "make";
    public static final String MODEL = "model";
    public static final String YEAR = "year";
    public static final String CYLINDERS = "cylinders";
    public static final String DISPLACEMENT = "displ";
    public static final String ENGINE_ID = "engId";
    public static final String ENGINE_DESCRIPTION = "eng_dscr";
    public static final String FUEL_TYPE = "fuelType";
    public static final String FUEL_TYPE_1 = "fuelType1";
    public static final String FUEL_TYPE_2 = "fuelType2";
    public static final String DRIVE = "drive";
    public static final String TRANSMISSION = "trany";
    public static final String TRANSMISSION_DESCRIPTION = "trans_dscr";
    public static final String TURBOCHARGED = "tCharger";
    public static final String SUPERCHARGED = "sCharger";
    public static final String ATV_TYPE = "atvType";
    public static final String MANUFACTURER_CODE = "mfrCode";

    private Context context;
    private SQLHelper sqlHelper;

    public AvailableCarsSQL(Context context) throws IOException {
        this.context = context;
        sqlHelper = new SQLHelper(context);
        sqlHelper.createDataBase();
        sqlHelper.openDataBase();
    }

    private class SQLHelper extends SQLiteOpenHelper {


        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "standard_cars.db";
        private String DATABASE_PATH = "/data/data/com.packruler.carmaintenance/databases/";

        private SQLiteDatabase myDataBase;

        /**
         * Constructor
         * Takes and keeps a reference of the passed context in order to access to the application
         * assets and resources.
         *
         * @param context
         */
        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Creates a empty database on the system and rewrites it with your own database.
         */
        public void createDataBase() throws IOException {

            boolean dbExist = checkDataBase();

            if (!dbExist) {
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                this.getReadableDatabase();

                try {

                    copyDataBase();

                } catch (IOException e) {

                    e.printStackTrace();

                }
            }

        }

        /**
         * Check if the database already exist to avoid re-copying the file each time you open the
         * application.
         *
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDataBase() {

            SQLiteDatabase checkDB = null;

            try {
                String myPath = DATABASE_PATH + DATABASE_NAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

            } catch (SQLiteException e) {

                //database does't exist yet.

            }

            if (checkDB != null) {

                checkDB.close();

            }

            return checkDB != null;
        }

        /**
         * Copies your database from your local assets-folder to the just created empty database in
         * the
         * system folder, from where it can be accessed and handled.
         * This is done by transfering bytestream.
         */
        private void copyDataBase() throws IOException {

            Log.i(TAG, "FILES: " + Arrays.toString(context.getAssets().list("")));
            //Open your local db as the input stream
            InputStream myInput = context.getAssets().open(DATABASE_NAME);

            // Path to the just created empty db
            File file = new File(DATABASE_PATH + DATABASE_NAME);
            Log.i(TAG, "File: " + file.getPath());
            Log.i(TAG, "Create db file: " + file.createNewFile());

            //Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(file);

            //transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            Log.i(TAG, "START MOVING");
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Log.i(TAG, "DONE MOVING");

        }

        public void openDataBase() {

            //Open the database
            String myPath = DATABASE_PATH + DATABASE_NAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }

        @Override
        public synchronized void close() {

            if (myDataBase != null)
                myDataBase.close();

            super.close();

        }


        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Add your public helper methods to access and get content from the database.
        // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
        // to you to create adapters for your views.
    }

    public Set<String> getAvailableMakes(String year) {
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(true, TABLE_NAME,
                new String[]{MAKE}, YEAR + "= " + year,
                null, null, null, null, null);
        cursor.moveToFirst();
        Log.i(TAG, "Total make count: " + cursor.getCount());
        TreeSet<String> makes = new TreeSet<>();
        while (!cursor.isAfterLast()) {
            makes.add(cursor.getString(0));
            cursor.moveToNext();
        }
        Log.i(TAG, "List size: " + makes.size());
        cursor.close();
        database.close();
        return makes;
    }

    public Set<String> getAvailableModels(String year, String make) {
        SQLiteDatabase database = sqlHelper.getReadableDatabase();
        Cursor cursor = database.query(true, TABLE_NAME,
                new String[]{MODEL}, YEAR + "= " + year + " AND " + MAKE + "= \"" + make + "\"",
                null, null, null, null, null);
        cursor.moveToFirst();
        Log.i(TAG, "Total model count: " + cursor.getCount());
        TreeSet<String> models = new TreeSet<>();
        while (!cursor.isAfterLast()) {
            models.add(cursor.getString(0));
            cursor.moveToNext();
        }
        Log.i(TAG, "List size: " + models.size());
        cursor.close();
        database.close();
        return models;
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqlHelper.getReadableDatabase();
    }
}

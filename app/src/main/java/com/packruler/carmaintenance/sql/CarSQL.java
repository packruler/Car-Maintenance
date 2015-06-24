package com.packruler.carmaintenance.sql;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.packruler.carmaintenance.sql.utilities.BitmapCache;
import com.packruler.carmaintenance.ui.utilities.VehicleMap;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.text.NumberFormat;

/**
 * Created by Packruler on 5/4/15.
 */
public class CarSQL {
    private final String TAG = getClass().getSimpleName();

    private String mainFilePath;

    private VehicleMap vehicleMap;
    private SQLDataOberservable vehicleDataOberservable = new SQLDataOberservable();
    private SQLDataObserver vehicleDataObserver = new SQLDataObserver() {
        @Override
        public void onChanged(String tableName, long row) {
            if (tableName.equals(Vehicle.TABLE_NAME))
                vehicleDataOberservable.notifiyChanged(tableName, row);
        }

        @Override
        public void onAdded(String tableName, long row) {
            super.onAdded(tableName, row);
            if (tableName.equals(Vehicle.TABLE_NAME))
                vehicleDataOberservable.notifiyChanged(tableName, row);
        }

        @Override
        public void onRemoved(String tableName, long row) {
            super.onRemoved(tableName, row);
            if (tableName.equals(Vehicle.TABLE_NAME))
                vehicleDataOberservable.notifiyChanged(tableName, row);
        }
    };

    private SQLHelper sqlHelper;
    private BitmapCache<Long, Bitmap> mMemoryCache;
    private Activity activity;

    public CarSQL(Activity activity) {
        this.activity = activity;
        sqlHelper = new SQLHelper(activity);
        mainFilePath = activity.getFilesDir().getPath();
        initializeBitmapCache();
        vehicleMap = loadVehicles();
    }

    public void registerObserver(SQLDataObserver observer) {
        try {
            vehicleDataOberservable.registerObserver(observer);
        } catch (IllegalStateException e) {
            Log.w(TAG, e.getMessage());
        }
    }

    public void unregisterAll() {
        try {
            vehicleDataOberservable.unregisterAll();
        } catch (IllegalStateException e) {
            Log.w(TAG, e.getMessage());
        }
    }

    public void unregisterObserver(SQLDataObserver observer) {
        try {
            vehicleDataOberservable.unregisterObserver(observer);
        } catch (IllegalStateException e) {
            Log.w(TAG, e.getMessage());
        }
    }

    private class SQLHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "Cars.db";

        public SQLHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.v(TAG, "Create new database");
            db.execSQL(Vehicle.SQL_CREATE);
            db.execSQL(ServiceTask.SQL_CREATE);
            db.execSQL(FuelStop.SQL_CREATE);
            db.execSQL(PartReplacement.SQL_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

    public Cursor loadVehicleCursor() {
        SQLiteDatabase database = getReadableDatabase();
        return database.query(Vehicle.TABLE_NAME, null, null, null, null, null, null);
    }

    public VehicleMap loadVehicles() {
        VehicleMap map = new VehicleMap();
        map.registerVehicleObserver(vehicleDataObserver);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.ROW_ID}, null, null, null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            return map;
        }

        while (!cursor.isAfterLast()) {
            map.put(new Vehicle(this, cursor.getLong(0)));
            cursor.moveToNext();
        }
        cursor.close();
        return map;
    }

    public int getVehicleCount() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.ROW_ID}, null, null, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private SQLiteDatabase database;

    public void beginTransaction() {
        if (database == null)
            database = sqlHelper.getWritableDatabase();
        database.beginTransaction();
    }

    public void setTransactionSuccessful() {
        database.setTransactionSuccessful();
    }

    public void endTransaction() {
        database.endTransaction();
    }

    public boolean close() {
        if (database != null) {
            if (!database.inTransaction()) {
                database.close();
                return true;
            }
            Log.e(TAG, "Database open");
        }
        return false;
    }

    public SQLiteDatabase getWritableDatabase() {
        if (database == null)
            database = sqlHelper.getWritableDatabase();

//        else if (database.inTransaction())
//            Log.v(TAG, "Database in transation");

        return database;
    }

    public SQLiteDatabase getReadableDatabase() {
        return sqlHelper.getReadableDatabase();
    }

    public boolean canUseCarName(String name) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(Vehicle.TABLE_NAME, new String[]{Vehicle.VEHICLE_NAME},
                Vehicle.VEHICLE_NAME + "= \"" + name + "\"",
                null, null, null, null);
        boolean canUse = cursor.getCount() == 0;
        cursor.close();
        return canUse;
    }

    public String getMainFilePath() {
        return mainFilePath;
    }

    private void initializeBitmapCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 6;

        mMemoryCache = new BitmapCache<>(cacheSize);
    }

    public void addBitmapToMemoryCache(long key, Bitmap bitmap) {
//        Bitmap cache = getBitmapFromMemCache(key);
//        if (cache != null)
//        {
////            if (!cache.sameAs(bitmap)) {
//            cache.recycle();
//            mMemoryCache.put(key, bitmap, true);
//            } else
//                Log.w(TAG, "Tried to store the same bitmap");
//        } else
        mMemoryCache.put(key, bitmap);
    }

    public boolean storeCacheToMemory(long key) {
        Bitmap temp = mMemoryCache.remove(-1l);
        if (temp != null) {
            addBitmapToMemoryCache(key, temp);
            return true;
        }
        return false;
    }

    public Bitmap getBitmapFromMemCache(long key) {
        return mMemoryCache.get(key);
    }

    public Bitmap getTempFromCache() {
        return mMemoryCache.get(-1l);
    }

    public void clearCache() {
        if (mMemoryCache.get(-1l) != null)
            mMemoryCache.remove(-1l).recycle();
    }

    public void loadBitmap(Uri uri, @Nullable ImageView imageView, @Nullable View loadingView,
                           @Nullable LoadedBitmapRunnable runnable) {
        new BitmapWorkerTask(imageView, loadingView, runnable)
                .execute(BitmapWorkerTask.temp, uri.toString());
    }

    public void loadBitmap(Vehicle vehicle, @Nullable ImageView imageView, @Nullable View loadingView,
                           @Nullable LoadedBitmapRunnable runnable) {
        new BitmapWorkerTask(imageView, loadingView, runnable)
                .execute(vehicle.getRow() + "", vehicle.getImage().toURI().toString());
    }


    public static boolean cancelPotentialWork(String[] data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String[] bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private String TAG = getClass().getSimpleName();
        public static final String temp = "-1";
        private final WeakReference<ImageView> imageViewReference;
        private final WeakReference<View> loadingReference;
        private final LoadedBitmapRunnable runnable;
        private String[] data;

        public BitmapWorkerTask(@Nullable ImageView imageView, @Nullable View loadingDisplay,
                                @Nullable LoadedBitmapRunnable runnable) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<>(imageView);
            loadingReference = new WeakReference<>(loadingDisplay);
            this.runnable = runnable;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(String... params) {
            Log.v(TAG, "Load bitmap");
            data = params;
            long key;
            try {
                key = Long.valueOf(params[0]);
            } catch (NumberFormatException e) {
                Log.w(TAG, e.getMessage());
                key = -1;
            }

            Bitmap bitmap = null;
            if (key != -1)
                bitmap = getBitmapFromMemCache(key);

            if (key == -1 || bitmap == null) {
                try {
                    Log.v(TAG, "Load Bitmap from: " + params[1]);
                    Bitmap tempBitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(Uri.parse(params[1])), null, null);

                    bitmap = scaleKeepAspectRatio(tempBitmap);
                } catch (FileNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                }

                if (bitmap != null) {
                    addBitmapToMemoryCache(key, bitmap);
                    Log.v(TAG, "New Bitmap size: " + (bitmap.getByteCount() / 1024) + "KB");
                    Log.v(TAG, "Cache usage: " +
                            NumberFormat.getInstance().format(mMemoryCache.size()) + "/" +
                            NumberFormat.getInstance().format(mMemoryCache.maxSize()) + "KB");
                }
                return bitmap;
            }
            Log.v(TAG, "Load bitmap row " + key + " from cache");
            return bitmap;
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                final ImageView imageView = imageViewReference.get();

                if (imageView != null)
                    imageView.setImageBitmap(bitmap);


                final View loading = loadingReference.get();
                if (loading != null)
                    loading.setVisibility(View.GONE);
            }

            if (runnable != null) {
                runnable.setBitmap(bitmap);
                runnable.run();
            }
        }
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(Resources.getSystem(), bitmap);
            bitmapWorkerTaskReference = new WeakReference<>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static abstract class LoadedBitmapRunnable implements Runnable {
        protected Bitmap bitmap;

        public abstract void run();

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }
    }

    private Bitmap scaleKeepAspectRatio(Bitmap bitmap) {
        float max = 1980;
        int inHeight = bitmap.getHeight();
        int inWidth = bitmap.getWidth();
        float scale;
        boolean recycle = true;

        if (inWidth > max &&
                inWidth > max) {
            if (inWidth > inHeight)
                scale = max / inWidth;
            else
                scale = max / inHeight;
        } else if (inWidth > max)
            scale = max / inWidth;
        else if (inHeight > max)
            scale = max / inHeight;
        else {
            Log.v(TAG, "No scale needed");
            recycle = false;
            scale = 1;
        }

        Log.v(TAG, "Scale: " + scale);
        Bitmap out = Bitmap.createScaledBitmap(bitmap, (int) (inWidth * scale), (int) (inHeight * scale), false);
        if (recycle) {
            bitmap.recycle();
            System.gc();
        }
        return out;
    }
}

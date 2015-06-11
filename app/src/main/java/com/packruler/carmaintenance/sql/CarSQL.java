package com.packruler.carmaintenance.sql;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.packruler.carmaintenance.ui.utilities.VehicleMap;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

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
    private LruCache<Long, Bitmap> mMemoryCache;
//    private Activity activity;

    public CarSQL(Activity activity) {
//        this.activity = activity;
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
        if (database == null) {
            Log.v(TAG, "Database in transation");
            database = sqlHelper.getWritableDatabase();
        }
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
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<Long, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Long key, Bitmap value) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return value.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(long key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null || !getBitmapFromMemCache(key).sameAs(bitmap))
            mMemoryCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(long key) {
        return mMemoryCache.get(key);
    }

//    public void loadBitmap(long vehicleRow, ImageView imageView) {
//        final Bitmap bitmap = getBitmapFromMemCache(vehicleRow);
//        AsyncDrawable drawable = new AsyncDrawable(Resources.getSystem(), bitmap, new BitmapWorkerTask(imageView));
//        if (bitmap != null) {
//            imageView.setImageDrawable(drawable);
//        } else {
//            imageView.setImageResource(android.R.drawable.ic_menu_camera);
//            BitmapWorkerTask task = new BitmapWorkerTask(mImageView);
//            task.execute(resId);
//        }
//    }

//    public void loadBitmapDrawable(int resId, ImageView imageView) {
//        if (cancelPotentialWork(resId, imageView)) {
//            final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
//            final AsyncDrawable asyncDrawable =
//                    new AsyncDrawable(getResources(), mPlaceHolderBitmap, task);
//            imageView.setImageDrawable(asyncDrawable);
//            task.execute(resId);
//        }
//    }

//    public static boolean cancelPotentialWork(int data, ImageView imageView) {
//        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
//
//        if (bitmapWorkerTask != null) {
//            final int bitmapData = bitmapWorkerTask.data;
//            // If bitmapData is not yet set or it differs from the new data
//            if (bitmapData == 0 || bitmapData != data) {
//                // Cancel previous task
//                bitmapWorkerTask.cancel(true);
//            } else {
//                // The same work is already in progress
//                return false;
//            }
//        }
//        // No task associated with the ImageView, or an existing task was cancelled
//        return true;
//    }

//    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
//        private final WeakReference<ImageView> imageViewReference;
//        private int data = 0;
//
//        public BitmapWorkerTask(ImageView imageView) {
//            // Use a WeakReference to ensure the ImageView can be garbage collected
//            imageViewReference = new WeakReference<ImageView>(imageView);
//        }
//
////        // Decode image in background.
////        @Override
////        protected Bitmap doInBackground(Integer... params) {
////            data = params[0];
////            return decodeSampledBitmapFromResource(getResources(), data, 100, 100));
////        }
////
////        // Decode image in background.
////        @Override
////        protected Bitmap doInBackground(Integer... params) {
////            final Bitmap bitmap = decodeSampledBitmapFromResource(
////                    getResources(), params[0], 100, 100));
////            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
////            return bitmap;
////        }
//
//        // Once complete, see if ImageView is still around and set bitmap.
//        @Override
//        protected void onPostExecute(Bitmap bitmap) {
//            if (imageViewReference != null && bitmap != null) {
//                final ImageView imageView = imageViewReference.get();
//                if (imageView != null) {
//                    imageView.setImageBitmap(bitmap);
//                }
//            }
//        }
//    }

//    static class AsyncDrawable extends BitmapDrawable {
//        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;
//
//        public AsyncDrawable(Resources res, Bitmap bitmap,
//                             BitmapWorkerTask bitmapWorkerTask) {
//            super(res, bitmap);
//            bitmapWorkerTaskReference =
//                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
//        }
//
//        public BitmapWorkerTask getBitmapWorkerTask() {
//            return bitmapWorkerTaskReference.get();
//        }
//    }
}

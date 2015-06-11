package com.packruler.carmaintenance.sql;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.ui.utilities.VehicleMap;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SQLTester extends ActionBarActivity {
    private final String TAG = getClass().getSimpleName();

    private Button fillSQL;
    private Button loadSQL;
    private CarSQL carSQL;
    private VehicleMap vehicleMap = new VehicleMap();

    private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private int numProcessors = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(numProcessors, numProcessors, 10, TimeUnit.SECONDS, workQueue);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqltester);
        Log.i(TAG, "onCreate");

        carSQL = new CarSQL(this);
        fillSQL = (Button) findViewById(R.id.fill_sql);
        fillSQL.setOnClickListener(new OnClickListener());
        loadSQL = (Button) findViewById(R.id.load_sql);
        loadSQL.setOnClickListener(new OnClickListener());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sqltester, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        poolExecutor.shutdown();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick");
            switch (v.getId()) {
                case R.id.fill_sql:
                    fillSQL();
                    break;
                case R.id.load_sql:
                    loadSQL();
                    break;
            }
        }
    }

    private void fillSQL() {
        Log.i(TAG, "Start Fill");
        long start = Calendar.getInstance().getTimeInMillis();

//        SQLiteDatabase database = carSQL.getWritableDatabase();
//        database.beginTransaction();
//        database.delete(Vehicle.TABLE_NAME, Vehicle.VEHICLE_ROW + " LIKE (\'Car%\')", null);
//        database.delete(ServiceTask.TABLE_NAME, ServiceTask.VEHICLE_ROW + " LIKE (\'Car%\')", null);
//        database.delete(FuelStop.TABLE_NAME, FuelStop.VEHICLE_ROW + " LIKE (\'Car%\')", null);
//        database.delete(Vehicle.TABLE_NAME, Vehicle.VEHICLE_ROW + " LIKE (\'THIS%\')", null);
//        database.delete(ServiceTask.TABLE_NAME, ServiceTask.VEHICLE_ROW + " LIKE (\'THIS%\')", null);
//        database.delete(FuelStop.TABLE_NAME, FuelStop.VEHICLE_ROW + " LIKE (\'THIS%\')", null);
//        database.setTransactionSuccessful();
//        database.endTransaction();

        Log.i(TAG, "Delete Took " + (Calendar.getInstance().getTimeInMillis() - start));
        HandlerThread handlerThread = new HandlerThread("fillSQL");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            private final String TAG = "fillSQL Runnable";

            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                long begin = calendar.getTimeInMillis();
                carSQL.beginTransaction();
                for (int x = 0; x < 5; x++) {
                    final Vehicle vehicle = new Vehicle(carSQL, "Car " + x);
                    vehicle.setMake("Mini");
                    vehicle.setModel("Cooper ");
                    vehicle.setSubmodel("S");
                    vehicle.setYear((int) (Math.random() * 1000));

                    Log.d(TAG, "Adding service to " + vehicle.getName());
                    long start = System.currentTimeMillis();
                    for (int y = 0; y < 3000; y++) {
                        ServiceTask serviceTask;
                        calendar.set((int) (Math.random() * 20) + 1990,
                                (int) (Math.random() * 11) + 1,
                                (int) (Math.random() * 28) + 1);
                        if (y % 3 == 0) {
                            serviceTask = vehicle.getNewFuelStop();
                            ((FuelStop) serviceTask).setOctane(93);
                            ((FuelStop) serviceTask).setCostPerVolume((float) (Math.random() * 10));
                        } else {
                            serviceTask = vehicle.getNewServiceTask();
                            if (Math.random() > .5)
                                serviceTask.setType("Oil Change");
                            else {
                                serviceTask.setType("Checkup");
                                //TODO: Setup part test
                            }
                        }
                        serviceTask.setDate(calendar.getTimeInMillis());
                        serviceTask.setMileage((long) (Math.random() * 10000));
                        serviceTask.setCost((float) (Math.random() * 100));
                        serviceTask.setCostUnits("$");

                        if (y % 1000 == 0)
                            Log.v(TAG, "Added task: " + y + " to " + vehicle.getName());
                    }
                    Log.i(TAG, "3000 tasks took: " + (System.currentTimeMillis() - start));
                }
                carSQL.setTransactionSuccessful();
                carSQL.endTransaction();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Done Filling", Toast.LENGTH_LONG).show();
                    }
                });
                Log.d(TAG, "Done filling values in " + (Calendar.getInstance().getTimeInMillis() - begin));
            }
        });
    }

    public void loadSQL() {
        HandlerThread thread = new HandlerThread("LoadSQL");
        thread.start();
        new Handler(thread.getLooper()).post(new Runnable() {
            @Override
            public void run() {
                vehicleMap = carSQL.loadVehicles();

                Vehicle vehicle = vehicleMap.get(vehicleMap.keySet().iterator().next());
                vehicle.setName("THIS CHANGED");

                ArrayList<String> names = new ArrayList<>(vehicleMap.size());
                ArrayList<CharSequence> keys = new ArrayList<>(vehicleMap.size());
                for (Map.Entry<Long, Vehicle> entry : vehicleMap.entrySet()) {
                    names.add(entry.getValue().getName());
                    keys.add(entry.getKey() + "");
                }
                Log.d(TAG, "Car names: " + names.toString());
                Log.d(TAG, "Keys: " + keys.toString());


                Log.d(TAG, "Name Changed");
                Log.d(TAG, "SQL updated");
            }
        });
    }
}

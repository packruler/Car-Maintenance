package com.packruler.carmaintenance.sql;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class SQLTester extends ActionBarActivity implements CarSql.VehicleContainer {
    private final String TAG = getClass().getName();

    private Button fillSQL;
    private Button loadSQL;
    private CarSql carSql;
    private Map<String, Vehicle> vehicleMap = new TreeMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqltester);
        Log.i(TAG, "onCreate");

        carSql = new CarSql(this);
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
        for (int x = 0; x < 100; x++) {
            Vehicle vehicle = new Vehicle();
            vehicle.setName("Car " + x);
            vehicle.setMake("Make");
            vehicle.setModel("Model");
            vehicle.setSubmodel("Submodel");
            vehicle.setYear((int) (Math.random() * 1000));
            carSql.putCar(vehicle);
            Log.i(TAG, "Adding service to " + vehicle.getName());
            for (int y = 0; y < 200; y++) {
                ServiceTask serviceTask;
                if (y % 4 == 0) {
                    serviceTask = new FuelStop(vehicle.getServiceTasks().size());
                    ((FuelStop) serviceTask).setOctane(93);
                    ((FuelStop) serviceTask).setCostPerVolume((float) (Math.random() * 10));
                } else
                    serviceTask = new ServiceTask(vehicle.getServiceTasks().size());

                serviceTask.setMileage((float) (Math.random() * 10000));
                serviceTask.setCarName(vehicle.getName());
                vehicle.addServiceTask(serviceTask);
                carSql.putServiceTask(serviceTask);
                Log.i(TAG, "Added new task");
            }
        }
    }

    public void loadCar(Vehicle vehicle) {
        Log.i(TAG, "Add: " + vehicle.getName());
        vehicleMap.put(vehicle.getName(), vehicle);
    }

    public void loadSQL() {
        carSql.loadCars();
        Set<String> names = vehicleMap.keySet();
        Log.i(TAG, "Car names: " + names.toString());
        Vehicle vehicle = vehicleMap.get(names.iterator().next());
        String oldName = vehicle.getName();
        vehicle.setName("THIS CHANGED");
        Log.i(TAG, "Name Changed");
        carSql.updateVehicleName(oldName, vehicle);
        Log.i(TAG, "SQL updated");
    }
}

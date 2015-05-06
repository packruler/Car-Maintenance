package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSql;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, CarSql.VehicleContainer {

    private final String TAG = getClass().getName();
    private static final String CAR_NAME_SET = "CAR_NAME_SET";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Map<String, Vehicle> vehicleMap = new TreeMap<>();

    private SharedPreferences sharedPreferences;
    private CarSql carsSQL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "New MainActivity");
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(getApplication().getPackageName(), MODE_MULTI_PROCESS);
//        sharedPreferences.edit().clear().apply();
//        Set<String> carNames = sharedPreferences.getStringSet(CAR_NAME_SET, new TreeSet<String>());
//        Log.i(TAG, "Names " + carNames.toString());
//        for (String carName : carNames) {
//            try {
//                String car = sharedPreferences.getString(carName, "");
//                Log.i(TAG, "Imported JSON: " + car);
//                Vehicle vehicle = new Vehicle(new JSONObject(car));
//                vehicleMap.put(carName, vehicle);
//            } catch (JSONException e) {
//                e.printStackTrace();
//                Log.i(TAG, "ERROR STRING: " + sharedPreferences.getString(carName, ""));
//            }
//        }

        Log.i(TAG, "Car map size: " + vehicleMap.size());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mNavigationDrawerFragment.updateDrawer();

        carsSQL = new CarSql(this);
        carsSQL.loadCars();
    }

    @Override
    public void onNavigationDrawerItemSelected(String name) {
        // update the main content by replacing fragments
        EditCarFragment editCarFragment;
        if (vehicleMap.containsKey(name))
            editCarFragment = new EditCarFragment(vehicleMap.get(name));
        else
            editCarFragment = new EditCarFragment();

        editCarFragment.setCarSql(carsSQL);
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, editCarFragment)
                .commit();
        Vehicle vehicle = vehicleMap.get(name);
        Log.i(TAG, "Vehicle null " + (vehicle == null));
        if (vehicle == null)
            vehicle = new Vehicle();
        editCarFragment.setVehicle(vehicle);

//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        if (number < vehicleMap.size()) {
            mTitle = vehicleMap.get(number).getName();
        } else if (mTitle != null && !mTitle.equals("New Car")) {
            mTitle = "New Car";
//            setupNewCar();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
//            ((MainActivity) activity).onSectionAttached(
//                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public Map<String, Vehicle> getVehicleMap() {
        return vehicleMap;
    }

    public List<String> getVehicleNames() {
        LinkedList<String> names = new LinkedList<>(vehicleMap.keySet());
        return names;
    }

    private EditCarFragment editCarFragment;

    private void setupNewCar() {
        if (editCarFragment == null) {
            Log.i(TAG, "New Car!");
            editCarFragment = new EditCarFragment();
//            getFragmentManager().beginTransaction().add(R.id.container, editCarFragment).commit();
        }
    }

    public boolean containsCar(String name) {
        Log.i(TAG, "Contains: " + name);
        return vehicleMap.containsKey(name);
    }

    public boolean updateCar(Vehicle vehicle) {
        vehicleMap.put(vehicle.getName(), vehicle);
        carsSQL.putCar(vehicle);
//        try {
//            Log.i(TAG, "JSON String: " + vehicle.getJsonObject().toString());
//            sharedPreferences.edit().putStringSet(CAR_NAME_SET, vehicleMap.keySet()).apply();
//            sharedPreferences.edit().putString(vehicle.getName(), vehicle.getJsonObject().toString()).apply();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        mNavigationDrawerFragment.updateDrawer();
        return true;
    }

    public void loadCar(Vehicle vehicle) {
        Log.i(TAG, "Loading car: " + vehicle.getName());
        vehicleMap.put(vehicle.getName(), vehicle);
    }
}

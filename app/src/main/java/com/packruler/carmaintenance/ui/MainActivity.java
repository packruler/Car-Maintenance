package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

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
    private CarSQL carsSQL;
    private AvailableCarsSQL availableCarsSQL;

    private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private int numProcessors = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(numProcessors, numProcessors, 10, TimeUnit.SECONDS, workQueue);
    private ActionBar actionBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "New MainActivity");
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(getApplication().getPackageName(), MODE_MULTI_PROCESS);
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    availableCarsSQL = new AvailableCarsSQL(MainActivity.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Log.i(TAG, "Car map size: " + vehicleMap.size());

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


        carsSQL = new CarSQL(this);
        for (String name : carsSQL.getCarNames()) {
            vehicleMap.put(name, new Vehicle(carsSQL, name));
        }
        getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).commit();

        mNavigationDrawerFragment.updateDrawer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        carsSQL.close();
    }

    @Override
    public void onNavigationDrawerItemSelected(String name) {
        // update the main content by replacing fragments
        Log.v(TAG, "Selected car name: " + name);

        editCarFragment = new EditCarFragment(this, availableCarsSQL, carsSQL);

        if (vehicleMap.containsKey(name))
            editCarFragment.setVehicle(vehicleMap.get(name));

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, editCarFragment)
                .commit();

//        Vehicle vehicle = vehicleMap.get(name);
//        Log.i(TAG, "Vehicle null " + (vehicle == null));
//        if (vehicle != null)
//            editCarFragment.setVehicle(vehicle);
    }

    public void onSectionAttached(int number) {
        if (number < vehicleMap.size()) {
            mTitle = vehicleMap.get(number).getName();
        } else if (mTitle != null && !mTitle.equals("New Car")) {
            mTitle = "New Car";
        }
        getSupportActionBar().setTitle(mTitle);
    }

    public void restoreActionBar() {
        actionBar = getSupportActionBar();
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
    public class MainFragment extends android.app.Fragment {

        public MainFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            rootView.findViewById(R.id.services_button).setOnClickListener(new View.OnClickListener() {
                private final String TAG = "Services Button";

                @Override
                public void onClick(View v) {
                    Log.v(TAG, "onClick");
                    poolExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            ServicesFragment servicesFragment = new ServicesFragment(vehicleMap.get("THIS CHANGED"), carsSQL);
                            getFragmentManager().beginTransaction().replace(R.id.container, servicesFragment).commit();
                        }
                    });
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }
    }

    public Map<String, Vehicle> getVehicleMap() {
        return vehicleMap;
    }

    public List<String> getVehicleNames() {
        List<String> names = carsSQL.getCarNames();
        Collections.sort(names);
        return names;
    }

    public ThreadPoolExecutor getPoolExecutor() {
        return poolExecutor;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    EditCarFragment editCarFragment;

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.i(TAG, "requestCode " + requestCode);
//        if (editCarFragment != null)
//            editCarFragment.onActivityResult(requestCode, resultCode, data);
//    }
}

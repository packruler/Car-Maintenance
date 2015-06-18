package com.packruler.carmaintenance.ui;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.utilities.Swatch;
import com.packruler.carmaintenance.ui.utilities.ToolbarColorizeHelper;
import com.packruler.carmaintenance.ui.utilities.VehicleMap;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        android.support.v4.app.FragmentManager.OnBackStackChangedListener {

    private final String TAG = getClass().getSimpleName();
    private static final String CAR_NAME_SET = "CAR_NAME_SET";
    private GoogleApiClient googleApiClient;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private VehicleMap vehicleMap;
    private Vehicle currentVehicle;

    private VehicleMainFragment mainFragment;

    private SharedPreferences sharedPreferences;
    private CarSQL carsSQL;
    private AvailableCarsSQL availableCarsSQL;

    private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private int numProcessors = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(numProcessors, numProcessors, 10, TimeUnit.SECONDS, workQueue);

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        mainFragment = new VehicleMainFragment(this);
//        Mobihelp.init(this, new MobihelpConfig("https://packruler.freshdesk.com", "carmaintenance-1-6a1ff09c57e9c2df0374ba007bcc9be7", "684a7217edaf7a384db1a10d98b76164430821db"));
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    availableCarsSQL = new AvailableCarsSQL(MainActivity.this);
                } catch (IOException e) {
                    Log.e(TAG, "availableCarsSQL ERROR: " + e.getMessage());
                }
                googleApiClient = new GoogleApiClient
                        .Builder(MainActivity.this)
                        .addApi(Places.GEO_DATA_API)
                        .addApi(Places.PLACE_DETECTION_API)
                        .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult connectionResult) {
                                Log.e(TAG, "Connection Failed");
                                Log.e(TAG, connectionResult.toString());
                            }
                        }).build();
            }
        });
        Log.i(TAG, "New MainActivity");
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences(getApplication().getPackageName(), MODE_MULTI_PROCESS);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                this);


        carsSQL = new CarSQL(this);
        mNavigationDrawerFragment.setCarSql(carsSQL);

        vehicleMap = carsSQL.loadVehicles();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();

        setUIColor(getResources().getColor(R.color.default_ui_color));
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new VehicleSelectFragment(carsSQL))
                .commit();
        getSupportActionBar().setTitle(getString(R.string.select_vehicle));
    }

    @Override
    protected void onDestroy() {
        carsSQL.close();
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        switch (position) {
            case 1:
                displaySelectVehicle();
                break;
        }
    }

    void displaySelectVehicle() {
        setUIColor(getResources().getColor(R.color.default_ui_color));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new VehicleSelectFragment(carsSQL))
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle(getString(R.string.select_vehicle));
    }

    private void changeVehicle(Vehicle vehicle) {
        currentVehicle = vehicle;
        changeVehicle();
    }

    public void changeVehicle(long vehicleRow) {
        currentVehicle = vehicleMap.get(vehicleRow);
        changeVehicle();
    }

    private void changeVehicle() {

        int color = getResources().getColor(R.color.default_ui_color);
        if (currentVehicle != null) {
            if (currentVehicle.getDisplayColor() != 0)
                color = currentVehicle.getDisplayColor();

            getSupportActionBar().setTitle(currentVehicle.getName());
        } else
            getSupportActionBar().setTitle(getString(R.string.app_name));

        mNavigationDrawerFragment.updateSelectedCar(currentVehicle);
        setUIColor(color);
        mainFragment.loadVehicleDetails();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, mainFragment)
                .commit();

        mNavigationDrawerFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
//        while (fragmentManager.getBackStackEntryCount() > 0) {
//            Log.v(TAG, "Back Stack count: " + fragmentManager.getBackStackEntryCount());
//            Log.v(TAG, "Pop stack " + (fragmentManager.popBackStackImmediate() ? "Success" : "FAIL"));
//        }
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public void restoreActionBar() {
        Log.v(TAG, "restoreActionBar");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.main, menu);
////            restoreActionBar();
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.v("onBackStackChanged", "Count: " + count);
        if (count == 0) {
            changeVehicle();
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            mNavigationDrawerFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            mNavigationDrawerFragment.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    public VehicleMap getVehicleMap() {
        return vehicleMap;
    }

    public List<String> getVehicleNames() {
        List<String> names = new LinkedList<>();
        for (Vehicle vehicle : vehicleMap.getVehiclesByName()) {
            names.add(vehicle.getName());
        }
        return names;
    }

    public AvailableCarsSQL getAvailableCarsSQL() {
        return availableCarsSQL;
    }

    public CarSQL getCarsSQL() {
        return carsSQL;
    }

    public void execute(Runnable runnable) {
        poolExecutor.execute(runnable);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setUIColor(final int color) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Swatch swatch = new Swatch(color);
                toolbar.setBackgroundColor(color);
                ToolbarColorizeHelper.colorizeToolbar(toolbar, swatch.getBodyTextColor(), MainActivity.this);
                mainFragment.setUIColor(color);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.setStatusBarColor(color);
                    window.setNavigationBarColor(color);
                }
            }
        });
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.v(TAG, "Back button press");

            if (mNavigationDrawerFragment.isDrawerOpen())
                return mNavigationDrawerFragment.closeDrawers();

            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            else return super.onKeyDown(keyCode, event);

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

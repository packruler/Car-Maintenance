package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.utilities.Swatch;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private TextView selectedCarName;
    private CircleImageView selectedCarIcon;
    private RelativeLayout selectedCarView;
    private CarSQL carSQL;
    private boolean setupDone = false;

    private int color = 0;
    private ArrayList<DrawerRow> rows = new ArrayList<>();

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
//        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);

        mDrawerListView.addHeaderView(View.inflate(getActivity(), R.layout.selected_car_display, null));
        selectedCarIcon = (CircleImageView) getActivity().findViewById(R.id.selected_car_icon);
        selectedCarName = (TextView) getActivity().findViewById(R.id.selected_car_name);
        selectedCarView = (RelativeLayout) getActivity().findViewById(R.id.selected_car_view);

        setupDone = true;
        if (((MainActivity) getActivity()).getCurrentVehicle() != null)
            updateSelectedCar(((MainActivity) getActivity()).getCurrentVehicle());
        else
            updateSelectedCar(null);
        color = getResources().getColor(R.color.default_ui_color);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        rows.add(new DrawerRow("Select Car", getResources().getDrawable(R.drawable.ic_car_icon)));
        CustomAdapter adapter = new CustomAdapter(getActivity());
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

//        updateDrawer();
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, MainActivity activity) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                activity.getToolbar(),             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };
//        mDrawerToggle.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        Log.v(TAG, "selectItem: " + position);
        mCurrentSelectedPosition = position;
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }

        if (mCallbacks != null)
            mCallbacks.onNavigationDrawerItemSelected(position);
    }

//    private void displaySelected(int position) {
//        ImageView icon;
//        TextView title;
//        int unselectedColor = getResources().getColor(R.color.material_grey_500);
//        for (int x = 0; x < icons.size(); x++) {
//            icon = icons.get(position - 1);
//            if (icon != null)
//                if (position - 1 == x)
//                    icon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
//                else
//                    icon.setColorFilter(unselectedColor, PorterDuff.Mode.SRC_IN);
//
//            title = titles.get(position - 1);
//            if (icon != null)
//                if (position - 1 == x)
//                    title.setTextColor(color);
//                else
//                    title.setTextColor(unselectedColor);
//        }
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // If the drawer is open, show the global app actions in the action bar. See also
//        // showGlobalContextActionBar, which controls the top-left area of the action bar.
//        if (mDrawerLayout != null && isDrawerOpen()) {
//            inflater.inflate(R.menu.global, menu);
//            showGlobalContextActionBar();
//        }
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public void setCarSql(CarSQL carSql) {
        this.carSQL = carSql;
    }

    private String[] nameArray = new String[0];

//    public void updateDrawer() {
//        List<String> vehicleNames = ((MainActivity) getActivity()).getVehicleNames();
//        vehicleNames.add(getString(R.string.add_car));
//        nameArray = new String[vehicleNames.size()];
//        vehicleNames.toArray(nameArray);
//
//        mDrawerListView.setAdapter(new ArrayAdapter<String>(
//                getActionBar().getThemedContext(),
//                android.R.layout.simple_list_item_activated_1,
//                android.R.id.text1,
//                nameArray));
//        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
//
//        Log.i(TAG, "Names: " + vehicleNames.toString());
//    }

    public void updateSelectedCar(Vehicle vehicle) {
        if (setupDone) {
            color = 0;
            if (vehicle != null) {
                selectedCarName.setText(vehicle.getName());

                if (vehicle.getImage().exists())
                    carSQL.loadBitmap(vehicle, selectedCarIcon, null, null);
                else
                    selectedCarIcon.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.material_grey_500)));

                color = vehicle.getUiColor();
            } else {
                selectedCarName.setText("None Selected");
                selectedCarIcon.setImageDrawable(new ColorDrawable(getResources().getColor(R.color.material_grey_500)));
            }

            if (color == 0)
                color = getResources().getColor(R.color.default_ui_color);

            selectedCarView.setBackgroundColor(color);
            selectedCarIcon.setBorderColor(Swatch.getForegroundColor());
            selectedCarName.setTextColor(Swatch.getForegroundColor());
        }
    }

    public void setDrawerLockMode(int lockMode) {
        mDrawerLayout.setDrawerLockMode(lockMode);
    }

    public boolean close() {
        mDrawerLayout.closeDrawers();
        return true;
    }

    public void open() {
        mDrawerLayout.openDrawer(Gravity.LEFT);
    }

    public void useDrawerIcon() {
        mDrawerToggle.setDrawerIndicatorEnabled(true);
    }

    public void useBackIcon() {
        mDrawerToggle.setDrawerIndicatorEnabled(false);
    }

    private class DrawerRow {
        private Drawable icon;
        private CharSequence title;

        public DrawerRow(CharSequence title, Drawable icon) {
            this.icon = icon;
            this.title = title;
        }

        public CharSequence getTitle() {
            return title;
        }

        public Drawable getIcon() {
            return icon;
        }
    }

    private class CustomAdapter extends ArrayAdapter<DrawerRow> {

        public CustomAdapter(Context context) {
            super(context, R.layout.drawer_item_layout, rows);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_layout, parent, false);

            ((TextView) view.findViewById(android.R.id.title)).setText(super.getItem(position).getTitle());
            ((TextView) view.findViewById(android.R.id.title)).setTextColor(getResources().getColor(R.color.material_grey_500));
            ((ImageView) view.findViewById(android.R.id.icon)).setImageDrawable(super.getItem(position).getIcon());
            ((ImageView) view.findViewById(android.R.id.icon)).setColorFilter(getResources().getColor(R.color.material_grey_500), PorterDuff.Mode.SRC_IN);
            return view;
        }
    }
}

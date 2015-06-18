package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gc.materialdesign.views.ButtonFloat;
import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.ServiceRecyclerAdapter;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.Calendar;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ServicesFragment extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getSimpleName();

    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView recyclerView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ServiceRecyclerAdapter mAdapter;

    private Vehicle vehicle;
    private MainActivity activity;
    private CarSQL carSQL;
    Handler mainHandler = new Handler(Looper.getMainLooper());
    com.gc.materialdesign.views.ButtonFloat buttonFloat;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServicesFragment(final MainActivity activity, Vehicle vehicle) {
        this.activity = activity;
        this.vehicle = vehicle;
        carSQL = activity.getCarsSQL();
        mAdapter = new ServiceRecyclerAdapter(activity, vehicle.getServiceTaskCursor());
        mAdapter.setHasStableIds(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(android.R.id.list);

        long start = Calendar.getInstance().getTimeInMillis();
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        long sort = Calendar.getInstance().getTimeInMillis();
        setSortOrder(ServiceTask.DATE, true);
        Log.v(TAG, "Took " + (sort - start) + "ms to set view and " + (Calendar.getInstance().getTimeInMillis() - sort) + "ms to get sorted version");

        mAdapter.setOnItemClickListener(new ServiceRecyclerAdapter.OnClickListener() {
            @Override
            public void onItemClick(long itemId) {

            }

            @Override
            public void onDeleteClick(long itemId) {

            }

            @Override
            public void onEditClick(long itemId) {
                long start = Calendar.getInstance().getTimeInMillis();
                ServiceTask task = new ServiceTask(carSQL, itemId);
                Log.v(TAG, "Type: " + task.getType() + " Date: " + DateFormat.getMediumDateFormat(activity).format(task.getDate()));
                Log.v(TAG, "Loading task took " + (Calendar.getInstance().getTimeInMillis() - start));
                activity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new EditService(task))
                        .addToBackStack("EditService-" + itemId).commit();
            }
        });

        buttonFloat = (ButtonFloat) view.findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setUIColor(vehicle.getDisplayColor());
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setSortOrder(String column, boolean inverse) {
        long start = Calendar.getInstance().getTimeInMillis();
        mAdapter.changeCursor(vehicle.getServiceTaskCursor(column, inverse));
        Log.v(TAG, "Sort took: " + (Calendar.getInstance().getTimeInMillis() - start));
    }

    private void setUIColor(int color) {
        if (color != 0) {
            buttonFloat.setBackgroundColor(color);
            activity.setUIColor(color);
        }
    }
}

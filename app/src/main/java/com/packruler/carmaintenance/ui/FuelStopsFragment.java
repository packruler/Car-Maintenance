package com.packruler.carmaintenance.ui;


import android.app.Fragment;
import android.os.Bundle;
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
import com.packruler.carmaintenance.ui.adapters.FuelStopAdapter;
import com.packruler.carmaintenance.ui.adapters.ServiceRecyclerAdapter;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FuelStopsFragment extends android.support.v4.app.Fragment {
    private static final String TAG = "FuelStopsFragment";

    private MainActivity activity;
    private Vehicle vehicle;
    private CarSQL carSQL;
    private View rootView;
    private RecyclerView recyclerView;
    private FuelStopAdapter mAdapter;

    private ButtonFloat buttonFloat;


    public FuelStopsFragment() {
        // Required empty public constructor
    }

    public FuelStopsFragment(final MainActivity activity, Vehicle vehicle) {
        this.activity = activity;
        this.vehicle = vehicle;
        carSQL = activity.getCarsSQL();
        mAdapter = new FuelStopAdapter(carSQL, vehicle.getFuelStopCursor());
        mAdapter.setHasStableIds(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_services, container, false);

        // Set the adapter
        recyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);

        long start = Calendar.getInstance().getTimeInMillis();
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        long sort = Calendar.getInstance().getTimeInMillis();
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

        buttonFloat = (ButtonFloat) rootView.findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        setUIColor(vehicle.getDisplayColor());
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setUIColor(int color) {
        if (color != 0) {
            buttonFloat.setBackgroundColor(color);
            activity.setUIColor(color);
        }
    }
}

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

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonFloat;
import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.FuelStopAdapter;
import com.packruler.carmaintenance.ui.adapters.ServiceRecyclerAdapter;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;

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

    private void updateCursor() {
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
            public void onItemClick(long itemId, RecyclerView.ViewHolder holder) {
            }

            @Override
            public void onDeleteClick(final long itemId, final RecyclerView.ViewHolder holder) {
                new MaterialDialog.Builder(activity)
                        .title(R.string.confirm).titleColor(activity.getUiColor())
                        .positiveText(R.string.accept).positiveColor(activity.getUiColor())
                        .negativeText(R.string.cancel).negativeColor(activity.getColor(R.color.material_grey_900))
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                Log.v(TAG, "Deleting fuel stop");
                                new FuelStop(carSQL, itemId).delete();
                                recyclerView.removeViewAt(holder.getLayoutPosition());
                                mAdapter.changeCursor(vehicle.getFuelStopCursor());
                            }
                        }).show();
//                builder.setTitle(getString(R.string.confirm));
//                builder.setNegativeButton(getString(R.string.cancel), null);
//                builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.v(TAG, "Deleting fuel stop");
//                        new FuelStop(carSQL, itemId).delete();
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//                builder.show();
            }

            @Override
            public void onEditClick(long itemId, RecyclerView.ViewHolder holder) {
                long start = Calendar.getInstance().getTimeInMillis();
                FuelStop task = new FuelStop(carSQL, itemId);
                Log.v(TAG, "Type: " + task.getType() + " Date: " + DateFormat.getMediumDateFormat(activity).format(task.getDate()));
                Log.v(TAG, "Loading task took " + (Calendar.getInstance().getTimeInMillis() - start));

                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new EditFuelStop(activity, task))
                        .addToBackStack(EditFuelStop.class.getSimpleName())
                        .commit();
            }
        });

        buttonFloat = (ButtonFloat) rootView.findViewById(R.id.buttonFloat);
        buttonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new EditFuelStop(activity, null))
                        .addToBackStack(EditFuelStop.class.getSimpleName())
                        .commit();
            }
        });
        setUIColor(vehicle.getUiColor());
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

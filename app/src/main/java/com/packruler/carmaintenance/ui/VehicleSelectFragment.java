package com.packruler.carmaintenance.ui;


import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.VehicleCursorAdapter;
import com.packruler.carmaintenance.vehicle.Vehicle;

/**
 * A simple {@link Fragment} subclass.
 */
public class VehicleSelectFragment extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getSimpleName();

    private View rootView;
    private CarSQL carSQL;
    private Cursor cursor;

    public VehicleSelectFragment() {
        // Required empty public constructor
    }

    public VehicleSelectFragment(CarSQL carSQL) {
        this.carSQL = carSQL;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_vehicle_select, container, false);

        rootView.findViewById(R.id.buttonFloat).setOnClickListener(new View.OnClickListener() {
            private final String TAG = getClass().getSimpleName();

            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick");
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new EditCar((MainActivity) getActivity()))
                        .addToBackStack(EditCar.TAG)
                        .commit();
            }
        });


        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.select_car_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        cursor = carSQL.getReadableDatabase().query(Vehicle.TABLE_NAME, null,
                null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            ((TextView) rootView.findViewById(android.R.id.empty)).setText("EMPTY");
            rootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        } else
            setupAdapter();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor != null && !cursor.isClosed())
            cursor.close();
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof MainActivity)
            ((MainActivity) activity).setUIColor(getResources().getColor(R.color.default_ui_color));
        super.onAttach(activity);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void setupAdapter() {
        VehicleCursorAdapter adapter = new VehicleCursorAdapter(getActivity(), cursor, carSQL);
        adapter.setOnClickListener(new VehicleCursorAdapter.OnClickListener() {
            @Override
            public void onClick(long itemId) {
                Log.v(TAG, "Item ID: " + itemId);
                if (getActivity() instanceof MainActivity)
                    ((MainActivity) getActivity()).changeVehicle(itemId);
            }
        });
        ((RecyclerView) rootView.findViewById(R.id.select_car_recycler)).setAdapter(adapter);
    }
}

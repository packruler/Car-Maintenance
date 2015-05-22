package com.packruler.carmaintenance.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.weiwangcn.betterspinner.library.BetterSpinner;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCar extends Fragment {

    private final String TAG = getClass().getName();

    private CarSQL carSQL;
    private AvailableCarsSQL availableCarsSQL;
    private MainActivity activity;

    private BetterSpinner yearSpinner;
    private BetterSpinner makeSpinner;
    private BetterSpinner modelSpinner;

    public EditCar() {
        // Required empty public constructor
    }

    public EditCar(MainActivity activity, CarSQL carSQL) {
        this.carSQL = carSQL;
        this.activity = activity;
        try {
            availableCarsSQL = new AvailableCarsSQL(this.activity);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_car2, container, false);
        setYearSpinner();
        initializeMakeSpinner();

        return rootView;
    }

    private void setYearSpinner() {
        yearSpinner = (BetterSpinner) rootView.findViewById(R.id.year_spinner);
        List<String> yearList = new LinkedList<>();

        yearList.add(getString(R.string.other_selection));
        for (int x = 1984; x <= Calendar.getInstance().get(Calendar.YEAR) + 1; x++) {
            yearList.add(0, x + "");
        }

        yearSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, yearList));
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final String TAG = "YearSpinnerItemClick";

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + yearSpinner.getAdapter().getItem(position));
                if (yearSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
                    //TODO: Other selected
                } else {
                    updateMakeSpinner();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private List<String> makeList = new LinkedList<>();

    private void initializeMakeSpinner() {
        makeList = new LinkedList<>();
        makeList.add(getString(R.string.other_selection));

        makeSpinner = (BetterSpinner) rootView.findViewById(R.id.make_spinner);
        makeSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, makeList));
        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private final String TAG = "MakeSpinnerItemClick";

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + yearSpinner.getAdapter().getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void updateMakeSpinner() {
        Log.v(TAG, "UpdateMakeSpinner");
        makeList.clear();
        makeList.add(getString(R.string.other_selection));
        makeList.addAll(0, availableCarsSQL.getAvailableMakes(yearSpinner.getText().toString()));

        ((ArrayAdapter) makeSpinner.getAdapter()).notifyDataSetChanged();
    }
}

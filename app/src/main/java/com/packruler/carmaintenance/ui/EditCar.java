package com.packruler.carmaintenance.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

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

    private EditText nameText;
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
        initializeNameText();
        setYearSpinner();
        initializeMakeSpinner();
        initizeModelSpinner();

        return rootView;
    }

    private void initializeNameText() {
        nameText = (EditText) rootView.findViewById(R.id.vehicle_name);
        nameText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    private void setYearSpinner() {
        yearSpinner = (BetterSpinner) rootView.findViewById(R.id.year);
        List<String> yearList = new LinkedList<>();

        yearList.add(getString(R.string.other_selection));
        for (int x = 1984; x <= Calendar.getInstance().get(Calendar.YEAR) + 1; x++) {
            yearList.add(0, x + "");
        }

        yearSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, yearList));
        yearSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "YearSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (yearSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
                    //TODO: Other selected
                } else {
                    updateMakeSpinner();
                }
            }
        });
    }

    private List<String> makeList = new LinkedList<>();

    private void initializeMakeSpinner() {
        makeList.add(getString(R.string.other_selection));

        makeSpinner = (BetterSpinner) rootView.findViewById(R.id.make);
        makeSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, makeList));
        makeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "MakeSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + makeSpinner.getAdapter().getItem(position));
                if (makeSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
                    //TODO: Setup Other
                } else {
                    updateModelSpinner();
                }

            }
        });
    }


    private void updateMakeSpinner() {
        Log.v(TAG, "UpdateMakeSpinner");
        makeList.clear();
        Log.v(TAG, "Year: " + yearSpinner.getText().toString());
        makeList.addAll(availableCarsSQL.getAvailableMakes(yearSpinner.getText().toString()));
        makeList.add(getString(R.string.other_selection));

        ((ArrayAdapter) makeSpinner.getAdapter()).notifyDataSetChanged();
    }

    private List<String> modelList = new LinkedList<>();

    private void initizeModelSpinner() {
        modelSpinner = (BetterSpinner) rootView.findViewById(R.id.model);
        modelList.add(getString(R.string.other_selection));

        modelSpinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, modelList));
        modelSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "MakeSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + modelSpinner.getAdapter().getItem(position));
                if (modelSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
                    //TODO: Setup Other
                } else {

                }

            }
        });
    }

    private void updateModelSpinner() {
        modelList.clear();

        modelList.addAll(availableCarsSQL.getAvailableModels(yearSpinner.getText().toString(), makeSpinner.getText().toString()));
        modelList.add(getString(R.string.other_selection));

        ((ArrayAdapter) modelSpinner.getAdapter()).notifyDataSetChanged();
    }
}

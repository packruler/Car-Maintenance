package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditFuelStop.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditFuelStop#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFuelStop extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getSimpleName();

    private FuelStop fuelStop;
    private MaterialEditText mileage;
    private NumberPicker numberPicker1;
    private NumberPicker decimal;
    private NumberPicker numberPicker2;
    private NumberPicker numberPicker3;
    private NumberPicker numberPicker4;

    public EditFuelStop() {
    }

    public EditFuelStop(FuelStop fuelStop) {
        this.fuelStop = fuelStop;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit_fuel_stop, container, false);

        numberPicker1 = (NumberPicker) rootView.findViewById(R.id.number1);
        numberPicker1.setMaxValue(9);
        numberPicker1.setMinValue(0);

        decimal = (NumberPicker) rootView.findViewById(R.id.decimal);
        decimal.setDisplayedValues(new String[]{"."});
        decimal.setValue('.');

        numberPicker2 = (NumberPicker) rootView.findViewById(R.id.number2);
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);

        numberPicker3 = (NumberPicker) rootView.findViewById(R.id.number3);
        numberPicker3.setMaxValue(9);
        numberPicker3.setMinValue(0);

        numberPicker4 = (NumberPicker) rootView.findViewById(R.id.number4);
        numberPicker4.setMaxValue(9);
        numberPicker4.setMinValue(0);
        numberPicker4.setValue(9);
        return rootView;
    }


}

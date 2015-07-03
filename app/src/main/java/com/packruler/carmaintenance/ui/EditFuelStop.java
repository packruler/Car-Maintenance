package com.packruler.carmaintenance.ui;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

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

    private View rootView;
    private FuelStop fuelStop;
    private MainActivity activity;
    private MaterialEditText mileage;
    private MaterialEditText costPerVolume;
    private MaterialEditText dateDisplay;
    private MaterialEditText volume;
    private MaterialEditText totalCost;

    private AlertDialog costPerVolumeDialog;
    private DatePickerDialog datePickerDialog;
    private float costPer;
    private long dateSet = -1l;

    public EditFuelStop() {
    }

    public EditFuelStop(MainActivity activity, @Nullable FuelStop fuelStop) {
        this.fuelStop = fuelStop;
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_fuel_stop, container, false);
        initializeCostPerVolume();


        return rootView;
    }

    private void initializeDate() {
        dateDisplay = (MaterialEditText) rootView.findViewById(R.id.date_display);


    }

    private Runnable setupDatePickerDialog = new Runnable() {
        @Override
        public void run() {
            Calendar currentDay = Calendar.getInstance();
            datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    dateSet = view.getCalendarView().getDate();
                }
            }, currentDay.get(Calendar.YEAR), currentDay.get(Calendar.MONTH), currentDay.get(Calendar.DAY_OF_MONTH));
        }
    };

    private void initializeCostPerVolume() {
        costPerVolume = (MaterialEditText) rootView.findViewById(R.id.cost_per_volume);
        rootView.findViewById(R.id.cost_per_volume_click).setOnClickListener(new View.OnClickListener() {
            private String TAG = "cost_per_volume_click";

            @Override
            public void onClick(View v) {
                costPerVolumeDialog.show();
            }
        });

        if (fuelStop != null)
            setCostPerVolumeDisplay(fuelStop.getCostPerVolume());

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.cost_per_volume));

        LinearLayout numLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.cost_per_volume_dialog, null);
        final EditText[] price = new EditText[numLayout.getChildCount()];
        for (int x = 0; x < numLayout.getChildCount(); x++) {
            price[x] = (EditText) numLayout.getChildAt(x);
            Log.v(TAG, x + " null: " + (price[x] == null));
            price[x].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        }
        price[1].setText("" + DecimalFormatSymbols.getInstance().getDecimalSeparator());
        price[1].setEnabled(false);
        price[4].setEnabled(false);
        price[0].addTextChangedListener(new TextWatcher() {
            private String TAG = "price0";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    price[2].requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        price[2].addTextChangedListener(new TextWatcher() {
            private String TAG = "price2";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0)
                    price[3].requestFocus();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        builder.setView(numLayout);

        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            private String TAG = "Accept.Button";

            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();
                String buffer;
                for (EditText curr : price) {
                    buffer = curr.getText().toString();
                    if (buffer.length() > 0)
                        stringBuilder.append(buffer);
                    else
                        stringBuilder.append('0');
                }
                setCostPerVolumeDisplay(Float.valueOf(stringBuilder.toString()));
            }
        });
        builder.setNegativeButton(R.string.cancel, null);

        costPerVolumeDialog = builder.create();
        costPerVolumeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            private String TAG = getClass().getSimpleName();

            @Override
            public void onShow(DialogInterface dialog) {
                price[0].requestFocus();
                costPerVolumeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });
    }

    private void setCostPerVolumeDisplay(float value) {
        costPer = value;
        NumberFormat format = new DecimalFormat("0.000");
        String currencyString = activity.getCurrentVehicle().getCurrency();
        Log.v(TAG, "Currency: " + currencyString);
        if (currencyString != null)
            currencyString = Currency.getInstance(currencyString).getSymbol();
        else
            currencyString = "";
        Log.v(TAG, "Currency: " + currencyString);

        costPerVolume.setText(currencyString + format.format(costPer));
    }

}

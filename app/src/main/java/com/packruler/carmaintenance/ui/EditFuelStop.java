package com.packruler.carmaintenance.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.CheckBox;
import com.gc.materialdesign.views.Switch;
import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.ui.utilities.MenuHandler;
import com.packruler.carmaintenance.ui.utilities.NumberTextWatcher;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;

public class EditFuelStop extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getSimpleName();

    private View rootView;
    private FuelStop fuelStop;
    private MainActivity activity;
    private MaterialEditText mileage;
    private MaterialEditText costPerVolumeDisplay;
    private MaterialEditText dateDisplay;
    private MaterialEditText volume;
    private MaterialEditText totalCost;
    private Switch completeFillup;
    private Switch missedFillup;

    private MaterialDialog costPerVolumeDialog;
    private float costPer;

    private MaterialDialog datePickerDialog;
    private Calendar date = Calendar.getInstance();
    private String currencyString;

    private int uiColor;

    public EditFuelStop() {
    }

    @SuppressLint("ValidFragment")
    public EditFuelStop(MainActivity activity, @Nullable FuelStop fuelStop) {
        this.fuelStop = fuelStop;
        this.activity = activity;
        uiColor = activity.getCurrentVehicle().getUiColor();
        if (uiColor == 0)
            uiColor = activity.getResources().getColor(R.color.default_ui_color);

        currencyString = activity.getCurrentVehicle().getCurrency();
        Log.v(TAG, "Currency: " + currencyString);
        if (currencyString != null)
            currencyString = Currency.getInstance(currencyString).getSymbol();
        else
            currencyString = "";
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_fuel_stop, container, false);
        ((TextView) rootView.findViewById(R.id.general_info_title)).setTextColor(uiColor);
        initializeDate();
        initializeVolume();
        initializeCalcEff();
        initializeMileage();
        initializeCostPerVolume();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            ((MainActivity) activity).getSupportActionBar().setTitle("Edit Fuel Stop");
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private Drawable[] icons = new Drawable[3];

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuHandler.setupEditMenu(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.save))) {
            activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            activity.execute(new Runnable() {
                @Override
                public void run() {
                    save();
                }
            });
        } else if (item.getTitle().equals(getString(R.string.discard)))
            Log.v(TAG, "Pop EditCar " + (getFragmentManager().popBackStackImmediate(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE) ? "Success" : "FAIL"));
        else if (item.getTitle().equals(getString(R.string.delete))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(getString(R.string.confirm));
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (fuelStop != null) {
                        Log.v(TAG, "Deleting fuel stop");
                        fuelStop.delete();
                    }
                    Log.v(TAG, "Pop FuelStop " + (getFragmentManager().popBackStackImmediate(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE) ? "Success" : "FAIL"));
                }
            });
            builder.show();
        } else if (item.getItemId() == android.R.id.home) {
            Log.v(TAG, "HOME");
            getFragmentManager().popBackStack();
        }
        return true;
    }

    private void initializeDate() {
        Log.v(TAG, "init Date");
        dateDisplay = (MaterialEditText) rootView.findViewById(R.id.date_display);
        dateDisplay.setPrimaryColor(uiColor);
        date = Calendar.getInstance();
        if (fuelStop != null)
            date.setTimeInMillis(fuelStop.getDate());

        final DatePicker picker = new DatePicker(activity);
        picker.setMaxDate(Calendar.getInstance().getTimeInMillis());

        datePickerDialog = new MaterialDialog.Builder(activity)
                .customView(picker, false)
                .positiveText(R.string.accept)
                .positiveColor(getResources().getColor(R.color.default_ui_color))
                .negativeText(R.string.cancel)
                .negativeColor(getResources().getColor(R.color.material_grey_900))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        date.set(picker.getYear(), picker.getMonth(), picker.getDayOfMonth());
                        displaySetDate();
                    }
                })
                .build();

        rootView.findViewById(R.id.date_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        displaySetDate();
    }

    private void saveDate() {
        fuelStop.setDate(date.getTimeInMillis());
    }

    private void displaySetDate() {
        dateDisplay.setText(DateFormat.getLongDateFormat(activity).format(new Date(date.getTimeInMillis())));
    }

    private void initializeCostPerVolume() {
        costPerVolumeDisplay = (MaterialEditText) rootView.findViewById(R.id.cost_per_volume);
        costPerVolumeDisplay.setPrimaryColor(uiColor);
        rootView.findViewById(R.id.cost_per_volume_click).setOnClickListener(new View.OnClickListener() {
            private String TAG = "cost_per_volume_click";

            @Override
            public void onClick(View v) {
                costPerVolumeDialog.show();
            }
        });

        if (fuelStop != null)
            setCostPerVolumeDisplay(fuelStop.getCostPerVolume());

        LinearLayout numLayout = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.cost_per_volume_dialog, null);

        final MaterialEditText[] price = new MaterialEditText[numLayout.getChildCount()];

        for (int x = 0; x < numLayout.getChildCount(); x++) {
            price[x] = (MaterialEditText) numLayout.getChildAt(x);
            price[x].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
            price[x].setPrimaryColor(uiColor);
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

        costPerVolumeDialog = new MaterialDialog.Builder(activity)
                .customView(numLayout, false)
                .title(R.string.cost_per_volume)
                .titleColor(uiColor)
                .positiveText(R.string.accept)
                .positiveColor(uiColor)
                .negativeText(R.string.cancel)
                .negativeColor(getResources().getColor(R.color.material_grey_900))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
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

                    @Override
                    public void onAny(MaterialDialog dialog) {
                        costPerVolumeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    }
                }).build();

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
        Log.v(TAG, "Currency: " + currencyString);

        costPerVolumeDisplay.setText(currencyString + format.format(costPer));

        try {
            value = Float.parseFloat(volume.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            value = 0;
        }
        value *= costPer;
        format = new DecimalFormat("0.00");

        totalCost.setText(currencyString + format.format(value));
    }

    private boolean saveCostPerVolume() {
        if (costPerVolumeDisplay.getText().toString().length() > 0) {
            fuelStop.setCostPerVolume(costPer);
            return true;
        }
        return false;
    }

    private void initializeVolume() {
        volume = (MaterialEditText) rootView.findViewById(R.id.volume_display);
        volume.setPrimaryColor(uiColor);
        totalCost = (MaterialEditText) rootView.findViewById(R.id.total_cost);
        totalCost.setPrimaryColor(uiColor);

        volume.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                float value = 0;
                try {
                    value = Float.parseFloat(s.toString());
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                    value = 0;
                }
                value *= costPer;
                NumberFormat format = new DecimalFormat("0.00");

                totalCost.setText(currencyString + format.format(value));

            }
        });
    }

    private boolean saveVolume() {
        float value;
        try {
            value = Float.parseFloat(volume.getText().toString());
        } catch (NumberFormatException e) {
            Log.e(TAG, e.getMessage());
            value = -1f;
        }
        if (value != -1f) {
            fuelStop.setVolume(Float.parseFloat(volume.getText().toString()));
            return true;
        }

        return false;
    }

    private void initializeMileage() {
        mileage = (MaterialEditText) rootView.findViewById(R.id.current_mileage);
        mileage.setPrimaryColor(uiColor);
        mileage.addTextChangedListener(new NumberTextWatcher(mileage));
        if (fuelStop != null)
            mileage.setText(fuelStop.getMileage() + "");
    }

    private boolean saveMileage() {
        if (mileage.getText().toString().length() > 0)
            try {
                fuelStop.setMileage(Long.parseLong(mileage.getText().toString()));
                return true;
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
            }
        return false;
    }

    private void initializeCalcEff() {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CheckBox) v).setChecked(!((CheckBox) v).isCheck());
            }
        };
        ((TextView) rootView.findViewById(R.id.calc_efficiency_title)).setTextColor(uiColor);

        missedFillup = (com.gc.materialdesign.views.Switch) rootView.findViewById(R.id.missed_fill_up);
        missedFillup.setBackgroundColor(uiColor);
        missedFillup.setOnClickListener(onClickListener);

        completeFillup = (Switch) rootView.findViewById(R.id.complete_fill_up);
        completeFillup.setBackgroundColor(uiColor);
        completeFillup.setOnClickListener(onClickListener);

        if (fuelStop != null) {
            missedFillup.setChecked(fuelStop.missedFillup());
            completeFillup.setChecked(fuelStop.isCompleteFillUp());
        } else {
            missedFillup.setChecked(true);
            completeFillup.setChecked(false);
        }
    }

    private void updateDistanceTraveled() {
        if (!missedFillup.isCheck() && completeFillup.isCheck())
            fuelStop.updateDistanceTraveled();
    }

    private void save() {
        boolean update = true;
        if (fuelStop == null)
            activity.getCurrentVehicle().getNewFuelStop();

        assert fuelStop != null;
        fuelStop.beginTransaction();
        if (!saveCostPerVolume())
            update = false;

        if (!saveMileage())
            update = false;

        if (!saveVolume())
            update = false;

        saveDate();

        if (update)
            updateDistanceTraveled();
    }

    private void delete() {

    }
}

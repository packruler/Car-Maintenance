package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.sql.SQLDataException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EditCarFragment extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getName();

    private Vehicle vehicle;
    private ListView listView;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private CarSQL carSQL;
    private AvailableCarsSQL availableCarsSQL;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private int numProcessors = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(numProcessors, numProcessors, 10, TimeUnit.SECONDS, workQueue);

    public EditCarFragment(Activity activity, AvailableCarsSQL availableCarsSQL) {
        for (int x = Calendar.getInstance().get(Calendar.YEAR) + 1; x >= 1984; x--) {
            years.add("" + x);
        }

        this.availableCarsSQL = availableCarsSQL;
        this.activity = activity;
    }

    public EditCarFragment(Activity activity, AvailableCarsSQL availableCarsSQL, Vehicle vehicle) {
        this(activity, availableCarsSQL);
        this.vehicle = vehicle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_new_car, container, false);
        setVariables(rootView);
        return rootView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private EditText nameText;

    private EditText yearText;
    private TextView yearDisplay;

    private EditText makeText;
    private TextView makeDisplay;

    private EditText modelText;
    private TextView modelDisplay;

    private EditText submodelText;
    private EditText mileageText;
    private EditText purchaseCost;
    private DatePicker purchaseDate;
    private boolean variablesSet = false;

    private void setVariables(View view) {
        nameText = (EditText) view.findViewById(R.id.edit_name);
        yearText = (EditText) view.findViewById(R.id.edit_year);
        view.findViewById(R.id.year_card).setOnClickListener(popupClickListener);
        yearDisplay = (TextView) view.findViewById(R.id.year_selected_display);

        makeText = (EditText) view.findViewById(R.id.edit_make);
        view.findViewById(R.id.make_card).setOnClickListener(popupClickListener);
        makeDisplay = (TextView) view.findViewById(R.id.make_selected_display);

        modelText = (EditText) view.findViewById(R.id.edit_model);
        view.findViewById(R.id.model_card).setOnClickListener(popupClickListener);
        modelDisplay = (TextView) view.findViewById(R.id.model_selected_display);

        submodelText = (EditText) view.findViewById(R.id.edit_submodel);
        mileageText = (EditText) view.findViewById(R.id.edit_mileage);
        purchaseCost = (EditText) view.findViewById(R.id.edit_purchase_cost);
        purchaseDate = (DatePicker) view.findViewById(R.id.edit_purchase_date);

        purchaseDate.setSpinnersShown(true);
        purchaseDate.setCalendarViewShown(false);
        purchaseDate.setMaxDate(Calendar.getInstance().getTime().getTime());

        view.findViewById(R.id.save_changes).setOnClickListener(saveDiscardListener);
        view.findViewById(R.id.discard_changes).setOnClickListener(saveDiscardListener);

        setupAlertDialogs();

//        loadVehicleDetails();
//        variablesSet = true;
    }

    private void storeVehicle() {
        String toastError = "Please fill:";
        String updated = verifyEntries(toastError);
        MainActivity activity = (MainActivity) getActivity();
        if (!updated.equals(toastError))
            Toast.makeText(activity, updated, Toast.LENGTH_LONG).show();
        else {
            if (activity.containsCar(nameText.getText().toString()))
                Toast.makeText(activity, "Name already in use. Please choose new name.", Toast.LENGTH_LONG).show();
            else {
                try {
                    vehicle.setName(nameText.getText().toString());
                } catch (SQLDataException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                if (yearText.getText().length() > 0)
                    vehicle.setYear(Integer.valueOf(yearText.getText().toString()));

                try {
                    if (makeText.getText().length() > 0)
                        vehicle.setMake(makeText.getText().toString());
                } catch (SQLDataException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                try {
                    if (modelText.getText().length() > 0)
                        vehicle.setModel(modelText.getText().toString());
                } catch (SQLDataException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                try {
                    if (submodelText.getText().length() > 0)
                        vehicle.setSubmodel(submodelText.getText().toString());
                } catch (SQLDataException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                if (mileageText.getText().length() > 0)
                    vehicle.setMileage(Long.valueOf(mileageText.getText().toString()));

                if (purchaseCost.getText().length() > 0)
                    vehicle.setPurchaseCost(Float.valueOf(purchaseCost.getText().toString()));

                vehicle.setPurchaseDate(new Date(purchaseDate.getYear(), purchaseDate.getMonth(), purchaseDate.getDayOfMonth()));
                activity.updateCar(vehicle);
            }
        }
    }

    private String verifyEntries(String toastError) {
        if (nameText.length() == 0)
            toastError += "\n" + getString(R.string.name).substring(0, getString(R.string.name).length() - 1);

//        if (yearText.length() == 0)
//            toastError += "\n" + getString(R.string.year).substring(0, getString(R.string.year).length() - 1);
//
//        if (makeText.length() == 0)
//            toastError += "\n" + getString(R.string.make).substring(0, getString(R.string.make).length() - 1);
//
//        if (modelText.length() == 0)
//            toastError += "\n" + getString(R.string.model).substring(0, getString(R.string.model).length() - 1);
//
//        if (mileageText.length() == 0)
//            toastError += "\n" + getString(R.string.mileage).substring(0, getString(R.string.mileage).length() - 1);
//
//        if (purchaseCost.length() == 0)
//            toastError += "\n" + getString(R.string.purchase_cost).substring(0, getString(R.string.purchase_cost).length() - 1);

        return toastError;
    }


    private void loadVehicleDetails() {
//        Map<String, String> defaultStrings = vehicle.getDetailValues();

        nameText.setText(vehicle.getName());
        yearText.setText(vehicle.getYear());
        makeText.setText(vehicle.getMake());
        modelText.setText(vehicle.getModel());
        submodelText.setText(vehicle.getSubmodel());
        mileageText.setText("" + vehicle.getMileage());
        purchaseCost.setText("" + vehicle.getPurchaseCost());

        Log.i(TAG, "Name: " + vehicle.getName());

        Date date = vehicle.getPurchaseDate();
        purchaseDate.init(date.getYear() + 1900, date.getMonth(), date.getDay(), null);
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (variablesSet)
            loadVehicleDetails();
    }

    private View.OnClickListener saveDiscardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick");
            switch (v.getId()) {
                case R.id.save_changes:
                    Log.i(TAG, "Name Length: " + nameText.getText().length());
                    storeVehicle();
                    break;
                case R.id.discard_changes:
                    Log.i(TAG, "Discard Changes");
                    loadVehicleDetails();
                    break;
            }
        }
    };

    public void setCarSQL(CarSQL carSQL) {
        this.carSQL = carSQL;
        if (vehicle == null)
            vehicle = new Vehicle(carSQL, "");
    }

    private View.OnClickListener popupClickListener = new View.OnClickListener() {
        private final String TAG = getClass().getName();

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick");
            switch (v.getId()) {
                case R.id.year_card:
                    Log.i(TAG, "Year card");
                    yearPopup.show();
                    break;
                case R.id.make_card:
                    Log.i(TAG, "Make Card");
                    makePopup.show();
                    break;
                case R.id.model_card:
                    Log.i(TAG, "Model Card");
                    modelPopup.show();
                    break;

            }
        }
    };

    private AlertDialog yearPopup;
    private AlertDialog makePopup;
    private AlertDialog modelPopup;

    private void setupAlertDialogs() {
        setYearPopup();
        setMakePopup(false);
    }

    private LinkedList<String> years = new LinkedList<>();

    private void setYearPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.year);

        if (!years.contains(getString(R.string.other_selection)))
            years.add(getString(R.string.other_selection));

        String[] yearArray = years.toArray(new String[years.size()]);

        builder.setSingleChoiceItems(yearArray, 0, yearMenuListener);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                yearPopup = builder.create();
            }
        });
    }

    private LinkedList<String> makes = new LinkedList<>();

    private void setMakePopup(boolean updated) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.make);

        if (updated)
            makes = new LinkedList<>(availableCarsSQL.getAvailableMakes(yearDisplay.getText().toString()));

        if (!makes.contains(getString(R.string.other_selection)))
            makes.add(getString(R.string.other_selection));


        String[] array = makes.toArray(new String[makes.size()]);

        int selection = makes.indexOf(makeDisplay.getText().toString());

        if (selection == -1)
            setMakeDisplay(getString(R.string.click_to_set));

        builder.setSingleChoiceItems(array, selection, makeMenuListener);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                makePopup = builder.create();
            }
        });
    }

    private LinkedList<String> models = new LinkedList<>();

    private void setModelPopup(boolean updated) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.model);

        if (updated)
            models = new LinkedList<>(availableCarsSQL.getAvailableModels(
                    yearDisplay.getText().toString(), makeDisplay.getText().toString()));


        if (!models.contains(getString(R.string.other_selection)))
            models.add(getString(R.string.other_selection));

        String[] array = models.toArray(new String[models.size()]);

        int selection = models.indexOf(modelDisplay.getText().toString());
        if (selection == -1)
            setModelDisplay(getString(R.string.click_to_set));

        builder.setSingleChoiceItems(array, selection, modelMenuListener);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                modelPopup = builder.create();
            }
        });
    }

    private AlertDialog.OnClickListener yearMenuListener = new AlertDialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setYearDisplay(years.get(which));
            dialog.dismiss();
        }
    };

    private AlertDialog.OnClickListener makeMenuListener = new AlertDialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setMakeDisplay(makes.get(which));
            dialog.dismiss();
        }
    };

    private AlertDialog.OnClickListener modelMenuListener = new AlertDialog.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setModelDisplay(models.get(which));
            dialog.dismiss();
        }
    };

    private void setYearDisplay(String year) {
        Log.i(TAG, "Set year to " + year);
        if (!yearDisplay.getText().equals(year)) {
            yearDisplay.setText(year);
            setMakePopup(true);
            setModelPopup(true);
        }
    }

    private void setMakeDisplay(String make) {
        Log.i(TAG, "Set make to " + make);
        if (!makeDisplay.getText().equals(make)) {
            makeDisplay.setText(make);
            setModelDisplay(getString(R.string.click_to_set));
            setModelPopup(true);
        }
    }

    private void setModelDisplay(String model) {
        Log.i(TAG, "Set Model to " + model);
        if (!modelDisplay.getText().equals(model)) {
            modelDisplay.setText(model);
        }
    }
}

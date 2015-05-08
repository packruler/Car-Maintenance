package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.sql.SQLDataException;
import java.util.Calendar;
import java.util.Date;

public class EditCarFragment extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getName();

    private Vehicle vehicle;
    private ListView listView;
    private MainActivity mainActivity;
    private SharedPreferences sharedPreferences;
    private CarSQL carSQL;


    public EditCarFragment() {
        // Required empty public constructor
//        sharedPreferences = getActivity().getSharedPreferences(getActivity().getApplication().getPackageName(), Context.MODE_MULTI_PROCESS);
    }

    public EditCarFragment(Vehicle vehicle) {
//        this();
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
        mainActivity = (MainActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private EditText nameText;
    private EditText yearText;
    private EditText makeText;
    private EditText modelText;
    private EditText submodelText;
    private EditText mileageText;
    private EditText purchaseCost;
    private DatePicker purchaseDate;
    private boolean variablesSet = false;

    private void setVariables(View view) {
        nameText = (EditText) view.findViewById(R.id.edit_name);
        yearText = (EditText) view.findViewById(R.id.edit_year);
        makeText = (EditText) view.findViewById(R.id.edit_make);
        modelText = (EditText) view.findViewById(R.id.edit_model);
        submodelText = (EditText) view.findViewById(R.id.edit_submodel);
        mileageText = (EditText) view.findViewById(R.id.edit_mileage);
        purchaseCost = (EditText) view.findViewById(R.id.edit_purchase_cost);
        purchaseDate = (DatePicker) view.findViewById(R.id.edit_purchase_date);

        purchaseDate.setSpinnersShown(true);
        purchaseDate.setCalendarViewShown(false);
        purchaseDate.setMaxDate(Calendar.getInstance().getTime().getTime());

        view.findViewById(R.id.save_changes).setOnClickListener(saveDiscardListener);
        view.findViewById(R.id.discard_changes).setOnClickListener(saveDiscardListener);

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

                try{
                if (modelText.getText().length() > 0)
                    vehicle.setModel(modelText.getText().toString());
                } catch (SQLDataException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }

                try{
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
}

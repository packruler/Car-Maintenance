package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.json.VINDecoder;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EditCarFragment extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getName();

    private Vehicle vehicle;
    private Activity activity;
    private CarSQL carSQL;
    private AvailableCarsSQL availableCarsSQL;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private int numProcessors = Runtime.getRuntime().availableProcessors();
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(numProcessors, numProcessors, 10, TimeUnit.SECONDS, workQueue);

    public EditCarFragment(Activity activity, AvailableCarsSQL availableCarsSQL, CarSQL carSQL) {
        for (int x = Calendar.getInstance().get(Calendar.YEAR) + 1; x >= 1984; x--) {
            years.add("" + x);
        }

        this.availableCarsSQL = availableCarsSQL;
        this.activity = activity;
        this.carSQL = carSQL;
    }

    public EditCarFragment(Activity activity, AvailableCarsSQL availableCarsSQL, CarSQL carSQL, Vehicle vehicle) {
        this(activity, availableCarsSQL, carSQL);
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


    private TextView nameDisplay;
    private TextView vinDisplay;

    private TextView yearDisplay;

    private TextView makeDisplay;

    private TextView modelDisplay;

    private TextView submodelDisplay;
    private TextView mileageDisplay;
    private TextView purchaseCostDisplay;
    private TextView purchaseDateDisplay;
    private boolean variablesSet = false;

    private void setVariables(View view) {
        view.findViewById(R.id.name_card).setOnClickListener(popupClickListener);
        nameDisplay = (TextView) view.findViewById(R.id.name_display);


        view.findViewById(R.id.vin_card).setOnClickListener(popupClickListener);
        vinDisplay = (TextView) view.findViewById(R.id.vin_display);
        view.findViewById(R.id.decode_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick");
                poolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        VINDecoder.decode(vinDisplay.getText().toString());
                    }
                });
            }
        });

        view.findViewById(R.id.year_card).setOnClickListener(popupClickListener);
        yearDisplay = (TextView) view.findViewById(R.id.year_display);

        view.findViewById(R.id.make_card).setOnClickListener(popupClickListener);
        makeDisplay = (TextView) view.findViewById(R.id.make_display);

        view.findViewById(R.id.model_card).setOnClickListener(popupClickListener);
        modelDisplay = (TextView) view.findViewById(R.id.model_display);

        view.findViewById(R.id.submodel_card).setOnClickListener(popupClickListener);
        submodelDisplay = (TextView) view.findViewById(R.id.submodel_display);

        view.findViewById(R.id.mileage_card).setOnClickListener(popupClickListener);
        mileageDisplay = (TextView) view.findViewById(R.id.mileage_display);

        view.findViewById(R.id.purchase_cost_card).setOnClickListener(popupClickListener);
        purchaseCostDisplay = (TextView) view.findViewById(R.id.purchase_cost_display);

        view.findViewById(R.id.purchase_date_card).setOnClickListener(popupClickListener);
        purchaseDateDisplay = (TextView) view.findViewById(R.id.purchase_date_display);

        view.findViewById(R.id.save_changes).setOnClickListener(saveDiscardListener);
        view.findViewById(R.id.discard_changes).setOnClickListener(saveDiscardListener);

        setupAlertDialogs();

//        loadVehicleDetails();
//        variablesSet = true;
    }

    private void storeVehicle() {
        if (!nameDisplay.getText().equals(getString(R.string.click_to_set))) {
            String tempString = nameDisplay.getText().toString();
            int tempInt;
            float tempFloat;
            long tempLong;

            ContentValues values = new ContentValues();

            if (vehicle == null)
                vehicle = new Vehicle(carSQL, tempString);

            if (!vehicle.getName().equals(tempString))
                vehicle.setName(tempString);

            tempString = vinDisplay.getText().toString();
            if (!tempString.equals(getString(R.string.click_to_set)) &&
                    !vehicle.getVin().equals(tempString))
                values.put(Vehicle.VIN, tempString);

            try {
                Log.i(TAG, "Year: " + yearDisplay.getText().toString());
                tempInt = Integer.valueOf(yearDisplay.getText().toString());
                if (vehicle.getYear() != tempInt)
                    values.put(Vehicle.YEAR, tempInt);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            tempString = makeDisplay.getText().toString();
            if (!tempString.equals(getString(R.string.click_to_set)) &&
                    (vehicle.getMake() == null || !vehicle.getMake().equals(tempString)))
                values.put(Vehicle.MAKE, tempString);

            tempString = modelDisplay.getText().toString();
            if (!tempString.equals(getString(R.string.click_to_set)) &&
                    (vehicle.getModel() == null || !vehicle.getModel().equals(tempString)))
                values.put(Vehicle.MODEL, tempString);

            tempString = submodelDisplay.getText().toString();
            if (!tempString.equals(getString(R.string.click_to_set)) &&
                    (vehicle.getSubmodel() == null || !vehicle.getSubmodel().equals(tempString)))
                values.put(Vehicle.SUBMODEL, tempString);

            storeMileage(values);

            storePurchaseCost(values);

            if (!purchaseDateDisplay.getText().toString().equals(getString(R.string.click_to_set))) {
                if (vehicle.getPurchaseDate() != dateTime)
                    values.put(Vehicle.PURCHASE_DATE, dateTime);
            }

            vehicle.setContentValues(values);
        }
    }

    private String verifyEntries(String toastError) {
        if (nameDisplay.length() == 0)
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
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        if (variablesSet)
            loadVehicleDetails();
    }

    private View.OnClickListener saveDiscardListener = new View.OnClickListener() {
        private final String TAG = getClass().getName();

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick");
            switch (v.getId()) {
                case R.id.save_changes:
                    Log.i(TAG, "Name Length: " + nameDisplay.getText().length());
                    storeVehicle();
                    break;
                case R.id.discard_changes:
                    Log.i(TAG, "Discard Changes");
                    loadVehicleDetails();
                    break;
            }
        }
    };

    private View.OnClickListener popupClickListener = new View.OnClickListener() {
        private final String TAG = getClass().getName();

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick");
            switch (v.getId()) {
                case R.id.name_card:
                    Log.i(TAG, "Name Card");
                    showNamePopup();
                    break;
                case R.id.vin_card:
                    Log.i(TAG, "VIN Card");
                    showVINPopup();
                    break;
                case R.id.year_card:
                    Log.i(TAG, "Year Card");
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
                case R.id.submodel_card:
                    Log.i(TAG, "Submodel Card");
                    showSubmodelPopup();
                    break;
                case R.id.mileage_card:
                    Log.i(TAG, "Mileage Card");
                    showMileagePopup();
                    break;
                case R.id.purchase_cost_card:
                    Log.i(TAG, "Purchase Cost Card");
                    showPurchaseCostPopup();
                    break;
                case R.id.purchase_date_card:
                    Log.i(TAG, "Purchase Date Card");
                    showPurchaseDatePopup();
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
        setModelPopup(false);

        Calendar instance = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                setPurchaseDateDisplay(calendar.getTimeInMillis());
            }
        }, instance.get(Calendar.YEAR), instance.get(Calendar.MONTH), instance.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
    }

    private LinkedList<String> years = new LinkedList<>();

    private void setYearPopup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.year);

        if (!years.contains(getString(R.string.other_selection)))
            years.add(getString(R.string.other_selection));

        String[] yearArray = years.toArray(new String[years.size()]);

        builder.setSingleChoiceItems(yearArray, years.indexOf(yearDisplay.getText().toString()), yearDialogListener);

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

        builder.setSingleChoiceItems(array, makes.indexOf(makeDisplay.getText().toString()), makeDialogListener);

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

        builder.setSingleChoiceItems(array, models.indexOf(modelDisplay.getText().toString()), modelDialogListener);

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                modelPopup = builder.create();
            }
        });
    }

    private AlertDialog.OnClickListener yearDialogListener = new AlertDialog.OnClickListener() {
        private final String TAG = "yearDialogListener";

        @Override
        public void onClick(DialogInterface dialog, int which) {
            String selection = years.get(which);
            if (selection.equals(getString(R.string.other_selection))) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                final EditText editText = new EditText(activity);

                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setFilters(new InputFilter[]{new InputFilter() {
                    private final String TAG = getClass().getName();

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!Character.isDigit(source.charAt(i))) {
                                Log.i(TAG, "Char removed");
                                return "";
                            }
                        }
                        return null;
                    }
                }});

                builder.setTitle(getString(R.string.year));
                builder.setView(editText);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Accept", null);


                final AlertDialog otherDialog = builder.create();

                otherDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        otherDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String entry = editText.getText().toString();
                                try {
                                    int value = Integer.valueOf(entry);

                                    if (entry.length() != 4)
                                        Toast.makeText(activity, "Please enter full year\nExample: 2015", Toast.LENGTH_LONG).show();
                                    else if (value < 1900 || value > Calendar.getInstance().get(Calendar.YEAR) + 1)
                                        Toast.makeText(activity, "Please enter valid year\n(1900-Current Year + 1)", Toast.LENGTH_LONG).show();
                                    else {
                                        setMakeDisplay(entry);
                                        setMakePopup(false);
                                        otherDialog.cancel();
                                    }

                                } catch (NumberFormatException e) {
                                    Toast.makeText(activity, "Please enter valid year\nExample: 2015", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                otherDialog.show();
            } else
                setYearDisplay(years.get(which));

            dialog.dismiss();
        }
    };

    private AlertDialog.OnClickListener makeDialogListener = new AlertDialog.OnClickListener() {
        private final String TAG = "makeDialogListener";

        @Override
        public void onClick(final DialogInterface dialog, int which) {
            String selection = makes.get(which);
            if (selection.equals(getString(R.string.other_selection))) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                final EditText editText = new EditText(activity);

                editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setFilters(new InputFilter[]{new InputFilter() {
                    private final String TAG = getClass().getName();

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!(Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i)))) {
                                Log.i(TAG, "Char removed");
                                return "";
                            }
                        }
                        return null;
                    }
                }});

                builder.setTitle(getString(R.string.make));
                builder.setView(editText);

                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Accept", null);

                final AlertDialog otherDialog = builder.create();

                otherDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        otherDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setMakeDisplay(editText.getText().toString());
                                setMakePopup(false);
                                otherDialog.cancel();
                            }
                        });
                    }
                });

                otherDialog.show();

            } else
                setMakeDisplay(selection);

            dialog.dismiss();
        }
    };

    private AlertDialog.OnClickListener modelDialogListener = new AlertDialog.OnClickListener() {
        private final String TAG = "modelDialogListener";

        @Override
        public void onClick(final DialogInterface dialog, int which) {
            String selection = models.get(which);
            if (selection.equals(getString(R.string.other_selection))) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                final EditText editText = new EditText(activity);

                editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editText.setFilters(new InputFilter[]{new InputFilter() {
                    private final String TAG = getClass().getName();

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!(Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i)))) {
                                Log.i(TAG, "Char removed");
                                return "";
                            }
                        }
                        return null;
                    }
                }});

                builder.setTitle(getString(R.string.model));
                builder.setView(editText);
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("Accept", null);

                final AlertDialog otherDialog = builder.create();

                otherDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface d) {
                        otherDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                try {
//                                    if (carSQL.checkString(editText.getText().toString(), 2)) {
                                setModelDisplay(editText.getText().toString());
                                setModelPopup(false);
                                otherDialog.cancel();
//                                    }
//                                } catch (SQLDataException e) {
//                                    Log.i(TAG, e.getMessage());
//                                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
//                                }
                            }
                        });
                    }
                });

                otherDialog.show();

            } else
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

    private void showNamePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.name));

        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editText.setFilters(new InputFilter[]{new InputFilter() {
            private final String TAG = getClass().getName();

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!(Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i)))) {
                        Log.i(TAG, "Char removed");
                        return "";
                    }
                }
                return null;
            }
        }});

        builder.setView(editText);

        builder.setPositiveButton("Accept", null);
        builder.setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tempName = editText.getText().toString();
                        if ((vehicle != null && vehicle.getName().equals(tempName)) || carSQL.canUseCarName(tempName)) {
                            setNameDisplay(tempName);
                            dialog.cancel();
                        } else {
                            Toast.makeText(activity, "Name already in use", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    private void setNameDisplay(String name) {
        nameDisplay.setText(name);
    }

    private void showVINPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.vin));

        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        editText.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        editText.setFilters(new InputFilter[]{new InputFilter() {
            private final String TAG = getClass().getName();

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!(Character.isUpperCase(source.charAt(i)) || Character.isDigit(source.charAt(i)))) {
                        Log.i(TAG, "Char removed");
                        return "";
                    }
                }
                return null;
            }
        }});

        builder.setView(editText);

        builder.setPositiveButton("Accept", null);
        builder.setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface d) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tempName = editText.getText().toString();
                        if (tempName.length() == 17) {
                            setVinDisplay(tempName);
                            dialog.cancel();
                        } else {
                            Toast.makeText(activity, "Please enter valid VIN\n17 characters long", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    private void setVinDisplay(String vin) {
        vinDisplay.setText(vin);
    }

    private void showSubmodelPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.submodel));

        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        editText.setFilters(new InputFilter[]{new InputFilter() {
            private final String TAG = getClass().getName();

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!(Character.isLetterOrDigit(source.charAt(i)) || Character.isSpaceChar(source.charAt(i)))) {
                        Log.i(TAG, "Char removed");
                        return "";
                    }
                }
                return null;
            }
        }});

        builder.setView(editText);

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submodelDisplay.setText(editText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    private void setSubmodelDisplay(String submodel) {
        submodelDisplay.setText(submodel);
    }

    private void showMileagePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.mileage));

        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setView(editText);

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setMileageDisplay(editText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    private void setMileageDisplay(String mileage) {
        NumberFormat miles = NumberFormat.getIntegerInstance();
        mileageDisplay.setText(miles.format(Long.valueOf(mileage)));
    }

    private void storeMileage(ContentValues values) {
        try {
            String current = mileageDisplay.getText().toString();
            if (!current.equals(getString(R.string.click_to_set))) {
                StringBuffer buffer = new StringBuffer();
                Log.i(TAG, "Initial: " + current);
                DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
                for (char c : current.toCharArray()) {
                    if (Character.isDigit(c))
                        buffer.append(c);
                }
                int tempInt = Integer.valueOf(buffer.toString());
                Log.i(TAG, "Output: " + tempInt);
                if (vehicle.getMileage() != tempInt)
                    values.put(Vehicle.MILEAGE, tempInt);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Non valid number");
        }
    }

    private void showPurchaseCostPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.purchase_cost));

        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        builder.setView(editText);

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPurchaseCostDisplay(editText.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", null);

        final AlertDialog dialog = builder.create();

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        dialog.show();
    }

    private void setPurchaseCostDisplay(String purchaseCost) {
        NumberFormat money = NumberFormat.getCurrencyInstance();
        purchaseCostDisplay.setText(money.format(Double.valueOf(purchaseCost)));
    }

    private void storePurchaseCost(ContentValues values) {
        try {
            String current = purchaseCostDisplay.getText().toString();
            if (!current.equals(getString(R.string.click_to_set))) {
                StringBuffer buffer = new StringBuffer();
                Log.i(TAG, "Initial: " + current);
                DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
                for (char c : current.toCharArray()) {
                    if (Character.isDigit(c) || c == decimalFormatSymbols.getDecimalSeparator())
                        buffer.append(c);
                }
                float tempFloat = Float.valueOf(buffer.toString());
                if (vehicle.getPurchaseCost() != tempFloat)
                    values.put(Vehicle.PURCHASE_COST, tempFloat);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Non valid number");
        }
    }

    private DatePickerDialog datePickerDialog;

    private void showPurchaseDatePopup() {
        datePickerDialog.show();
    }

    long dateTime;

    private void setPurchaseDateDisplay(long time) {
        Log.i(TAG, "Time: " + time);
        dateTime = time;
        purchaseDateDisplay.setText(DateFormat.getDateInstance().format(new Date(dateTime)));
    }
}

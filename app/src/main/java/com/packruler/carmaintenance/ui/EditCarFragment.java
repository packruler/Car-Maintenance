package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.json.VINDecoder;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
    private ImageView vehicleImage;
    private View loadingImageSpinner;
    private boolean variablesSet = false;

    private void setVariables(View view) {
        view.findViewById(R.id.name_card).setOnClickListener(popupClickListener);
        nameDisplay = (TextView) view.findViewById(R.id.name_display);


        view.findViewById(R.id.vin_card).setOnClickListener(popupClickListener);
        vinDisplay = (TextView) view.findViewById(R.id.vin_display);
        view.findViewById(R.id.decode_button).setOnClickListener(new View.OnClickListener() {
            private final String TAG = "decode_button";

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick");
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

        vehicleImage = (ImageView) view.findViewById(R.id.vehicle_image);
        vehicleImage.setOnClickListener(new View.OnClickListener() {
            private final String TAG = "ImageCardClick";

            @Override
            public void onClick(View v) {
                Log.d(TAG, "Open Selection");
                openImageSelection();
            }
        });

        loadingImageSpinner = view.findViewById(R.id.loading_image_display);

        setupAlertDialogs();

        if (vehicle != null) {
            loadVehicleDetails();
        } else
            loadingImageSpinner.setVisibility(View.GONE);

        variablesSet = true;
    }

    private void storeVehicle() {
        if (!nameDisplay.getText().equals(getString(R.string.click_to_set))) {
            String tempString = nameDisplay.getText().toString();
            String clickToSet = getString(R.string.click_to_set);
            int tempInt;
            float tempFloat;
            long tempLong;

            ContentValues values = new ContentValues();

            if (vehicle == null)
                vehicle = new Vehicle(carSQL, tempString);

            if (!vehicle.getName().equals(tempString))
                vehicle.setName(tempString);

            tempString = vinDisplay.getText().toString();
            if (!tempString.equals(clickToSet) &&
                    !vehicle.getVin().equals(tempString))
                values.put(Vehicle.VIN, tempString);

            if (!yearDisplay.getText().toString().equals(getString(R.string.click_to_set)))
                try {
                    Log.d(TAG, "Year: " + yearDisplay.getText().toString());
                    tempInt = Integer.valueOf(yearDisplay.getText().toString());
                    if (vehicle.getYear() != tempInt)
                        values.put(Vehicle.YEAR, tempInt);
                } catch (NumberFormatException e) {
                    Log.e(TAG, e.getMessage());
                }

            tempString = makeDisplay.getText().toString();
            if (!tempString.equals(clickToSet) &&
                    (vehicle.getMake() == null || !vehicle.getMake().equals(tempString)))
                values.put(Vehicle.MAKE, tempString);

            tempString = modelDisplay.getText().toString();
            if (!tempString.equals(clickToSet) &&
                    (vehicle.getModel() == null || !vehicle.getModel().equals(tempString)))
                values.put(Vehicle.MODEL, tempString);

            tempString = submodelDisplay.getText().toString();
            if (!tempString.equals(clickToSet) &&
                    (vehicle.getSubmodel() == null || !vehicle.getSubmodel().equals(tempString)))
                values.put(Vehicle.SUBMODEL, tempString);

            storeMileage(values);

            storePurchaseCost(values);

            if (!purchaseDateDisplay.getText().toString().equals(clickToSet)) {
                if (vehicle.getPurchaseDate() != dateTime)
                    values.put(Vehicle.PURCHASE_DATE, dateTime);
            }

            storeImage();

            if (values.size() > 0)
                vehicle.setContentValues(values);
        }
    }

    private void loadVehicleDetails() {
        Log.v(TAG, "loadVehicleDetails");
        String tempString = vehicle.getName();
        String clickToSetString = getString(R.string.click_to_set);

        loadImage();

        setNameDisplay(tempString);

        setYearDisplay(vehicle.getYear() + "");

        tempString = vehicle.getMake();
        if (tempString != null)
            setMakeDisplay(tempString);
        else setNameDisplay(clickToSetString);

        tempString = vehicle.getModel();
        if (tempString != null)
            setModelDisplay(tempString);
        else setNameDisplay(clickToSetString);

        tempString = vehicle.getSubmodel();
        if (tempString != null)
            setSubmodelDisplay(tempString);
        else setNameDisplay(clickToSetString);

        setMileageDisplay(vehicle.getMileage() + "");

        setPurchaseCostDisplay(vehicle.getPurchaseCost() + "");

        setPurchaseDateDisplay(vehicle.getPurchaseDate());
    }

    public void setVehicle(Vehicle vehicle) {
        Log.v(TAG, "setVehicle: " + vehicle.getName());
        this.vehicle = vehicle;
        if (variablesSet)
            loadVehicleDetails();
    }

    private View.OnClickListener saveDiscardListener = new View.OnClickListener() {
        private final String TAG = getClass().getName();

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick");
            switch (v.getId()) {
                case R.id.save_changes:
                    Log.v(TAG, "Name Length: " + nameDisplay.getText().length());
                    storeVehicle();
                    break;
                case R.id.discard_changes:
                    Log.v(TAG, "Discard Changes");
                    loadVehicleDetails();
                    break;
            }
        }
    };

    private View.OnClickListener popupClickListener = new View.OnClickListener() {
        private final String TAG = getClass().getName();

        @Override
        public void onClick(View v) {
            Log.v(TAG, "onClick");
            switch (v.getId()) {
                case R.id.name_card:
                    Log.v(TAG, "Name Card");
                    showNamePopup();
                    break;
                case R.id.vin_card:
                    Log.v(TAG, "VIN Card");
                    showVINPopup();
                    break;
                case R.id.year_card:
                    Log.v(TAG, "Year Card");
                    yearPopup.show();
                    break;
                case R.id.make_card:
                    Log.v(TAG, "Make Card");
                    makePopup.show();
                    break;
                case R.id.model_card:
                    Log.v(TAG, "Model Card");
                    modelPopup.show();
                    break;
                case R.id.submodel_card:
                    Log.v(TAG, "Submodel Card");
                    showSubmodelPopup();
                    break;
                case R.id.mileage_card:
                    Log.v(TAG, "Mileage Card");
                    showMileagePopup();
                    break;
                case R.id.purchase_cost_card:
                    Log.v(TAG, "Purchase Cost Card");
                    showPurchaseCostPopup();
                    break;
                case R.id.purchase_date_card:
                    Log.v(TAG, "Purchase Date Card");
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
                                Log.v(TAG, "Char removed");
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
                                Log.v(TAG, "Char removed");
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
                                Log.v(TAG, "Char removed");
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
                                setModelDisplay(editText.getText().toString());
                                setModelPopup(false);
                                otherDialog.cancel();
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
        Log.v(TAG, "Set year to " + year);
        if (!yearDisplay.getText().equals(year)) {
            yearDisplay.setText(year);

            setMakePopup(true);
            setModelPopup(true);
        }
    }

    private void setMakeDisplay(String make) {
        Log.v(TAG, "Set make to " + make);
        if (!makeDisplay.getText().equals(make)) {
            makeDisplay.setText(make);
            setModelDisplay(getString(R.string.click_to_set));
            setModelPopup(true);
        }
    }

    private void setModelDisplay(String model) {
        Log.v(TAG, "Set Model to " + model);
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
                        Log.v(TAG, "Char removed: " + source.charAt(i));
                        return "";
                    }
                }
                return null;
            }
        }});

        if (!nameDisplay.getText().toString().equals(getString(R.string.click_to_set)))
            editText.setText(nameDisplay.getText());

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
        Log.v(TAG, "Set name: " + name);
        nameDisplay.setText(name);
        Log.v(TAG, nameDisplay.getText().toString());
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
                        Log.v(TAG, "Char removed: " + source.charAt(i));
                        return "";
                    }
                    if (editText.getText().length() < 16)
                        editText.setTextColor(Color.RED);
                    else
                        editText.setTextColor(Color.BLACK);
                }
                return null;
            }
        }, new InputFilter.LengthFilter(17)});

        if (!vinDisplay.getText().toString().equals(getString(R.string.click_to_set)))
            editText.setText(vinDisplay.getText());

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
                        Log.v(TAG, "Char removed: " + source.charAt(i));
                        return "";
                    }
                }
                return null;
            }
        }});

        if (!submodelDisplay.getText().toString().equals(getString(R.string.click_to_set)))
            editText.setText(submodelDisplay.getText());

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

        if (!mileageDisplay.getText().toString().equals(getString(R.string.click_to_set)))
            editText.setText(mileageDisplay.getText());

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
                Log.v("storeMileage", "Initial: " + current);
                DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
                for (char c : current.toCharArray()) {
                    if (Character.isDigit(c))
                        buffer.append(c);
                }
                int tempInt = Integer.valueOf(buffer.toString());
                Log.v("storeMileage", "Output: " + tempInt);
                if (vehicle.getMileage() != tempInt)
                    values.put(Vehicle.MILEAGE, tempInt);
            }
        } catch (NumberFormatException e) {
            Log.e("storeMileage", "Non valid number");
        }
    }

    private void showPurchaseCostPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(getString(R.string.purchase_cost));

        final EditText editText = new EditText(activity);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        if (!purchaseCostDisplay.getText().toString().equals(getString(R.string.click_to_set)))
            editText.setText(purchaseCostDisplay.getText());

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
                Log.v("storePurchaseCost", "Initial: " + current);
                DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
                for (char c : current.toCharArray()) {
                    if (Character.isDigit(c) || c == decimalFormatSymbols.getDecimalSeparator())
                        buffer.append(c);
                }
                float tempFloat = Float.valueOf(buffer.toString());
                Log.v("storePurchaseCost", "Output: " + tempFloat);
                if (vehicle.getPurchaseCost() != tempFloat)
                    values.put(Vehicle.PURCHASE_COST, tempFloat);
            }
        } catch (NumberFormatException e) {
            Log.e("storePurchaseCost", "Non valid number");
        }
    }

    private DatePickerDialog datePickerDialog;

    private void showPurchaseDatePopup() {
        if (dateTime != 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(dateTime);
            datePickerDialog.getDatePicker().init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
        }
        datePickerDialog.show();
    }

    long dateTime;

    private void setPurchaseDateDisplay(long time) {
        Log.d(TAG, "Time: " + time);
        if (time == 0)
            purchaseDateDisplay.setText(getString(R.string.click_to_set));
        else {
            dateTime = time;
            purchaseDateDisplay.setText(DateFormat.getDateInstance().format(new Date(dateTime)));
        }
    }

    private static final int SELECT_PICTURE_REQUEST_CODE = 1;

    private void openImageSelection() {
        Log.v(TAG, "openImageSelection");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("return-data", true);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(photoPickerIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    private static final int PIC_CROP = 2;

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setData(uri);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PIC_CROP);
    }

    private void doCrop(final Uri uri) {
        Log.v(TAG, "doCrop Uri: " + uri);
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(activity, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.putExtra("outputX", 200);
//            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                activity.grantUriPermission(res.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                activity.grantUriPermission(res.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setData(uri);

                startActivityForResult(i, PIC_CROP);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = activity.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = activity.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    activity.grantUriPermission(res.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    activity.grantUriPermission(res.activityInfo.packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent i = new Intent(intent);
                    i.setData(uri);
                    co.appIntent = i;

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }


                CropOptionAdapter adapter = new CropOptionAdapter(activity.getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, PIC_CROP);
                    }
                });

                builder.setOnCancelListener(null);

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private File getTempFile() {
        File parent = new File(activity.getFilesDir() + "/cache/");
        File temp = new File(parent.getPath() + "/temp.jpg");
        try {
            if (!parent.exists())
                Log.v(TAG, "Make cache dir: " + parent.mkdirs());

            if (!temp.exists())
                Log.v(TAG, "Create temp file: " + temp.createNewFile());
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadImage(final Uri uri, boolean doCrop) {
        if (doCrop) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        File file = getTempFile();
                        if (file != null) {
                            FileOutputStream outputStream = new FileOutputStream(file);

                            MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri)
                                    .compress(Bitmap.CompressFormat.JPEG, 80, outputStream);

                            Uri out = FileProvider.getUriForFile(activity, "com.packruler.carmaintenance", file);
                            doCrop(out);
                        }
                        Log.d(TAG, "Image Loaded");
                    } catch (IOException e) {
                        e.printStackTrace();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } else
            loadImage(uri);
    }

    private void loadImage(final Uri uri) {
        loadingImageSpinner.setVisibility(View.VISIBLE);

        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                                    bitmap.getScaledWidth(DisplayMetrics.DENSITY_LOW), bitmap.getScaledHeight(DisplayMetrics.DENSITY_LOW), false));
                            loadingImageSpinner.setVisibility(View.GONE);
                        }
                    });
                    Log.d(TAG, "Image Loaded");
                } catch (IOException e) {
                    e.printStackTrace();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "Error loading image", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
    }

    private void loadImage() {
        final File file = new File(getActivity().getFilesDir().getPath() + "/Images/" + vehicle.getName() + "/" + "/vehicle.jpg");

        if (file.exists()) {
            loadingImageSpinner.setVisibility(View.VISIBLE);

            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                                    bitmap.getScaledWidth(DisplayMetrics.DENSITY_LOW), bitmap.getScaledHeight(DisplayMetrics.DENSITY_LOW), false));
                            loadingImageSpinner.setVisibility(View.GONE);
                        }
                    });
                    Log.d(TAG, "Image Loaded");
                }
            });
        } else
            loadingImageSpinner.setVisibility(View.GONE);
    }

    private Bitmap bitmap;

    private void storeImage() {
        final File temp = getTempFile();
        if (temp != null && temp.exists()) {
            poolExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    FileOutputStream out = null;
                    FileInputStream in = null;
                    try {
                        File outFile = new File(getActivity().getFilesDir().getPath() + "/Images/" + vehicle.getName() + "/" + "/vehicle.jpg");

                        if (!outFile.getParentFile().exists())
                            Log.v(TAG, "Make dirs success: " + outFile.getParentFile().mkdirs());

                        if (!outFile.exists())
                            Log.v(TAG, "Create new file success: " + outFile.createNewFile());

                        out = new FileOutputStream(outFile);
                        in = new FileInputStream(temp);
                        Log.v(TAG, "Begin move");
                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }

                        Log.v(TAG, "Delete temp: " + getTempFile().delete());
//                        vehicle.setImagePath(outFile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (out != null) {
                                out.flush();
                                out.close();
                            }
                            if (in != null) {
                                in.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Result code: " + (Activity.RESULT_OK == resultCode));
        Uri selected = data == null ? null : data.getData();
        Log.v(TAG, "Output: " + selected);
        Log.v(TAG, "requestCode " + requestCode);
        switch (requestCode) {
            case SELECT_PICTURE_REQUEST_CODE:
                Log.v(TAG, "Select image");
                if (selected != null)
                    loadImage(selected, true);
//                    doCrop(selected);
                break;
            case PIC_CROP:
                if (selected != null)
                    loadImage(selected);
                break;
        }
    }
}

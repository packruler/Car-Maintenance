package com.packruler.carmaintenance.ui;


import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.ColorSelector;
import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.CropOptionAdapter;
import com.packruler.carmaintenance.ui.adapters.PaletteAdapter;
import com.packruler.carmaintenance.ui.utilities.MaterialPopupHandler;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ResourceType")
public class EditCar extends Fragment {

    private final String TAG = getClass().getName();

    private CarSQL carSQL;
    private AvailableCarsSQL availableCarsSQL;
    private MainActivity activity;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private Vehicle vehicle;

    private MaterialEditText vehicleName;
    private MaterialEditText yearSpinner;
    private MaterialEditText make;
    private MaterialPopupHandler makePopup;
    private MaterialEditText model;
    private MaterialPopupHandler modelPopup;
    private MaterialEditText subModel;
    private MaterialEditText vin;
    private MaterialEditText mileage;
    private MaterialEditText mileageUnit;
    private MaterialEditText power;
    private MaterialEditText powerUnit;
    private MaterialEditText torque;
    private MaterialEditText torqueUnit;
    private ImageView vehicleImage;
    private RelativeLayout displayColorIcon;
    private RelativeLayout loadingImageSpinner;
    private MaterialEditText vehicleColor;
    private MaterialEditText weight;
    private MaterialEditText weightUnits;
    private MaterialEditText purchaseDate;
    private AlertDialog datePickerDialog;
    private MaterialEditText purchaseCost;
    private MaterialEditText purchaseCostUnit;

    public EditCar() {
        // Required empty public constructor
    }

    public EditCar(MainActivity activity, CarSQL carSQL) {
        this.carSQL = carSQL;
        this.activity = activity;
        availableCarsSQL = activity.getAvailableCarsSQL();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_car, container, false);
        rootView.isInEditMode();
        activity.getToolbar().setTitle("New Car");
        initializeView();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Log.v(TAG, "Delete Temp: " + getTempFile().delete());
        } catch (NullPointerException e) {
            Log.v(TAG, "Temp NullPointerException");
        }
    }

    boolean viewInitialized = false;

    private void initializeView() {
        activity.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                } catch (RuntimeException e) {
                    //Looper already prepared
                }
                initializeNameText();
                initializeYearSpinner();
                initializeMakeSpinner();
                initializeModelSpinner();
                initializeSubModel();
                initializeVIN();
                initializeMileage();
                initializeWeight();
                initializePower();
                initializeTorque();
                initializeVehicleImage();
                initializePurchaseCost();

                viewInitialized = true;
                if (vehicle == null || vehicle.getDisplayColor() == 0)
                    setHighlightColors(getResources().getColor(R.color.default_ui_color));

                if (vehicle != null)
                    loadVehicle();

                initializePurchaseDate();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        menu.add(getString(R.string.save));
        menu.add(getString(R.string.discard));
        activity.getToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            private final String TAG = "MenuItem";

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.v(TAG, item.getTitle().toString());
                if (item.getTitle().equals(getString(R.string.save)))
                    saveVehicle();
                else loadVehicle(null);
                return false;
            }
        });
        Log.v(TAG, "onCreateOptionsMenu");
        Log.v(TAG, "Menu size: " + menu.size());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getString(R.string.save)))
            saveVehicle();
        return super.onOptionsItemSelected(item);
    }

    private void loadVehicle() {
        if (viewInitialized) {
            String tempString = vehicle.getName();
            if (tempString != null) {
                activity.getToolbar().setTitle(tempString);
                vehicleName.setText(tempString);
            }

            long tempLong = vehicle.getYear();
            if (tempLong != 0)
                yearSpinner.setText(tempLong + "");

            tempString = vehicle.getMake();
            Log.v(TAG, "Make: " + tempString);
            if (tempString != null)
                make.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                model.setText(tempString);

            tempString = vehicle.getSubmodel();
            if (tempString != null)
                model.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                model.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                model.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                model.setText(tempString);

            tempLong = vehicle.getDisplayColor();
            if (tempLong != 0)
                setLoadedColor((int) tempLong);
        }
    }

    public void loadVehicle(Vehicle in) {
        vehicle = in;
        loadVehicle();
        loadPurchaseDate();
    }

    private int currentColor;

    private void setHighlightColors(int color) {
        if (currentColor != color) {
            currentColor = color;
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    activity.setUIColor(currentColor);
                    yearSpinner.setPrimaryColor(currentColor);
                    make.setPrimaryColor(currentColor);
                    model.setPrimaryColor(currentColor);

                    ((TextView) rootView.findViewById(R.id.general_info_title)).setTextColor(currentColor);
                    ((TextView) rootView.findViewById(R.id.performance_details_title)).setTextColor(currentColor);
                    ((TextView) rootView.findViewById(R.id.visual_details_title)).setTextColor(currentColor);
                    ((TextView) rootView.findViewById(R.id.purchase_details_title)).setTextColor(currentColor);

                    vehicleName.setPrimaryColor(currentColor);
                    vin.setPrimaryColor(currentColor);
                    subModel.setPrimaryColor(currentColor);
                    mileage.setPrimaryColor(currentColor);
                    mileageUnit.setPrimaryColor(currentColor);
                    power.setPrimaryColor(currentColor);
                    powerUnit.setPrimaryColor(currentColor);
                    torque.setPrimaryColor(currentColor);
                    torqueUnit.setPrimaryColor(currentColor);
                }
            });
        }
    }

    private void saveVehicle() {
        if (vehicle == null) {
            if (vehicleName.getText().length() > 0)
                vehicle = new Vehicle(carSQL, vehicleName.getText().toString());
        } else if (!vehicle.getName().equals(vehicleName.getText().toString()))
            vehicle.setName(vehicleName.getText().toString());

        if (vehicle != null) {
            ContentValues values = new ContentValues();
            saveYear(values);
            saveMake(values);
            saveModel(values);
            saveSubModel(values);
            saveVIN(values);
            StringBuilder alert = null;
            if (!saveMileage(values)) {
                Log.v(TAG, "No units selected");
                alert = new StringBuilder("Please select units for " + getString(R.string.mileage) + " value");
            }

            if (!saveWeight(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for " + getString(R.string.weight) + " value");
                else
                    alert.append("Please select units for " + getString(R.string.weight) + " value");
            }

            if (!savePower(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for " + getString(R.string.power) + " value");
                else
                    alert.append("Please select units for " + getString(R.string.power) + " value");
            }

            if (!saveTorque(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for " + getString(R.string.torque) + " value");
                else
                    alert.append("Please select units for " + getString(R.string.torque) + " value");
            }

            if (!savePurchaseCost(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for " + getString(R.string.purchase_cost) + " value");
                else
                    alert.append("Please select units for " + getString(R.string.purchase_cost) + " value");
            }

            if (alert != null) {
                sendToast(alert.toString());
                return;
            }

            Log.v(TAG, "Continue past unit check");
            saveVehicleColor(values);
            saveDisplayColor(values);
            savePurchaseDate(values);
            vehicle.putContentValues(values);
            storeImage();
        }
    }

    private void initializeNameText() {
        vehicleName = (MaterialEditText) rootView.findViewById(R.id.vehicle_name);
        vehicleName.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    private void initializeYearSpinner() {
        yearSpinner = (MaterialEditText) rootView.findViewById(R.id.year);
        yearSpinner.setPrimaryColor(currentColor);
        final MaterialPopupHandler popup = new MaterialPopupHandler((RelativeLayout) rootView.findViewWithTag(getString(R.string.container_layout))
                , yearSpinner);

        for (int x = Calendar.getInstance().get(Calendar.YEAR) + 1; x >= 1984; x--) {
            popup.add(x + "");
        }
        popup.add(getString(R.string.other_selection));

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.other_selection))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.year);

                    final MaterialEditText editText = new MaterialEditText(activity);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    editText.setPrimaryColor(currentColor);

                    builder.setView(editText);
                    builder.setPositiveButton(R.string.accept, null);
                    builder.setNegativeButton(R.string.discard, null);
                    final AlertDialog otherDialog = builder.create();

                    otherDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        private String TAG = "YearOther";

                        @Override
                        public void onShow(DialogInterface d) {
                            otherDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                private final String TAG = "PositiveClick";

                                @Override
                                public void onClick(View v) {

                                    String entry = editText.getText().toString();
                                    try {
                                        int value = Integer.valueOf(entry);

                                        if (entry.length() != 4)
                                            sendToast("Please enter full year\nExample: 2015");
                                        else if (value < 1900 || value > Calendar.getInstance().get(Calendar.YEAR) + 1)
                                            sendToast("Please enter valid year\n(1900-Current Year + 1)");
                                        else {
                                            yearSpinner.setText(editText.getText().toString());
                                            updateMakeSpinner();
                                            otherDialog.cancel();
                                        }

                                    } catch (NumberFormatException e) {
                                        sendToast("Please enter valid year\nExample: 2015");
                                    }
                                }
                            });
                        }
                    });

                    otherDialog.show();
                } else {
                    yearSpinner.setText(item.getTitle());
                    updateMakeSpinner();
                }
                return true;
            }
        });
    }

    private void saveYear(ContentValues values) {
        if (yearSpinner.getText().toString().length() > 0)
            values.put(Vehicle.YEAR, yearSpinner.getText().toString());
    }

    private List<String> makeList = new LinkedList<>();

    private void initializeMakeSpinner() {
        makeList.add(getString(R.string.other_selection));

        make = (MaterialEditText) rootView.findViewById(R.id.make);

        makePopup = new MaterialPopupHandler((RelativeLayout) rootView.findViewWithTag(getString(R.string.container_layout))
                , make);
        makePopup.setList(makeList);

        makePopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            private final String TAG = "MakeItemClick";

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.other_selection))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.make);

                    final MaterialEditText editText = new MaterialEditText(activity);
                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    editText.setPrimaryColor(currentColor);

                    builder.setView(editText);
                    builder.setPositiveButton(R.string.accept, null);
                    builder.setNegativeButton(R.string.discard, null);
                    final AlertDialog dialog = builder.create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        private String TAG = "MakeOther";

                        @Override
                        public void onShow(DialogInterface d) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                private final String TAG = "PositiveClick";

                                @Override
                                public void onClick(View v) {
                                    make.setText(editText.getText().toString());
                                    updateModelSpinner();
                                    dialog.cancel();
                                }
                            });
                        }
                    });

                    dialog.show();
                } else {
                    make.setText(item.getTitle());
                    updateModelSpinner();
                }
                return true;
            }
        });
    }

    private void updateMakeSpinner() {
        Log.v(TAG, "UpdateMakeSpinner");
        makeList.clear();
        Log.v(TAG, "Year: " + yearSpinner.getText().toString());
        makeList.addAll(availableCarsSQL.getAvailableMakes(yearSpinner.getText().toString()));
        makeList.add(getString(R.string.other_selection));

        makePopup.setList(makeList);

        if (!makeList.contains(make.getText().toString())) {
            make.setText("");
            updateModelSpinner();
        }
    }

    private void saveMake(ContentValues values) {
        if (make.getText().toString().length() > 0)
            values.put(Vehicle.MAKE, make.getText().toString());
    }

    private List<String> modelList = new LinkedList<>();

    private void initializeModelSpinner() {
        model = (MaterialEditText) rootView.findViewById(R.id.model);
        modelList.add(getString(R.string.other_selection));

        modelPopup = new MaterialPopupHandler((RelativeLayout) rootView.findViewWithTag(getString(R.string.container_layout))
                , model);
        modelPopup.setList(modelList);

        modelPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            private final String TAG = "ModelSpinnerItemClick";

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.v(TAG, "Selected: " + item.getTitle());
                if (item.getTitle().equals(getString(R.string.other_selection))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(R.string.model);

                    final MaterialEditText editText = new MaterialEditText(activity);
                    editText.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                    editText.setPrimaryColor(currentColor);

                    builder.setView(editText);
                    builder.setPositiveButton(R.string.accept, null);
                    builder.setNegativeButton(R.string.discard, null);
                    final AlertDialog dialog = builder.create();

                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        private String TAG = "MakeOther";

                        @Override
                        public void onShow(DialogInterface d) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                private final String TAG = "PositiveClick";

                                @Override
                                public void onClick(View v) {
                                    model.setText(editText.getText().toString());
                                    dialog.cancel();
                                }
                            });
                        }
                    });

                    dialog.show();
                } else
                    model.setText(item.getTitle());

                return true;
            }
        });
    }

    private void updateModelSpinner() {
        modelList.clear();

        modelList.addAll(availableCarsSQL.getAvailableModels(yearSpinner.getText().toString(), make.getText().toString()));
        modelList.add(getString(R.string.other_selection));

        modelPopup.setList(modelList);

        if (!modelList.contains(model.getText().toString()))
            model.setText("");
    }

    private void saveModel(ContentValues values) {
        if (model.getText().toString().length() > 0)
            values.put(Vehicle.MODEL, model.getText().toString());
    }

    private void initializeSubModel() {
        subModel = (MaterialEditText) rootView.findViewById(R.id.submodel);
    }

    private void saveSubModel(ContentValues values) {
        if (subModel.getText().toString().length() > 0)
            values.put(Vehicle.SUBMODEL, subModel.getText().toString());
    }

    private void initializeVIN() {
        vin = (MaterialEditText) rootView.findViewById(R.id.vin);
//        vin.setBottomTextSize(0);
        vin.addValidator(new METValidator("Length must be 17 characters") {
            @Override
            public boolean isValid(@NonNull CharSequence charSequence, boolean b) {
                return charSequence.length() == 0 || charSequence.length() == 17;
            }
        });
        vin.setAutoValidate(true);
        vin.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        vin.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        vin.setFilters(new InputFilter[]{new InputFilter() {
            private final String TAG = getClass().getName();

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!(Character.isUpperCase(source.charAt(i)) || Character.isDigit(source.charAt(i)))) {
                        return "";
                    }
                }
                return null;
            }
        }, new InputFilter.LengthFilter(17)});
    }

    private void saveVIN(ContentValues values) {
        if (vin.validate())
            values.put(Vehicle.VIN, vin.getText().toString());
    }

    private void initializeMileage() {
        mileage = (MaterialEditText) rootView.findViewById(R.id.mileage);
        mileageUnit = (MaterialEditText) rootView.findViewById(R.id.mileage_units);

        final MaterialPopupHandler popupHandler = new MaterialPopupHandler(
                (RelativeLayout) rootView.findViewById(R.id.performance_details_card).findViewWithTag(getString(R.string.container_layout))
                , mileageUnit);
        popupHandler.setList(R.array.large_distance_units);
        popupHandler.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.miles)))
                    mileageUnit.setText(getString(R.string.miles_short));
                else
                    mileageUnit.setText(getString(R.string.kilometers_short));
                return true;
            }
        });
//        mileageUnit.setDropDownWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private boolean saveMileage(ContentValues values) {
        if (mileage.getText().toString().length() > 0) {
            if (mileageUnit.getText().toString().length() == 0)
                return false;
            values.put(Vehicle.MILEAGE, Long.valueOf(mileage.getText().toString()));
            values.put(Vehicle.MILEAGE_UNITS, mileageUnit.getText().toString());
        }
        return true;
    }

    private void initializeWeight() {
        weight = (MaterialEditText) rootView.findViewById(R.id.weight);
        weightUnits = (MaterialEditText) rootView.findViewById(R.id.weight_units);

        final MaterialPopupHandler popupHandler = new MaterialPopupHandler(
                (RelativeLayout) rootView.findViewById(R.id.performance_details_card).findViewWithTag(getString(R.string.container_layout))
                , weightUnits);
        popupHandler.setList(R.array.weight_units);

        popupHandler.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.pounds)))
                    weightUnits.setText(getString(R.string.pounds_short));
                else
                    weightUnits.setText(getString(R.string.kilogram_short));
                return true;
            }
        });
    }

    private boolean saveWeight(ContentValues values) {
        if (weight.getText().toString().length() > 0) {
            if (weightUnits.getText().toString().length() == 0)
                return false;
            values.put(Vehicle.WEIGHT, Integer.valueOf(weightUnits.getText().toString()));
            values.put(Vehicle.WEIGHT_UNITS, weightUnits.getText().toString());
        }
        return true;
    }

    private void initializePower() {
        power = (MaterialEditText) rootView.findViewById(R.id.power);
        powerUnit = (MaterialEditText) rootView.findViewById(R.id.power_units);

        final MaterialPopupHandler popupHandler = new MaterialPopupHandler(
                (RelativeLayout) rootView.findViewById(R.id.performance_details_card).findViewWithTag(getString(R.string.container_layout))
                , powerUnit);
        popupHandler.setList(R.array.power_units);

        popupHandler.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.horsepower)))
                    powerUnit.setText(getString(R.string.horsepower_short));
                else
                    powerUnit.setText(getString(R.string.kilowatt_short));
                return true;
            }
        });
    }

    private boolean savePower(ContentValues values) {
        if (power.getText().toString().length() > 0) {
            if (powerUnit.getText().toString().length() == 0)
                return false;
            values.put(Vehicle.POWER, Integer.valueOf(power.getText().toString()));
            values.put(Vehicle.POWER_UNITS, powerUnit.getText().toString());
        }
        return true;
    }

    private void initializeTorque() {
        torque = (MaterialEditText) rootView.findViewById(R.id.torque);
        torqueUnit = (MaterialEditText) rootView.findViewById(R.id.torque_units);

        final MaterialPopupHandler popupHandler = new MaterialPopupHandler(
                (RelativeLayout) rootView.findViewById(R.id.performance_details_card).findViewWithTag(getString(R.string.container_layout))
                , torqueUnit);
        popupHandler.setList(R.array.torque_units);

        popupHandler.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                torqueUnit.setText(item.getTitle());
                return true;
            }
        });
    }

    private boolean saveTorque(ContentValues values) {
        if (torque.getText().toString().length() > 0) {
            if (torqueUnit.getText().toString().length() == 0)
                return false;
            values.put(Vehicle.TORQUE, Integer.valueOf(torque.getText().toString()));
            values.put(Vehicle.TORQUE_UNITS, powerUnit.getText().toString());
        }
        return true;
    }

    private void saveVehicleColor(ContentValues values) {
        if (vehicleColor.getText().toString().length() > 0)
            values.put(Vehicle.COLOR, vehicleColor.getText().toString());
    }

    private void saveDisplayColor(ContentValues values) {
        if (currentColor != getResources().getColor(R.color.default_ui_color)) {
            values.put(Vehicle.DISPLAY_COLOR, currentColor);
        }
    }

    private void initializeVehicleImage() {
        vehicleImage = (ImageView) rootView.findViewById(R.id.vehicle_image);
        vehicleColor = (MaterialEditText) rootView.findViewById(R.id.vehicle_color);
        vehicleImage.setOnClickListener(new View.OnClickListener() {
            private final String TAG = "VehicleImageClick";

            @Override
            public void onClick(View v) {
                openImageSelection();
            }
        });
        displayColorIcon = (RelativeLayout) rootView.findViewById(R.id.display_color_display);
        displayColorIcon.setOnClickListener(new View.OnClickListener() {
            private final String TAG = "Display Color Click";

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setItems(getResources().getStringArray(R.array.set_color_options), new DialogInterface.OnClickListener() {
                    private String TAG = "InitialDialog";
                    private AlertDialog paletteDialog;

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                if (swatches != null) {
                                    AlertDialog.Builder secondBuilder = new AlertDialog.Builder(activity);
                                    paletteDialog = secondBuilder.create();
                                    RecyclerView recyclerView = new RecyclerView(activity);
                                    PaletteAdapter adapter = new PaletteAdapter(swatches);
                                    adapter.setOnItemClickListener(new PaletteAdapter.OnItemClickListener() {
                                        private String TAG = "AdapterOnClick";

                                        @Override
                                        public void onItemClick(int color) {
                                            setLoadedColor(color);
                                            paletteDialog.dismiss();
                                        }
                                    });
                                    recyclerView.setAdapter(adapter);
                                    recyclerView.setLayoutManager(new GridLayoutManager(activity, 2));
                                    secondBuilder.setTitle("Select Color");
                                    secondBuilder.setView(recyclerView);
                                    secondBuilder.setNegativeButton(R.string.cancel, null);
                                    paletteDialog = secondBuilder.create();
                                    paletteDialog.show();

                                } else sendToast("No Image Loaded");
                                break;
                            case 1:
                                ColorSelector colorSelector = new ColorSelector(activity, currentColor, new ColorSelector.OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(int i) {
                                        setLoadedColor(i);
                                    }
                                });
                                colorSelector.show();
                        }
                    }
                });

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });
        loadingImageSpinner = (RelativeLayout) rootView.findViewById(R.id.loading_image_display);

        loadImage();
    }

    private static final int SELECT_PICTURE_REQUEST_CODE = 1;

    private void openImageSelection() {
        Log.v(TAG, "openImageSelection");
        Intent photoPickerIntent = new Intent();
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("return-data", true);
        startActivityForResult(photoPickerIntent, SELECT_PICTURE_REQUEST_CODE);
    }

    private static final int PIC_CROP = 2;

    private void doCrop(final Uri uri) {
        Log.v(TAG, "doCrop Uri: " + uri);
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            sendToast("Can not find image crop app");

            return;
        } else {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

//                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
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

    private void loadImage(final Uri uri, boolean crop) {
        if (crop) {
            try {
                File file = getTempFile();
                if (file != null) {
                    FileOutputStream outputStream = new FileOutputStream(file);

                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    bitmap.recycle();

                    Uri out = FileProvider.getUriForFile(activity, "com.packruler.carmaintenance", file);
                    doCrop(out);
                }
                Log.d(TAG, "Image Loaded");
            } catch (IOException e) {
                e.printStackTrace();
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        sendToast("Error loading image");
                    }
                });
            }
        } else
            loadImage(uri);
    }

    private void loadImage(final Uri uri) {
        loadingImageSpinner.setVisibility(View.VISIBLE);

        activity.getPoolExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                                    bitmap.getScaledWidth(DisplayMetrics.DENSITY_LOW), bitmap.getScaledHeight(DisplayMetrics.DENSITY_LOW), false));
                            loadingImageSpinner.setVisibility(View.GONE);
                        }
                    });

                    setLoadedColor(Palette.from(bitmap).maximumColorCount(30).generate());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            bitmap.recycle();
                        }
                    });
                    Log.d(TAG, "Image Loaded");
                } catch (IOException e) {
                    e.printStackTrace();
                    sendToast("Error loading image");
                }
            }
        });
    }

    private void loadImage() {
        Log.v(TAG, "Load cached image");
        if (vehicle != null) {

            final File file = new File(getActivity().getFilesDir().getPath() + "/Images/" + vehicle.getName() + "/" + "/vehicle.jpg");

            if (file.exists()) {
                loadingImageSpinner.setVisibility(View.VISIBLE);

                activity.getPoolExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    vehicleImage.setImageBitmap(bitmap);
                                    loadingImageSpinner.setVisibility(View.GONE);

                                }
                            });
                            Log.d(TAG, "Image Loaded");
                            loadSwatches(Palette.from(bitmap).maximumColorCount(30).generate());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else
                loadingImageSpinner.setVisibility(View.GONE);
        } else
            loadingImageSpinner.setVisibility(View.GONE);
    }

    List<Palette.Swatch> swatches;

    private void loadSwatches(Palette palette) {
        swatches = palette.getSwatches();
//        swatches = new LinkedList<>();
//        for (Palette.Swatch swatch : palette.getSwatches()) {
//            if (swatches.isEmpty())
//                swatches.add(swatch);
//            else {
//                boolean added = false;
//                for (int x = 0; x < swatches.size(); x++) {
//                    if (swatch.getPopulation() > swatches.get(x).getPopulation()) {
//                        swatches.add(x, swatch);
//                        added = true;
//                        break;
//                    }
//                }
//                if (!added)
//                    swatches.add(swatch);
//            }
//        }
    }

    private void setLoadedColor(Palette palette) {
        loadSwatches(palette);
        if (palette.getVibrantSwatch() != null)
            setLoadedColor(palette.getVibrantSwatch().getRgb());
        else if (palette.getDarkMutedSwatch() != null)
            setLoadedColor(palette.getDarkMutedSwatch().getRgb());
        else if (palette.getMutedSwatch() != null)
            setLoadedColor(palette.getMutedSwatch().getRgb());
        else
            setLoadedColor(swatches.get(0).getRgb());
    }

    private void setLoadedColor(final int color) {
        mainHandler.post(new Runnable() {
            private final String TAG = "setLoadedColor";

            @Override
            public void run() {
                setHighlightColors(color);
                displayColorIcon.setBackgroundColor(color);
            }
        });
    }

    private void storeImage() {
        final File temp = getTempFile();
        if (temp != null && temp.exists()) {
            activity.getPoolExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    FileOutputStream out = null;
                    FileInputStream in = null;
                    try {
                        File outFile = new File(getActivity().getFilesDir().getPath() + "/Images/" + vehicle.getName() + "/" + "/vehicle.jpg");

                        if (!outFile.getParentFile().exists())
                            Log.v(TAG, "Make dirs success: " + outFile.getParentFile().mkdirs());

                        boolean fileExisted = outFile.exists();
                        if (!fileExisted)
                            Log.v(TAG, "Create new file success: " + outFile.createNewFile());

                        out = new FileOutputStream(outFile);
                        Bitmap bitmap = BitmapFactory.decodeFile(getTempFile().getPath());
                        if (bitmap != null)
                            Bitmap.createScaledBitmap(bitmap, bitmap.getScaledWidth(DisplayMetrics.DENSITY_MEDIUM), bitmap.getScaledHeight(DisplayMetrics.DENSITY_MEDIUM), false)
                                    .compress(Bitmap.CompressFormat.JPEG, 90, out);
                        else if (!fileExisted) Log.v(TAG, "Delete outFile: " + outFile.delete());

//                        in = new FileInputStream(temp);
//                        Log.v(TAG, "Begin move");
//                        byte[] buffer = new byte[1024];
//                        int read;
//                        while ((read = in.read(buffer)) != -1) {
//                            out.write(buffer, 0, read);
//                        }

                        Log.v(TAG, "Delete temp: " + getTempFile().delete());
                        sendToast("Vehicle Data Stored Successfully");

//                        vehicle.setImagePath(outFile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendToast("Error storing image");
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

    long datePurchased;

    private void initializePurchaseCost() {
        purchaseCost = (MaterialEditText) rootView.findViewById(R.id.purchase_cost);
        purchaseCostUnit = (MaterialEditText) rootView.findViewById(R.id.purchase_cost_unit);

        final MaterialPopupHandler popupHandler = new MaterialPopupHandler(
                (RelativeLayout) rootView.findViewById(R.id.purchase_details_card).findViewWithTag(getString(R.string.container_layout))
                , purchaseCostUnit);
        popupHandler.setList(R.array.cost_units);

        popupHandler.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getString(R.string.dollar)))
                    purchaseCostUnit.setText(getString(R.string.dollar_short));
                else if (item.getTitle().equals(getString(R.string.euro)))
                    purchaseCostUnit.setText(getString(R.string.euro_short));
                else
                    purchaseCostUnit.setText(getString(R.string.english_pound_short));
                return true;
            }
        });
    }

    private boolean savePurchaseCost(ContentValues values) {
        if (purchaseCost.getText().toString().length() > 0) {
            if (purchaseCostUnit.getText().toString().length() == 0)
                return false;
            values.put(Vehicle.PURCHASE_COST, purchaseCost.getText().toString());
            values.put(Vehicle.PURCHASE_COST_UNITS, purchaseCostUnit.getText().toString());
        }
        return true;
    }

    boolean datePurchasedSet = false;

    private void initializePurchaseDate() {
        purchaseDate = (MaterialEditText) rootView.findViewById(R.id.purchase_date);

        rootView.findViewById(R.id.purchase_date_click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPurchaseDate();
            }
        });
    }

    private void loadPurchaseDate() {
        final Calendar calendar = Calendar.getInstance();
        if (vehicle != null && vehicle.getPurchaseDate() != 0) {
            calendar.setTime(new Date(vehicle.getPurchaseDate()));
            setDisplayPurchaseDate();
        } else {
            purchaseDate.setText("");
        }

        Log.v(TAG, calendar.get(Calendar.YEAR) + " " +
                calendar.get(Calendar.MONTH) + " " +
                calendar.get(Calendar.DAY_OF_MONTH));

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.purchase_date);

        DatePicker datePicker = new DatePicker(activity);
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datePurchasedSet = true;
                calendar.set(year, monthOfYear, dayOfMonth);
                datePurchased = calendar.getTimeInMillis();
                setDisplayPurchaseDate();
                datePickerDialog.dismiss();
            }
        });

        builder.setView(datePicker);

        datePickerDialog = builder.create();
        datePickerDialog.show();
    }

    private void setDisplayPurchaseDate() {
        purchaseDate.setText(DateFormat.getMediumDateFormat(activity).format(datePurchased));
    }

    private void savePurchaseDate(ContentValues values) {
        if (datePurchasedSet)
            values.put(Vehicle.PURCHASE_DATE, datePurchased);
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
                if (selected != null) {
                    loadImage(selected, true);
                }
                break;
            case PIC_CROP:
                if (selected != null)
                    loadImage(selected);
                break;
        }
    }

    private void sendToast(CharSequence message) {
        sendToast(message, Toast.LENGTH_LONG);
    }

    private void sendToast(final CharSequence message, final int length) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, message, length).show();
            }
        });
    }
}

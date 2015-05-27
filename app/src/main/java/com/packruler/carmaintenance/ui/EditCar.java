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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
    private MaterialBetterSpinner yearSpinner;
    private MaterialBetterSpinner makeSpinner;
    private MaterialBetterSpinner modelSpinner;
    private MaterialEditText subModel;
    private MaterialEditText vin;
    private MaterialEditText mileage;
    private MaterialBetterSpinner mileageUnit;
    private MaterialEditText power;
    private MaterialBetterSpinner powerUnit;
    private MaterialEditText torque;
    private MaterialBetterSpinner torqueUnit;
    private ImageView vehicleImage;
    private RelativeLayout displayColorIcon;
    private RelativeLayout loadingImageSpinner;
    private MaterialEditText vehicleColor;
    private MaterialEditText weight;
    private MaterialBetterSpinner weightUnits;

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

                viewInitialized = true;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (vehicle != null && vehicle.getDisplayColor() != 0) {
                            Log.v(TAG, "Palette: " + vehicle.getDisplayColor());
                            setHighlightColors(vehicle.getDisplayColor());
                        } else
                            setHighlightColors(getResources().getColor(R.color.material_deep_orange_500));
                        if (vehicle != null)
                            loadVehicle();
                    }
                });
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
                makeSpinner.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                modelSpinner.setText(tempString);

            tempString = vehicle.getSubmodel();
            if (tempString != null)
                modelSpinner.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                modelSpinner.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                modelSpinner.setText(tempString);

            tempString = vehicle.getModel();
            if (tempString != null)
                modelSpinner.setText(tempString);

            tempLong = vehicle.getDisplayColor();
            if (tempLong != 0)
                setLoadedColor((int) tempLong);
        }
    }

    public void loadVehicle(Vehicle in) {
        vehicle = in;
        loadVehicle();
    }

    private int currentColor;

    private void setHighlightColors(int color) {
        if (currentColor != color) {
            currentColor = color;
            activity.setUIColor(color);
            yearSpinner.setPrimaryColor(color);
            makeSpinner.setPrimaryColor(color);
            modelSpinner.setPrimaryColor(color);

            ((TextView) rootView.findViewById(R.id.general_info_title)).setTextColor(color);
            ((TextView) rootView.findViewById(R.id.details_title)).setTextColor(color);
            ((TextView) rootView.findViewById(R.id.visual_details_title)).setTextColor(color);

            vehicleName.setPrimaryColor(color);
            vin.setPrimaryColor(color);
            subModel.setPrimaryColor(color);
            mileage.setPrimaryColor(color);
            mileageUnit.setPrimaryColor(color);
            power.setPrimaryColor(color);
            powerUnit.setPrimaryColor(color);
            torque.setPrimaryColor(color);
            torqueUnit.setPrimaryColor(color);
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
                alert = new StringBuilder("Please select units for Mileage value");
            }

            if (!saveWeight(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for Weight value");
                else
                    alert.append("Please select units for Weight value");
            }

            if (!savePower(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for Power value");
                else
                    alert.append("Please select units for Power value");
            }

            if (!saveTorque(values)) {
                Log.v(TAG, "No units selected");

                if (alert == null)
                    alert = new StringBuilder("Please select units for Torque value");
                else
                    alert.append("Please select units for Torque value");
            }

            if (alert != null) {
                sendToast(alert.toString());
                return;
            }

            Log.v(TAG, "Continue past unit check");
            saveVehicleColor(values);
            saveDisplayColor(values);
            vehicle.putContentValues(values);
            storeImage();
        }
    }

    private void initializeNameText() {
        vehicleName = (MaterialEditText) rootView.findViewById(R.id.vehicle_name);
        vehicleName.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    private void saveName(ContentValues values) {
        if (vehicleName.getText().toString().length() > 0)
            values.put(Vehicle.YEAR, yearSpinner.getText().toString());
    }

    private void initializeYearSpinner() {
        yearSpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.year);
        final List<String> yearList = new LinkedList<>();

        yearList.add(getString(R.string.other_selection));
        for (int x = 1984; x <= Calendar.getInstance().get(Calendar.YEAR) + 1; x++) {
            yearList.add(0, x + "");
        }
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                yearSpinner.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, yearList));
                ((ArrayAdapter) yearSpinner.getAdapter()).setNotifyOnChange(true);
                yearSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    private final String TAG = "YearSpinnerItemClick";

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (yearSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setTitle(R.string.year);

                            final MaterialEditText editText = new MaterialEditText(activity);
                            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                            editText.setPrimaryColor(currentColor);

                            builder.setView(editText);
                            builder.setPositiveButton(R.string.accept, null);
                            builder.setNegativeButton(R.string.discard, null);
                            final AlertDialog dialog = builder.create();

                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                private String TAG = "YearOther";

                                @Override
                                public void onShow(DialogInterface d) {
                                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
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
                                                    dialog.cancel();
                                                }

                                            } catch (NumberFormatException e) {
                                                sendToast("Please enter valid year\nExample: 2015");
                                            }
                                        }
                                    });
                                }
                            });

                            dialog.show();
                        } else {
                            updateMakeSpinner();
                        }
                    }
                });
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

        makeSpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.make);
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                makeSpinner.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, makeList));
            }
        });

        makeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Item selected");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.v(TAG, "Nothing Selected");
            }
        });

        makeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "MakeSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + makeSpinner.getAdapter().getItem(position));
                if (makeSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
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
                                    makeSpinner.setText(editText.getText().toString());
                                    updateModelSpinner();
                                    dialog.cancel();
                                }
                            });
                        }
                    });

                    dialog.show();
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

        if (!makeList.contains(makeSpinner.getText().toString())) {
            makeSpinner.setText("");
            updateModelSpinner();
        }
    }

    private void saveMake(ContentValues values) {
        if (makeSpinner.getText().toString().length() > 0)
            values.put(Vehicle.MAKE, makeSpinner.getText().toString());
    }

    private List<String> modelList = new LinkedList<>();

    private void initializeModelSpinner() {
        modelSpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.model);
        modelList.add(getString(R.string.other_selection));

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                modelSpinner.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, modelList));
            }
        });

        modelSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "ModelSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + modelSpinner.getAdapter().getItem(position));
                if (modelSpinner.getAdapter().getItem(position).equals(getString(R.string.other_selection))) {
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
                                    modelSpinner.setText(editText.getText().toString());
                                    dialog.cancel();
                                }
                            });
                        }
                    });

                    dialog.show();
                }
            }
        });
    }

    private void updateModelSpinner() {
        modelList.clear();

        modelList.addAll(availableCarsSQL.getAvailableModels(yearSpinner.getText().toString(), makeSpinner.getText().toString()));
        modelList.add(getString(R.string.other_selection));

        ((ArrayAdapter) modelSpinner.getAdapter()).notifyDataSetChanged();

        if (!modelList.contains(modelSpinner.getText().toString()))
            modelSpinner.setText("");
    }

    private void saveModel(ContentValues values) {
        if (modelSpinner.getText().toString().length() > 0)
            values.put(Vehicle.MODEL, modelSpinner.getText().toString());
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
        mileageUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.mileage_units);
        mileageUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.large_distance_units)));
        mileageUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mileageUnit.setText("Mi");
                        break;
                    case 1:
                        mileageUnit.setText("KM");
                        break;
                }
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
        weightUnits = (MaterialBetterSpinner) rootView.findViewById(R.id.weight_units);

        weightUnits.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.weight_units)));

        weightUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        weightUnits.setText("LB");
                        break;
                    case 1:
                        weightUnits.setText("KG");
                        break;
                }
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
        powerUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.power_units);
        powerUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.power_units)));
//        powerUnit.setDropDownWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
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
        torqueUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.torque_units);
        torqueUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.torque_units)));
//        torqueUnit.setDropDownWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
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

    private Palette palette;

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
                                if (palette != null) {
                                    AlertDialog.Builder secondBuilder = new AlertDialog.Builder(activity);
                                    paletteDialog = secondBuilder.create();
                                    RecyclerView recyclerView = new RecyclerView(activity);
                                    PaletteAdapter adapter = new PaletteAdapter(activity, palette);
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
                                    secondBuilder.setNegativeButton(R.string.cancelbutton, null);
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

                builder.setNegativeButton(R.string.cancelbutton, null);
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
                            palette = Palette.from(bitmap).maximumColorCount(30).generate();
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

    private void setLoadedColor(Palette palette) {
        this.palette = palette;
        if (palette.getVibrantSwatch() != null)
            setLoadedColor(palette.getVibrantSwatch().getRgb());
        else if (palette.getMutedSwatch() != null)
            setLoadedColor(palette.getMutedSwatch().getRgb());
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
                        else
//                        if (!fileExisted) Log.v(TAG, "Delete outFile: " + outFile.delete());

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

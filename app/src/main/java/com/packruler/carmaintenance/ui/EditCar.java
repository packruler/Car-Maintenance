package com.packruler.carmaintenance.ui;


import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.widgets.ColorSelector;
import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.AvailableCarsSQL;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.PaletteAdapter;
import com.packruler.carmaintenance.ui.utilities.Swatch;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ResourceType")
public class EditCar extends Fragment /*implements Toolbar.OnMenuItemClickListener*/ {

    final static String TAG = "EditCar";

    private CarSQL carSQL;
    private AvailableCarsSQL availableCarsSQL;
    private MainActivity activity;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private Vehicle vehicle;

    private MaterialEditText vehicleName;
    private MaterialBetterSpinner year;
    private MaterialBetterSpinner make;
    private MaterialBetterSpinner model;
    private MaterialEditText subModel;
    private MaterialEditText vin;
    private MaterialEditText currentMileage;
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
    private MaterialEditText purchaseDate;
    private MaterialEditText purchaseCost;
    private MaterialBetterSpinner costUnit;
    private MaterialEditText purchaseMileage;
    private MaterialEditText purchaseLocation;

    public EditCar() {
    }

    public EditCar(MainActivity activity) {
        this();
        this.activity = activity;
        carSQL = activity.getCarsSQL();
        availableCarsSQL = activity.getAvailableCarsSQL();
        currentColor = activity.getResources().getColor(R.color.default_ui_color);
    }

    public EditCar(MainActivity activity, Vehicle vehicle) {
        this(activity);
        this.vehicle = vehicle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private ScrollView rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        rootView = (ScrollView) inflater.inflate(R.layout.fragment_edit_car, container, false);
        rootView.isInEditMode();
        initializeView();

        return rootView;
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        if (getTempFile(false).exists())
            Log.v(TAG, "Delete Temp: " + getTempFile(false).delete());

        carSQL.clearCache();

        availableCarsSQL.close();
    }

    boolean viewInitialized = false;

    private void initializeView() {
        activity.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();
                } catch (RuntimeException e) {
                    //Looper prepared already
                }
                initializeNameText();
                initializeYearSpinner();
                initializeMakeSpinner();
                initializeModelSpinner();
                initializeSubModel();
                initializeVIN();
                initializeCurrentMileage();
                initializeWeight();
                initializePower();
                initializeTorque();
                initializeVehicleImage();
                initializePurchaseCost();
                initializePurchaseDate();
                initializePurchaseMileage();

                viewInitialized = true;
                if (vehicle == null || vehicle.getDisplayColor() == 0)
                    setHighlightColors(getResources().getColor(R.color.default_ui_color));

                if (vehicle != null)
                    loadVehicle();
                else
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            activity.getToolbar().setTitle(getString(R.string.add_car));
                        }
                    });
            }
        });
    }

    private Drawable[] icons = new Drawable[3];

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        menu.add(getString(R.string.save));

        icons[0] = getResources().getDrawable(R.drawable.ic_save);
        if (icons[0] != null) {
            icons[0].setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);
            icons[0].setVisible(false, false);
        }
        menu.getItem(0).setIcon(icons[0]).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(getString(R.string.discard));
        icons[1] = getResources().getDrawable(R.drawable.ic_cancel);
        if (icons[1] != null) {
            icons[1].setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);
            icons[1].setVisible(false, false);
        }
        menu.getItem(1).setIcon(icons[1]).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        menu.add(getString(R.string.delete));
        icons[2] = getResources().getDrawable(R.drawable.ic_delete);
        if (icons[2] != null) {
            icons[2].setColorFilter(currentColor, PorterDuff.Mode.MULTIPLY);
            icons[2].setVisible(false, false);
        }
        menu.getItem(2).setIcon(icons[2]);

//        activity.getToolbar().setOnMenuItemClickListener(this);
        Log.v(TAG, "onCreateOptionsMenu");
        Log.v(TAG, "Menu size: " + menu.size());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        Log.v(TAG, "Item: " + item.getTitle());
        if (item.getTitle().equals(getString(R.string.save)))
            saveVehicle();
        else if (item.getTitle().equals(getString(R.string.discard)))
            Log.v(TAG, "Pop EditCar " + (getFragmentManager().popBackStackImmediate(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE) ? "Success" : "FAIL"));
        else if (item.getTitle().equals(getString(R.string.delete))) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle(getString(R.string.confirm));
            builder.setNegativeButton(getString(R.string.cancel), null);
            builder.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (vehicle != null) {
                        Log.v(TAG, "Deleting vehicle");
                        activity.getVehicleMap().remove(vehicle.getRow()).delete();
                        activity.getToolbar().setTitle(getString(R.string.select_vehicle));
                        activity.displaySelectVehicle();
                    } else
                        Log.v(TAG, "Pop EditCar " + (getFragmentManager().popBackStackImmediate(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE) ? "Success" : "FAIL"));
                }
            });
            builder.show();
        }
        return true;
    }

    private void loadVehicle() {
        Log.v(TAG, "LoadVehicle");
        if (viewInitialized) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (vehicle.getName() != null) {
                        vehicleName.setText(vehicle.getName());
                    }
                    activity.getToolbar().setTitle(getString(R.string.edit_car));

                    if (vehicle.getYear() != 0)
                        year.setText(vehicle.getYear() + "");

                    if (vehicle.getMake() != null)
                        make.setText(vehicle.getMake());

                    if (vehicle.getModel() != null) {
                        model.setText(vehicle.getModel());
                    }

                    updateMakeSpinner(false);

                    if (vehicle.getSubmodel() != null)
                        subModel.setText(vehicle.getSubmodel());

                    if (vehicle.getCurrentMileage() != 0) {
                        currentMileage.setText(vehicle.getCurrentMileage() + "");
                        currentMileage.setEnabled(false);

                        if (vehicle.getCurrentMileageUnits() != null)
                            mileageUnit.setText(vehicle.getCurrentMileageUnits());
                    }

                    if (vehicle.getWeight() != 0) {
                        weight.setText(vehicle.getWeight() + "");

                        if (vehicle.getWeightUnits() != null)
                            weightUnits.setText(vehicle.getWeightUnits());
                    }

                    if (vehicle.getPower() != 0) {
                        power.setText(vehicle.getPower() + "");

                        if (vehicle.getPowerUnits() != null)
                            powerUnit.setText(vehicle.getPowerUnits());
                    }

                    if (vehicle.getTorque() != 0) {
                        torque.setText(vehicle.getTorque() + "");

                        if (vehicle.getTorqueUnits() != null)
                            torqueUnit.setText(vehicle.getTorqueUnits());
                    }

                    if (vehicle.getPurchaseCost() != 0) {
                        purchaseCost.setText(new DecimalFormat("0.00").format(vehicle.getPurchaseCost()));

                        if (vehicle.getCostUnits() != null)
                            costUnit.setText(vehicle.getCostUnits());
                    }

                    if (vehicle.getPurchaseMileage() != 0)
                        purchaseMileage.setText(vehicle.getPurchaseMileage() + "");


                    if (vehicle.getColor() != null)
                        vehicleColor.setText(vehicle.getColor());

                    datePurchased = vehicle.getPurchaseDate();
                    if (datePurchased != 0)
                        setDisplayPurchaseDate();

                    if (vehicle.getDisplayColor() != 0)
                        setLoadedColor(vehicle.getDisplayColor());

                }
            });
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

            activity.setUIColor(currentColor);
            setCardTitleColors();

            activity.execute(new Runnable() {
                @Override
                public void run() {
                    year.setPrimaryColor(currentColor);
                    make.setPrimaryColor(currentColor);
                    model.setPrimaryColor(currentColor);

                    vehicleName.setPrimaryColor(currentColor);
                    vin.setPrimaryColor(currentColor);
                    subModel.setPrimaryColor(currentColor);
                    currentMileage.setPrimaryColor(currentColor);
                    mileageUnit.setPrimaryColor(currentColor);
                    weight.setPrimaryColor(currentColor);
                    weightUnits.setPrimaryColor(currentColor);
                    power.setPrimaryColor(currentColor);
                    powerUnit.setPrimaryColor(currentColor);
                    torque.setPrimaryColor(currentColor);
                    torqueUnit.setPrimaryColor(currentColor);
                    vehicleColor.setPrimaryColor(currentColor);
                    purchaseCost.setPrimaryColor(currentColor);
                    costUnit.setPrimaryColor(currentColor);
                    purchaseDate.setPrimaryColor(currentColor);
                }
            });
            for (Drawable icon : icons) {
                if (icon != null) {
                    color = new Swatch(currentColor).getBodyTextColor();
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//                        icon.setTint(color);
//                    else
                    icon.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
                    icon.setVisible(true, true);
                }
            }
        }
    }

    private void setCardTitleColors() {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                RelativeLayout layout = (RelativeLayout) rootView.getChildAt(0);
                for (int x = 0; x < layout.getChildCount(); x++) {
                    TextView title = (TextView) layout.getChildAt(x).findViewWithTag(getString(R.string.card_title_tag));
                    if (title != null)
                        title.setTextColor(currentColor);
                }
            }
        });
    }

    private void saveVehicle() {
        boolean newVehicle = vehicle == null;
        sendToast(getString(R.string.saving));
        if (newVehicle) {
            if (vehicleName.getText().length() > 0) {
                vehicle = new Vehicle(carSQL, vehicleName.getText().toString());
                activity.getVehicleMap().put(vehicle);
            }
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

            boolean distanceSet = saveCurrentMileage(values);
            Log.v(TAG, "DistanceSet: " + distanceSet);
            if (!distanceSet)
                distanceSet = savePurchaseMileage(values);
            else savePurchaseMileage(values);
            Log.v(TAG, "DistanceSet: " + distanceSet);

            if (distanceSet && !saveDistanceUnit(values))
                alert = new StringBuilder("Please select " + getString(R.string.distance_unit));

            if (!saveWeight(values))
                if (alert == null)
                    alert = new StringBuilder("Please select " + getString(R.string.weight_unit));
                else
                    alert.append("\nPlease select ").append(getString(R.string.weight_unit));


            if (!savePower(values))
                if (alert == null)
                    alert = new StringBuilder("Please select " + getString(R.string.power_unit));
                else
                    alert.append("\nPlease select ").append(getString(R.string.power_unit));


            if (!saveTorque(values))
                if (alert == null)
                    alert = new StringBuilder("Please select " + getString(R.string.torque_unit));
                else
                    alert.append("\nPlease select ").append(getString(R.string.torque_unit));


            if (!savePurchaseCost(values))
                if (alert == null)
                    alert = new StringBuilder("Please select units for " + getString(R.string.cost_unit));
                else
                    alert.append("\nPlease select units for ").append(getString(R.string.purchase_cost));


            if (alert != null) {
                sendToast(alert.toString());
                return;
            }

            Log.v(TAG, "Continue past unit check");
            saveVehicleColor(values);
            saveDisplayColor(values);
            savePurchaseDate(values);
            vehicle.putContentValues(values);
            if (!storeImage(newVehicle)) {
                sendToast(getString(R.string.save_success));
                if (newVehicle)
                    activity.changeVehicle(vehicle.getRow());
                else
                    getFragmentManager().popBackStackImmediate(TAG, android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    private void initializeNameText() {
        vehicleName = (MaterialEditText) rootView.findViewById(R.id.task_type);
        vehicleName.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
    }

    private void initializeYearSpinner() {
        year = (MaterialBetterSpinner) rootView.findViewById(R.id.year);
        year.setPrimaryColor(currentColor);
        final List<String> yearList = new LinkedList<>();
        for (int x = Calendar.getInstance().get(Calendar.YEAR) + 1; x >= 1984; x--) {
            yearList.add(x + "");
        }
        yearList.add(getString(R.string.other_selection));

        year.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, yearList));
        year.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (yearList.get(position).equals(getString(R.string.other_selection))) {
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
                                            year.setText(editText.getText().toString());
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
                    year.setText(yearList.get(position));
                    updateMakeSpinner();
                }
            }
        });

    }

    private void saveYear(ContentValues values) {
        if (year.getText().toString().length() > 0 || !year.getText().toString().equals(String.valueOf(vehicle.getYear())))
            values.put(Vehicle.YEAR, year.getText().toString());
    }

    private List<String> makeList = new LinkedList<>();

    private void initializeMakeSpinner() {
        makeList.add(getString(R.string.other_selection));

        make = (MaterialBetterSpinner) rootView.findViewById(R.id.make);

        make.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, makeList));
        make.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "MakeSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (makeList.get(position).equals(getString(R.string.other_selection))) {
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
                    make.setText(makeList.get(position));
                    updateModelSpinner();
                }
            }
        });
    }

    private void updateMakeSpinner() {
        updateMakeSpinner(true);
    }

    private void updateMakeSpinner(boolean update) {
        Log.v(TAG, "UpdateMakeSpinner");
        makeList.clear();
        Log.v(TAG, "Year: " + year.getText().toString());
        makeList.addAll(availableCarsSQL.getAvailableMakes(year.getText().toString()));
        makeList.add(getString(R.string.other_selection));

        ((ArrayAdapter) make.getAdapter()).notifyDataSetChanged();
        if (update && !makeList.contains(make.getText().toString())) {
            make.setText("");
        }
        updateModelSpinner(update);
    }

    private void saveMake(ContentValues values) {
        if (make.getText().toString().length() > 0 || !make.getText().toString().equals(vehicle.getMake()))
            values.put(Vehicle.MAKE, make.getText().toString());
    }

    private List<String> modelList = new LinkedList<>();

    private void initializeModelSpinner() {
        model = (MaterialBetterSpinner) rootView.findViewById(R.id.model);
        modelList.add(getString(R.string.other_selection));

        model.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, modelList));

        model.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private final String TAG = "ModelSpinnerItemClick";

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "Selected: " + modelList.get(position));
                if (modelList.get(position).equals(getString(R.string.other_selection))) {
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
                    model.setText(modelList.get(position));

            }
        });
    }

    private void updateModelSpinner() {
        updateModelSpinner(true);
    }

    private void updateModelSpinner(boolean update) {
        modelList.clear();

        modelList.addAll(availableCarsSQL.getAvailableModels(year.getText().toString(), make.getText().toString()));
        modelList.add(getString(R.string.other_selection));

        ((ArrayAdapter) model.getAdapter()).notifyDataSetChanged();

        if (update && !modelList.contains(model.getText().toString()))
            model.setText("");
    }

    private void saveModel(ContentValues values) {
        if (model.getText().toString().length() > 0 || !model.getText().toString().equals(vehicle.getModel()))
            values.put(Vehicle.MODEL, model.getText().toString());
    }

    private void initializeSubModel() {
        subModel = (MaterialEditText) rootView.findViewById(R.id.submodel);
    }

    private void saveSubModel(ContentValues values) {
        if (subModel.getText().toString().length() > 0 || !subModel.getText().toString().equals(vehicle.getSubmodel()))
            values.put(Vehicle.SUBMODEL, subModel.getText().toString());
    }

    private void initializeVIN() {
        vin = (MaterialEditText) rootView.findViewById(R.id.vin);
        vin.setMaxCharacters(17);
        vin.addValidator(new METValidator("") {
            @Override
            public boolean isValid(@NonNull CharSequence charSequence, boolean b) {
//                this.errorMessage = charSequence.length() + "/17";
                return charSequence.length() == 0 || charSequence.length() == 17;
            }
        });
        vin.setAutoValidate(true);
        vin.setInputType(InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE);
        vin.setInputType(InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        vin.setFilters(new InputFilter[]{new InputFilter() {
            private final String TAG = getClass().getSimpleName();

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

    private void initializeCurrentMileage() {
        currentMileage = (MaterialEditText) rootView.findViewById(R.id.current_mileage);

        mileageUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.distance_units);
        mileageUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getStringArray(R.array.large_distance_units)));
//        mileageUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getAdapter().getItem(position).equals(getString(R.string.miles)))
//                    mileageUnit.setText(getString(R.string.miles_short));
//                else
//                    mileageUnit.setText(getString(R.string.kilometers_short));
//            }
//        });
    }

    private boolean saveCurrentMileage(ContentValues values) {
        if (currentMileage.getText().toString().length() > 0 &&
                vehicle.getCurrentMileage() != Long.valueOf(currentMileage.getText().toString())) {
            values.put(Vehicle.CURRENT_MILEAGE, Long.valueOf(currentMileage.getText().toString()));
            return true;
        }
        return false;
    }

    private void initializeWeight() {
        weight = (MaterialEditText) rootView.findViewById(R.id.weight);
        weightUnits = (MaterialBetterSpinner) rootView.findViewById(R.id.weight_units);

        weightUnits.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.weight_units)));
//        weightUnits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getAdapter().getItem(position).equals(getString(R.string.pounds)))
//                    weightUnits.setText(getString(R.string.pounds_short));
//                else
//                    weightUnits.setText(getString(R.string.kilogram_short));
//            }
//        });
    }

    private boolean saveWeight(ContentValues values) {
        if (weight.getText().toString().length() > 0 &&
                vehicle.getWeight() != Long.valueOf(weight.getText().toString())) {
            if (weightUnits.getText().toString().length() == 0)
                return false;
            values.put(Vehicle.WEIGHT, Integer.valueOf(weight.getText().toString()));
            values.put(Vehicle.WEIGHT_UNITS, weightUnits.getText().toString());
        }
        return true;
    }

    private void initializePower() {
        power = (MaterialEditText) rootView.findViewById(R.id.power);
        powerUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.power_units);
        powerUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.power_units)));

//        powerUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getAdapter().getItem(position).equals(getString(R.string.horsepower)))
//                    powerUnit.setText(getString(R.string.horsepower_short));
//                else
//                    powerUnit.setText(getString(R.string.kilowatt_short));
//            }
//        });
    }

    private boolean savePower(ContentValues values) {
        if (power.getText().toString().length() > 0) {
            if (powerUnit.getText().toString().length() == 0)
                return false;
            if (!power.getText().toString().equals(String.valueOf(vehicle.getPower())))
                values.put(Vehicle.POWER, Integer.valueOf(power.getText().toString()));

            if (!powerUnit.getText().toString().equals(vehicle.getPowerUnits()))
                values.put(Vehicle.POWER_UNITS, powerUnit.getText().toString());
        }
        return true;
    }

    private void initializeTorque() {
        torque = (MaterialEditText) rootView.findViewById(R.id.torque);
        torqueUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.torque_units);

        torqueUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.torque_units)));
//        torqueUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                torqueUnit.setText((CharSequence) parent.getItemAtPosition(position));
//            }
//        });
    }

    private boolean saveTorque(ContentValues values) {
        if (torque.getText().toString().length() > 0) {
            if (torqueUnit.getText().toString().length() == 0)
                return false;

            if (!torque.getText().toString().equals(String.valueOf(vehicle.getTorque())))
                values.put(Vehicle.TORQUE, Integer.valueOf(torque.getText().toString()));

            if (!torqueUnit.getText().toString().equals(vehicle.getTorqueUnits()))
                values.put(Vehicle.TORQUE_UNITS, torqueUnit.getText().toString());
        }
        return true;
    }

    private void saveVehicleColor(ContentValues values) {
        if (vehicleColor.getText().toString().length() > 0 || !vehicleColor.getText().toString().equals(vehicle.getColor()))
            values.put(Vehicle.COLOR, vehicleColor.getText().toString());
    }

    private void saveDisplayColor(ContentValues values) {
        if (currentColor != getResources().getColor(R.color.default_ui_color)) {
            values.put(Vehicle.PRIMARY_COLOR, currentColor);
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
        loadingImageSpinner.setVisibility(View.INVISIBLE);

        loadImage();
    }

    private static final int SELECT_PICTURE_REQUEST_CODE = 1;

    private void openImageSelection() {
        Log.v(TAG, "openImageSelection");
        final Intent photoPickerIntent = new Intent();
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("crop", "true");
        photoPickerIntent.putExtra("return-data", true);
        activity.execute(new Runnable() {
            @Override
            public void run() {
                startActivityForResult(photoPickerIntent, SELECT_PICTURE_REQUEST_CODE);
            }
        });
    }

    private static final int PIC_CROP = 2;

    private void doCrop(final Uri uri) {
        Log.v(TAG, "doCrop Uri: " + uri);
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        intent.setClass(activity, this.getClass());

        List<ResolveInfo> list = activity.getPackageManager().queryIntentActivities(intent, 0);

//        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());

        try {
            PendingIntent.getActivity(activity, PIC_CROP, intent, PendingIntent.FLAG_CANCEL_CURRENT).send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
//
//        startActivityForResult(intent, PIC_CROP);
    }

    private File getTempFile(boolean make) {
        File temp = new File(activity.getCacheDir() + "/temp.jpg");
        if (make) {
            try {
                if (!activity.getCacheDir().exists())
                    Log.v(TAG, "Create cache folder: " + activity.getCacheDir());

                if (!temp.exists())
                    Log.v(TAG, "Create temp file: " + temp.createNewFile());

                return temp;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return temp;
    }

    private File getTempFile() {
        File temp = new File(activity.getCacheDir() + "/temp.jpg");
        try {
            if (!activity.getCacheDir().exists())
                Log.v(TAG, "Create cache folder: " + activity.getCacheDir());

            if (!temp.exists())
                Log.v(TAG, "Create temp file: " + temp.createNewFile());

            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Uri getTempUri() {
        return FileProvider.getUriForFile(activity, "com.packruler.carmaintenance", getTempFile());
    }

    private void loadImage(final Uri uri, boolean crop) {
        loadingImageSpinner.setVisibility(View.VISIBLE);
        carSQL.loadBitmap(uri, vehicleImage, loadingImageSpinner,
                new CarSQL.LoadedBitmapRunnable() {
                    private String TAG = getClass().getName();

                    @Override
                    public void run() {
                        activity.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (bitmap != null) {
                                    setLoadedColor(new Palette.Builder(bitmap).maximumColorCount(30).generate());
                                }
                            }
                        });
                    }
                });
//        try {
//            File file = getTempFile();
//            if (file != null) {
//                FileOutputStream outputStream = new FileOutputStream(file);
//
//                /*final Bitmap */bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
//                if (crop) {
//                    doCrop(getTempUri());
//                    recycle(bitmap);
//                } else {
//                    mainHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            loadingImageSpinner.setVisibility(View.VISIBLE);
//                        }
//                    });
//                    loadImage(uri);
//                }
//            }
//            Log.d(TAG, "Image Loaded");
//        } catch (IOException e) {
//            e.printStackTrace();
//            mainHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    sendToast("Error loading image");
//                }
//            });
//        }
    }

//    private void loadImage(final File image) {
//        loadingImageSpinner.setVisibility(View.VISIBLE);
//
//        activity.execute(new Runnable() {
//            @Override
//            public void run() {
//                /*final Bitmap */
//                bitmap = BitmapFactory.decodeFile(image.getPath());
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
//                                bitmap.getScaledWidth(DisplayMetrics.DENSITY_LOW), bitmap.getScaledHeight(DisplayMetrics.DENSITY_LOW), false));
//                        loadingImageSpinner.setVisibility(View.GONE);
//                    }
//                });
//                setLoadedColor(Palette.from(bitmap).maximumColorCount(30).generate());
//                mainHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        recycle(bitmap);
//                    }
//                });
//                Log.d(TAG, "Image Loaded");
//            }
//        });
//    }

    private void loadImage(final Bitmap bitmap) {
        activity.execute(new Runnable() {
            @Override
            public void run() {
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
                        recycle(bitmap);
                    }
                });
            }
        });
    }

//    private void loadImage(final Uri uri) {
//        loadingImageSpinner.setVisibility(View.VISIBLE);
//
//        activity.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    /*final Bitmap */
//                    bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
//                    mainHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
//                                    bitmap.getScaledWidth(DisplayMetrics.DENSITY_LOW), bitmap.getScaledHeight(DisplayMetrics.DENSITY_LOW), false));
//                            loadingImageSpinner.setVisibility(View.GONE);
//                        }
//                    });
//
//                    setLoadedColor(Palette.from(bitmap).maximumColorCount(30).generate());
//                    mainHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            recycle(bitmap);
//                        }
//                    });
//                    Log.d(TAG, "Image Loaded");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    sendToast("Error loading image");
//                }
//            }
//        });
//    }

    private void loadImage() {
        Log.v(TAG, "Load cached image");
        if (vehicle != null) {

            final File file = vehicle.getImage();

            if (file.exists())
                carSQL.loadBitmap(vehicle, vehicleImage, loadingImageSpinner,
                        new CarSQL.LoadedBitmapRunnable() {
                            private String TAG = getClass().getName();

                            @Override
                            public void run() {
                                activity.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (bitmap != null)
                                            loadSwatches(new Palette.Builder(bitmap).maximumColorCount(30).generate());
                                    }
                                });
                            }
                        });
//                Log.v(TAG, "Cached file exists");
//                loadingImageSpinner.setVisibility(View.VISIBLE);
//
//                activity.execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            final Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
//                            mainHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
//                                            bitmap.getScaledWidth(activity.getResources().getDisplayMetrics().densityDpi),
//                                            bitmap.getScaledHeight(activity.getResources().getDisplayMetrics().densityDpi),
//                                            false));
//                                    loadingImageSpinner.setVisibility(View.GONE);
//                                    recycle(bitmap);
//                                }
//                            });
//                            Log.d(TAG, "Image Loaded");
//                            loadSwatches(Palette.from(bitmap).maximumColorCount(30).generate());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });

        }
    }

    List<Palette.Swatch> swatches;

    private void loadSwatches(Palette palette) {
        swatches = palette.getSwatches();
    }

    private void setLoadedColor(Palette palette) {
        loadSwatches(palette);
        if (!loadedColorSet) {
            if (palette.getVibrantSwatch() != null)
                setLoadedColor(palette.getVibrantSwatch().getRgb());
            else if (palette.getDarkMutedSwatch() != null)
                setLoadedColor(palette.getDarkMutedSwatch().getRgb());
            else if (palette.getMutedSwatch() != null)
                setLoadedColor(palette.getMutedSwatch().getRgb());
            else
                setLoadedColor(swatches.get(0).getRgb());
        }
    }

    boolean loadedColorSet = false;

    private void setLoadedColor(final int color) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                setHighlightColors(color);
                displayColorIcon.setBackgroundColor(color);
                loadedColorSet = true;
            }
        });
    }

    private boolean storeImage(final boolean newVehicle) {
//        final File temp = getTempFile(false);
//        if (temp != null && temp.exists()) {

        final Bitmap bitmap = carSQL.getTempFromCache();
        Log.v(TAG, "Store image " + (bitmap != null));
        if (bitmap != null) {
            activity.execute(new Runnable() {
                @Override
                public void run() {
                    FileOutputStream out = null;
                    try {
                        File outFile = vehicle.getImage();

                        if (!outFile.getParentFile().exists())
                            Log.v(TAG, "Make dirs success: " + outFile.getParentFile().mkdirs());

                        boolean fileExisted = outFile.exists();
                        if (!fileExisted)
                            Log.v(TAG, "Create new file success: " + outFile.createNewFile());

                        out = new FileOutputStream(outFile);
//                        Bitmap bitmap = BitmapFactory.decodeFile(getTempFile().getPath());
//                        if (bitmap != null)
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, out);
//                        else if (!fileExisted)
//                            Log.v(TAG, "Delete outFile: " + outFile.delete());
                        carSQL.storeCacheToMemory(vehicle.getRow());
//
//                        Log.v(TAG, "Delete temp: " + getTempFile().delete());
                        sendToast(getString(R.string.save_success));
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (newVehicle)
                                    activity.changeVehicle(vehicle.getRow());
                                else
                                    Log.v(TAG, "Pop EditCar " + (getFragmentManager().popBackStackImmediate(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE) ? "Success" : "FAIL"));
                            }
                        });

//                        vehicle.setImagePath(outFile.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendToast(getString(R.string.error_saving_image));
                    } finally {
                        try {
                            if (out != null) {
                                out.flush();
                                out.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }

    private void initializePurchaseCost() {
        purchaseCost = (MaterialEditText) rootView.findViewById(R.id.purchase_cost);
        costUnit = (MaterialBetterSpinner) rootView.findViewById(R.id.cost_unit);

        costUnit.setAdapter(new ArrayAdapter<>(activity, R.layout.one_line_selector, getResources().getTextArray(R.array.cost_units)));
//        costUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).equals(getString(R.string.dollar)))
//                    costUnit.setText(getString(R.string.dollar_short));
//                else if (parent.getItemAtPosition(position).equals(getString(R.string.euro)))
//                    costUnit.setText(getString(R.string.euro_short));
//                else
//                    costUnit.setText(getString(R.string.english_pound_short));
//            }
//        });
    }

    private boolean savePurchaseCost(ContentValues values) {
        if (purchaseCost.getText().toString().length() > 0) {
            if (costUnit.getText().toString().length() == 0)
                return false;
            if (!purchaseCost.getText().toString().equals(String.valueOf(vehicle.getPurchaseCost())))
                values.put(Vehicle.PURCHASE_COST, Double.valueOf(purchaseCost.getText().toString()));
            if (!costUnit.getText().toString().equals(vehicle.getCostUnits()))
                values.put(Vehicle.COST_UNITS, costUnit.getText().toString());
        }
        return true;
    }

    private boolean datePurchasedSet = false;
    private long datePurchased;
    private DatePickerDialog datePickerDialog;

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
        datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            private String TAG = "OnDateSetListener";

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                datePurchasedSet = true;
                calendar.set(year, monthOfYear, dayOfMonth);
                datePurchased = calendar.getTimeInMillis();
                setDisplayPurchaseDate();
                datePickerDialog.dismiss();
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private void setDisplayPurchaseDate() {
        purchaseDate.setText(DateFormat.getMediumDateFormat(activity).format(datePurchased));
    }

    private void savePurchaseDate(ContentValues values) {
        if (datePurchasedSet)
            values.put(Vehicle.PURCHASE_DATE, datePurchased);
    }

    private void initializePurchaseMileage() {
        purchaseMileage = (MaterialEditText) rootView.findViewById(R.id.purchase_mileage);
    }

    private boolean savePurchaseMileage(ContentValues values) {
        if (purchaseMileage.getText().toString().length() == 0) {
            if (currentMileage.getText().toString().length() > 0) {
                values.put(Vehicle.PURCHASE_MILEAGE, Long.valueOf(currentMileage.getText().toString()));
                return true;
            }
        } else if (vehicle.getPurchaseMileage() != Long.valueOf(purchaseMileage.getText().toString())) {
            values.put(Vehicle.PURCHASE_MILEAGE, Long.valueOf(purchaseMileage.getText().toString()));
            return true;
        }
        return false;
    }

    private boolean saveDistanceUnit(ContentValues values) {
        if (mileageUnit.getText().toString().length() == 0)
            return false;

        values.put(Vehicle.DISTANCE_UNITS, mileageUnit.getText().toString());
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Result code: " + resultCode);
        Uri selected = data == null ? null : data.getData();
        Log.v(TAG, "Output: " + selected);
        Log.v(TAG, "requestCode " + requestCode);
        switch (requestCode) {
            case SELECT_PICTURE_REQUEST_CODE:
                Log.v(TAG, "Select image");
                if (selected != null) {
                    loadImage(selected, false);
//                    doCrop(selected);
                }
                break;
            case PIC_CROP:
                if (selected != null)
//                    loadImage(selected);
                    break;
        }

    }

    private Toast toast;

    private void sendToast(CharSequence message) {
        sendToast(message, Toast.LENGTH_LONG);
    }

    private void sendToast(final CharSequence message, final int length) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (toast != null)
                    toast.cancel();
                toast = Toast.makeText(activity, message, length);
                toast.show();
            }
        });
    }

    private void recycle(Bitmap bitmap) {
        bitmap.recycle();
        System.gc();
    }
}

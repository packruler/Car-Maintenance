package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.VehicleStatisticsHandler;
import com.packruler.carmaintenance.ui.utilities.Swatch;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;

import java.text.DecimalFormat;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Packruler on 6/10/15.
 */
public class VehicleMainFragment extends android.support.v4.app.Fragment {
    private final String TAG = getClass().getSimpleName();
    private MainActivity activity;
    private CardView editCarButton;
    private CardView servicesButton;
    private CardView fuelStopsButton;
    private ImageView vehicleImage;
    private TextView vehicleName;
    private GridLayout extraDetails;
    private VehicleStatisticsHandler statisticsHandler;
    private boolean viewInitialized = false;

    public VehicleMainFragment() {
    }

    public VehicleMainFragment(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        vehicleImage = (ImageView) rootView.findViewById(R.id.vehicle_image);

        servicesButton = (CardView) rootView.findViewById(R.id.services_button);
        servicesButton.setOnClickListener(new View.OnClickListener() {
            private final String TAG = "Services Button";

            @Override
            public void onClick(View v) {
                Log.v(TAG, "onClick");
                Log.v(TAG, "" + (activity.getCurrentVehicle() != null));
                ServicesFragment servicesFragment = new ServicesFragment(activity, activity.getCurrentVehicle());
                getFragmentManager().beginTransaction().replace(R.id.container, servicesFragment).addToBackStack("Services").commit();
            }
        });
        editCarButton = (CardView) rootView.findViewById(R.id.edit_car_button);
        editCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Edit Car Click");
                if (activity.getCurrentVehicle() != null)
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new EditCar(activity, activity.getCurrentVehicle()))
                            .addToBackStack(EditCar.TAG)
                            .commit();
            }
        });

        fuelStopsButton = (CardView) rootView.findViewById(R.id.fuel_stop_button);
        fuelStopsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG, "Fuel Stop Click");
                if (activity.getCurrentVehicle() != null)
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container, new FuelStopsFragment(activity, activity.getCurrentVehicle()))
                            .addToBackStack(FuelStop.class.getSimpleName())
                            .commit();
            }
        });
        vehicleName = (TextView) rootView.findViewById(R.id.vehicle_name);
        viewInitialized = true;
        extraDetails = (GridLayout) rootView.findViewById(R.id.extra_details);
        statisticsHandler = new VehicleStatisticsHandler((FrameLayout) rootView.findViewById(R.id.vehicle_expandable_statistics));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadVehicleDetails();
    }

    public void setUIColor(int color) {
        try {
            Log.v(TAG, "Color: " + color);
            if (color == 0)
                color = activity.getResources().getColor(R.color.default_ui_color);

            editCarButton.setCardBackgroundColor(color);
            int textColor = Swatch.getForegroundColor();
            ((TextView) editCarButton.findViewWithTag(getString(R.string.title_tag))).setTextColor(textColor);
            ((ImageView) editCarButton.findViewWithTag(getString(R.string.image_tag))).setColorFilter(textColor, PorterDuff.Mode.SRC_IN);

            servicesButton.setCardBackgroundColor(color);
            ((TextView) servicesButton.findViewWithTag(getString(R.string.title_tag))).setTextColor(textColor);
            ((ImageView) servicesButton.findViewWithTag(getString(R.string.image_tag))).setColorFilter(textColor, PorterDuff.Mode.SRC_IN);

            fuelStopsButton.setCardBackgroundColor(color);
            ((TextView) fuelStopsButton.findViewWithTag(getString(R.string.title_tag))).setTextColor(textColor);
            ((ImageView) fuelStopsButton.findViewWithTag(getString(R.string.image_tag))).setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void loadVehicleDetails() {
        if (viewInitialized) {
            final Vehicle vehicle = activity.getCurrentVehicle();
            setUIColor(activity.getUiColor());
            if (vehicle != null) {

                vehicleName.setText(vehicle.getName());
                if (vehicle.getImage().exists()) {
                    vehicleImage.setVisibility(View.VISIBLE);
                    activity.getCarsSQL().loadBitmap(vehicle, vehicleImage, null, new CarSQL.LoadedBitmapRunnable() {
                        @Override
                        public void run() {
                            vehicleImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }
                    });
                } else
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "Load null to UI");
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                                vehicleImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.missing_photo_icon, null));
                            else
                                vehicleImage.setImageDrawable(activity.getResources().getDrawable(R.drawable.missing_photo_icon));

                            vehicleImage.setScaleType(ImageView.ScaleType.CENTER);
                        }
                    });

                extraDetails.removeAllViews();

                float avgEff = vehicle.getFuelEfficiency();
                LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();
                queue.add(getString(R.string.avg_efficiency));
                queue.add("");
                queue.add(new DecimalFormat("0.##").format(avgEff));

                String units = vehicle.getFuelEfficiencyUnits();
                if (units != null)
                    queue.add(units);
                else
                    queue.add("");

                float costOfFuel = vehicle.getAverageCostOfFuel();
                queue.add(getString(R.string.avg_cost_of_fuel));
                queue.add(vehicle.getCurrencySymbol());
                queue.add(new DecimalFormat("0.000").format(costOfFuel));
                queue.add("");
                statisticsHandler.addValues(queue, activity);

                extraDetails.requestLayout();
                statisticsHandler.close(true);
            } else {
                vehicleImage.setVisibility(View.GONE);
            }
        }
    }
}

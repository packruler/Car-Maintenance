package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.Vehicle;

/**
 * Created by Packruler on 6/10/15.
 */
public class VehicleMainFragment extends Fragment {
    private final String TAG = getClass().getName();
    private MainActivity activity;
    private CardView editCarButton;
    private CardView servicesButton;
    private CardView fuelStopsButton;
    private ImageView vehicleImage;
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
                            .addToBackStack("EditCar").commit();
            }
        });

        fuelStopsButton = (CardView) rootView.findViewById(R.id.fuel_stop_button);
        viewInitialized = true;

        loadVehicleDetails();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public void setUIColor(int color) {
        if (color == 0)
            color = activity.getResources().getColor(R.color.default_ui_color);

        editCarButton.setCardBackgroundColor(color);
        Palette.Swatch swatch = new Palette.Swatch(color, 100);
        ((TextView) editCarButton.findViewWithTag(getString(R.string.title_tag))).setTextColor(swatch.getBodyTextColor());
        ((ImageView) editCarButton.findViewWithTag(getString(R.string.image_tag))).setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);

        servicesButton.setCardBackgroundColor(color);
        ((TextView) servicesButton.findViewWithTag(getString(R.string.title_tag))).setTextColor(swatch.getBodyTextColor());
        ((ImageView) servicesButton.findViewWithTag(getString(R.string.image_tag))).setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);

        fuelStopsButton.setCardBackgroundColor(color);
        ((TextView) fuelStopsButton.findViewWithTag(getString(R.string.title_tag))).setTextColor(swatch.getBodyTextColor());
        ((ImageView) fuelStopsButton.findViewWithTag(getString(R.string.image_tag))).setColorFilter(swatch.getBodyTextColor(), PorterDuff.Mode.SRC_IN);
    }

    public void loadVehicleDetails() {
        if (viewInitialized) {
            final Vehicle vehicle = activity.getCurrentVehicle();
            if (vehicle != null) {
                setUIColor(vehicle.getDisplayColor());

                if (vehicle.getImage().exists())
                    activity.execute(new Runnable() {
                        private final String TAG = "loadImage";

                        @Override
                        public void run() {
                            Log.v(TAG, "Load from file");
                            final Bitmap bitmap = BitmapFactory.decodeFile(vehicle.getImage().getPath());
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.v(TAG, "Load to UI");
                                    vehicleImage.setVisibility(View.VISIBLE);
                                    vehicleImage.setImageBitmap(Bitmap.createScaledBitmap(bitmap,
                                            bitmap.getScaledWidth(activity.getResources().getDisplayMetrics().densityDpi),
                                            bitmap.getScaledHeight(activity.getResources().getDisplayMetrics().densityDpi),
                                            false));
                                    Log.v(TAG, "Initial bitmap recycled");
//                                    bitmap.recycle();
                                }
                            });
                        }
                    });
                else
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "Load null to UI");
                            vehicleImage.setImageBitmap(null);
                            vehicleImage.setVisibility(View.GONE);
                        }
                    });
            } else {
                vehicleImage.setVisibility(View.GONE);
            }
        }
    }
}

package com.packruler.carmaintenance.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Packruler on 6/28/15.
 */
public class FuelStopAdapter extends CursorRecyclerViewAdapter<FuelStopAdapter.ViewHolder> {
    private static String TAG = "FuelStopAdapter";

    private final CarSQL carSQL;
    private ServiceRecyclerAdapter.OnClickListener onClickListener;
    private ViewHolder expandedView;
    private static int initialHideValue;

    public FuelStopAdapter(CarSQL carSQL, Cursor cursor) {
        super(cursor);
        this.carSQL = carSQL;
        Log.v(getClass().getSimpleName(), "Column names: ");
        for (String col : cursor.getColumnNames()) {
            Log.v(getClass().getSimpleName(), col);
        }
    }

    class ViewHolder extends ExpandableViewHolder {
        private TextView efficiencyDisplay;
        private TextView costDisplay;
        private TextView distanceDisplay;
        private TextView dateDisplay;
        private LinearLayout detailLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedView == ViewHolder.this) close(false);
                    else expand();

                    onItemClick(ViewHolder.this.getItemId(), ViewHolder.this);
                }
            });
            layout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewHolder.this.equals(expandedView))
                        onEditClick(ViewHolder.this.getItemId(), ViewHolder.this);
                    else {
                        if (expandedView == ViewHolder.this) close(false);
                        else expand();

                        onItemClick(ViewHolder.this.getItemId(), ViewHolder.this);
                    }
                }
            });

            layout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewHolder.this.equals(expandedView))
                        onDeleteClick(ViewHolder.this.getItemId(), ViewHolder.this);
                    else {
                        if (expandedView == ViewHolder.this) close(false);
                        else expand();

                        onItemClick(ViewHolder.this.getItemId(), ViewHolder.this);
                    }
                }
            });
            efficiencyDisplay = (TextView) itemView.findViewById(R.id.fuel_efficiency);
            costDisplay = (TextView) itemView.findViewById(R.id.cost_display);
            distanceDisplay = (TextView) itemView.findViewById(R.id.distance_display);
            dateDisplay = (TextView) itemView.findViewById(R.id.date_display);
            detailLayout = (LinearLayout) itemView.findViewById(R.id.extra_details);
        }

        @Override
        void setDisplay(Cursor cursor) {
            FuelStop fuelStop = new FuelStop(carSQL, cursor.getLong(cursor.getColumnIndex(FuelStop.ID)));
            int distance = fuelStop.getDistanceTraveled();
            float volume = fuelStop.getVolume();
            float costPer = fuelStop.getCostPerVolume();
            float cost = volume * costPer;

            Vehicle vehicle = new Vehicle(carSQL, cursor.getLong(cursor.getColumnIndex(FuelStop.VEHICLE_ROW)));

            String currency = vehicle.getCurrency();
            if (currency == null)
                currency = "";
//            else
//                currency = Currency.getInstance(currency).getSymbol();

            String volumeUnits = vehicle.getVolumeUnits();
            if (volumeUnits == null)
                volumeUnits = "";

            String distanceUnits = vehicle.getMileageUnits();
            if (distanceUnits == null)
                distanceUnits = "";

            String fuelEfficiencyUnits = vehicle.getFuelEfficiencyUnits();
            if (fuelEfficiencyUnits == null)
                fuelEfficiencyUnits = "";

            DecimalFormat numberFormat = new DecimalFormat("0.00");

            if (distance > 0 && volume > 0) {
                float efficiency = (float) distance / volume;
                efficiencyDisplay.setText(numberFormat.format(efficiency) + ' ' + fuelEfficiencyUnits);
            } else
                efficiencyDisplay.setText("--.-- " + fuelEfficiencyUnits);

            costDisplay.setText(currency + numberFormat.format(cost));

            dateDisplay.setText(DateFormat.getDateInstance().format(fuelStop.getDate()));
            distanceDisplay.setText(NumberFormat.getInstance().format(fuelStop.getMileage()) + " " + distanceUnits);

            Context context = detailLayout.getContext();
            detailLayout.removeAllViews();

            TextView timeDisplay = new TextView(context);
            timeDisplay.setTextAppearance(R.style.TextAppearance_AppCompat_Medium_Inverse);
            timeDisplay.setGravity(Gravity.END);
            timeDisplay.setText(DateFormat.getTimeInstance(DateFormat.SHORT).format(fuelStop.getDate()));
            detailLayout.addView(timeDisplay);

            TextView volumeDisplay = new TextView(context);
            volumeDisplay.setTextAppearance(R.style.TextAppearance_AppCompat_Medium_Inverse);
            volumeDisplay.setText(context.getString(R.string.volume) + ": " + new DecimalFormat("0.000").format(volume) + volumeUnits);
            detailLayout.addView(volumeDisplay);

            if (costPer > 0) {
                TextView costPerDisplay = new TextView(context);
                costPerDisplay.setTextAppearance(R.style.TextAppearance_AppCompat_Medium_Inverse);
                costPerDisplay.setText(currency + costPer + volumeUnits);
                detailLayout.addView(costPerDisplay);
            }

            if (fuelStop.getDistanceTraveled() > 0) {
                TextView distanceTraveled = new TextView(context);
                distanceTraveled.setTextAppearance(R.style.TextAppearance_AppCompat_Medium_Inverse);
                distanceTraveled.setText("Distance Traveled: " + new DecimalFormat("#,##0").format(distance) + ' ' + distanceUnits);
                detailLayout.addView(distanceTraveled);
            }

        }

        @Override
        public void expand() {
            if (expandedView != null)
                expandedView.close(false);
            super.expand();
            expandedView = this;
        }

        @Override
        public void close(boolean initialLoad) {
            super.close(initialLoad);
            expandedView = null;
        }
    }

    @Override
    public void onBindViewHolderCursor(FuelStopAdapter.ViewHolder holder, Cursor cursor) {
        holder.onBind(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fuel_stop_selector, parent, false));
    }

    private void onItemClick(long position, RecyclerView.ViewHolder holder) {
//        Log.v("onItemClick", "Position: " + position);
        onClickListener.onItemClick(position, holder);
    }

    private void onEditClick(long itemId, RecyclerView.ViewHolder holder) {
//        Log.v(TAG, "Edit item: " + itemId);
        if (onClickListener != null)
            onClickListener.onEditClick(itemId, holder);
    }

    private void onDeleteClick(long itemId, RecyclerView.ViewHolder holder) {
//        Log.v(TAG, "Delete item: " + itemId);
        if (onClickListener != null)
            onClickListener.onDeleteClick(itemId, holder);
    }

    public void setOnItemClickListener(ServiceRecyclerAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

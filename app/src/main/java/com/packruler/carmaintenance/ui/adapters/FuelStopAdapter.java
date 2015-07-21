package com.packruler.carmaintenance.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.FuelStop;

import java.text.DecimalFormat;
import java.util.Currency;

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
        private String TAG = "Position: " + getItemId();
        private TextView efficiencyDisplay;
        private TextView costDisplay;
        private TextView distanceDisplay;
        private TextView volumeDisplay;
        private TextView costPerDisplay;
        private LinearLayout detailLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Current expanded: " + expandedView);
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
                        Log.v(TAG, "Current expanded: " + expandedView);
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
                        Log.v(TAG, "Current expanded: " + expandedView);
                        if (expandedView == ViewHolder.this) close(false);
                        else expand();

                        onItemClick(ViewHolder.this.getItemId(), ViewHolder.this);
                    }
                }
            });
            efficiencyDisplay = (TextView) itemView.findViewById(R.id.fuel_efficiency);
            costDisplay = (TextView) itemView.findViewById(R.id.cost_display);
            distanceDisplay = (TextView) itemView.findViewById(R.id.distance_display);
            volumeDisplay = (TextView) itemView.findViewById(R.id.volume_display);
            costPerDisplay = (TextView) itemView.findViewById(R.id.cost_per);
            detailLayout = (LinearLayout) itemView.findViewById(R.id.extra_details);
        }

        @Override
        void setDisplay(Cursor cursor) {
            int distance = cursor.getInt(cursor.getColumnIndex(FuelStop.DISTANCE_TRAVELED));
            float volume = cursor.getFloat(cursor.getColumnIndex(FuelStop.VOLUME));
            float costPer = cursor.getFloat(cursor.getColumnIndex(FuelStop.COST_PER_VOLUME));
            float cost = volume * costPer;

            Vehicle vehicle = new Vehicle(carSQL, cursor.getLong(cursor.getColumnIndex(FuelStop.VEHICLE_ROW)));

            String currency = vehicle.getCurrency();
            if (currency == null)
                currency = "";
            else
                currency = Currency.getInstance(currency).getSymbol();

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
            volumeDisplay.setText(numberFormat.format(volume) + " " + volumeUnits);
            if (distance > 0)
                distanceDisplay.setText(distance + " " + distanceUnits);
            else distanceDisplay.setText("");
            costPerDisplay.setText(currency + costPer + volumeUnits);
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
        Log.v("onItemClick", "Position: " + position);
        onClickListener.onItemClick(position, holder);
    }

    private void onEditClick(long itemId, RecyclerView.ViewHolder holder) {
        Log.v(TAG, "Edit item: " + itemId);
        if (onClickListener != null)
            onClickListener.onEditClick(itemId, holder);
    }

    private void onDeleteClick(long itemId, RecyclerView.ViewHolder holder) {
        Log.v(TAG, "Delete item: " + itemId);
        if (onClickListener != null)
            onClickListener.onDeleteClick(itemId, holder);
    }

    public void setOnItemClickListener(ServiceRecyclerAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

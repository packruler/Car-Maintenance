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

/**
 * Created by Packruler on 6/28/15.
 */
public class FuelStopAdapter extends CursorRecyclerViewAdapter<FuelStopAdapter.ViewHolder> {
    private static String TAG = "FuelStopAdapter";

    private final CarSQL carSQL;
    private ServiceRecyclerAdapter.OnClickListener onClickListener;

    public FuelStopAdapter(CarSQL carSQL, Cursor cursor) {
        super(cursor);
        this.carSQL = carSQL;
        Log.v(getClass().getSimpleName(), "Column names: ");
        for (String col : cursor.getColumnNames()) {
            Log.v(getClass().getSimpleName(), col);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView efficiencyDisplay;
        private TextView costDisplay;
        private TextView distanceDisplay;
        private TextView volumeDisplay;
        private TextView costPerDisplay;
        private LinearLayout extraDetailsDisplay;

        public ViewHolder(View itemView) {
            super(itemView);
            efficiencyDisplay = (TextView) itemView.findViewById(R.id.fuel_efficiency);
            costDisplay = (TextView) itemView.findViewById(R.id.cost_display);
            distanceDisplay = (TextView) itemView.findViewById(R.id.distance_display);
            volumeDisplay = (TextView) itemView.findViewById(R.id.volume_display);
            costPerDisplay = (TextView) itemView.findViewById(R.id.cost_per);
            extraDetailsDisplay = (LinearLayout) itemView.findViewById(R.id.extra_details);
        }

        public void setDisplay(Cursor cursor) {
            int distance = cursor.getInt(cursor.getColumnIndex(FuelStop.DISTANCE_TRAVELED));
            float volume = cursor.getFloat(cursor.getColumnIndex(FuelStop.VOLUME));
            float costPer = cursor.getFloat(cursor.getColumnIndex(FuelStop.COST_PER_VOLUME));
            float cost = cursor.getFloat(cursor.getColumnIndex(FuelStop.COST));

            Vehicle vehicle = new Vehicle(carSQL, cursor.getLong(cursor.getColumnIndex(FuelStop.VEHICLE_ROW)));

            String costUnits = vehicle.getCostUnits();
            if (costUnits == null)
                costUnits = "";

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

            costDisplay.setText(costUnits + numberFormat.format(cost));
            volumeDisplay.setText(numberFormat.format(volume) + ' ' + volumeUnits);
            distanceDisplay.setText(distance + ' ' + distanceUnits);
            costPerDisplay.setText(costUnits + costPer + volumeUnits);
        }
    }

    @Override
    public void onBindViewHolderCursor(FuelStopAdapter.ViewHolder holder, Cursor cursor) {
        holder.setDisplay(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fuel_stop_selector, parent, false));
    }

    private void onItemClick(long position) {
        Log.v("onItemClick", "Position: " + position);
        onClickListener.onItemClick(position);
    }

    private void onEditClick(long itemId) {
        Log.v(TAG, "Edit item: " + itemId);
        if (onClickListener != null)
            onClickListener.onEditClick(itemId);
    }

    private void onDeleteClick(long itemId) {
        Log.v(TAG, "Delete item: " + itemId);
        if (onClickListener != null)
            onClickListener.onDeleteClick(itemId);
    }

    public void setOnItemClickListener(ServiceRecyclerAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

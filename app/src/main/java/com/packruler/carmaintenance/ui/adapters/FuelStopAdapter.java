package com.packruler.carmaintenance.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private ViewHolder exandedView;

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
        private LinearLayout layout;
        private RelativeLayout expandedMenu;
        private LinearLayout detailLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.expandable_layout);
            layout.setLayoutAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    notifyItemChanged(getAdapterPosition());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Current expanded: " + exandedView);
                    if (exandedView == ViewHolder.this) close();
                    else expand();

                    onItemClick(ViewHolder.this.getItemId(), ViewHolder.this);
                }
            });
            layout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClick(ViewHolder.this.getItemId(), ViewHolder.this);
                }
            });

            layout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClick(ViewHolder.this.getItemId(), ViewHolder.this);
                }
            });
            efficiencyDisplay = (TextView) itemView.findViewById(R.id.fuel_efficiency);
            costDisplay = (TextView) itemView.findViewById(R.id.cost_display);
            distanceDisplay = (TextView) itemView.findViewById(R.id.distance_display);
            volumeDisplay = (TextView) itemView.findViewById(R.id.volume_display);
            costPerDisplay = (TextView) itemView.findViewById(R.id.cost_per);
            detailLayout = (LinearLayout) itemView.findViewById(R.id.extra_details);
            expandedMenu = (RelativeLayout) itemView.findViewById(R.id.expanded_menu);
//            layout.removeView(expandedMenu);
            expandedMenu.setVisibility(View.GONE);
        }

        public void setDisplay(Cursor cursor) {
            int distance = cursor.getInt(cursor.getColumnIndex(FuelStop.DISTANCE_TRAVELED));
            float volume = cursor.getFloat(cursor.getColumnIndex(FuelStop.VOLUME));
            float costPer = cursor.getFloat(cursor.getColumnIndex(FuelStop.COST_PER_VOLUME));
            float cost = cursor.getFloat(cursor.getColumnIndex(FuelStop.COST));

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
            volumeDisplay.setText(numberFormat.format(volume) + ' ' + volumeUnits);
            distanceDisplay.setText(distance + ' ' + distanceUnits);
            costPerDisplay.setText(currency + costPer + volumeUnits);
        }

        void expand() {
            if (exandedView != null)
                exandedView.close();
            expandedMenu.setVisibility(View.VISIBLE);
            exandedView = this;
        }

        void close() {
            expandedMenu.setVisibility(View.GONE);

            exandedView = null;
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

    private void onItemClick(long position, RecyclerView.ViewHolder holder) {
        Log.v("onItemClick", "Position: " + position);
        onClickListener.onItemClick(position,holder);
    }

    private void onEditClick(long itemId, RecyclerView.ViewHolder holder) {
        Log.v(TAG, "Edit item: " + itemId);
        if (onClickListener != null)
            onClickListener.onEditClick(itemId,holder);
    }

    private void onDeleteClick(long itemId, RecyclerView.ViewHolder holder) {
        Log.v(TAG, "Delete item: " + itemId);
        if (onClickListener != null)
            onClickListener.onDeleteClick(itemId,holder);
    }

    public void setOnItemClickListener(ServiceRecyclerAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

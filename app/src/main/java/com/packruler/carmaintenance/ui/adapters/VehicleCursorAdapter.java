package com.packruler.carmaintenance.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Packruler on 6/16/15.
 */
public class VehicleCursorAdapter extends CursorRecyclerViewAdapter<VehicleCursorAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();

    private CarSQL carSQL;
    private OnClickListener onClickListener;
    private Context context;

    public VehicleCursorAdapter(Context context, Cursor cursor, CarSQL carSQL) {
        super(cursor);
        this.carSQL = carSQL;
        this.context = context;
        this.setHasStableIds(true);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView mileage;
//        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.vehicle_name);
            mileage = (TextView) itemView.findViewById(R.id.current_mileage);
//            image = (ImageView) itemView.findViewById(R.id.vehicle_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null)
                        onClickListener.onClick(ViewHolder.this.getItemId());
                }
            });
        }

        private void setup(Cursor cursor) {
            name.setText(cursor.getString(cursor.getColumnIndex(Vehicle.VEHICLE_NAME)));
            mileage.setText(context.getString(R.string.current_mileage) + ": " +
                    NumberFormat.getInstance().format(cursor.getLong(cursor.getColumnIndex(Vehicle.CURRENT_MILEAGE))));
            Vehicle vehicle = new Vehicle(carSQL, cursor.getLong(0));
            vehicle.getFuelEfficiency();
            Log.v(TAG, "Avg cost: " + new DecimalFormat("0.000").format(vehicle.getAverageCostOfFuel()));
            Log.v(TAG, "Total fuel used: " + new DecimalFormat("0.000").format(vehicle.getTotalFuelUsed()));
        }
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.setup(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_selector, parent, false));
    }

    public interface OnClickListener {
        void onClick(long itemId);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

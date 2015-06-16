package com.packruler.carmaintenance.ui.adapters;

import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.text.NumberFormat;

/**
 * Created by Packruler on 6/16/15.
 */
public class VehicleCursorAdapter extends CursorRecyclerViewAdapter<VehicleCursorAdapter.ViewHolder> {
    private CarSQL carSQL;

    public VehicleCursorAdapter(Cursor cursor, CarSQL carSQL) {
        super(cursor);
        this.carSQL = carSQL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView mileage;
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.vehicle_name);
            mileage = (TextView) itemView.findViewById(R.id.current_mileage);
            image = (ImageView) itemView.findViewById(R.id.vehicle_image);
        }

        private void setup(Cursor cursor) {
            name.setText(cursor.getString(cursor.getColumnIndex(Vehicle.VEHICLE_NAME)));
            mileage.setText(Resources.getSystem().getString(R.string.current_mileage) + ": " +
                    NumberFormat.getInstance().format(cursor.getLong(cursor.getColumnIndex(Vehicle.CURRENT_MILEAGE))));
            Vehicle vehicle = new Vehicle(carSQL, cursor.getLong(0));
            if (vehicle.getImage().exists())
                carSQL.loadBitmap(new Vehicle(carSQL, cursor.getLong(0)), image, null, new CarSQL.LoadedBitmapRunnable() {
                    @Override
                    public void run() {
                        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                });
            else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    image.setImageDrawable(Resources.getSystem().getDrawable(R.drawable.missing_photo_icon, null));
                else
                    image.setImageDrawable(Resources.getSystem().getDrawable(R.drawable.missing_photo_icon));

                image.setScaleType(ImageView.ScaleType.CENTER);
            }

        }
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.setup(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_selector, null));
    }
}

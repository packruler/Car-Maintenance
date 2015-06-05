package com.packruler.carmaintenance.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by Packruler on 5/22/15.
 */
public class ServiceRecyclerAdapter extends CursorRecyclerViewAdapter<ServiceRecyclerAdapter.ViewHolder> {
    private Context context;

    public ServiceRecyclerAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

    private onRecyclerItemClickListener onRecyclerItemClickListener;

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.setDisplay(cursor);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView typeDisplay;
        private TextView mileageDisplay;
        private TextView costDisplay;
        private TextView dateDisplay;

        public ViewHolder(View v) {
            super(v);
            typeDisplay = (TextView) v.findViewById(R.id.type_display);
            mileageDisplay = (TextView) v.findViewById(R.id.mileageDisplay);
            costDisplay = (TextView) v.findViewById(R.id.costDisplay);
            dateDisplay = (TextView) v.findViewById(R.id.dateDisplay);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(ViewHolder.this.getItemId());
                }
            });
        }

        public void setDisplay(Cursor cursor){
            typeDisplay.setText(getType(cursor));
            costDisplay.setText(getCost(cursor));
            mileageDisplay.setText(getMileage(cursor));
            dateDisplay.setText(getDate(cursor));
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_selector, parent, false);
        return new ViewHolder(v);
    }

    public String getType(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(ServiceTask.TYPE));
    }

    public String getCost(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(ServiceTask.COST_UNITS)) +
                new DecimalFormat("0.00").format(cursor.getFloat(cursor.getColumnIndex(ServiceTask.COST)));
    }

    public String getMileage(Cursor cursor) {
            return context.getString(R.string.mileage) + ": " + NumberFormat.getInstance().format(cursor.getLong(cursor.getColumnIndex(ServiceTask.MILEAGE)));
    }

    public String getDate(Cursor cursor) {
        return DateFormat.getDateFormat(context).format(new Date(cursor.getLong(cursor.getColumnIndex(ServiceTask.DATE))));
    }

    private void onItemClick(long position) {
        Log.v("onItemClick", "Position: " + position);
        onRecyclerItemClickListener.onItemClick(position);
    }

    public void setOnItemClickListener(onRecyclerItemClickListener listener) {
        onRecyclerItemClickListener = listener;
    }

    public interface onRecyclerItemClickListener {
        void onItemClick(long rowId);
    }
}

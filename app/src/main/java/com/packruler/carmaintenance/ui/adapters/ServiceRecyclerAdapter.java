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

import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by Packruler on 5/22/15.
 */
public class ServiceRecyclerAdapter extends CursorRecyclerViewAdapter<ServiceRecyclerAdapter.ViewHolder> {

    public ServiceRecyclerAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    private onRecyclerItemClickListener onRecyclerItemClickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private View view;
        public TextView typeDisplay;
        public TextView mileageDisplay;
        public TextView costDisplay;
        public TextView dateDisplay;
        public int position;

        public ViewHolder(View v) {
            super(v);
            view = v;
            typeDisplay = (TextView) v.findViewById(R.id.typeDisplay);
            mileageDisplay = (TextView) v.findViewById(R.id.mileageDisplay);
            costDisplay = (TextView) v.findViewById(R.id.costDisplay);
            dateDisplay = (TextView) v.findViewById(R.id.dateDisplay);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(v, ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.service_selector, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        ServiceTaskItem item = new ServiceTaskItem(cursor);
        holder.typeDisplay.setText(item.getType());
        holder.costDisplay.setText(item.getCost());
        holder.mileageDisplay.setText("Mileage: " + item.getMileage());
        holder.dateDisplay.setText(item.getDate());
    }

    private class ServiceTaskItem {
        private Cursor cursor;

        public ServiceTaskItem(Cursor cursor) {
            this.cursor = cursor;
        }

        public String getType() {
            return cursor.getString(cursor.getColumnIndex(ServiceTask.TYPE));
        }

        public String getCost() {
            return NumberFormat.getCurrencyInstance().format(cursor.getFloat(cursor.getColumnIndex(ServiceTask.COST)));
        }

        public String getMileage() {
            return NumberFormat.getInstance().format(cursor.getLong(cursor.getColumnIndex(ServiceTask.MILEAGE)));
        }

        public String getDate() {
            return DateFormat.getDateFormat(mContext).format(new Date(cursor.getLong(cursor.getColumnIndex(ServiceTask.DATE))));
        }
    }

    private void onItemClick(View view, int position) {
        Log.v("onItemClick", "Position: " + position);
        onRecyclerItemClickListener.onItemClick(view, position);
    }

    public void setOnItemClickListener(onRecyclerItemClickListener listener) {
        onRecyclerItemClickListener = listener;
    }

    public interface onRecyclerItemClickListener {
        void onItemClick(View view, int position);
    }
}

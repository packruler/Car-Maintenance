package com.packruler.carmaintenance.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
    private String TAG = getClass().getName();

    private OnClickListener onClickListener;

    public ServiceRecyclerAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

//    private OnClickListener onClickListener;

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
        private LinearLayout layout;
        private RelativeLayout expandedMenu;
        private LinearLayout detailLayout;
        private boolean expanded = false;

        public ViewHolder(View v) {
            super(v);
            layout = (LinearLayout) v.findViewById(R.id.expandable_layout);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!expanded) {
                        layout.addView(expandedMenu);
                    } else {
                        layout.removeView(expandedMenu);
                    }

                    expanded = !expanded;
                    onItemClick(ViewHolder.this.getItemId());
                }
            });
            layout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClick(ViewHolder.this.getItemId());
                }
            });

            layout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClick(ViewHolder.this.getItemId());
                }
            });
            typeDisplay = (TextView) v.findViewById(R.id.type_display);
            mileageDisplay = (TextView) v.findViewById(R.id.mileage_display);
            costDisplay = (TextView) v.findViewById(R.id.cost_display);
            dateDisplay = (TextView) v.findViewById(R.id.date_display);

            expandedMenu = (RelativeLayout) v.findViewById(R.id.expanded_menu);
            layout.removeView(expandedMenu);

            detailLayout = (LinearLayout) layout.findViewById(R.id.extra_details);
        }

        public void setDisplay(Cursor cursor) {
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
        return new DecimalFormat("0.00").format(cursor.getFloat(cursor.getColumnIndex(ServiceTask.COST)));
    }

    public String getMileage(Cursor cursor) {
        return context.getString(R.string.mileage) + ": " + NumberFormat.getInstance().format(cursor.getLong(cursor.getColumnIndex(ServiceTask.MILEAGE)));
    }

    public String getDate(Cursor cursor) {
        return DateFormat.getDateFormat(context).format(new Date(cursor.getLong(cursor.getColumnIndex(ServiceTask.DATE))));
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

    public interface OnClickListener {
        void onItemClick(long itemId);

        void onDeleteClick(long itemId);

        void onEditClick(long itemId);
    }

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

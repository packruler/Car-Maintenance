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
    private ViewHolder expandedView;

    public ServiceRecyclerAdapter(Context context, Cursor cursor) {
        super(cursor);
        this.context = context;
    }

//    private OnClickListener onClickListener;

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.setDisplay(cursor);
    }

    public class ViewHolder extends ExpandableViewHolder {
        // each data item is just a string in this case
        private TextView typeDisplay;
        private TextView mileageDisplay;
        private TextView costDisplay;
        private TextView dateDisplay;
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
            typeDisplay = (TextView) itemView.findViewById(R.id.type_display);
            mileageDisplay = (TextView) itemView.findViewById(R.id.mileage_display);
            costDisplay = (TextView) itemView.findViewById(R.id.cost_display);
            dateDisplay = (TextView) itemView.findViewById(R.id.date_display);
            detailLayout = (LinearLayout) layout.findViewById(R.id.extra_details);
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

        @Override
        void setDisplay(Cursor cursor) {
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

    public interface OnClickListener {
        void onItemClick(long itemId, RecyclerView.ViewHolder holder);

        void onDeleteClick(long itemId, RecyclerView.ViewHolder holder);

        void onEditClick(long itemId, RecyclerView.ViewHolder holder);
    }

    public void setOnItemClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

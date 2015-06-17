package com.packruler.carmaintenance.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.maintenence.PartReplacement;

import java.text.DecimalFormat;

/**
 * Created by Packruler on 6/4/15.
 */
public class ExpandablePartRecyclerAdapter extends CursorRecyclerViewAdapter<ExpandablePartRecyclerAdapter.ViewHolder> {
    private final String TAG = getClass().getSimpleName();

    public ExpandablePartRecyclerAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.setValues(cursor);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.part_expandable_selector, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView part;
        private TextView brand;
        private TextView cost;
        private LinearLayout expandableLayout;
        private LinearLayout detailLayout;
        private RelativeLayout expandedMenu;
        private boolean expanded = false;

        public ViewHolder(View itemView) {
            super(itemView);
            expandableLayout = (LinearLayout) itemView;

            expandableLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!expanded)
                        expandableLayout.addView(expandedMenu);
                    else
                        expandableLayout.removeView(expandedMenu);

                    expanded = !expanded;
                    onItemClick(ViewHolder.this.getItemId());
                }
            });

            expandableLayout.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClick(ViewHolder.this.getItemId());
                }
            });

            expandableLayout.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClick(ViewHolder.this.getItemId());
                }
            });

            expandedMenu = (RelativeLayout) expandableLayout.findViewById(R.id.expanded_menu);
            part = (TextView) expandableLayout.findViewById(R.id.part);
            brand = (TextView) expandableLayout.findViewById(R.id.brand_display);
            cost = (TextView) expandableLayout.findViewById(R.id.cost_display);
            detailLayout = (LinearLayout) expandableLayout.findViewById(R.id.extra_details);
            expandableLayout.removeView(expandedMenu);
        }

        public void setValues(Cursor cursor) {
            part.setText(getPart(cursor));
            brand.setText(getBrand(cursor));
            cost.setText(getCost(cursor));
            loadDetails(cursor, detailLayout);
        }
    }

    private String getPart(Cursor cursor) {
        String out = cursor.getString(cursor.getColumnIndex(PartReplacement.PART_NAME));
        if (out == null)
            return "";
        return out;
    }

    private String getBrand(Cursor cursor) {
        String out = cursor.getString(cursor.getColumnIndex(PartReplacement.BRAND));
        if (out == null)
            return "";
        return out;
    }

    private String getCost(Cursor cursor) {
        if (cursor.getFloat(cursor.getColumnIndex(PartReplacement.COST)) == 0)
            return "";
        return new DecimalFormat("0.00").format(cursor.getFloat(cursor.getColumnIndex(PartReplacement.COST)));
    }

    private void loadDetails(Cursor cursor, LinearLayout layout) {
        if (cursor.getString(cursor.getColumnIndex(PartReplacement.EXPECTED_LIFE_DISTANCE)) != null) {
            handleDetailPlacement(layout.getContext().getString(R.string.expected_distance) + ": " +
                    cursor.getString(cursor.getColumnIndex(PartReplacement.EXPECTED_LIFE_DISTANCE))
                    , layout);
        }
        if (cursor.getString(cursor.getColumnIndex(PartReplacement.EXPECTED_LIFE_TIME)) != null) {
            handleDetailPlacement(layout.getContext().getString(R.string.expected_lifetime) + ": " +
                    cursor.getString(cursor.getColumnIndex(PartReplacement.EXPECTED_LIFE_TIME))
                    , layout);
        }
        if (cursor.getString(cursor.getColumnIndex(PartReplacement.WARRANTY_LIFE_DISTANCE)) != null) {
            handleDetailPlacement(layout.getContext().getString(R.string.warranty_distance) + ": " +
                    cursor.getString(cursor.getColumnIndex(PartReplacement.WARRANTY_LIFE_DISTANCE))
                    , layout);
        }
        if (cursor.getString(cursor.getColumnIndex(PartReplacement.WARRANTY_LIFE_TIME)) != null) {
            handleDetailPlacement(layout.getContext().getString(R.string.warranty_lifetime) + ": " +
                    cursor.getString(cursor.getColumnIndex(PartReplacement.WARRANTY_LIFE_TIME))
                    , layout);
        }
    }

    private void handleDetailPlacement(String value, LinearLayout layout) {
        TextView textView = new TextView(layout.getContext());
        textView.setText(value);
        textView.setTextAppearance(layout.getContext(), android.R.style.TextAppearance_Small);
        layout.addView(textView);
    }

    private void onItemClick(long itemId) {
        Log.v(TAG, "Clicked item: " + itemId);
        if (onClickListener != null)
            onClickListener.onItemClick(itemId);
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

    private OnClickListener onClickListener;

    public interface OnClickListener {
        void onItemClick(long itemId);

        void onDeleteClick(long itemId);

        void onEditClick(long itemId);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}

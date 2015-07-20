package com.packruler.carmaintenance.ui.adapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
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

    class ViewHolder extends RecyclerView.ViewHolder {
        private String TAG = "Position: " + getItemId();
        private TextView efficiencyDisplay;
        private TextView costDisplay;
        private TextView distanceDisplay;
        private TextView volumeDisplay;
        private TextView costPerDisplay;
        private FrameLayout layout;
        private RelativeLayout expandedMenu;
        private LinearLayout detailLayout;
        private CardView card;
        private ValueAnimator valueAnimator;
        private int minHeight;
        private boolean setup = false;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (FrameLayout) itemView.findViewById(R.id.expandable_layout);
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Log.v(TAG, "LAYOUT");
                    close(true);
                    if (setup) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        else layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
            card = (CardView) layout.findViewById(R.id.card);
//            layout.setLayoutAnimationListener(new Animation.AnimationListener() {
//                @Override
//                public void onAnimationStart(Animation animation) {
//                }
//
//                @Override
//                public void onAnimationEnd(Animation animation) {
//                    notifyItemChanged(getAdapterPosition());
//                }
//
//                @Override
//                public void onAnimationRepeat(Animation animation) {
//
//                }
//            });
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
            expandedMenu = (RelativeLayout) itemView.findViewById(R.id.expanded_menu);
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
            if (minHeight <= (layout.getPaddingTop() * 2))
                minHeight = card.getHeight() + (layout.getPaddingTop() * 2);

            if (expandedView != null)
                expandedView.close(false);

            valueAnimator = ValueAnimator.ofInt(0, card.getHeight() + layout.getPaddingTop());
            valueAnimator.setDuration(500);

            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int currentValue = (int) valueAnimator.getAnimatedValue();
                    expandedMenu.setY(currentValue);
//                    Log.v(TAG, "Current Value:" + currentValue);
                    expandedMenu.requestLayout();

                    float height = currentValue + expandedMenu.getHeight();
//                    Log.v(TAG, "Height: " + height + " | " + minHeight);
                    if (height < minHeight)
                        height = minHeight;

                    if (height != 0)
                        layout.getLayoutParams().height = (int) height;
                }
            });
            valueAnimator.start();
            expandedView = this;
        }

        void close(boolean initialLoad) {
            if (initialLoad) {
                layout.requestLayout();
                card.requestLayout();
                expandedMenu.requestLayout();
            }
//            Log.v(TAG, "Card height: " + card.getHeight());
            minHeight = card.getHeight() + (layout.getPaddingTop() * 2);
            int hideValue = card.getHeight() - expandedMenu.getHeight();

            if (initialLoad) {
                expandedMenu.setY(hideValue);
                if (minHeight > (layout.getPaddingTop() * 2)) {
                    ((RecyclerView.LayoutParams) layout.getLayoutParams()).height = card.getHeight() + (layout.getPaddingTop() * 2);
//                    Log.d(TAG, "SUCCESS");
                    setup = true;
                } else {
//                    Log.d(TAG, "FAILED");
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
//                            Log.d(TAG, "RERUN");
                            close(true);
                        }
                    }, 100);
                }

                layout.requestLayout();
//                Log.v(TAG, "Current Value:" + hideValue);
            } else {
                if (valueAnimator != null && valueAnimator.isRunning())
                    valueAnimator.cancel();

                valueAnimator = ValueAnimator.ofInt((int) expandedMenu.getY(), hideValue);

                valueAnimator.setDuration(500);
                valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int currentValue = (int) valueAnimator.getAnimatedValue();
                        expandedMenu.setY(currentValue);

                        int height = currentValue + expandedMenu.getHeight();
                        if (height < minHeight)
                            height = minHeight;
//                        Log.v(TAG, "Height: " + height + " | " + minHeight);
                        if (height != 0) {
                            ((RecyclerView.LayoutParams) layout.getLayoutParams()).height = height;
                        }
                        layout.requestLayout();
                    }
                });
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        valueAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                valueAnimator.start();

                expandedView = null;
            }
        }

        public void onBind(Cursor cursor) {
            TAG = "Position: " + getItemId();
            setDisplay(cursor);
            close(true);
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Long)
                return (Long) o == getItemId();
            else
                return o instanceof ViewHolder && ((ViewHolder) o).getItemId() == getItemId();
        }
    }
//
//    @Override
//    public void onViewAttachedToWindow(ViewHolder holder) {
//        super.onViewAttachedToWindow(holder);
//        holder.close(true);
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//        notifyDataSetChanged();
//    }

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

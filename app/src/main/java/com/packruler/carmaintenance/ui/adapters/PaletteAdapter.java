package com.packruler.carmaintenance.ui.adapters;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.packruler.carmaintenance.R;

import java.util.List;

/**
 * Created by Packruler on 5/26/15.
 */
public class PaletteAdapter extends RecyclerView.Adapter<PaletteAdapter.ViewHolder> {
    private List<Palette.Swatch> swatches;
    private Context context;

    public PaletteAdapter(Context context, Palette palette) {
        swatches = palette.getSwatches();
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.palette_selector, parent, false);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setColor(swatches.get(position).getRgb());
    }

    @Override
    public int getItemCount() {
        return swatches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private View colorDisplay;
        private int color;

        public ViewHolder(View itemView) {
            super(itemView);
            colorDisplay = itemView.findViewById(android.R.id.icon);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(color);
                }
            });
        }

        public void setColor(int color) {
            colorDisplay.setBackgroundColor(color);
            this.color = color;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int color);
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void onItemClick(int color) {
        try {
            onItemClickListener.onItemClick(color);
        } catch (Exception e) {
            //Fuckit
        }
    }
}

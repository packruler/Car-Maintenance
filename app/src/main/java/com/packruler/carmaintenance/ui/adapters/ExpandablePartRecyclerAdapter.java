package com.packruler.carmaintenance.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Packruler on 6/4/15.
 */
public class ExpandablePartRecyclerAdapter extends CursorRecyclerViewAdapter<ExpandablePartRecyclerAdapter.ViewHolder> {
    public ExpandablePartRecyclerAdapter(Cursor cursor) {
        super(cursor);
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}

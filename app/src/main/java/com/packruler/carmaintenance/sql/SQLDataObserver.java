package com.packruler.carmaintenance.sql;

/**
 * Created by Packruler on 6/11/15.
 */
public abstract class SQLDataObserver {
    public void onChanged(String tableName, long row) {
        //do nothing
    }

    public void onRemoved(String tableName, long row) {
        //do nothing
    }

    public void onAdded(String tableName, long row) {
        //do nothing
    }
}

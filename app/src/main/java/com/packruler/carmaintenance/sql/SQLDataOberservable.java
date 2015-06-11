package com.packruler.carmaintenance.sql;


import android.database.Observable;

/**
 * Created by Packruler on 6/11/15.
 */
public class SQLDataOberservable extends Observable<SQLDataObserver> {
    protected String table;
    protected long row;

    public void notifiyChanged() {
        if (table != null)
            synchronized (mObservers) {
                // since onChanged() is implemented by the app, it could do anything, including
                // removing itself from {@link mObservers} - and that could cause problems if
                // an iterator is used on the ArrayList {@link mObservers}.
                // to avoid such problems, just march thru the list in the reverse order.
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onChanged(table, row);
                }
            }
    }

    public void notifiyChanged(String table, long row) {
        synchronized (mObservers) {
            // since onChanged() is implemented by the app, it could do anything, including
            // removing itself from {@link mObservers} - and that could cause problems if
            // an iterator is used on the ArrayList {@link mObservers}.
            // to avoid such problems, just march thru the list in the reverse order.
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged(table, row);
            }
        }
    }

    public void notifyAdded() {
        if (table != null)
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onAdded(table, row);
                }
            }
    }

    public void notifyAdded(String table, long row) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onAdded(table, row);
            }
        }
    }

    public void notifyRemoved() {
        if (table != null)
            synchronized (mObservers) {
                for (int i = mObservers.size() - 1; i >= 0; i--) {
                    mObservers.get(i).onRemoved(table, row);
                }
            }
    }

    public void notifyRemoved(String table, long row) {
        synchronized (mObservers) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onRemoved(table, row);
            }
        }
    }
}

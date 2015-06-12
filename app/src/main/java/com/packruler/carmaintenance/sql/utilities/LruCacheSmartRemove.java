package com.packruler.carmaintenance.sql.utilities;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Packruler on 6/12/15.
 */
public class LruCacheSmartRemove<K, V extends Bitmap> extends LruCache<K, V> {
    private final String TAG = getClass().getSimpleName();
    ArrayBlockingQueue<K> queue = new ArrayBlockingQueue<K>(5, true);

    /**
     * @param maxSize
     *         sizeOf is Bitmap.getByteCount()/1024
     */
    public LruCacheSmartRemove(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(K key, V bitmap) {
        // The cache size will be measured in kilobytes rather than
        // number of items.
        return bitmap.getByteCount() / 1024;
    }

    @Override
    public void trimToSize(int maxSize) {
        if (size() > maxSize) {
            if (queue.poll() != null) {
                Log.v(TAG, "Trim based on queue");
                try {
                    remove(queue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (size() >= maxSize)
                    trimToSize(maxSize);
            } else {
                Log.v(TAG, "Use default trim");
                super.trimToSize(maxSize);
            }
        }
    }

    public V put(K key, V value, boolean rememberUsage) {
        if (rememberUsage) {
            queue.remove(key);
            try {
                queue.add(key);
            } catch (IllegalStateException e) {
                Log.w(TAG, "Queue full");
            }
        }
        return super.put(key, value);
    }

    public V get(K key, boolean rememberUsage) {
        if (rememberUsage) {
            queue.remove(key);
            try {
                queue.add(key);
            } catch (IllegalStateException e) {
                Log.w(TAG, "Queue full");
            }
        }
        return get(key);
    }
}

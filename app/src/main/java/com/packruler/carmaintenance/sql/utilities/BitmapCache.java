package com.packruler.carmaintenance.sql.utilities;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Packruler on 6/23/15.
 */
public class BitmapCache<K, V extends Bitmap> implements Map<K, V> {
    private final String TAG = getClass().getSimpleName();

    private int max;
    private HashMap<K, V> map = new HashMap<>();
    private int size;
    private ArrayBlockingQueue<K> queueForRemoval = new ArrayBlockingQueue<K>(10, true);


    /**
     * @param maxSize
     *         Maximum size in kB
     */
    public BitmapCache(int maxSize) {
        max = maxSize;
    }

    @Override
    public void clear() {
        map.clear();
        queueForRemoval.clear();
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsValue(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @NonNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public V get(Object key) {
        queueForRemoval.remove(key);

        queueForRemoval.add((K) key);
        return map.get(key);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @NonNull
    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public V put(K key, V value) {
        for (Map.Entry<K, V> entry : map.entrySet()) {
            Log.v(TAG, entry.getKey().toString() + "| Size: " + (entry.getValue().getByteCount() / 1024));
        }

        int inSize = sizeOf(value);
        if (inSize > maxSize())
            return null;

        V current = map.remove(key);
        if (current != null) {
            size -= sizeOf(current);
            current.recycle();
            System.gc();
        }
        Log.v(TAG, "Current size: " + size + "| inSize: " + inSize + "| max: " + max + "| Trim required: " + ((size + inSize) < max));
        if ((size + inSize) > max)
            while ((size + inSize) > max)
                trim();

        queueForRemoval.remove(key);
        queueForRemoval.add(key);

        size += inSize;
        return map.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        V value = map.remove(key);
        if (value != null) {
            size -= sizeOf(value);
            Log.v(TAG, "Remove " + key + "| size: " + sizeOf(value) + " | Current size: " + size);
        }

        queueForRemoval.remove(key);

        return value;
    }

    @Override
    public int size() {
        return size;
    }

    @NonNull
    @Override
    public Collection<V> values() {
        return map.values();
    }

    private int sizeOf(V value) {
        return value.getByteCount() / 1024;
    }

    public int maxSize() {
        return max;
    }

    private boolean canAdd(V value) {
        return (size + sizeOf(value)) < maxSize();
    }

    public void trim() {
        Log.v(TAG + ".TRIM", "Current size: " +
                NumberFormat.getInstance().format(size) + "/" +
                NumberFormat.getInstance().format(max) + "KB");
        if (map.containsKey(queueForRemoval.peek())) {
            Log.v(TAG + ".TRIM", queueForRemoval.peek().toString() + " | Size: " + sizeOf(map.get(queueForRemoval.peek())));
            remove(queueForRemoval.poll());
        } else {
            Log.e(TAG, "Key: \'" + queueForRemoval.poll() + "\' is missing");
            if (!queueForRemoval.isEmpty())
                trim();
        }
    }
}

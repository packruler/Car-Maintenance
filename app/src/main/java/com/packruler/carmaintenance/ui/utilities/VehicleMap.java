package com.packruler.carmaintenance.ui.utilities;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.util.Log;

import com.packruler.carmaintenance.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Packruler on 6/8/15.
 */
public class VehicleMap extends DataSetObservable implements Map<CharSequence, Vehicle> {
    private final String TAG = getClass().getSimpleName();
    private Map<CharSequence, Vehicle> map = new TreeMap<>();

    @Override
    public void clear() {
        Log.e(TAG, "CLEAR DOES NOTHING");
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (!(value instanceof Vehicle))
            return false;

        for (Vehicle vehicle : values()) {
            if (vehicle.equals(value))
                return true;
        }
        return false;
    }

    @NonNull
    @Override
    public Set<Entry<CharSequence, Vehicle>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Vehicle get(Object key) {
        return map.get(key);
    }

    public Vehicle get(long rowId) {
        for (Vehicle vehicle : map.values()) {
            if (vehicle.getRow() == rowId)
                return vehicle;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @NonNull
    @Override
    public Set<CharSequence> keySet() {
        return map.keySet();
    }

    public Vehicle put(Vehicle vehicle) {
        return put(vehicle.getName(), vehicle);
    }

    @Override
    public Vehicle put(CharSequence key, Vehicle value) {
        value.registerObserver(vehicleObserver);
        map.put(key, value);
        notifyChanged();
        return value;
    }

    @Override
    public void putAll(Map<? extends CharSequence, ? extends Vehicle> map) {
        for (Entry<? extends CharSequence, ? extends Vehicle> entry : map.entrySet()) {
            put(entry.getValue());
        }
    }

    @Override
    public Vehicle remove(Object key) {
        Vehicle vehicle = map.remove(key);
        vehicle.unregisterObserver(vehicleObserver);
        notifyChanged();
        return vehicle;
    }

    @Override
    public int size() {
        return map.size();
    }

    @NonNull
    @Override
    public Collection<Vehicle> values() {
        return map.values();
    }

    public boolean updateKeys() {
        boolean updated = false;
        LinkedList<CharSequence> updateKeys = new LinkedList<>();
        for (Entry<CharSequence, Vehicle> entry : entrySet()) {
            if (!entry.getKey().equals(entry.getValue().getName()))
                updateKeys.add(entry.getKey());
        }
        for (CharSequence key : updateKeys) {
            updated = updateKey(key);
        }
        return updated;
    }

    public boolean updateKey(CharSequence key) {
        put(remove(key));
        return true;
    }

    public List<Vehicle> getVehiclesByName(boolean descendingOrder) {
        List<Vehicle> vehicleList = new ArrayList<>(values());
        if (!descendingOrder)
            Collections.sort(vehicleList, nameComparator);
        else
            Collections.sort(vehicleList, Collections.reverseOrder(nameComparator));

        return vehicleList;
    }

    public List<Vehicle> getVehiclesByName() {
        return getVehiclesByName(false);
    }

    private DataSetObserver vehicleObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateKeys();
        }
    };

    private static Comparator<Vehicle> nameComparator = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle lhs, Vehicle rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };

    private static Comparator<Vehicle> yearComparator = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle lhs, Vehicle rhs) {
            return lhs.getYear() - rhs.getYear();
        }
    };

    private static Comparator<Vehicle> mileageComparator = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle lhs, Vehicle rhs) {
            return (int) (lhs.getCurrentMileage() - rhs.getCurrentMileage());
        }
    };

    private static Comparator<Vehicle> costComparator = new Comparator<Vehicle>() {
        @Override
        public int compare(Vehicle lhs, Vehicle rhs) {
            return (int) (lhs.getPurchaseCost() - rhs.getPurchaseCost());
        }
    };
}

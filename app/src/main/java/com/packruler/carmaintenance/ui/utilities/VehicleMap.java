package com.packruler.carmaintenance.ui.utilities;

import android.support.annotation.NonNull;
import android.util.Log;

import com.packruler.carmaintenance.sql.SQLDataOberservable;
import com.packruler.carmaintenance.sql.SQLDataObserver;
import com.packruler.carmaintenance.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by Packruler on 6/8/15.
 */
public class VehicleMap implements Map<Long, Vehicle> {
    private final String TAG = getClass().getSimpleName();
    private Map<Long, Vehicle> map = new TreeMap<>();

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
    public Set<Entry<Long, Vehicle>> entrySet() {
        return map.entrySet();
    }

    @Override
    public Vehicle get(Object key) {
        if (key instanceof Long) {
            return map.get(key);
        }

        return null;
    }

    public Vehicle get(CharSequence name) {
        for (Vehicle vehicle : map.values()) {
            if (vehicle.getName().equals(name))
                return vehicle;
        }
        return null;
    }

    public Vehicle get(long row) {
        Log.v(TAG, "Row: " + row);
        return map.get(row);
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @NonNull
    @Override
    public Set<Long> keySet() {
        return map.keySet();
    }

    public Vehicle put(Vehicle vehicle) {
        return put(vehicle.getRow(), vehicle);
    }

    @Override
    public Vehicle put(Long row, Vehicle value) {
        if (!map.containsValue(value)) {
            try {
                value.registerObserver(dataObserver);
            } catch (IllegalStateException e) {
                //Already registered
            }
            map.put(row, value);
        }
        return value;
    }

    @Override
    public void putAll(Map<? extends Long, ? extends Vehicle> map) {
        for (Entry<? extends Long, ? extends Vehicle> entry : map.entrySet()) {
            put(entry.getValue());
        }
    }

    @Override
    public Vehicle remove(Object key) {
        Vehicle vehicle = map.remove(key);
        vehicle.unregisterObserver(dataObserver);
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

    private SQLDataOberservable vehicleObservable = new SQLDataOberservable();

    private SQLDataObserver dataObserver = new SQLDataObserver() {
        @Override
        public void onChanged(String tableName, long row) {
            if (tableName.equals(Vehicle.TABLE_NAME))
                vehicleObservable.notifiyChanged(tableName, row);
        }

        @Override
        public void onAdded(String tableName, long row) {
            super.onAdded(tableName, row);
            if (tableName.equals(Vehicle.TABLE_NAME))
                vehicleObservable.notifiyChanged(tableName, row);
        }

        @Override
        public void onRemoved(String tableName, long row) {
            super.onRemoved(tableName, row);
            if (tableName.equals(Vehicle.TABLE_NAME))
                vehicleObservable.notifiyChanged(tableName, row);
        }
    };

    public void registerVehicleObserver(SQLDataObserver observer) {
        vehicleObservable.registerObserver(observer);
    }

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

package com.packruler.carmaintenance;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Created by packr_000 on 12/1/2014.
 */
public class EditCarFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String TAG = ((Object) this).getClass().getSimpleName();
    protected final static String CAR_NUMBER = "CAR_NUMBER";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int numCars = 0;
    private int carNumber;
    String carName;
    EditTextPreference yearPreference;
    ListPreference makePreference;
    ListPreference modelPreference;
    EditTextPreference extraPreference;

    public void setCarNumber(int inNum) {
        carNumber = inNum;
        carName = "Car" + carNumber;

        yearPreference.setKey(carName + getString(R.string.year));
        yearPreference.setText(preferences.getString(carName + getString(R.string.year), ""));

        makePreference.setKey(carName + getString(R.string.make));
        makePreference.setValue(preferences.getString(carName + getString(R.string.make), ""));

        modelPreference.setKey(carName + getString(R.string.model));
        modelPreference.setValue(preferences.getString(carName + getString(R.string.model), ""));
        setModelArray();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.new_car);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.package_name));
        preferences = getPreferenceManager().getSharedPreferences();
        editor = preferences.edit();
        numCars = preferences.getInt(getString(R.string.num_cars), 0);
        numCars++;
        editor.putInt(getString(R.string.num_cars), numCars);

        yearPreference = (EditTextPreference) findPreference(getString(R.string.year));
        makePreference = (ListPreference) findPreference(getString(R.string.make));
        modelPreference = (ListPreference) findPreference(getString(R.string.model));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(carName + getString(R.string.make))) {
            Log.i(TAG, "Make Changed: " + sharedPreferences.getString(key, ""));
            setModelArray();
        }
    }

    private void setModelArray() {
        Log.i(TAG, "setModelArray()");
//        String[] makes = getResources().getStringArray(R.array.car_makes);
        if (preferences.getString(carName + getString(R.string.make), "").equals("Acura")) {
            modelPreference.setEntries(R.array.Acura_Models);
            modelPreference.setEntryValues(R.array.Acura_Models);
        } else if (preferences.getString(carName + getString(R.string.make), "").equals("Alfa Romeo")) {
            modelPreference.setEntries(R.array.Alfa_Romeo_Models);
            modelPreference.setEntryValues(R.array.Alfa_Romeo_Models);
        } else if (preferences.getString(carName + getString(R.string.make), "").equals("Ariel")) {
            modelPreference.setEntries(R.array.Ariel_Models);
            modelPreference.setEntryValues(R.array.Ariel_Models);
        } else if (preferences.getString(carName + getString(R.string.make), "").equals("ARO")) {
            modelPreference.setEntries(R.array.ARO_Models);
            modelPreference.setEntryValues(R.array.ARO_Models);
        } else if (preferences.getString(carName + getString(R.string.make), "").equals("Aston Martin")) {
            modelPreference.setEntries(R.array.Aston_Martin_Models);
            modelPreference.setEntryValues(R.array.Aston_Martin_Models);
        }
    }
}

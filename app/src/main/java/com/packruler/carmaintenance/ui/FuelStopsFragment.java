package com.packruler.carmaintenance.ui;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.packruler.carmaintenance.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FuelStopsFragment extends Fragment {
    private static final String TAG = "FuelStopsFragment";

    private View rootView;

    public FuelStopsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_services, container, false);

        return rootView;
    }


}

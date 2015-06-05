package com.packruler.carmaintenance.ui;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditService extends Fragment {
    private final String TAG = getClass().getName();

    private ServiceTask serviceTask;
    private MaterialBetterSpinner type;

    public EditService() {
        // Required empty public constructor
    }

    public EditService(ServiceTask task) {
        serviceTask = task;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_service, container, false);
        type = (MaterialBetterSpinner) view.findViewById(R.id.task_type);
        if (serviceTask != null)
            loadTask();
        return view;
    }

    private void loadTask() {
        type.setText(serviceTask.getType());
    }


}

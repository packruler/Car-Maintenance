package com.packruler.carmaintenance.ui;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditService extends Fragment {
    private final String TAG = getClass().getName();

    private MainActivity activity;
    View rootView;
    private ServiceTask serviceTask;
    private MaterialBetterSpinner type;
    private MaterialEditText date;

    public EditService() {
        // Required empty public constructor
    }

    public EditService(ServiceTask task) {
        serviceTask = task;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_edit_service, container, false);

        initializeType();
        initializeDate();

        return rootView;
    }

    private void initializeType() {
        type = (MaterialBetterSpinner) rootView.findViewById(R.id.task_type);
        if (serviceTask != null && serviceTask.getType() != null)
            type.setText(serviceTask.getType());
    }

    private boolean dateSet = false;
    private long dateLong;
    private DatePickerDialog datePickerDialog;

    private void initializeDate() {
        date = (MaterialEditText) rootView.findViewById(R.id.date);
        rootView.findViewById(R.id.date_click);
        if (serviceTask != null && serviceTask.getDate() != 0) {
            dateLong = serviceTask.getDate();
            setDateDisplay();
        }
    }

    private void loadDateDialog() {
        final Calendar calendar = Calendar.getInstance();
        if (serviceTask != null && serviceTask.getDate() != 0) {
            calendar.setTime(new Date(serviceTask.getDate()));
            setDateDisplay();
        } else {
            date.setText("");
        }

        Log.v(TAG, calendar.get(Calendar.YEAR) + " " +
                calendar.get(Calendar.MONTH) + " " +
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            private String TAG = "OnDateSetListener";

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateSet = true;
                calendar.set(year, monthOfYear, dayOfMonth);
                dateLong = calendar.getTimeInMillis();
                setDateDisplay();
                datePickerDialog.dismiss();
            }
        },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }

    private void setDateDisplay() {
        date.setText(DateFormat.getMediumDateFormat(activity).format(dateLong));
    }
}

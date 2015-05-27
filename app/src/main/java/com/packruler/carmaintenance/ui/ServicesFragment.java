package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.adapters.ServiceRecyclerAdapter;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.util.Calendar;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class ServicesFragment extends Fragment {
    private final String TAG = getClass().getName();

//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private RecyclerView recyclerView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ServiceRecyclerAdapter mAdapter;

    private Vehicle vehicle;
    private MainActivity activity;
    private CarSQL carSQL;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServicesFragment(final MainActivity activity, final Vehicle vehicle, CarSQL carSQL) {
        this.activity = activity;
        this.vehicle = vehicle;
        this.carSQL = carSQL;
        mAdapter = new ServiceRecyclerAdapter(ServicesFragment.this.activity, vehicle.getServiceTaskCursor());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(android.R.id.list);

        long start = Calendar.getInstance().getTimeInMillis();
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);
        long sort = Calendar.getInstance().getTimeInMillis();
        setSortOrder(ServiceTask.DATE, true);
        Log.v(TAG, "Took " + (sort - start) + " to set view and " + (Calendar.getInstance().getTimeInMillis() - sort) + " to get sorted version");

        mAdapter.setOnItemClickListener(new ServiceRecyclerAdapter.onRecyclerItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                long start = Calendar.getInstance().getTimeInMillis();
                Cursor cursor = mAdapter.getItem(position);
                ServiceTask task = new ServiceTask(carSQL, cursor.getLong(0));
                Log.v(TAG, "Loading task took " + (Calendar.getInstance().getTimeInMillis() - start));
                setSortOrder(ServiceTask.DATE, position % 2 == 0);
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setSortOrder(String column, boolean inverse) {
        long start = Calendar.getInstance().getTimeInMillis();
        mAdapter.changeCursor(vehicle.getServiceTaskCursor(column, inverse));
        Log.v(TAG, "Sort took: " + (Calendar.getInstance().getTimeInMillis() - start));
    }
}

package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebHistoryItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.ui.dummy.DummyContent;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.io.InputStream;
import java.security.PublicKey;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

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
    private RecyclerView.Adapter mAdapter;

    private List<ServiceTask> serviceTasks = new ArrayList<>();
    private Vehicle vehicle;
    private MainActivity activity;
    private ThreadPoolExecutor poolExecutor;
    private CarSQL carSQL;
    Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ServicesFragment(Vehicle vehicle, CarSQL carSQL) {
        this.vehicle = vehicle;
        this.carSQL = carSQL;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new Adapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        // Set the adapter
        recyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
        recyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        poolExecutor = ((MainActivity) activity).getPoolExecutor();
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = vehicle.getServiceTaskCursor();
                cursor.moveToFirst();

                int taskNum = 0;
                final String carName = vehicle.getName();
                final int taskCount = vehicle.getServiceTaskCount();
//                if (taskCount > 1500) {
//                    long start = Calendar.getInstance().getTimeInMillis();
//                    carSQL.getWritableDatabase().delete(ServiceTask.TABLE_NAME,
//                            ServiceTask.TASK_NUM + "> " + 1500, null);
//                    Log.v(TAG, "Took " + (Calendar.getInstance().getTimeInMillis() - start) +
//                            " ms to delete " + (taskCount - 1500) + " tasks");
//                }

                Log.v(TAG, "taskCount: " + taskCount);
                final long start = Calendar.getInstance().getTimeInMillis();
                while (taskNum < taskCount) {
                    final int useTask = ++taskNum;
                    serviceTasks.add(new ServiceTask(carSQL, carName, useTask, true));
                    Log.i(TAG, "added task: " + useTask);
                    if (useTask == taskCount)
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.v(TAG, useTask + " Tasks Took " + (Calendar.getInstance().getTimeInMillis() - start) + " to load");
                                recyclerView.getAdapter().notifyDataSetChanged();
                            }
                        });
                }
                serviceTasks = vehicle.getServiceTasks();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView typeDisplay;
            public TextView mileageDisplay;
            public TextView costDisplay;
            public int position;

            public ViewHolder(View v) {
                super(v);
                typeDisplay = (TextView) v.findViewById(R.id.typeDisplay);
                mileageDisplay = (TextView) v.findViewById(R.id.mileageDisplay);
                costDisplay = (TextView) v.findViewById(R.id.costDisplay);
                v.setOnClickListener(onClickListener);
            }

            private View.OnClickListener onClickListener = new View.OnClickListener() {
                private final String TAG = "ViewHolder.onClick";

                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Position clicked: " + position);
                    onItemSelected(position);
                }
            };
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.service_selector, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ServiceTask task = serviceTasks.get(position);
            holder.typeDisplay.setText(task.getType());
            holder.costDisplay.setText(moneyFormat.format(task.getCost()));
            holder.mileageDisplay.setText(task.getMileage() + "");
            holder.position = position;
        }

        @Override
        public int getItemCount() {
            return serviceTasks.size();
        }
    }

    private void onItemSelected(int position) {

    }

}

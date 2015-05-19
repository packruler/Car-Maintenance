package com.packruler.carmaintenance.ui;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.packruler.carmaintenance.R;
import com.packruler.carmaintenance.sql.CarSQL;
import com.packruler.carmaintenance.vehicle.Vehicle;
import com.packruler.carmaintenance.vehicle.maintenence.ServiceTask;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Adapter mAdapter = new Adapter();

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
//                final Cursor cursor = vehicle.getServiceTaskCursor();
//                cursor.moveToFirst();
//
//                int taskNum = 0;
//                String carName = vehicle.getName();
//                int taskCount = vehicle.getServiceTaskCount();
//                if (taskCount > 1500) {
//                    long start = Calendar.getInstance().getTimeInMillis();
                carSQL.getWritableDatabase().delete(ServiceTask.TABLE_NAME,
                        ServiceTask.DATE + "< " + 1000, null);
//                    Log.v(TAG, "Took " + (Calendar.getInstance().getTimeInMillis() - start) +
//                            " ms to delete " + (taskCount - 1500) + " tasks");
//                }
//
//                Log.d(TAG, "taskCount: " + taskCount);
//                final long start = Calendar.getInstance().getTimeInMillis();
//                while (taskNum < taskCount) {
//                    serviceTasks.add(new ServiceTask(carSQL, carName, ++taskNum, true));
//                    Log.v(TAG, "added task: " + taskNum);
//                    if (taskNum == taskCount)
//                        mainHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Log.d(TAG, "Tasks Took " + (Calendar.getInstance().getTimeInMillis() - start) + " to load");
//                                recyclerView.getAdapter().notifyDataSetChanged();
//                            }
//                        });
//                }

                final long start = Calendar.getInstance().getTimeInMillis();
                final Cursor cursor = ServiceTask.getServiceTaskCursorForCar(carSQL, vehicle.getName());

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (cursor.moveToFirst()) {
                            String carName = vehicle.getName();
                            Log.v(TAG, "Cursor size: " + cursor.getCount());
                            while (!cursor.isAfterLast()) {
                                mAdapter.addItem(new ServiceTask(carSQL, carName, cursor.getLong(cursor.getColumnIndex(ServiceTask.DATE)), false));
                                cursor.moveToNext();
                            }
                        }
                        cursor.close();
                        long done = Calendar.getInstance().getTimeInMillis();
                        Log.v(TAG, "Getting list of " + mAdapter.size() +
                                "\nThe whole process took : " + (done - start));
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
        private NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
        private SortedList<ServiceTask> taskList = new SortedList<>(ServiceTask.class, new SortedListAdapterCallback<ServiceTask>(this) {
            private int compareBy = -1;
            public static final int DATE = 0;
            public static final int COST = 1;
            public static final int DATE_INV = -1;
            public static final int COST_INV = -2;

            @Override
            public int compare(ServiceTask o1, ServiceTask o2) {
                switch (compareBy) {
                    default:
                    case DATE_INV:
                        return Float.compare(o2.getDateLong(), o1.getDateLong());
                    case DATE:
                        return Float.compare(o1.getDateLong(), o2.getDateLong());
                    case COST:
                        return Float.compare(o1.getCost(), o2.getCost());
                    case COST_INV:
                        return Float.compare(o2.getCost(), o1.getCost());
                }
            }

            @Override
            public boolean areContentsTheSame(ServiceTask oldItem, ServiceTask newItem) {
                switch (compareBy) {
                    default:
                    case DATE:
                        return oldItem.getDateLong() == newItem.getDateLong();
                    case COST:
                        return oldItem.getCost() == newItem.getCost();
                }
            }

            @Override
            public boolean areItemsTheSame(ServiceTask item1, ServiceTask item2) {
                return item1.equals(item2);
            }
        });

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView typeDisplay;
            public TextView mileageDisplay;
            public TextView costDisplay;
            public TextView dateDisplay;
            public int position;

            public ViewHolder(View v) {
                super(v);
                typeDisplay = (TextView) v.findViewById(R.id.typeDisplay);
                mileageDisplay = (TextView) v.findViewById(R.id.mileageDisplay);
                costDisplay = (TextView) v.findViewById(R.id.costDisplay);
                dateDisplay = (TextView) v.findViewById(R.id.dateDisplay);
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
            ServiceTask task = taskList.get(position);
            holder.typeDisplay.setText(task.getType());
            holder.costDisplay.setText(moneyFormat.format(task.getCost()));
            holder.mileageDisplay.setText("Mileage: " + NumberFormat.getInstance().format(task.getMileage()));
            holder.position = position;
            holder.dateDisplay.setText(DateFormat.getDateFormat(activity).format(task.getDate()));
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        public void addAll(final List<ServiceTask> serviceTasks) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    for (ServiceTask task : serviceTasks) {
                        taskList.add(task);
                    }
                }
            });
        }

        public void addItem(final ServiceTask task) {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    taskList.add(task);
                }
            });
        }

        public ServiceTask getItem(int position) {
            return taskList.get(position);
        }

        public boolean removeItem(ServiceTask task) {
            return taskList.remove(task);
        }

        public int size() {
            return taskList.size();
        }
    }

    private void onItemSelected(int position) {
        ServiceTask task = mAdapter.getItem(position);
        Log.d(TAG, "Date: " + task.getDate() + " dateLong: " + task.getDateLong());
    }

}

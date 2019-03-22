package com.example.androidfinalprojectw18;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class FlightStatusTracker extends AppCompatActivity {
        /**
     * Placeholder for ListView items
     */
    String[] testData = new String[]{"A","B","C","D","E","F","G","H","I"};

    /**
     * Used to simulate progressbar progress until Async functionality implemented
     */
    private Handler hdlr = new Handler();

    /**
     * Starting point for activity launch
     * @param savedInstanceState instance of saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_status_tracker);

        /**
         * Root of layout, used for Snackbars
         */
        CoordinatorLayout coordinatorLayout = findViewById(R.id.flight_tracker_coordinator_layout);

        /**
         * ListView containing items
         */
        ListView list = findViewById(R.id.fight_tracker_listview);
        /**
         * EditText for text input
         */
        EditText editText = findViewById(R.id.fight_tracker_edittext);
        /**
         * Button for user to click
         */
        Button button = findViewById(R.id.fight_tracker_button);
        /**
         * ProgressBar to track async tasks
         */
        ProgressBar progressBar = findViewById(R.id.fight_tracker_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        /**
         * ListAdapter for ListView
         */
        ListAdapter adt = new FlightTrackerListAdapter<>(testData);

        list.setAdapter(adt);

        /**
         * sets click listener for ListView item
         */
        list.setOnItemClickListener((parent,view,position,id)->{
            Snackbar snackBar = Snackbar.make(coordinatorLayout, "You've clicked on item " + position, Snackbar.LENGTH_LONG);
            snackBar.show();
        });

        /**
         * Simulates progression of progressBar (until Async implemented) and displays Toast
         */
        new Thread((new Runnable() {
            @Override
            public void run() {
                for(int i=0;i<100;i++){
                    int finalI = i;
                    hdlr.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(finalI);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        })).start();

        button.setOnClickListener(view -> {
            /**
             * AlertDialog Builder to build AlertDialog
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            /**
             * LayoutInflator to inflate flight_tracker_custom_dialog
             */
            LayoutInflater inflater = getLayoutInflater();

            /**
             * View returned by inflate
             */
            View v = inflater.inflate(R.layout.flight_tracker_custom_dialog,null);

            /**
             * Builds custom dialog box and displays
             */
            builder.setView(v)
                    .setPositiveButton("Positive Button", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create().show();
        });
    }

    /**
     * ListView Adapter
     * @param <E> Generic data type
     */
    protected class FlightTrackerListAdapter<E> extends BaseAdapter{
        /**
         * Reference of data fed into constructor
         */
        private List<E> dataCopy = null;

        /**
         * Constructor for List parameter
         * @param originalData List with which to populate ListView
         */
        public FlightTrackerListAdapter(List<E> originalData){
            this.dataCopy = originalData;
        }

        /**
         * Constructor for Array parameter
         * @param array Array of items to populate ListView
         */
        public FlightTrackerListAdapter(E [] array){
            this.dataCopy = Arrays.asList(array);
        }

        /**
         * Returns count of dataCopy
         * @return size of dataCopy
         */
        @Override
        public int getCount() {
            return dataCopy.size();
        }

        /**
         *
         * @param i position of item in dataCopy
         * @return item at position i
         */
        @Override
        public Object getItem(int i) {
            return dataCopy.get(i);
        }

        /**
         * Stub, using default behaviour until database created
         * @param i position in dataCopy
         * @return itemId
         */
        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            /**
             * LayoutInflator to inflate ListView rows
             */
            LayoutInflater inflater = getLayoutInflater();

            /**
             * View getView will return, recycle views if possible
             */
            TextView root = (TextView) view;

            if(view==null)
                root = (TextView)inflater.inflate(android.R.layout.simple_list_item_1, viewGroup,false);

            /**
             * String to display in row: i
             */
            String toDisplay = getItem(i).toString();

            root.setText(toDisplay);

            return root;
        }
    }
}

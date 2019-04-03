package com.example.androidfinalprojectw18;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import com.example.androidfinalprojectw18.websterdictionary.MerriamWebsterDictionary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
     * ListAdapter for ListView
     */
    ListAdapter adt;
    /**
     * ListView containing items
     */
    ListView listview;

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

        listview = findViewById(R.id.fight_tracker_listview);
        /**
         * ListAdapter for ListView
         */
        //adt =

        //listview.setAdapter(adt);

        DataFetcher networkThread = new DataFetcher();
        networkThread.execute();

        /**
         * sets click listener for ListView item
         */
        listview.setOnItemClickListener((parent,view,position,id)->{
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

        /**
         * Toolbar to navigate to other activities
         */
        Toolbar toolBar = findViewById(R.id.flight_tracker_toolbar);
        setSupportActionBar(toolBar);
    }

    /**
     * Inflates Toolbar Menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /**
         * Menu inflater to inflate menu
         */
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flight_tracker_menu, menu);
        return true;
    }

    /**
     * Handler for menu items
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * intent to navigate to other activities
         */
        Intent intent;
        switch (item.getItemId()){
            case R.id.flight_to_nyt:
                intent = new Intent(this, ArticleSearchNYT.class);
                startActivity(intent);
                break;
            case R.id.flight_to_dictionary:
                intent = new Intent(this, MerriamWebsterDictionary.class);
                startActivity(intent);
                break;
            case R.id.flight_to_newsfeed:
                intent = new Intent(this, NewsFeed.class);
                startActivity(intent);
                break;
            case R.id.flight_help:
                /**
                 * Alert dialog builder for help
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.flight_help_text).create().show();
        }
        return true;
    }

    /**
     * Private class to fetch data from external source
     */
    private class DataFetcher extends AsyncTask<String, Integer, String>{
        /**
         * Array of JSON objects pulled from api
         */
        JSONArray jsonArray;

        @Override
        protected void onPostExecute(String s) {
            /**
             * Listview data
             */
            testData = new String[jsonArray.length()];
            /**
             * Build list for listview adapter
             */
            for(int i=0; i<jsonArray.length();i++){
                /**
                 * temp JSON object to pull info from
                 */
                JSONObject obj = new JSONObject();
                try{obj = jsonArray.getJSONObject(i);} catch (JSONException e){Log.e("Crash!!", e.getMessage());}
                /**
                 * temp JSON object to pull info from
                 */
                JSONObject obj2 = new JSONObject();
                try{obj2 = obj.getJSONObject("flight");} catch (JSONException e){Log.e("Crash!!", e.getMessage());}
                try{testData[i] = (obj2.getString("iataNumber"));}catch (JSONException e){Log.e("Crash!!", e.getMessage());}
            }
//            ((FlightTrackerListAdapter) adt).updateItems(Arrays.asList(testData));
            adt = new FlightTrackerListAdapter<>(testData);
            listview.setAdapter(adt);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        /**
         * Fetches data from api on background thread
         * @param strings
         * @return
         */
        @Override
        protected String doInBackground(String... strings) {
            try{
                /**
                 * URL of api
                 */
                URL url = new URL("http://torunski.ca/flights.json");
                /**
                 * HTTP connection to url
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                /**
                 * input stream
                 */
                InputStream inputStream = connection.getInputStream();

                /**
                 * Buffered reader to build JSON object
                 */
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                /**
                 * StringBuilder to build JSON object
                 */
                StringBuilder stringBuilder = new StringBuilder();

                /**
                 * line read from api
                 */
                String line = null;
                while ((line = reader.readLine())!=null)
                    stringBuilder.append(line + "\n");
                /**
                 * result of JSON object build as string
                 */
                String result = stringBuilder.toString();
                jsonArray = new JSONArray(result);

            }catch (Exception ex){
                Log.e("Crash!!", ex.getMessage());
            }
            return "Finished task";
        }
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

        /**
         * To update listview list
         * @param newList
         */
//        public void updateItems(List<E> newList) {
//            dataCopy.clear();
//            dataCopy.addAll(newList);
//            this.notifyDataSetChanged();
//        }
    }
}

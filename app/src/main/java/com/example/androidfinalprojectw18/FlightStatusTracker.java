package com.example.androidfinalprojectw18;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.example.androidfinalprojectw18.newfeeds.MainActivity;
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
import java.util.List;

public class FlightStatusTracker extends AppCompatActivity {
    /**
     * ListAdapter for list
     */
    ListAdapter adt;
    /**
     * ListView containing items
     */
    ListView listview;

    /**
     * Arrays of JSON objects pulled from api
     */
    JSONArray arrivals, departures;

    /**
     * list for list adapter
     */
    List<Flight> dataCopy = new ArrayList<>();

    /**
     * DB opener
     */
    FlightTrackerDbHelper dbOpener;

    /**
     * SQLLite DB
     */
    SQLiteDatabase db;

    /**
     * Root of layout, used for Snackbars
     */
    CoordinatorLayout coordinatorLayout;

    /**
     * Used to simulate progressbar progress until Async functionality implemented
     */
    private Handler hdlr = new Handler();

    /**
     * Shared Preferences
     */
    SharedPreferences prefs;

    /**
     * EditText for text input
     */
    EditText editText;

    /**
     * Constants for budlepassing to fragment
     */
    public static final String FLIGHT_LOCATION_LATITUDE = "LATITUDE";
    public static final String FLIGHT_LOCATION_LONGITUDE = "LONGITUDE";
    public static final String FLIGHT_ALTITUDE = "ALTITUDE";
    public static final String FLIGHT_STATUS = "STATUS";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final int EMPTY_ACTIVITY = 345;

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
        coordinatorLayout = findViewById(R.id.flight_tracker_coordinator_layout);

        /**
         * EditText for text input
         */
        editText = findViewById(R.id.fight_tracker_edittext);
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
         * Boilerplate code for dealing with database
         */
        dbOpener = new FlightTrackerDbHelper(this);
        db = dbOpener.getWritableDatabase();
        //query all the results from the database:
        String [] columns = {FlightTrackerDbHelper.COL_ID, FlightTrackerDbHelper.COL_FLIGHT_NUMBER, FlightTrackerDbHelper.COL_FLIGHT_LATITUDE,
                FlightTrackerDbHelper.COL_FLIGHT_LONGITUDE,FlightTrackerDbHelper.COL_FLIGHT_ARRIVAL,FlightTrackerDbHelper.COL_FLIGHT_ALTITUDE,
                FlightTrackerDbHelper.COL_FLIGHT_STATUS,};
        Cursor results = db.query(false, FlightTrackerDbHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        //find the column indices:
        int numberColumnIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_FLIGHT_NUMBER);
        int lattitudeColumnIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_FLIGHT_LATITUDE);
        int longitudeColumnIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_FLIGHT_LONGITUDE);
        int arrivalColumnIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_FLIGHT_ARRIVAL);
        int altitudeColumnIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_FLIGHT_ALTITUDE);
        int statusColumnIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_FLIGHT_STATUS);
        int idColIndex = results.getColumnIndex(FlightTrackerDbHelper.COL_ID);
        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String flightNumber= results.getString(numberColumnIndex);
            String arrival= results.getString(arrivalColumnIndex);
            double location_lat= results.getDouble(lattitudeColumnIndex);
            double location_long= results.getDouble(longitudeColumnIndex);
            double altitude= results.getDouble(altitudeColumnIndex);
            String status= results.getString(statusColumnIndex);
            long id = results.getLong(idColIndex);

            //add the new Contact to the array list:
            dataCopy.add(new Flight(flightNumber, arrival,location_lat,location_long,altitude,status, id));
        }

        /**
         * listview and adapter
         */
        listview = findViewById(R.id.fight_tracker_listview);
        adt = new FlightTrackerListAdapter();

        /**
         * whether or not tablet
         */
        boolean isTablet = findViewById(R.id.flight_tracker_fragment) != null;
        listview.setAdapter(adt);

        /**
         * click listener for listview, opens fragment
         */
        listview.setOnItemClickListener( (list, item, position, id) -> {

            /**
             * Bundle to pass to fragment
             */
            Bundle dataToPass = new Bundle();
            dataToPass.putDouble(FLIGHT_LOCATION_LATITUDE, dataCopy.get(position).location_lat);
            dataToPass.putDouble(FLIGHT_LOCATION_LONGITUDE, dataCopy.get(position).location_long);
            dataToPass.putDouble(FLIGHT_ALTITUDE, dataCopy.get(position).altitude);
            dataToPass.putString(FLIGHT_STATUS, dataCopy.get(position).status);
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);

            /**
             * determine if tablet to load activity
             */
            if(isTablet)
            {
                /**
                 * DetailFragment to open if tablet
                 */
                FlightTrackerDetailFragment dFragment = new FlightTrackerDetailFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.flight_tracker_fragment, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                /**
                 * Load Empty Activity which will load fragment
                 */
                Intent nextActivity = new Intent(this, FlightTrackerEmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, EMPTY_ACTIVITY); //make the transition
            }
        });

        /**
         * click listener for button, searches for flights
         */
        button.setOnClickListener(view -> {
            /**
             * urls for api call
             */
            String urls[] = {"http://aviation-edge.com/v2/public/flights?key=1bfd01-eb0bba&arrIata=",
            "http://aviation-edge.com/v2/public/flights?key=1bfd01-eb0bba&depIata="};

            /**
             * appends airport code to urls
             */
            for(int i=0;i<urls.length;i++){
                urls[i] = urls[i].concat(editText.getText().toString().toUpperCase());
            }
            /**
             * Create new DataFetcher
             */
            DataFetcher networkThread = new DataFetcher();
            /**
             * make api call as separate thread
             */
            networkThread.execute(urls);

            Snackbar.make(coordinatorLayout, "Fetching flight data",Snackbar.LENGTH_LONG).show();
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

        prefs = getSharedPreferences("flightTrackerSharedPrefs",MODE_PRIVATE);
        String airportCode = prefs.getString("airportCode", null);
        if(airportCode!="")
            editText.setText(airportCode);

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
     * To store shared preferences
     */
    @Override
    protected void onPause(){
        super.onPause();
        prefs = getSharedPreferences("flightTrackerSharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("airportCode", editText.getText().toString());
        editor.commit();
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
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.flight_help:
                /**
                 * Alert dialog builder for help
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                //builder.setMessage(R.string.flight_help_text).create().show();
                builder.setMessage(R.string.flight_help_text)
                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "You dismissed help", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create().show();
        }
        return true;
    }

    /**
     * Private class to fetch data from external source
     */
    private class DataFetcher extends AsyncTask<String, Integer, String>{


        @Override
        protected void onPostExecute(String s) {
            /**
             * memebers of Flight class
             */
            String flightNumber;
            String arrival;
            double location_lat;
            double location_long;
            double altitude;
            String status;

            dataCopy.clear();
            for(int i=0;i<arrivals.length();i++){
                /**
                 * temp JSON object to pull from
                 */
                JSONObject obj = new JSONObject();
                try{obj = arrivals.getJSONObject(i);} catch (JSONException e){Log.e("Crash!!", e.getMessage());}

                try{
                    /**
                     * temp JSON objects to pull from
                     */
                    JSONObject geo, flight, arive;
                    geo = obj.getJSONObject("geography");
                    location_lat = geo.getDouble("latitude");
                    location_long = geo.getDouble("longitude");
                    altitude = geo.getDouble("altitude");
                    flight = obj.getJSONObject("flight");
                    flightNumber = flight.getString("number");
                    status = obj.getString("status");
                    arive = obj.getJSONObject("arrival");
                    arrival = arive.getString("iataCode");
                    ContentValues newRowValues = new ContentValues();
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_ALTITUDE, altitude);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_ARRIVAL, arrival);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_LATITUDE, location_lat);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_LONGITUDE, location_long);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_NUMBER, flightNumber);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_STATUS, status);
                    long newId = db.insert(FlightTrackerDbHelper.TABLE_NAME, null, newRowValues);

                    dataCopy.add(new Flight (flightNumber,  arrival,  location_lat, location_long, altitude, status, newId));
                } catch (JSONException e){Log.e("Crash!!", e.getMessage());}
            }

            for(int i=0;i<departures.length();i++){
                /**
                 * temp JSON object to pull from
                 */
                JSONObject obj = new JSONObject();
                try{obj = departures.getJSONObject(i);} catch (JSONException e){Log.e("Crash!!", e.getMessage());}

                try{
                    /**
                     * temp JSON object to pull from
                     */
                    JSONObject geo, flight, arive;
                    geo = obj.getJSONObject("geography");
                    location_lat = geo.getDouble("latitude");
                    location_long = geo.getDouble("longitude");
                    altitude = geo.getDouble("altitude");
                    flight = obj.getJSONObject("flight");
                    flightNumber = flight.getString("number");
                    status = obj.getString("status");
                    arive = obj.getJSONObject("arrival");
                    arrival = arive.getString("iataCode");
                    ContentValues newRowValues = new ContentValues();
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_ALTITUDE, altitude);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_ARRIVAL, arrival);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_LATITUDE, location_lat);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_LONGITUDE, location_long);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_NUMBER, flightNumber);
                    newRowValues.put(FlightTrackerDbHelper.COL_FLIGHT_STATUS, status);
                    long newId = db.insert(FlightTrackerDbHelper.TABLE_NAME, null, newRowValues);
                    dataCopy.add(new Flight (flightNumber,  arrival,  location_lat, location_long, altitude, status, newId));
                } catch (JSONException e){Log.e("Crash!!", e.getMessage());}
            }
            ((FlightTrackerListAdapter) adt).notifyDataSetChanged();
//            adt = new FlightTrackerListAdapter();
//            listview.setAdapter(adt);
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
            /**
             * get flight data
             */
            arrivals = jsonArrayMaker(strings[0]);
            departures = jsonArrayMaker(strings[1]);
            return "Finished task";
        }
    }

    /**
     * ListView Adapter
     * @param <E> Generic data type
     */
    protected class FlightTrackerListAdapter<E> extends BaseAdapter {
//        /**
//         * Reference of data fed into constructor
//         */
//        private List<E> dataCopy = null;
//
//        /**
//         * Constructor for List parameter
//         *
//         * @param originalData List with which to populate ListView
//         */
//        public FlightTrackerListAdapter(List<E> originalData) {
//            this.dataCopy = originalData;
//        }
//
//        /**
//         * Constructor for Array parameter
//         *
//         * @param array Array of items to populate ListView
//         */
//        public FlightTrackerListAdapter(E[] array) {
//            this.dataCopy = Arrays.asList(array);
//        }
//
//        /**
//         * Returns count of dataCopy
//         *
//         * @return size of dataCopy
//         */
        @Override
        public int getCount() {
            return dataCopy.size();
        }

        /**
         * @param i position of item in dataCopy
         * @return item at position i
         */
        @Override
        public Object getItem(int i) {
            return dataCopy.get(i);
        }

        /**
         * Stub, using default behaviour until database created
         *
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
             * row to be inflated
             */
            View row;

            /**
             * Textviews into which list data is copied
             */
            TextView flightNumber, flightArrival;

            row = inflater.inflate(R.layout.flight_tracker_list_row, viewGroup, false);
            flightNumber = row.findViewById(R.id.flight_number);
            flightArrival = row.findViewById(R.id.flight_arrival);

            flightNumber.setText(dataCopy.get(i).flightNumber);
            flightArrival.setText(dataCopy.get(i).arrival);
            return row;
        }
    }

    JSONArray jsonArrayMaker(String apiUrl){
        try{
            /**
             * URL of api
             */
            URL url = new URL(apiUrl);
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
            JSONArray jsonArray = new JSONArray(result);
            return jsonArray;
        }catch (Exception ex) {
            Log.e("Crash!!", ex.getMessage());
        }
        return null;
    }

    /**
     * Class to hold flight data
     */
    protected class Flight{
        /**
         * class members
         */
        String flightNumber;
        String arrival;
        double location_lat;
        double location_long;
        double altitude;
        String status;
        long id;

        /**
         * constructor
         * @param flightNumber
         * @param arrival
         * @param location_lat
         * @param location_long
         * @param altitude
         * @param status
         */
        public Flight(String flightNumber, String arrival, double location_lat,double location_long,double altitude,String status, Long id){
            this.flightNumber = flightNumber;
            this.arrival = arrival;
            this.location_lat = location_lat;
            this.location_long = location_long;
            this.altitude = altitude;
            this.status = status;
            this.id = id;
        }
    }

    /**
     * Boiler plate code
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == EMPTY_ACTIVITY)
        {
            if(resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(ITEM_ID, 0);
                deleteMessageId((int)id);
            }
        }
    }

    /**
     * deletes item from listview
     * @param id
     */
    public void deleteMessageId(int id)
    {
        Log.i("Delete this message:" , " id="+id);
        dataCopy.remove(id);
        ((FlightTrackerListAdapter) adt).notifyDataSetChanged();
    }

}
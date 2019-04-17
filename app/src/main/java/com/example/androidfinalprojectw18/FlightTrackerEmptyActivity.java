package com.example.androidfinalprojectw18;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.androidfinalprojectw18.FlightTrackerDetailFragment;
import com.example.androidfinalprojectw18.R;

/**
 * Boiler plate code for starting fragment on phone
 */
public class FlightTrackerEmptyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flight_tracker_empty);

        /**
         * bundle to pass to fragment
         */
        Bundle dataToPass = getIntent().getExtras();

        /**
         * initialize new FlightTrackerDetailFragment
         */
        FlightTrackerDetailFragment dFragment = new FlightTrackerDetailFragment();
        dFragment.setArguments(dataToPass);
        dFragment.setTablet(false);
        /**
         * start fragment
         */
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.flight_tracker_fragment, dFragment)
                //.addToBackStack("AnyName")
                .commit();
    }
}
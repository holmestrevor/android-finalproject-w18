package com.example.androidfinalprojectw18.newyorktimes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.androidfinalprojectw18.R;

public class Empty extends AppCompatActivity {

    /**
     * onCreate method for the activity with no Fragment
     * From Lab 7 by Professor @etorunski
     * https://github.com/etorunski/InClassExamples_W19/tree/week8
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newyork_empty);

        Bundle dataToPass = getIntent().getExtras(); //get the data that was passed from FragmentExample

        //This is copied directly from FragmentExample.java lines 47-54
        Fragment dFragment = new Fragment();
        dFragment.setArguments( dataToPass ); //pass data to the the fragment
        dFragment.setTablet(false); //tell the Fragment that it's on a phone.
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nytimes_fragment, dFragment)
                .addToBackStack("AnyName")
                .commit();
    }//end onCreate
}//end class

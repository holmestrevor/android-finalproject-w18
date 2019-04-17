package com.example.androidfinalprojectw18.websterdictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.androidfinalprojectw18.R;

public class MerriamEmpty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_empty);

        Bundle dataToPass = getIntent().getExtras();

        WebsterFragment fragment = new WebsterFragment();
        fragment.setArguments(dataToPass);
        fragment.setTablet(true);
        getSupportFragmentManager()
                .beginTransaction();

    }
}

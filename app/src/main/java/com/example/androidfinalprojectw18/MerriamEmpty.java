package com.example.androidfinalprojectw18;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MerriamEmpty extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_empty);

        Bundle dataToPass = getIntent().getExtras();


    }
}

package com.example.androidfinalprojectw18.websterdictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.androidfinalprojectw18.R;

public class MerriamWebsterDictionary extends AppCompatActivity {

    ListView words;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_webster_dictionary);
    }
}

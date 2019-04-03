package com.example.androidfinalprojectw18;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidfinalprojectw18.websterdictionary.MerriamWebsterDictionary;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;
    TextView percent_TextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.main_progressBar);
        percent_TextView = findViewById(R.id.percent_textView);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);

        progressAnimation();

    }

    public void progressAnimation(){
        ProgressBarAnimation animation = new ProgressBarAnimation(this, progressBar, percent_TextView, 0f, 100f);
        animation.setDuration(3000);
        progressBar.setAnimation(animation);
    }

}

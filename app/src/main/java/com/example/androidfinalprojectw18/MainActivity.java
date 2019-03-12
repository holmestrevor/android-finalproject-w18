package com.example.androidfinalprojectw18;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Launches one of the four activities, based on what button was pressed.
     * @param view - The button being pressed
     */
    public void launchActivity(View view) {
        Button b = (Button)view;
        Intent i;
        switch(b.getId()) {
            case R.id.websterButton:
                i = new Intent(this, MerriamWebsterDictionary.class);
                break;
            case R.id.newsFeedButton:
                i = new Intent(this, NewsFeed.class);
                break;
            case R.id.flightStatusButton:
                i = new Intent(this, FlightStatusTracker.class);
                break;
            case R.id.articleSearchButton:
                i = new Intent(this, ArticleSearchNYT.class);
                break;
            default:
                i = new Intent(this, MerriamWebsterDictionary.class);
        }
        startActivity(i);
    }
}

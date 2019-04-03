package com.example.androidfinalprojectw18;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
    }

    /**
     * Launches one of the four activities, based on what button was pressed.
     * @param view - The button being pressed
     */
    public void launchActivity(View view) {
        Button b = (Button)view;
        Intent i;
        switch(b.getId()) {
            case R.id.dictionary_btn:
                i = new Intent(this, MerriamWebsterDictionary.class);
                break;
            case R.id.feed_btn:
                i = new Intent(this, NewsFeed.class);
                break;
            case R.id.flight_btn:
                i = new Intent(this, FlightStatusTracker.class);
                break;
            case R.id.search_btn:
                i = new Intent(this, ArticleSearchNYT.class);
                break;
            default:
                i = new Intent(this, MerriamWebsterDictionary.class);
        }
        startActivity(i);
    }
}

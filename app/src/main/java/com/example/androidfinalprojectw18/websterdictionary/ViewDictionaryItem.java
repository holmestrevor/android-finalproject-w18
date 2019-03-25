package com.example.androidfinalprojectw18.websterdictionary;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;

public class ViewDictionaryItem extends AppCompatActivity {

    TextView word, pronunciation, definitions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary_item);

        Intent i = getIntent();

        word = (TextView)findViewById(R.id.wordExpanded);
        pronunciation = (TextView)findViewById(R.id.pronunciationExpanded);
        definitions = (TextView)findViewById(R.id.definitionsExpanded);
        word.setText(i.getStringExtra("word"));
        pronunciation.setText(i.getStringExtra("pronunciation"));

        String s = "";
        //Formatting the definitions to be placed into the TextView
        for(int j=0; j<i.getIntExtra("definitionCount", 0); j++) {
            s += j+1 + ": " + i.getStringExtra("definition" + j) + "\n";
        }
        definitions.setText(s);

    }
}

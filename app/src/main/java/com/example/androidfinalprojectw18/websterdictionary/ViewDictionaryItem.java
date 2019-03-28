package com.example.androidfinalprojectw18.websterdictionary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;

public class ViewDictionaryItem extends AppCompatActivity {

    TextView word, pronunciation, definitions;
    Button saveItem;
    DBOpener dbOpener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary_item);

        Intent i = getIntent();

        word = (TextView)findViewById(R.id.wordExpanded);
        pronunciation = (TextView)findViewById(R.id.pronunciationExpanded);
        definitions = (TextView)findViewById(R.id.definitionsExpanded);

        //Check if we are looking at a saved word
        if(i.getBooleanExtra("fromSaved", false)) {
            word.setText(i.getStringExtra("word"));
            pronunciation.setText(i.getStringExtra("pronunciation"));

            String s = "";
            //Formatting the definitions to be placed into the TextView
            for(int j=0; j<i.getIntExtra("definitionCount", 0); j++) {
                s += j+1 + ": " + i.getStringExtra("definition" + j) + "\n";
            }
            definitions.setText(s);
        //Otherwise, we are accessing a searched item
        } else {
            String search = i.getStringExtra("searchWord").trim();
            String url = "https://www.dictionaryapi.com/api/v1/references/sd3/xml/" + search + "?key=4556541c-b8ed-4674-9620-b6cba447184f";
        }

    }
}

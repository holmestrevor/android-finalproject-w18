package com.example.androidfinalprojectw18.websterdictionary;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.listview.DictionaryItem;
import com.example.androidfinalprojectw18.websterdictionary.listview.DictionaryItemAdapter;

import java.util.ArrayList;

public class MerriamWebsterDictionary extends AppCompatActivity {

    //ListView containing dictionary items
    ListView wordList;
    //Progress bar
    ProgressBar progressBar;
    //ArrayList containing dictionary objects, to be passed into ListView
    ArrayList<DictionaryItem> words;
    //Toolbar
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_webster_dictionary);

        progressBar = (ProgressBar)findViewById(R.id.dictionaryProgressBar);
        toolbar = (Toolbar)findViewById(R.id.dictionaryToolbar);
        setSupportActionBar(toolbar);

        words = new ArrayList<>();

        words.add(new DictionaryItem());
        words.add(new DictionaryItem());

        ArrayAdapter adt = new DictionaryItemAdapter(this, words);
        wordList = findViewById(R.id.wordList);
        if(words!=null) {
            wordList.setAdapter(adt);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dictionary_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.dictionarySearch:
                break;
            case R.id.helpButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Webster Dictionary\nAuthor: Trevor Holmes\n\nFrom this page, you can search Merriam Webster for words by touching the magnifying glass in the top right and entering your word.\nYou can also view words you have saved; tap on them to get more details.")
                        .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Do nothing
                            }
                        }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.example.androidfinalprojectw18.websterdictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
    //ArrayAdapter for the ListView.
    ArrayAdapter adt;

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

        adt = new DictionaryItemAdapter(this, words);
        wordList = findViewById(R.id.wordList);
        if(words!=null) {
            wordList.setAdapter(adt);
        }
        /*Setting the onItemClickListener for the ListView. The attributes of the dictionary item
        will be passed to the next activity as intent items.
        */
        wordList.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(MerriamWebsterDictionary.this, ViewDictionaryItem.class);
            i.putExtra("word", words.get(position).getWord());
            i.putExtra("pronunciation", words.get(position).getPronunciation());
            i.putExtra("definitionCount", words.get(position).getDefinitions().length);
            for(int j=0; j<words.get(position).getDefinitions().length; j++) {
                i.putExtra("definition" + (j), words.get(position).getDefinitions()[j]);
            }
            startActivity(i);
        });
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
                builder.setMessage(R.string.helpText)
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

    /**
     * Removes all the Dictionary items from the ListView, as well as dropping them from the SQLite database.
     * @param view parent layout.
     */
    public void clearDictionaryItems(View view) {
        LinearLayout linearLayout = (LinearLayout)view;
        adt.clear();
        adt.notifyDataSetChanged();
        Toast.makeText(this, "All items successfully deleted.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Refreshes the items in the ListView, if any changes have been made.
     * @param view
     */
    public void refreshItems(View view) {
        adt.notifyDataSetChanged();
        Snackbar.make(view, "Items were refreshed.", Snackbar.LENGTH_SHORT)
                .setAction("Okay", b -> {
                    //Do nothing
                }).show();
    }
}

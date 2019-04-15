package com.example.androidfinalprojectw18.websterdictionary;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.SearchView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.ArticleSearchNYT;
import com.example.androidfinalprojectw18.FlightStatusTracker;
import com.example.androidfinalprojectw18.NewsFeed;
import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;
import com.example.androidfinalprojectw18.websterdictionary.listview.DictionaryItem;
import com.example.androidfinalprojectw18.websterdictionary.listview.DictionaryItemAdapter;

import java.nio.channels.NotYetBoundException;
import java.util.ArrayList;

public class MerriamWebsterDictionary extends AppCompatActivity {

    //ListViews containing dictionary items & recently searched items respectively
    ListView wordList, recentList;
    //ArrayList containing dictionary objects, to be passed into ListView
    ArrayList<DictionaryItem> words;
    //Toolbar
    Toolbar toolbar;
    //ArrayAdapter for the words ListView.
    ArrayAdapter adt;
    //SharedPreferences object to hold the most recently searched word
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_webster_dictionary);
        toolbar = (Toolbar)findViewById(R.id.dictionaryToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Fetching the ArrayList items from the SQLite database
        words = loadItems();

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
            i.putExtra("definitions", words.get(position).getDefinitions());
            i.putExtra("fromSaved", true);
            startActivity(i);
        });

        recentList = findViewById(R.id.recentList);
        preferences = getSharedPreferences("webster", MODE_PRIVATE);

        recentList.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = new Intent(MerriamWebsterDictionary.this, ViewDictionaryItem.class);
            i.putExtra("word", preferences.getString("word", ""));
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dictionary_menu, menu);
        MenuItem search = menu.findItem(R.id.dictionarySearch);
        SearchView searchView = (SearchView)search.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("word", query);
                editor.apply();
                Intent i = new Intent(MerriamWebsterDictionary.this, ViewDictionaryItem.class);
                i.putExtra("searchWord", query);
                i.putExtra("fromSaved", false);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch(item.getItemId()) {
            case R.id.dictionarySearch:
                break;
            case R.id.helpButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.help_text)
                        .setPositiveButton("Okay", (dialog, which) -> {
                            //Do nothing
                        }).show();
                break;
            case R.id.dictionary_to_flight:
                i = new Intent(MerriamWebsterDictionary.this, FlightStatusTracker.class);
                startActivity(i);
                break;
            case R.id.dictionary_to_newsfeed:
                i = new Intent(MerriamWebsterDictionary.this, NewsFeed.class);
                startActivity(i);
                break;
            case R.id.dictionary_to_nyt:
                i = new Intent(MerriamWebsterDictionary.this, ArticleSearchNYT.class);
                startActivity(i);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage(R.string.delete_dialog)
                .setPositiveButton(R.string.delete_confirm, (dialog, which) -> {
                    SQLiteOpenHelper dbOpener = new DBOpener(MerriamWebsterDictionary.this);
                    SQLiteDatabase db = dbOpener.getReadableDatabase();
                    db.delete(DBOpener.TABLE1_NAME, null, null);
                    adt.clear();
                    adt.notifyDataSetChanged();
                    Toast.makeText(view.getContext(), getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                    db.close();
                    dbOpener.close();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {
                    //Do nothing
                }).show();
    }

    /**
     * Refreshes the items in the ListView, if any changes have been made.
     * @param view
     */
    public void refreshItems(View view) {
        words = loadItems();
        adt.notifyDataSetChanged();
        Snackbar.make(view, "Items were refreshed.", Snackbar.LENGTH_SHORT)
                .setAction("Okay", b -> {
                    //Do nothing
                }).show();
    }

    /**
     * Fetches saved dictionary items from the SQLite Database
     * @return ArrayList of DictionaryItems
     */
    public ArrayList<DictionaryItem> loadItems() {
        ArrayList<DictionaryItem> items = new ArrayList<>();
        String word, pronunciation;
        String[] definitions = new String[5];
        SQLiteOpenHelper dbOpener = new DBOpener(this);
        SQLiteDatabase db = dbOpener.getReadableDatabase();

        //SELECT * FROM DictionaryItem
        Cursor c = db.query(false, DBOpener.TABLE1_NAME, new String[]{
                DBOpener.COL_ID,
                DBOpener.COL_WORD,
                DBOpener.COL_PRONUNCIATION,
                DBOpener.COL_DEFINITION0,
                DBOpener.COL_DEFINITION1,
                DBOpener.COL_DEFINITION2,
                DBOpener.COL_DEFINITION3,
                DBOpener.COL_DEFINITION4
        }, null, null, null, null, null, null);

        DBOpener.printCursor(c);

        while(c.moveToNext()) {
            word = c.getString(c.getColumnIndex(DBOpener.COL_WORD));
            pronunciation = c.getString(c.getColumnIndex(DBOpener.COL_PRONUNCIATION));
            long id = c.getLong(c.getColumnIndex(DBOpener.COL_ID));
            definitions[0] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION0)));
            definitions[1] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION1)));
            definitions[2] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION2)));
            definitions[3] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION3)));
            definitions[4] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION4)));

            items.add(new DictionaryItem(word, pronunciation, definitions));
            definitions = new String[5];
        }
        db.close();
        dbOpener.close();
        return items;
    }



}

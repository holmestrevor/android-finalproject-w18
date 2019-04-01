package com.example.androidfinalprojectw18.websterdictionary;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;
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
        toolbar = (Toolbar)findViewById(R.id.dictionaryToolbar);
        setSupportActionBar(toolbar);

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
            i.putExtra("definitionCount", words.get(position).getDefinitions().length);
            for(int j=0; j<words.get(position).getDefinitions().length; j++) {
                i.putExtra("definition" + (j), words.get(position).getDefinitions()[j]);
            }
            i.putExtra("fromSaved", true);
            startActivity(i);
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
        alertDialog.setMessage("Are you sure you want to delete all items? This process cannot be undone.")
                .setPositiveButton("Yes, I'm sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteOpenHelper dbOpener = new DBOpener(MerriamWebsterDictionary.this);
                        SQLiteDatabase db = dbOpener.getReadableDatabase();
                        db.delete(DBOpener.TABLE2_NAME, null, null);
                        db.delete(DBOpener.TABLE1_NAME, null, null);
                        adt.clear();
                        adt.notifyDataSetChanged();
                        Toast.makeText(view.getContext(), "All items successfully deleted", Toast.LENGTH_SHORT).show();
                        db.close();
                        dbOpener.close();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing
                    }
                }).show();
    }

    /**
     * Refreshes the items in the ListView, if any changes have been made.
     * @param view
     */
    public void refreshItems(View view) {
        loadItems();
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
        String word = "", pronunciation = "";
        String[] definitions = new String[1];
        SQLiteOpenHelper dbOpener = new DBOpener(this);
        SQLiteDatabase db = dbOpener.getReadableDatabase();

        db.delete(DBOpener.TABLE2_NAME, null, null);
        db.delete(DBOpener.TABLE1_NAME, null, null);

        /*
        This section inserts sample items into the database when it loads.
         */
        Cursor c = db.rawQuery("SELECT * FROM " + DBOpener.TABLE1_NAME, null);
        ((DBOpener) dbOpener).printCursor(c);
        c = db.rawQuery("SELECT * FROM " + DBOpener.TABLE2_NAME, null);
        ((DBOpener) dbOpener).printCursor(c);

//        ContentValues cv = new ContentValues();
//        cv.put(DBOpener.COL_WORD, "Cromulent");
//        cv.put(DBOpener.COL_PRONUNCIATION, "Exactly as it sounds");
//
//        long id = db.insert(DBOpener.TABLE1_NAME, null, cv);
//
//        cv.put(DBOpener.COL_DEFINITION, "Agreeable or correct");
//        cv.put(DBOpener.COL_ITEM_ID, id);
//
//        db.insert(DBOpener.TABLE2_NAME, null, cv);

        while(c.moveToNext()) {
            word = c.getString(c.getColumnIndex(DBOpener.COL_WORD));
            pronunciation = c.getString(c.getColumnIndex(DBOpener.COL_PRONUNCIATION));
            long id = c.getLong(c.getColumnIndex(DBOpener.COL_ID));
            //For each dictionary item, we call a "Sub-query" to find the descriptions.
            Cursor d = db.rawQuery("SELECT * FROM " + DBOpener.TABLE2_NAME + " WHERE " + DBOpener.COL_ITEM_ID + " = ?", new String[] { String.valueOf(id) });
            int i = 0;
            while(d.moveToNext()) {
                definitions = new String[d.getColumnCount()];
                //Insert the definition into the string
                definitions[i] = d.getString(d.getColumnIndex(DBOpener.COL_DEFINITION));
                i++;
            }
            items.add(new DictionaryItem(word, pronunciation, definitions));
            d.close();
        }
        c.close();
        db.close();
        dbOpener.close();
        return items;
    }
}

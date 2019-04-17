package com.example.androidfinalprojectw18.websterdictionary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.ArticleSearchNYT;
import com.example.androidfinalprojectw18.FlightStatusTracker;
import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.newfeeds.MainActivity;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;
import com.example.androidfinalprojectw18.websterdictionary.listview.DictionaryItem;
import com.example.androidfinalprojectw18.websterdictionary.listview.DictionaryItemAdapter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MerriamWebsterDictionary extends AppCompatActivity {

    //ListViews containing dictionary items & recently searched items respectively
    ListView wordList;
    TextView recentList;
    //ArrayList containing dictionary objects, to be passed into ListView
    ArrayList<DictionaryItem> words;
    //ArrayList containing one item; the most recently searched word.
    List<String> recentWord = new ArrayList<String>(1);
    //Toolbar
    Toolbar toolbar;
    //ArrayAdapter for the words ListView.
    ArrayAdapter adt;
    //SharedPreferences object to hold the most recently searched word
    SharedPreferences preferences;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        recentList.setText(preferences.getString("word", ""));
        //Deleting an item
        if(requestCode==15) {
            if(resultCode==RESULT_OK) {
                long id = data.getLongExtra(DBOpener.COL_ID, 0);
                deleteMessage(id);
                Toast.makeText(this, "Item successfully deleted.", Toast.LENGTH_SHORT).show();
            }
        }
        //An item was saved, refresh items
        if(requestCode==30) {
            if(resultCode==RESULT_OK){
                refreshItems(wordList);
            }
        }
    }

    /**
     * Deletes a message in the ListView.
     * @param id Database ID of the element
     */
    public void deleteMessage(long id) {
        Iterator<DictionaryItem> iter = words.iterator();
        while (iter.hasNext()) {
            DictionaryItem d = iter.next();
            if(d.getId()==id) {
                iter.remove();
            }
        }
        adt.notifyDataSetChanged();
        SQLiteOpenHelper dbOpener = new DBOpener(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.delete(DBOpener.TABLE1_NAME, DBOpener.COL_ID + " = ?", new String[]{String.valueOf(id)});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merriam_webster_dictionary);

        boolean isTablet = findViewById(R.id.fragmentPosition) != null;

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

            Bundle dataToPass = new Bundle();
            long itemId = ((DictionaryItem)wordList.getItemAtPosition(position)).getId();
            dataToPass.putLong(DBOpener.COL_ID, itemId);
            dataToPass.putBoolean("fromSaved", true);

            if(isTablet) {
                WebsterFragment fragment = new WebsterFragment();
                fragment.setArguments(dataToPass);
                fragment.setTablet(true);
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.tabletFragmentPos, fragment)
                        .addToBackStack("AnyName")
                        .commit();
            } else {
                Intent i = new Intent(MerriamWebsterDictionary.this, MerriamEmpty.class);
                i.putExtras(dataToPass);
                startActivityForResult(i, 15);
            }
        });

        recentList = findViewById(R.id.recentList);
        preferences = getSharedPreferences("webster", MODE_PRIVATE);
        recentList.setText(preferences.getString("word", ""));
        recentList.setVisibility(View.VISIBLE);

//        recentList.setOnItemClickListener((parent, view, position, id) -> {
//            Intent i = new Intent(MerriamWebsterDictionary.this, ViewDictionaryItem.class);
//            Bundle dataToPass = new Bundle();
//            dataToPass.putBoolean("fromSaved", false);
//            dataToPass.putLong(DBOpener.COL_ID, words.get(position).getId());
//            i.putExtras(dataToPass);
//            startActivity(i);
//        });

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
                searchView.clearFocus();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("word", query);
                editor.apply();
                Intent i = new Intent(MerriamWebsterDictionary.this, MerriamEmpty.class);
                Bundle dataToPass = new Bundle();
                dataToPass.putString("searchWord", query);
                dataToPass.putBoolean("fromSaved", false);
                i.putExtras(dataToPass);
                startActivityForResult(i, 30);
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
                i = new Intent(MerriamWebsterDictionary.this, MainActivity.class);
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
        words.clear();
        words.addAll(loadItems());
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
        Cursor c = db.rawQuery("SELECT * FROM " + DBOpener.TABLE1_NAME, null, null);

        DBOpener.printCursor(c);

        if(c.getCount()>0) {
            do {
                word = c.getString(c.getColumnIndex(DBOpener.COL_WORD));
                pronunciation = c.getString(c.getColumnIndex(DBOpener.COL_PRONUNCIATION));
                long id = c.getLong(c.getColumnIndex(DBOpener.COL_ID));
                definitions[0] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION0)));
                definitions[1] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION1)));
                definitions[2] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION2)));
                definitions[3] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION3)));
                definitions[4] = c.getString(c.getColumnIndex((DBOpener.COL_DEFINITION4)));

                items.add(new DictionaryItem(word, pronunciation, definitions));
                items.get(items.size()-1).setId(id);
                definitions = new String[5];
            } while(c.moveToNext());
        }

        db.close();
        dbOpener.close();
        return items;
    }



}

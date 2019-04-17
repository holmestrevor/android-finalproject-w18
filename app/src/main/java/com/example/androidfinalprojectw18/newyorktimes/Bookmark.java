package com.example.androidfinalprojectw18.newyorktimes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidfinalprojectw18.FlightStatusTracker;
import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.MerriamWebsterDictionary;

import java.util.ArrayList;

public class Bookmark extends AppCompatActivity implements StoryAdapter.AdapterCallback {

    private int mStoryItemCount = 0;//to count item on toolbar
    TextView textStoryItemCount; //textview on toolbar
    ListView listView;
    ProgressBar progressBar;
    ArrayList<StoryModel> storyModelArrayList;
    int positionClicked = 0;
    StoryAdapter adt;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newyork_bookmark);

        //Save file for send back to mainactivty and search
        sp = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        storyModelArrayList = new ArrayList<StoryModel>();

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setTitle("New York Times");
        mTitle.setText(toolbar.getTitle());
        //click Toolbar title to go back Main Activity
        mTitle.setOnClickListener(click -> {
            startActivity(new Intent(this, MainActivity.class));
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }//arrow icon to back to previous activity
        //back to previous activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(40);
                System.out.println("finish");
                finish();
            }
        });
        //finish setup toolbar

        //get a database, copy from Lab 5
        StoryDBHelper dbOpener = new StoryDBHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();

        //query all the results from the database:
        String[] columns = {StoryDBHelper.COL_ID, StoryDBHelper.COL_TITLE, StoryDBHelper.COL_AUTHOR, StoryDBHelper.COL_HEADLINE, StoryDBHelper.COL_URL, StoryDBHelper.COL_IMAGEURL};
        Cursor results = db.query(false, StoryDBHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int idIndex = results.getColumnIndex(StoryDBHelper.COL_ID);
        int titleIndex = results.getColumnIndex(StoryDBHelper.COL_TITLE);
        int authorIndex = results.getColumnIndex(StoryDBHelper.COL_AUTHOR);
        int headLineIndex = results.getColumnIndex(StoryDBHelper.COL_HEADLINE);
        int urlIndex = results.getColumnIndex(StoryDBHelper.COL_URL);
        int imageUrlIndex = results.getColumnIndex(StoryDBHelper.COL_IMAGEURL);

        //iterate over the results, return true if there is a next item:
        while (results.moveToNext()) {
            String title = results.getString(titleIndex);
            String author = results.getString(authorIndex);
            String headLine = results.getString(headLineIndex);
            String url = results.getString(urlIndex);
            String imageUrl = results.getString(imageUrlIndex);
            Long id = results.getLong(idIndex);

            //add the new Contact to the array list:
            storyModelArrayList.add(new StoryModel(id, title, headLine, author, url, imageUrl));
        }

        //create an adapter object and send it to the listVIew
        adt = new StoryAdapter(this, storyModelArrayList);
        listView.setAdapter(adt);


        //listview item click action
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Bundle dataToPass = new Bundle();
            dataToPass.putString("title", storyModelArrayList.get(position).getTitle());
            dataToPass.putString("author", storyModelArrayList.get(position).getAuthor());
            dataToPass.putString("headLine", storyModelArrayList.get(position).getHeadLine());
            dataToPass.putString("url", storyModelArrayList.get(position).getUrl());
            dataToPass.putString("imageUrl", storyModelArrayList.get(position).getImageURL());
            dataToPass.putLong("id", storyModelArrayList.get(position).getId());
            //go to Empty then to Fragment
            Intent nextActivity = new Intent(this, Empty.class);
            nextActivity.putExtras(dataToPass);
            startActivityForResult(nextActivity, 25);
        });
        setupBadge();
    }//finish onCreate

    /**
     * Add items to menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.newyorktimes_main_menu, menu);

        final MenuItem menuItem = menu.findItem(R.id.bookmark);

        View actionView = MenuItemCompat.getActionView(menuItem);
        textStoryItemCount = (TextView) actionView.findViewById(R.id.bookmark_badge);

        setupBadge();//change the badge item count

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }//end onCreateOptionsMenu

    /**
     * this method to setup the action for each menu item
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.bookmark:
                startActivity(new Intent(this, Bookmark.class));
                break;
            case R.id.search:
                //Show the toast immediately:
                openSearchDialog();
                break;
            case R.id.help:
                helpDialog();
                break;
            case R.id.menu_dictionary:
                startActivity(new Intent(this, MerriamWebsterDictionary.class));
                break;
            case R.id.menu_newsfeed:
                startActivity(new Intent(this, com.example.androidfinalprojectw18.newfeeds.MainActivity.class));
                break;
            case R.id.menu_flight:
                startActivity(new Intent(this, FlightStatusTracker.class));
                break;
        }
        return true;
    }//end onOptionsItemSelected

    /**
     * Create a custome Dialog for search function
     * get text from EditText and save to SharedPreference
     * then send back to MainActivity
     */
    public void openSearchDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.newyork_search_dialog, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);
        Button searchBtn = (Button) dialogView.findViewById(R.id.searchBtn);
        Button cancelBtn = (Button) dialogView.findViewById(R.id.cancelBtn);

        //search buttion action
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                String whatWasTyped = editText.getText().toString();
                editor.putString("ReserveName", whatWasTyped);
                editor.commit();

                Intent mainPage = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainPage);
            }
        });//end search button

        //cancel button
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });//end cancel button

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    /**
     * This method invoke when click on the bookmarkButton on Listview's items
     * Create a ContentValues with StoryModel object and insert into DB
     * after inserted into db will return the id in DB for later use.
     * Update item counter on toolbar after inserted
     * @param storyModel
     * @return
     */
    @Override
    public long addToBookmark(StoryModel storyModel) {
        StoryDBHelper dbOpener = new StoryDBHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        ContentValues newRow = new ContentValues();
        newRow.put(StoryDBHelper.COL_TITLE, storyModel.getTitle());
        newRow.put(StoryDBHelper.COL_AUTHOR, storyModel.getAuthor());
        newRow.put(StoryDBHelper.COL_HEADLINE, storyModel.getHeadLine());
        newRow.put(StoryDBHelper.COL_URL, storyModel.getUrl());
        newRow.put(StoryDBHelper.COL_IMAGEURL, storyModel.getImageURL());

        long id = db.insert(StoryDBHelper.TABLE_NAME, null, newRow);

        setupBadge();
        return id;
    }//end addToBookmark

    /**
     * This method to remove the Story has added to DB
     * Update item counter on toolbar after inserted
     * @param id
     */
    @Override
    public void removeFromBookmark(long id) {
        StoryDBHelper dbOpener = new StoryDBHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.delete(StoryDBHelper.TABLE_NAME, StoryDBHelper.COL_ID + "=?", new String[]{Long.toString(id)});
        setupBadge();
    }//end removeFromBookmark

    /**
     * This method to set the item counter's textview on toolbar
     * If there is no story has added to the bookmark, then hide the badge
     */
    public void setupBadge() {
        StoryDBHelper dbOpener = new StoryDBHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        Cursor rs = db.rawQuery("select * from " + StoryDBHelper.TABLE_NAME, null);
        mStoryItemCount = rs.getCount();
        if (textStoryItemCount != null) {
            //if no story added, then hide the badge
            if (mStoryItemCount == 0) {
                if (textStoryItemCount.getVisibility() != View.GONE) {
                    textStoryItemCount.setVisibility(View.GONE);
                }
            } else {
                textStoryItemCount.setText(String.valueOf(Math.min(mStoryItemCount, 99)));
                if (textStoryItemCount.getVisibility() != View.VISIBLE) {
                    textStoryItemCount.setVisibility(View.VISIBLE);
                }
            }//end if
        }//end if
    }//end setupBadge
    /**
     * display helpDialog
     */
    public void helpDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.newyork_help_dialog, null);
        Button closeBtn = (Button) dialogView.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }
}

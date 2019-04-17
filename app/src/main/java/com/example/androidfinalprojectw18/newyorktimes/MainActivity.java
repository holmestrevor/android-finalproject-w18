package com.example.androidfinalprojectw18.newyorktimes;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements StoryAdapter.AdapterCallback {

    private int mStoryItemCount = 0;
    TextView textStoryItemCount;
    private ArrayList<StoryModel> storyModelArrayList;
    StoryModel storyModel;
    ListView listView;
    StoryAdapter adt;
    ProgressBar progressBar;
    SharedPreferences sp;
    int positionClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newyork_main);
        boolean isTablet = findViewById(R.id.nytimes_fragment) != null;
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);
        storyModelArrayList = new ArrayList<StoryModel>();
        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setTitle("New York Times");
        mTitle.setText(toolbar.getTitle());
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
                finish();
            }
        });
        //finish setup toolbar


        listView.setOnItemClickListener((parent, view, position, id) -> {
            Bundle dataToPass = new Bundle();
            dataToPass.putString("title", storyModelArrayList.get(position).getTitle());
            dataToPass.putString("author", storyModelArrayList.get(position).getAuthor());
            dataToPass.putString("headLine", storyModelArrayList.get(position).getHeadLine());
            dataToPass.putString("url", storyModelArrayList.get(position).getUrl());
            dataToPass.putString("imageUrl", storyModelArrayList.get(position).getImageURL());
            if (storyModelArrayList.get(position).getId() != null)
                dataToPass.putLong("id", storyModelArrayList.get(position).getId());
            if (isTablet) {
                Fragment dFragment = new Fragment(); //add a DetailFragment
                dFragment.setArguments(dataToPass); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.nytimes_fragment, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            } else //isPhone
            {
                positionClicked = position;
                Intent nextActivity = new Intent(MainActivity.this, Empty.class);
                nextActivity.putExtras(dataToPass);
                startActivityForResult(nextActivity, 23);
            }

        });

        sp = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = sp.getString("ReserveName", "Telsa");

        StoryFetcher networkThread = new StoryFetcher();
        networkThread.execute("https://api.nytimes.com/svc/search/v2/articlesearch.json?q=" + savedString + "&api-key=XpPNXdnkdjss2DwIjIh09gTjoqMMCiLd");
        adt = new StoryAdapter(this, storyModelArrayList);
        setupBadge();
    }//finish onCreate


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If you're coming back from the view contact activity
        System.out.println("back: ");
        setupBadge();
    }

    /**
     * This method to fetch the json using AsyncTask
     * then parse Json into objects
     * and add into the arraylist
     */
    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class StoryFetcher extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {


            try {
                //get the string url:
                String myUrl = params[0];

                //create the network connection:
                URL url = new URL(myUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inStream = urlConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }//end while
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject = new JSONObject(result);
                JSONArray jsonArray = jObject.getJSONObject("response").getJSONArray("docs");
                progressBar.setMax(jsonArray.length());
                storyModelArrayList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    storyModel = new StoryModel();
                    JSONObject articleObject = jsonArray.getJSONObject(i);
                    storyModel.setAuthor(articleObject.getJSONObject("byline").getString("original"));
                    storyModel.setTitle(articleObject.getJSONObject("headline").getString("main"));
                    if (articleObject.has("lead_paragraph"))
                        storyModel.setHeadLine(articleObject.getString("lead_paragraph"));
                    if (articleObject.getJSONArray("multimedia").length() > 0)
                        storyModel.setImageURL("https://www.nytimes.com/" + articleObject.getJSONArray("multimedia").getJSONObject(0).getString("url"));
                    storyModel.setUrl(articleObject.getString("web_url"));
                    storyModelArrayList.add(storyModel);

                    progressBar.setProgress(i + 1);
                    Thread.sleep(100);
                }//end for

            } catch (Exception ex) {
                Log.e("Crash!!", ex.getMessage());
            }//end try/catch

            return "Finished task";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("AsyncTaskExample", "update:" + values[0]);

        }

        @Override
        protected void onPostExecute(String s) {
            listView.setAdapter(adt);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }//end StoryFetcher

    /**
     * Setting up the item on the toolbar
     *
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
                Intent bookmarkPage = new Intent(this, Bookmark.class);
                startActivityForResult(bookmarkPage, 55);
                break;
            case R.id.search:
                //Show the toast immediately:
                openSearchDialog();
                break;
            case R.id.help:
                //Show the toast immediately:
                helpDialog();
                break;
//            case R.id.menu_dictionary:
//                startActivity(new Intent(this, ));
//                break;
//            case R.id.menu_newsfeed:
//                startActivity(new Intent(this, ));
//                break;
//            case R.id.menu_flight:
//                startActivity(new Intent(this, ));
//                break;

        }
        return true;
    }//end onOptionsItemSelected

    /**
     * This method to change the number on the bookmark item on the toolbar
     * by : Ferdous Ahamed
     * https://stackoverflow.com/questions/43194243/notification-badge-on-action-item-android
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
     * This method invoke when click on the bookmarkButton on Listview's items
     * Create a ContentValues with StoryModel object and insert into DB
     * after inserted into db will return the id in DB for later use.
     * Update item counter on toolbar after inserted
     *
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
    }

    /**
     * This method to remove the Story has added to DB
     * Update item counter on toolbar after inserted
     *
     * @param id
     */
    @Override
    public void removeFromBookmark(long id) {
        StoryDBHelper dbOpener = new StoryDBHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        db.delete(StoryDBHelper.TABLE_NAME, StoryDBHelper.COL_ID + "=?", new String[]{Long.toString(id)});
        setupBadge();
    }

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

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sp.edit();
                String whatWasTyped = editText.getText().toString();
                editor.putString("ReserveName", whatWasTyped);
                editor.commit();
                StoryFetcher networkThread = new StoryFetcher();
                networkThread.execute("https://api.nytimes.com/svc/search/v2/articlesearch.json?q=" + editText.getText().toString() + "&api-key=XpPNXdnkdjss2DwIjIh09gTjoqMMCiLd");
                adt = new StoryAdapter(MainActivity.this, storyModelArrayList);
                listView.setAdapter(adt);
                dialogBuilder.dismiss();

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
                dialogBuilder.dismiss();
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

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

package com.example.androidfinalprojectw18.newyorktimes;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.newfeeds.DetailFragment;
import com.example.androidfinalprojectw18.newfeeds.EmptyActivity;

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
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newyork_main);

        //setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        toolbar.setTitle("New York Times");
        mTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }//arrow icon to back to previous activity
        //finish setup toolbar

        listView = (ListView)findViewById(R.id.listView) ;
        storyModelArrayList = new ArrayList<StoryModel>();


        StoryFetcher networkThread = new StoryFetcher();
        networkThread.execute("https://api.nytimes.com/svc/search/v2/articlesearch.json?q=telsa&api-key=XpPNXdnkdjss2DwIjIh09gTjoqMMCiLd");
        adt = new StoryAdapter( this, storyModelArrayList);



    }//finish onCreate


    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class StoryFetcher extends AsyncTask<String, Integer, String>
    {
        @Override
        protected String doInBackground(String ... params) {


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
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }//end while
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject = new JSONObject(result);
                JSONArray jsonArray = jObject.getJSONObject("response").getJSONArray("docs");
                System.out.println(jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    storyModel =  new StoryModel();
                    JSONObject articleObject = jsonArray.getJSONObject(i);
                    storyModel.setAuthor(articleObject.getJSONObject("byline").getString("original"));
                    storyModel.setTitle(articleObject.getJSONObject("headline").getString("main"));
                    storyModel.setHeadLine(articleObject.getString("lead_paragraph"));
                    storyModel.setImageURL("https://www.nytimes.com/"+articleObject.getJSONArray("multimedia").getJSONObject(0).getString("url"));
                    storyModel.setUrl(articleObject.getString("web_url"));
                    storyModelArrayList.add(storyModel);
                    System.out.println("adding");
                    if (isCancelled()) {
                        break;
                    }
                }//end for

            }catch (Exception ex)
            {
                Log.e("Crash!!", ex.getMessage() );
            }//end try/catch

            return "Finished task";
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.i("AsyncTaskExample", "update:" + values[0]);
//            messageBox.setText("At step:" + values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("onPostExecute");

            listView.setAdapter(adt);
            listView.setOnItemClickListener( (list, item, position, id) -> {

                System.out.println(position);
            });
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
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.bookmark:
                Toast.makeText(this, "Clicked on bookmark button", Toast.LENGTH_LONG).show();
                break;
            case R.id.help:
                //Show the toast immediately:
                Toast.makeText(this, "Clicked on help button", Toast.LENGTH_LONG).show();
                break;

            //Snackbar code:
//                Snackbar sb = Snackbar.make(toolbar, "This is the Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Go Back?", e -> finish());
//                sb.show();
        }
        return true;
    }//end onOptionsItemSelected

    /**
     * This method to change the number on the bookmark item on the toolbar
     * by : Ferdous Ahamed
     * https://stackoverflow.com/questions/43194243/notification-badge-on-action-item-android
     */
    @Override
    public void setupBadge() {
        System.out.println(mStoryItemCount);
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

    @Override
    public void increaseCounter(){
        mStoryItemCount++;
    }

}

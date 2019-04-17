package com.example.androidfinalprojectw18.newfeeds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.ArticleSearchNYT;
import com.example.androidfinalprojectw18.FlightStatusTracker;
import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.MerriamWebsterDictionary;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView lastSearch;
    EditText searchEdit;
    ListView listView;
    Button searchButton;
    ProgressBar progressBar;
    SharedPreferences sp;

    ArrayList<PostModel> postModels = new ArrayList<>();
    PostModel postModel;
    PostAdapter adapter;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed_main_acitivty);

        lastSearch = (TextView)findViewById(R.id.lastSearchWord);
        listView = (ListView)findViewById(R.id.searchResultsListView);
        searchButton = (Button)findViewById(R.id.nf_main_searchBtn);
        searchEdit = (EditText)findViewById(R.id.searchEdit);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;
        sp = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        String savedString = sp.getString("ReserveName", "No search");

        lastSearch.setText(savedString);

        //toolbar configs
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//disappear the title
        //add back navigation button
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        //back to previous activity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchButton.setOnClickListener(click -> {
            if (searchEdit.getText().toString() == ""){
                Toast.makeText(this, "Enter something to search", Toast.LENGTH_LONG);
            }else {
                SharedPreferences.Editor editor = sp.edit();

                //save what was typed under the name "ReserveName"
                String whatWasTyped = searchEdit.getText().toString();
                editor.putString("ReserveName", whatWasTyped);

                //write it to disk:
                editor.commit();
                progressBar.setVisibility(View.VISIBLE);  //show the progress bar
                postModels.clear();
                PostsFetcher networkThread = new PostsFetcher();
                networkThread.execute("http://webhose.io/filterWebContent?token=a7f9824c-eb03-40c0-a0f2-0251c713965e&format=xml&sort=crawled&q="+searchEdit.getText().toString());
            }
        });

        lastSearch.setOnClickListener(click -> {
            if (!savedString.equals("No search")){
                progressBar.setVisibility(View.VISIBLE);  //show the progress bar
                postModels.clear();
                PostsFetcher networkThread = new PostsFetcher();
                networkThread.execute("http://webhose.io/filterWebContent?token=a7f9824c-eb03-40c0-a0f2-0251c713965e&format=xml&sort=crawled&q="+savedString);
            }
        });

        listView.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString("title", postModels.get(position).getTitle() );
            dataToPass.putString("website", postModels.get(position).getWebsite() );
            dataToPass.putString("author", postModels.get(position).getAuthor() );
            dataToPass.putString("url", postModels.get(position).getUrl() );
            dataToPass.putString("text", postModels.get(position).getText());
            dataToPass.putBoolean("isSaved", false);
            if(isTablet)
            {
                DetailFragment dFragment = new DetailFragment(); //add a DetailFragment
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                dFragment.setTablet(true);  //tell the fragment if it's running on a tablet or not
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .addToBackStack("AnyName") //make the back button undo the transaction
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(MainActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, 35); //make the transition
            }
        });
    }

    /**
     * create menu item on toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newsfeed_menu, menu);

        return true;
    }

    /**
     * set action for each items on toolbar's menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.newsfeed_menu_saved:
                Intent favorite = new Intent(this, Favorite.class);
                startActivityForResult(favorite, 38); //make the transition
                break;
            case R.id.newsfeed_menu_help:
                //Show the toast immediately:
                Toast.makeText(this, "Author’s name Toan Dinh" +
                        " Activity version number: 1.0" +
                        " How to use my application: You type name of article " +
                        " that you want to search then click Search button. " +
                        " A list of articles will appear " +
                        " Click on the artcile which you want to read. you will see its detial." +
                        " you can choose 'ADD TO FAVORITE' or 'VISIT WEBSITE' " +
                        " ADD TO FAVORITE: save the article in your favorite window)  " +
                        " VISIT WEBSITE: go to the website which contains the article" , Toast.LENGTH_LONG).show();
                break;

            case R.id.ArticleSearchNYT:
                 Intent article = new Intent(this, ArticleSearchNYT.class );
                 startActivityForResult(article, 39);

                 break;

            case R.id.FlightStatusTracker:
                Intent flight = new Intent(this, FlightStatusTracker.class);
                startActivityForResult(flight, 40);
                break;

            case R.id.WebDictionary:
                Intent web = new Intent(this, MerriamWebsterDictionary.class);
                startActivityForResult(web,41);
                break;

                //Snackbar code:
//                Snackbar sb = Snackbar.make(toolbar, "This is the Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Go Back?", e -> finish());
//                sb.show();
        }
        return true;
    }

    /**
     * BaseAdapter from the Lab5, by Professor @etorunski
     * https://github.com/etorunski/InClassExamples_W19/tree/week5
     */
    protected class PostAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {
            return postModels.size();
        }

        public PostModel getItem(int position){
            return postModels.get(position);
        }

        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            View newView = inflater.inflate(R.layout.newsfeed_listview_row, parent, false );

            PostModel thisRow = getItem(position);

            TextView rowTitle = (TextView)newView.findViewById(R.id.newsfeed_listview_row_title);
            TextView rowSite = (TextView)newView.findViewById(R.id.newsfeed_listview_row_site);
            TextView rowAuthor = (TextView)newView.findViewById(R.id.newsfeed_listview_row_author);
            rowTitle.setText(thisRow.getTitle());
            rowSite.setText(thisRow.getWebsite());
            rowAuthor.setText(thisRow.getAuthor());
            //return the row:
            return newView;
        }

        /**
         *
         * @param position
         * @return
         */
        public long getItemId(int position)
        {
            return (long)position;
        }
    }

    /**
     * PostsFetcher class using XmlPullParser from Lab6
     * by @etorunski
     * https://github.com/etorunski/InClassExamples_W19/tree/week6
     */
    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class PostsFetcher extends AsyncTask<String, Integer, String>
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

                //create a pull parser:
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( inStream  , "UTF-8");  //inStream comes from line 46
                String text = "";
                int numOfPost = 0;
                //now loop over the XML:
                while(xpp.getEventType() != XmlPullParser.END_DOCUMENT)
                {
                    String tagName = xpp.getName();
                    switch (xpp.getEventType()) {
                        case XmlPullParser.START_TAG:
                            if (tagName.equalsIgnoreCase("post")) {
                                // create a new instance of employee
                                postModel = new PostModel();
                                numOfPost++;
                            }
                            break;

                        case XmlPullParser.TEXT:
                            text = xpp.getText();
                            break;

                        case XmlPullParser.END_TAG:
                            if (tagName.equalsIgnoreCase("post")) {
                                // add employee object to list
                                postModels.add(postModel);
                            } else if (tagName.equalsIgnoreCase("title_full")) {
                                postModel.setTitle(text);
                            } else if (tagName.equalsIgnoreCase("site")) {
                                postModel.setWebsite(text);
                            } else if (tagName.equalsIgnoreCase("author")) {
                                postModel.setAuthor(text);
                            }else if (tagName.equalsIgnoreCase("url")) {
                                postModel.setUrl(text);
                            }else if (tagName.equalsIgnoreCase("text")) {
                                postModel.setText(text);
                            }
                            break;

                        default:
                            break;
                    }

                    progressBar.setProgress(numOfPost*5);
                    xpp.next(); //advance to next XML event
                    if(isCancelled() || numOfPost == 20)
                        break;
                }
                //End of XML reading
            }catch (Exception ex)
            {
                Log.e("Crash!!", ex.getMessage() );
            }
            //return type 3, which is String:
            return "Finished task";
        }


        /**
         *
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        /**
         *
         * @param s
         */
        @Override
        protected void onPostExecute(String s) {
            //the parameter String s will be "Finished task" from line 27
            adapter = new PostAdapter();
            listView.setAdapter(adapter);
            progressBar.setVisibility(View.INVISIBLE);

            System.out.println("update");
        }
    }
}

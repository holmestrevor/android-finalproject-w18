package com.example.androidfinalprojectw18.newfeeds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Favorite extends AppCompatActivity {

    Toolbar toolbar;
    ListView listView;
    ProgressBar progressBar;

    ArrayList<PostModel> postModels = new ArrayList<>();
    PostModel postModel;
    PostAdapter adapter;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newsfeed_favorite_layout);

        listView = (ListView)findViewById(R.id.searchResultsListView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;


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


        getData();


        listView.setOnItemClickListener( (list, item, position, id) -> {

            Bundle dataToPass = new Bundle();
            dataToPass.putString("title", postModels.get(position).getTitle() );
            dataToPass.putString("website", postModels.get(position).getWebsite() );
            dataToPass.putString("author", postModels.get(position).getAuthor() );
            dataToPass.putString("url", postModels.get(position).getUrl() );
            dataToPass.putString("text", postModels.get(position).getText());
            dataToPass.putLong("id",postModels.get(position).getId());
            dataToPass.putBoolean("isSaved", true);
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
                Intent nextActivity = new Intent(this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, 39); //make the transition
            }
        });
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If you're coming back from the view contact activity
        if(requestCode == 39)
        {
           if (resultCode == 36){
               getData();
           }
        }
        System.out.println(requestCode +" "+ resultCode);
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
                Toast.makeText(this, "Clicked on help button", Toast.LENGTH_LONG).show();
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

        public long getItemId(int position)
        {
            return (long)position;
        }
    }

    /**
     * query data to listview, copy from lab 5
     */
    public void getData(){
        postModels.clear();
        //get a database:
        MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(this);
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        //query all the results from the database:
        String [] columns = {MyDatabaseOpenHelper.COL_ID, MyDatabaseOpenHelper.COL_TITLE, MyDatabaseOpenHelper.COL_WEBSITE, MyDatabaseOpenHelper.COL_AUTHOR, MyDatabaseOpenHelper.COL_URL, MyDatabaseOpenHelper.COL_TEXT};
        Cursor results = db.query(false, MyDatabaseOpenHelper.TABLE_NAME, columns, null, null, null, null, null, null);

        //find the column indices:
        int titleColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TITLE);
        int websiteColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_WEBSITE);
        int authorColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_AUTHOR);
        int urlColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_URL);
        int textColumnIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_TEXT);
        int idColIndex = results.getColumnIndex(MyDatabaseOpenHelper.COL_ID);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String title = results.getString(titleColumnIndex);
            String website = results.getString(websiteColumnIndex);
            String author = results.getString(authorColumnIndex);
            String url = results.getString(urlColumnIndex);
            String text = results.getString(textColumnIndex);
            long id = results.getLong(idColIndex);

            postModels.add(new PostModel(id, title, website, author, url, text));
        }

        //create an adapter object and send it to the listVIew
        adapter = new PostAdapter();

        listView.setAdapter(adapter);
    }
}

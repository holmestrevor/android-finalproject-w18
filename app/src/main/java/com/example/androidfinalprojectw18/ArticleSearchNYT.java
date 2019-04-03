package com.example.androidfinalprojectw18;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ArticleSearchNYT extends AppCompatActivity {

    private ProgressBar progressBar;
    private Toolbar toolbar;
    private ListView listview;
    private ArrayList<ArticleModel> articleModelArrayList;
    private String url ="https://api.nytimes.com/svc/search/v2/articlesearch.json?q=";
    private String token = "&api-key=89kmL9QdZSaSnHNrZtgRuPmf11e3mPQh";
    private String keyword ;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_search_nyt);
        sp = getSharedPreferences("FileName", Context.MODE_PRIVATE);
        keyword = sp.getString("Keyword", "Tesla");
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        listview = findViewById(R.id.listView);
        articleModelArrayList =  new ArrayList<>();

        ArticleQuery article = new ArticleQuery();
        article.execute();

        toolbar = findViewById(R.id.nytimes_toolbar);
        toolbar.setTitle("New York Times");
        setSupportActionBar(toolbar);

        //add back navigation button
        if (getSupportActionBar() !=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                System.out.println(position);
            }
        });
        System.out.println("finish oncreate");

    }

    // a subclass of AsyncTask                  Type1    Type2    Type3
    private class ArticleQuery extends AsyncTask<String, Integer, String>
    {


        @Override
        protected String doInBackground(String ... params) {
            System.out.println("start doin");
            try {

                //Start of JSON reading of UV factor:

                //create the network connection:
                URL UVurl = new URL(url + keyword +token);
                System.out.println(UVurl);
                HttpURLConnection UVConnection = (HttpURLConnection) UVurl.openConnection();
                InputStream inStream = UVConnection.getInputStream();

                //create a JSON object from the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                //now a JSON table:
                JSONObject jObject = new JSONObject(result);
//                Log.e("jObject", jObject.toString());
                JSONArray jsonArray = jObject.getJSONObject("response").getJSONArray("docs");
                for (int i = 0; i < jsonArray.length(); i++) {
                    ArticleModel articleModel =  new ArticleModel();
                    JSONObject articleObject = jsonArray.getJSONObject(i);
                    articleModel.setAuthor(articleObject.getJSONObject("byline").getString("original"));
                    articleModel.setTitle(articleObject.getJSONObject("headline").getString("main"));
                    articleModel.setDescription(articleObject.getString("lead_paragraph"));
                    articleModel.setImgURL("https://www.nytimes.com/"+articleObject.getJSONArray("multimedia").getJSONObject(0).getString("url"));
                    articleModel.setLink(articleObject.getString("web_url"));
                    articleModelArrayList.add(articleModel);
                    Thread.sleep(500);
                    publishProgress(50);
                    if (isCancelled()) {
                        break;
                    }
                }


            }catch (Exception ex)
            {
                Log.e("Crash!!", ex.getMessage() );
            }

            for (int i = 0; i < articleModelArrayList.size(); i++){
//                Log.e("test", articleModelArrayList.get(i).getLink());
            }

            System.out.println("finish ondoin");

            return "Finished task";
        }



        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
            System.out.println("finish onprogress");
//
        }

        @Override
        protected void onPostExecute(String s) {

            ArticleAdapter adt = new ArticleAdapter(articleModelArrayList, getApplicationContext());
            listview.setAdapter(adt);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    System.out.println(position);
                }
            });
            System.out.println("finish onPost");
        }

        @Override
        protected void onPreExecute() {
            listview.setOnItemClickListener( (list, item, position, id) -> {
                System.out.println("onclick");
                Bundle dataToPass = new Bundle();
                dataToPass.putString("link", articleModelArrayList.get(position).getLink() );


                Intent nextActivity = new Intent(getApplicationContext(), ArticleDetailActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivityForResult(nextActivity, 123); //make the transition

            });                                                     // 1
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nytimes_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            //what to do when the menu item is selected:
            case R.id.MenuItems_search:
                //Show the toast immediately:
                alertExample();


                break;
            case R.id.MenuItems_bookmark:
                //Show the toast immediately:
                Toast.makeText(this, "This is bookmark", Toast.LENGTH_LONG).show();
                break;


        }
        return true;
    }

    public void alertExample()
    {
        View middle = getLayoutInflater().inflate(R.layout.search_dialog, null);
        final EditText et = (EditText)middle.findViewById(R.id.searchInput);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Search input")
                .setPositiveButton("Search", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        keyword = et.getText().toString();
                        System.out.println(keyword);
                        ArticleQuery article = new ArticleQuery();
                        article.execute();
                        articleModelArrayList.clear();
                        ArticleAdapter adt = new ArticleAdapter(articleModelArrayList, getApplicationContext());
                        listview.setAdapter(adt);
                        System.out.println("finish");

                        //get an editor object
                        SharedPreferences.Editor editor = sp.edit();

                        //save what was typed under the name "ReserveName"
                        String whatWasTyped = et.getText().toString();
                        editor.putString("Keyword", whatWasTyped);

                        //write it to disk:
                        editor.commit();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // What to do on Cancel
                    }
                }).setView(middle);

        builder.create().show();
    }
}

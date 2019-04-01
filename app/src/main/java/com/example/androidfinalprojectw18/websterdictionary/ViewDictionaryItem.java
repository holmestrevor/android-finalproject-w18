package com.example.androidfinalprojectw18.websterdictionary;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class ViewDictionaryItem extends AppCompatActivity {

    TextView wordView, pronunciationView, definitionsView;
    Button saveItem;
    DBOpener dbOpener;
    String search, urlString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_dictionary_item);

        Intent i = getIntent();

        wordView = (TextView)findViewById(R.id.wordExpanded);
        pronunciationView = (TextView)findViewById(R.id.pronunciationExpanded);
        definitionsView = (TextView)findViewById(R.id.definitionsExpanded);

        //Check if we are looking at a saved word
        if(i.getBooleanExtra("fromSaved", false)) {
            wordView.setText(i.getStringExtra("word"));
            pronunciationView.setText(i.getStringExtra("pronunciation"));

            String s = "";
            //Formatting the definitions to be placed into the TextView
            for(int j=0; j<i.getIntExtra("definitionCount", 0); j++) {
                s += j+1 + ": " + i.getStringExtra("definition" + j) + "\n";
            }
            definitionsView.setText(s);
        //Otherwise, we are accessing a searched item
        } else {
            search = i.getStringExtra("searchWord").trim();
            DictionaryQuery query = new DictionaryQuery();
            query.execute("");
        }

    }

    private class DictionaryQuery extends AsyncTask<String, Integer, String> {

        String word, pronunciation;
        String[] definitions;

        @Override
        protected String doInBackground(String... strings) {
            try {
                urlString = "https://www.dictionaryapi.com/api/v1/references/sd3/xml/" + search + "?key=4556541c-b8ed-4674-9620-b6cba447184f";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                conn.connect();
                InputStream in = conn.getInputStream();
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);

                while(parser.next()!=XmlPullParser.END_DOCUMENT) {
                    if(parser.getEventType()!=XmlPullParser.START_TAG) {
                        continue;
                    }
                    int i = 0;
                    if(parser.getName().equals("suggestion")) {
                        word = getResources().getString(R.string.no_words_found);

                    }
                    if(parser.getName().equals("entry")) {
                        word = parser.getAttributeValue(null, "hw");
                        pronunciation = parser.getAttributeValue(null, "pr");
                    }
                }
            } catch(ProtocolException e) {
                e.printStackTrace();
            } catch(IOException e) {
                e.printStackTrace();
            } catch(XmlPullParserException e) {
                e.printStackTrace();
            }
            //
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            wordView.setText(word);
            pronunciationView.setText(pronunciation);
            StringBuilder definitionsFormatted = new StringBuilder();
            for(int i=0;i<definitions.length; i++) {
                definitionsFormatted.append(definitions[i] + "\n");
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}

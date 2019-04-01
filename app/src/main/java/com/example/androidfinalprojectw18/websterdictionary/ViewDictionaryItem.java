package com.example.androidfinalprojectw18.websterdictionary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    Button saveButton;
    ProgressBar progressBar;
    DBOpener dbOpener;
    String search, urlString;
    String word, pronunciation;
    //Array will retrieve a maximum of five definitions
    String[] definitions = new String[5];

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

            StringBuilder sb = new StringBuilder();
            //Formatting the definitions to be placed into the TextView
            for(int j=0; j<i.getIntExtra("definitionCount", 0); j++) {
                sb.append(j+1 + ": " + i.getStringExtra("definition" + j) + "\n");
            }
            definitionsView.setText(sb.toString());
        //Otherwise, we are accessing a searched item
        } else {
            progressBar = findViewById(R.id.dictionaryProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            saveButton = findViewById(R.id.saveButton);
            saveButton.setVisibility(View.VISIBLE);
            search = i.getStringExtra("searchWord");
            DictionaryQuery query = new DictionaryQuery();
            query.execute("");
            saveButton.setOnClickListener(b ->{
                dbOpener = new DBOpener(this);
                SQLiteDatabase db = dbOpener.getWritableDatabase();
                ContentValues cv = new ContentValues();
                cv.put(DBOpener.COL_WORD, word);
                cv.put(DBOpener.COL_PRONUNCIATION, pronunciation);
                long id = db.insert(DBOpener.TABLE1_NAME, null, cv);
                for(int j=0; j<definitions.length; j++) {
                    cv.put(DBOpener.COL_DEFINITION, definitions[j]);
                    cv.put(DBOpener.COL_ITEM_ID, id);
                    id = db.insert(DBOpener.TABLE2_NAME, null, cv);
                    cv.clear();
                }
            });
        }

    }

    private class DictionaryQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                if(search.contains(" ")) {
                    search = search.replaceAll(" ", "+");
                }
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

                int i = 0;
                while(parser.next()!=XmlPullParser.END_DOCUMENT) {
                    if(parser.getEventType()!=XmlPullParser.START_TAG) {
                        continue;
                    }
                    /*
                    If this returns true, the word that was entered was not found in the dictionary.
                    Suggestions will be offered.
                     */
                    if(parser.getName().equals("suggestion")) {
                        if(i>=5){
                            break;
                        }
                        word = getResources().getString(R.string.no_words_found);
                        pronunciation = getResources().getString(R.string.suggestions);
                        definitions[i] = parser.nextText();
                        i++;
                    }
                    if(parser.getName().equals("entry")) {
                        if(word!=null) {
                            break;
                        }
                        word = parser.getAttributeValue(null, "id");
                        /*
                        Some words contain a definition number, like "Pasta[1]". this block removes
                        that number by finding the index of the open square bracket and creating a
                        substring.
                         */
                        if(word.contains("[")) {
                            int index = word.indexOf("[");
                            word = word.substring(0, index);
                            publishProgress(25);
                        }
                    }
                    if(parser.getName().equals("pr")) {
                        if(pronunciation!=null) {
                            break;
                        }
                        pronunciation = parser.nextText();
                        publishProgress(50);
                    }
                    if(parser.getName().equals("dt")) {
                        if(i>=5) {
                            break;
                        }
                        String nextText = parser.nextText();
                        if(nextText.equals("") || nextText.equals(":")) {
                            if(parser.next()==XmlPullParser.START_TAG) {

                            }
                        } else {
                            definitions[i] = nextText;
                        }
                        i++;
                    }
                }
                publishProgress(100);
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
                if(definitions[i]==null) {
                    break;
                }
                definitionsFormatted.append(i+1 + " " + definitions[i] + "\n");
            }
            definitionsView.setText(definitionsFormatted.toString());
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setProgress(values[0]);
        }

    }

}

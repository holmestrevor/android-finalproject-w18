package com.example.androidfinalprojectw18.websterdictionary;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLInput;

public class ViewDictionaryItem extends AppCompatActivity {

    //View Items
    private TextView wordView, pronunciationView, definitionsView;
    /*
    Save and delete buttons. These exist in the same place, but only one will
    show at a time based on whether or not a saved/searched item is being
    accessed.
     */
    private Button saveButton, deleteButton;
    private ProgressBar progressBar;

    //Database opener
    private DBOpener dbOpener;
    private SQLiteDatabase db;

    private String search, urlString;
    private String word, pronunciation;

    //Global boolean to
    private boolean resultsFound = true;

    //Array will retrieve a maximum of five definitions
    private String[] definitions = new String[5];

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

            dbOpener = new DBOpener(this);
            db = dbOpener.getReadableDatabase();
            Cursor c = db.query(true,
                    DBOpener.TABLE1_NAME,
                    null,
                    DBOpener.COL_ID + " = ?",
                    new String[] { String.valueOf(i.getLongExtra("id", 0)) },
                    null,
                    null,
                    null,
                    null
                    );

            while(c.moveToNext()) {
                wordView.setText(c.getString(c.getColumnIndex(DBOpener.COL_WORD)));
                pronunciationView.setText(c.getString(c.getColumnIndex(DBOpener.COL_PRONUNCIATION)));

                StringBuilder sb = new StringBuilder();

                sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION0)) + "\n");
                sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION1)) + "\n");
                sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION2)) + "\n");
                sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION3)) + "\n");
                sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION4)) + "\n");
                definitionsView.setText(sb.toString());

            }

            deleteButton = findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(b -> {
                dbOpener = new DBOpener(this);
                db = dbOpener.getWritableDatabase();
                db.delete(DBOpener.TABLE1_NAME, DBOpener.COL_ID + " = ?", new String[]{String.valueOf(c.getLong(c.getColumnIndex(DBOpener.COL_ID)))} );
                Toast.makeText(this, "Item successfully deleted.", Toast.LENGTH_SHORT).show();
                finish();
            });
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
                if(definitions[0]!=null) {
                    cv.put(DBOpener.COL_DEFINITION0, definitions[0]);
                }
                if(definitions[1]!=null) {
                    cv.put(DBOpener.COL_DEFINITION1, definitions[1]);
                }
                if(definitions[2]!=null) {
                    cv.put(DBOpener.COL_DEFINITION2, definitions[2]);
                }
                if(definitions[3]!=null) {
                    cv.put(DBOpener.COL_DEFINITION3, definitions[3]);
                }
                if(definitions[4]!=null) {
                    cv.put(DBOpener.COL_DEFINITION4, definitions[4]);
                }
                Log.i("ContentValues", cv.toString());
                //The word is a duplicate, do not add to the database
                if(checkForDuplicates(word, db)) {
                    Toast.makeText(this, "You have already saved that word.", Toast.LENGTH_SHORT).show();
                //The word will be saved to the database
                } else {
                    long id = db.insert(DBOpener.TABLE1_NAME, null, cv);
                    Toast.makeText(this, "Item was saved successfully", Toast.LENGTH_SHORT).show();
                }
                cv.clear();
                db.close();
                dbOpener.close();
            });
        }

    }

    /**
     * A method to check if the word that the user is attempting to save already exists in the database.
     * @param word The word that needs to be compared with database entries
     * @param db the SQLiteDatabase Object
     * @return
     */
    public boolean checkForDuplicates(String word, SQLiteDatabase db) {
        Cursor c = db.query(false,
                DBOpener.TABLE1_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        if(c.getCount()>0) {
            while(c.moveToNext()) {
                if(c.getString(c.getColumnIndex(DBOpener.COL_WORD)).equals(word)) {
                    return true;
                }
            }
        }
        return false;
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
                        resultsFound = false;
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
                        parser.next();
                        Log.i("parser", parser.getText());
                        definitions[i] = parser.getText();
                        if(definitions[i].trim().equals(":")) {
                            do {
                                parser.next();
                                if(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().equals("sx")) {
                                    if(i>=5) {
                                        break;
                                    }
                                    parser.next();
                                    definitions[i] = parser.getText();
                                    if(!definitions[i].startsWith(":")) {
                                        definitions[i] = ":" + definitions[i];
                                    }
                                    i++;
                                }
                            } while(parser.getEventType()!=XmlPullParser.START_TAG);
                        } else {
                            i++;
                        }
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
            if(!resultsFound) {
                saveButton.setVisibility(View.INVISIBLE);
            }
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

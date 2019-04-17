package com.example.androidfinalprojectw18.websterdictionary;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.dbopener.DBOpener;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;

public class WebsterFragment extends Fragment {

    private TextView wordView, pronunciationView, definitionsView;

    private Button saveButton, deleteButton;
    private ProgressBar progressBar;

    private DBOpener dbOpener;
    private SQLiteDatabase db;

    private String search, urlString;
    private String word, pronunciation;
    private String[] definitions = new String[5];

    private boolean resultsFound = true;

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataFromActivity = getArguments();
        id = dataFromActivity.getLong(DBOpener.COL_ID);
        boolean fromSaved = dataFromActivity.getBoolean("fromSaved");

        View result = inflater.inflate(R.layout.activity_webster_fragment, container, false);
        wordView = (TextView)result.findViewById(R.id.wordExpanded);
        pronunciationView = (TextView)result.findViewById(R.id.pronunciationExpanded);
        definitionsView = (TextView)result.findViewById(R.id.definitionsExpanded);

        if(dataFromActivity.getBoolean("fromSaved")) {
            dbOpener = new DBOpener(getActivity());
            db = dbOpener.getReadableDatabase();
            Cursor c = db.query(true,
                    DBOpener.TABLE1_NAME,
                    null,
                    DBOpener.COL_ID + " = ?",
                    new String[] { String.valueOf(dataFromActivity.getLong(DBOpener.COL_ID)) },
                    null,
                    null,
                    null,
                    null
            );

            while(c.moveToNext()) {
                wordView.setText(c.getString(c.getColumnIndex(DBOpener.COL_WORD)));
                pronunciationView.setText(c.getString(c.getColumnIndex(DBOpener.COL_PRONUNCIATION)));

                StringBuilder sb = new StringBuilder();
                if(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION0))!=null) {
                    sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION0)) + "\n");
                }
                if(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION1))!=null) {
                    sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION1)) + "\n");
                }
                if(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION2))!=null) {
                    sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION2)) + "\n");
                }
                if(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION3))!=null) {
                    sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION3)) + "\n");
                }
                if(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION4))!=null) {
                    sb.append(c.getString(c.getColumnIndex(DBOpener.COL_DEFINITION4)) + "\n");
                }
                definitionsView.setText(sb.toString());

            }

            deleteButton = result.findViewById(R.id.deleteButton);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(b -> {
                if(isTablet) {
                    MerriamWebsterDictionary parent = (MerriamWebsterDictionary)getActivity();
                    parent.deleteMessage(id);
                    parent.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(this)
                            .commit();
                } else {
                    MerriamEmpty parent = (MerriamEmpty)getActivity();
                    Intent backToParent = new Intent();
                    backToParent.putExtra(DBOpener.COL_ID, dataFromActivity.getLong(DBOpener.COL_ID));

                    parent.setResult(Activity.RESULT_OK, backToParent);
                    parent.finish();
                }
            });
        //Else we are searching for an item
        } else {
            progressBar = result.findViewById(R.id.dictionaryProgressBar);
            progressBar.setVisibility(View.VISIBLE);
            saveButton = result.findViewById(R.id.saveButton);
            saveButton.setVisibility(View.VISIBLE);
            search = dataFromActivity.getString("searchWord");
            DictionaryQuery query = new DictionaryQuery();
            query.execute("");
            saveButton.setOnClickListener(b ->{
                dbOpener = new DBOpener(getActivity());
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
                    Toast.makeText(getContext(), "You have already saved that word.", Toast.LENGTH_SHORT).show();
                    //The word will be saved to the database
                } else {
                    long id = db.insert(DBOpener.TABLE1_NAME, null, cv);
                    Toast.makeText(getContext(), "Item was saved successfully", Toast.LENGTH_SHORT).show();
                }
                cv.clear();
                db.close();
                dbOpener.close();
                if(isTablet) {
                    MerriamWebsterDictionary parent = (MerriamWebsterDictionary)getActivity();
                    parent.refreshItems(parent.findViewById(R.id.wordList));
                    parent.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(this)
                            .commit();
                } else {
                    MerriamEmpty parent = (MerriamEmpty)getActivity();
                    Intent backToParent = new Intent();

                    parent.setResult(Activity.RESULT_OK, backToParent);
                    parent.finish();
                }
            });
        }

        return result;
    }

    /**
     * A method to check if the word that the user is attempting to save already exists in the database.
     * @param word The word that needs to be compared with database entries
     * @param db the SQLiteDatabase Object
     * @return true if there is a duplicate, false if there are none
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
                            continue;
                        }
                        pronunciation = parser.nextText();
                        publishProgress(50);
                    }
//                    if(parser.getName().equals("dt")) {
//                        if(i>=5) {
//                            break;
//                        }
//                        parser.next();
//                        if(parser.getName()!= null && parser.getName().equals("un")) {
//                            definitions[i] = parser.nextText();
//                            i++;
//                            continue;
//                        } else {
//                            definitions[i] = parser.getText();
//                        }
//                        if(definitions[i].trim().equals(":")) {
//                            do {
//                                parser.next();
//                                if(parser.getEventType()==XmlPullParser.START_TAG && parser.getName().equals("sx")) {
//                                    if(i>=5) {
//                                        break;
//                                    }
//                                    parser.next();
//                                    definitions[i] = parser.getText();
//                                    i++;
//                                }
//                            } while(parser.getEventType()!=XmlPullParser.START_TAG);
//                        } else {
//                            i++;
//                        }
//                    }
                    if(parser.getName().equals("dt")) {
                        Log.i("HERE", "HERE");
                        if(i>=5) {
                            break;
                        }
                        String s = "";
                        if(parser.next()==XmlPullParser.TEXT) {
                            s = parser.getText();
                            do {
                                parser.next();
                            } while(parser.getEventType()!=XmlPullParser.START_TAG);
                        }
                        if(s==null) {
                            continue;
                        }
                        if(s.trim().equals(":")) {
                            continue;
                        }
                        if(s.length()>0) {
                            definitions[i] = s;
                            i++;
                        }
                    }
                    if(parser.getName().equals("sx")) {
                        if(i>=5) {
                            break;
                        }
                        definitions[i] = parser.nextText();
                        i++;
                    }
                    if(parser.getName().equals("un")) {
                        if(i>=5) {
                            break;
                        }
                        definitions[i] = parser.nextText();
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

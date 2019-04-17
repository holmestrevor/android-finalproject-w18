package com.example.androidfinalprojectw18.newfeeds;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.androidfinalprojectw18.R;


public class DetailFragment extends Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;
    private long id;
    private TextView title, website, author, text;
    private String url;
    private long dbID;

    public void setTablet(boolean tablet) { isTablet = tablet; }


    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        dataFromActivity = getArguments();
        url = dataFromActivity.getString("url");

        // Inflate the layout for this fragment
        View result =  inflater.inflate(R.layout.newsfeed_fragment, container, false);

         title = (TextView)result.findViewById(R.id.title);
         title.setText(dataFromActivity.getString("title"));
        website = (TextView)result.findViewById(R.id.website);
        website.setText(dataFromActivity.getString("website"));
        author = (TextView)result.findViewById(R.id.author);
        author.setText(dataFromActivity.getString("author"));
        text = (TextView)result.findViewById(R.id.text);
        text.setText(dataFromActivity.getString("text"));
        dbID = dataFromActivity.getLong("id");
        System.out.println(dbID);
        Button backBtn = (Button)result.findViewById(R.id.backBtn);

        if (isTablet){
            backBtn.setVisibility(View.GONE);
        }

        backBtn.setOnClickListener(click -> {
            EmptyActivity parent = (EmptyActivity) getActivity();
            Intent backToFragmentExample = new Intent();

            parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
            parent.finish(); //go back
        });

        Button gobtn = (Button)result.findViewById(R.id.goToBtn);
        gobtn.setOnClickListener(click -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        Button saveBtn = (Button)result.findViewById(R.id.saveBtn);
        Button deleteButton = (Button)result.findViewById(R.id.deleteBtn);
        saveBtn.setOnClickListener(click -> {
            //get a database:
            MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(getActivity());
            SQLiteDatabase db = dbOpener.getWritableDatabase();

            ContentValues newRowValues = new ContentValues();
            newRowValues.put(MyDatabaseOpenHelper.COL_TITLE, title.getText().toString());
            newRowValues.put(MyDatabaseOpenHelper.COL_WEBSITE, website.getText().toString());
            newRowValues.put(MyDatabaseOpenHelper.COL_AUTHOR, author.getText().toString());
            newRowValues.put(MyDatabaseOpenHelper.COL_URL, url);
            newRowValues.put(MyDatabaseOpenHelper.COL_TEXT, text.getText().toString());
            dbID = db.insert(MyDatabaseOpenHelper.TABLE_NAME, null, newRowValues);
            if (dbID > 0){
                Snackbar.make(gobtn, R.string.fragmentadd, Snackbar.LENGTH_LONG).show();
            }
            deleteButton.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            System.out.println(dbID);

        });

        // get the delete button, and add a click listener:

        deleteButton.setOnClickListener( clk -> {
            MyDatabaseOpenHelper dbOpener = new MyDatabaseOpenHelper(getActivity());
            SQLiteDatabase db = dbOpener.getWritableDatabase();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            //This is the builder pattern, just call many functions on the same object:
            AlertDialog dialog = builder.setTitle(R.string.fragmentalert)
                    .setMessage(R.string.fragmentdelete)
                    .setPositiveButton(R.string.fragmentdl, new DialogInterface.OnClickListener() {

                        /**
                         *
                         * @param dialog
                         * @param which
                         */
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            //If you click the "Delete" button
                            int numDeleted = db.delete(MyDatabaseOpenHelper.TABLE_NAME, MyDatabaseOpenHelper.COL_ID + "=?", new String[] {Long.toString(dbID)});

                            Log.i("", "Deleted " + numDeleted + " rows");

                            if (!isTablet){
                                //set result to PUSHED_DELETE to show clicked the delete button
                                EmptyActivity parent = (EmptyActivity) getActivity();
                                Intent backToFragmentExample = new Intent();

                                parent.setResult(36, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
                                parent.finish(); //go back
                            }else {
                                Favorite parent = (Favorite) getActivity();
                                parent.getData();
                            }
                        }
                    })
                    //If you click the "Cancel" button:
                    .setNegativeButton(R.string.fragmentcancel, (d,w) -> {  /* nothing */})
                    .create();

            //then show the dialog
            dialog.show();

            if(isTablet) { //both the list and details are on the screen:
//                FragmentExample parent = (FragmentExample)getActivity();
//                parent.deleteMessageId((int)id); //this deletes the item and updates the list
//
//
//                //now remove the fragment since you deleted it from the database:
//                // this is the object to be removed, so remove(this):
//                parent.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            //for Phone:
            else //You are only looking at the details, you need to go back to the previous list page
            {
//                EmptyActivity parent = (EmptyActivity) getActivity();
//                Intent backToFragmentExample = new Intent();
//                backToFragmentExample.putExtra(FragmentExample.ITEM_ID, dataFromActivity.getLong(FragmentExample.ITEM_ID ));
//
//                parent.setResult(Activity.RESULT_OK, backToFragmentExample); //send data back to FragmentExample in onActivityResult()
//                parent.finish(); //go back
            }

            deleteButton.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
        });

        if (dataFromActivity.getBoolean("isSaved")){
            deleteButton.setVisibility(View.VISIBLE);
            saveBtn.setVisibility(View.GONE);
        }else {
            deleteButton.setVisibility(View.GONE);
            saveBtn.setVisibility(View.VISIBLE);
        }

        return result;
    }




}

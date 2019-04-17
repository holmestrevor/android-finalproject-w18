package com.example.androidfinalprojectw18.newyorktimes;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidfinalprojectw18.FlightStatusTracker;
import com.example.androidfinalprojectw18.R;
import com.example.androidfinalprojectw18.websterdictionary.MerriamWebsterDictionary;
import com.squareup.picasso.Picasso;

public class Fragment extends android.support.v4.app.Fragment {

    private boolean isTablet;
    private Bundle dataFromActivity;

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    String title, author, headLine, url, imageUrl;
    Long id;
    private int mStoryItemCount = 0;
    TextView textStoryItemCount;
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        title = dataFromActivity.getString("title");
        author = dataFromActivity.getString("author");
        headLine = dataFromActivity.getString("headLine");
        url = dataFromActivity.getString("url");
        imageUrl = dataFromActivity.getString("imageUrl");
        id = dataFromActivity.getLong("id");
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.newyork_detail, container, false);


        TextView titleTV = (TextView) result.findViewById(R.id.title);
        TextView authorTV = (TextView) result.findViewById(R.id.author);
        TextView headLineTV = (TextView) result.findViewById(R.id.headLine);
        ImageView image = (ImageView) result.findViewById(R.id.image);
        ImageButton bookmarkBtn = (ImageButton) result.findViewById(R.id.bookmarkBtn);
        Button readMoreBtn = (Button) result.findViewById(R.id.readMoreBtn);
        Toolbar toolbar = (Toolbar) result.findViewById(R.id.toolbar);

        if (isTablet) {
            //hide bookmark button on tablet, because it have their buttons on the listview on main activity
            bookmarkBtn.setVisibility(View.GONE);
            toolbar.setVisibility(View.GONE);
        } else {
            //setup toolbar on phone
            sp = ((AppCompatActivity) getActivity()).getSharedPreferences("FileName", Context.MODE_PRIVATE);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
            toolbar.setTitle("New York Times");
            mTitle.setText(toolbar.getTitle());
            mTitle.setOnClickListener(click -> {
                startActivity(new Intent(getContext(), MainActivity.class));
            });
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            }//arrow icon to back to previous activity
            //back to previous activity
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().setResult(30);
                    getActivity().finish();
                }
            });
            //finish setup toolbar

            //if the story is added to bookmark, then change the color of bookmark button
            if (id != 0) {
                bookmarkBtn.setImageResource(R.drawable.ic_ny_bookmark_off);
                bookmarkBtn.setTag("remove");
            } else {
                bookmarkBtn.setImageResource(R.drawable.ic_ny_bookmark_on);
                bookmarkBtn.setTag("add");
            }
        }//end isTablet if-else

        //set info into the layour
        titleTV.setText(title);
        authorTV.setText(author);
        headLineTV.setText(headLine);
        Picasso.get().load(imageUrl).into(image);

        //bookmark button action
        bookmarkBtn.setOnClickListener(click -> {

            if (bookmarkBtn.getTag().equals("add")) {
                StoryModel storyModel = new StoryModel();
                storyModel.setTitle(title);
                storyModel.setAuthor(author);
                storyModel.setHeadLine(headLine);
                storyModel.setUrl(url);
                storyModel.setImageURL(imageUrl);
                id = addToBookmark(storyModel);
                bookmarkBtn.setTag("remove");
                bookmarkBtn.setImageResource(R.drawable.ic_ny_bookmark_off);
            } else {
                bookmarkBtn.setTag("add");
                removeFromBookmark(id);
                bookmarkBtn.setImageResource(R.drawable.ic_ny_bookmark_on);
            }
        });

        //readmore button to open browser and go to the website
        readMoreBtn.setOnClickListener(click -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });

        return result;
    }

    /**
     * Setting up the item on the toolbar
     *
     * @param menu
     * @return
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, ((AppCompatActivity) getActivity()).getMenuInflater());
        inflater.inflate(R.menu.newyorktimes_main_menu, menu);

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
                startActivity(new Intent(getContext(), Bookmark.class));
                break;
            case R.id.search:
                //Show the toast immediately:
                openSearchDialog();
                break;
            case R.id.help:
                helpDialog();
                break;

            case R.id.menu_dictionary:
                startActivity(new Intent(getContext(), MerriamWebsterDictionary.class));
                break;
            case R.id.menu_newsfeed:
                startActivity(new Intent(getContext(), com.example.androidfinalprojectw18.newfeeds.MainActivity.class));
                break;
            case R.id.menu_flight:
                startActivity(new Intent(getContext(), FlightStatusTracker.class));
                break;
        }
        return true;
    }//end onOptionsItemSelected

    /**
     * This method to change the number on the bookmark item on the toolbar
     * by : Ferdous Ahamed
     * https://stackoverflow.com/questions/43194243/notification-badge-on-action-item-android
     */
    public void setupBadge() {
        StoryDBHelper dbOpener = new StoryDBHelper(getActivity());
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
     * @param storyModel
     * @return
     */
    public long addToBookmark(StoryModel storyModel) {
        StoryDBHelper dbOpener = new StoryDBHelper(getActivity());
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
     * @param id
     */
    public void removeFromBookmark(long id) {
        StoryDBHelper dbOpener = new StoryDBHelper(getActivity());
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
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
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

                Intent mainPage = new Intent(getContext(), MainActivity.class);
                startActivity(mainPage);

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
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getContext()).create();
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

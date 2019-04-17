package com.example.androidfinalprojectw18.newyorktimes;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StoryDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StoryDB";
    public static final int VERSION_NUM = 1;
    public static final String TABLE_NAME = "Story";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_AUTHOR = "AUTHOR";
    public static final String COL_HEADLINE = "HEADLINE";
    public static final String COL_URL = "URL";
    public static final String COL_IMAGEURL = "IMAGEURL";


    public StoryDBHelper(Activity ctx){
        //The factory parameter should be null, unless you know a lot about Database Memory management
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    public void onCreate(SQLiteDatabase db)
    {
        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT, "
                + COL_AUTHOR + " TEXT, "
                + COL_HEADLINE + " TEXT, "
                + COL_URL + " TEXT, "
                + COL_IMAGEURL + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}

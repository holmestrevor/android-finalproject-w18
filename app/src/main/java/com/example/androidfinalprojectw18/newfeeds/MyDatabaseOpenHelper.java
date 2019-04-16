package com.example.androidfinalprojectw18.newfeeds;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDatabaseOpenHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDatabaseFile";
    public static final int VERSION_NUM = 1;
    public static final String TABLE_NAME = "Posts";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "TITLE";
    public static final String COL_WEBSITE = "WEBSITE";
    public static final String COL_AUTHOR = "AUTHOR";
    public static final String COL_URL = "URL";
    public static final String COL_TEXT = "TEXT";


    public MyDatabaseOpenHelper(Activity ctx){
        //The factory parameter should be null, unless you know a lot about Database Memory management
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    public void onCreate(SQLiteDatabase db)
    {
        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_TITLE + " TEXT, " + COL_WEBSITE + " TEXT, "
                + COL_AUTHOR+ " TEXT, " + COL_URL + " TEXT, " + COL_TEXT + " TEXT)");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}
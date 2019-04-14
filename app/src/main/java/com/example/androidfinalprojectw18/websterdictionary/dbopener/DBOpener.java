package com.example.androidfinalprojectw18.websterdictionary.dbopener;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBOpener extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dictionarydb";
    public static final int VERSION_NUM = 1;
    public static final String TABLE1_NAME = "DictionaryItem";
    public static final String COL_ID = "_id";
    public static final String COL_WORD = "WORD";
    public static final String COL_PRONUNCIATION = "PRONUNCIATION";
    public static final String TABLE2_NAME = "Definition";
    public static final String COL_DEFINITION = "DEFINITION";
    public static final String COL_ITEM_ID = "ITEM_id";

    public static final String COL_DEFINITION0 = "Definition0";
    public static final String COL_DEFINITION1 = "Definition1";
    public static final String COL_DEFINITION2 = "Definition2";
    public static final String COL_DEFINITION3 = "Definition3";
    public static final String COL_DEFINITION4 = "Definition4";

    public DBOpener(Activity ctx) {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE1_NAME + "("
        + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COL_WORD + " TEXT, "
        + COL_PRONUNCIATION + " TEXT, "
        + COL_DEFINITION0 + " TEXT, "
        + COL_DEFINITION1 + " TEXT, "
        + COL_DEFINITION2 + " TEXT, "
        + COL_DEFINITION3 + " TEXT, "
        + COL_DEFINITION4 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Database upgrade", "Old version: " + oldVersion + "New version: " + newVersion);

    }

    public void printCursor(Cursor c) {
        Log.i("MyOpener", "Database ver: " + VERSION_NUM);
        String[] columnNames = new String[c.getColumnCount()];
        for(int i=0; i<c.getColumnCount(); i++) {
            columnNames[i] = c.getColumnName(i);
        }
        Log.i("MyOpener", "Column count = " + c.getColumnCount() + " Column names: " + columnNames[0] + ", " + columnNames[1] + ", " + columnNames[2]);
        Log.i("MyOpener", "Total returned rows: " + c.getCount());
        String[] rowResults = new String[c.getCount()];
//        if(c.moveToFirst()) {
//            while(c.moveToNext()) {
//                Log.i("MyOpener", "Row info:\nID: " + c.getLong(c.getColumnIndexOrThrow(COL_ID)) + "\nWord: " + c.getString(c.getColumnIndexOrThrow(COL_WORD)) + "\nPronunciation: " + c.getInt(c.getColumnIndexOrThrow(COL_PRONUNCIATION)));
//            }
//        }
    }

}

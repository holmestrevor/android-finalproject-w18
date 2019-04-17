package com.example.androidfinalprojectw18;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Boiler plate SQLiteOpenHelper
 */
public class FlightTrackerDbHelper extends SQLiteOpenHelper{

    /**
     * Constants we need
     */
    public static final String DATABASE_NAME = "FlightTrackerDataBase";
    public static final int VERSION_NUM = 1;
    public static final String TABLE_NAME = "Flights";
    public static final String COL_ID = "_id";
    public static final String COL_FLIGHT_NUMBER = "NUMBER";
    public static final String COL_FLIGHT_LATITUDE = "LATITUDE";
    public static final String COL_FLIGHT_LONGITUDE = "LONGITUDE";
    public static final String COL_FLIGHT_ARRIVAL = "ARRIVAL";
    public static final String COL_FLIGHT_ALTITUDE = "ALTITUDE";
    public static final String COL_FLIGHT_STATUS = "STATUS";


    /**
     * Constructor
     * @param ctx
     */
    public FlightTrackerDbHelper(Activity ctx){
        //The factory parameter should be null, unless you know a lot about Database Memory management
        super(ctx, DATABASE_NAME, null, VERSION_NUM );
    }

    /**
     * onCreate sets up db
     * @param db
     */
    public void onCreate(SQLiteDatabase db)
    {
        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL("CREATE TABLE " + TABLE_NAME + "( "
                + COL_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_FLIGHT_NUMBER + " TEXT, " + COL_FLIGHT_LATITUDE + " REAL,"
                + COL_FLIGHT_LONGITUDE + " REAL," + COL_FLIGHT_ARRIVAL +" TEXT,"
                + COL_FLIGHT_ALTITUDE + " REAL," + COL_FLIGHT_STATUS + " TEXT)");
    }

    /**
     * Deals with upgrades
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }

    /**
     * Deals with downgrades
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:"+newVersion);

        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create a new table:
        onCreate(db);
    }
}

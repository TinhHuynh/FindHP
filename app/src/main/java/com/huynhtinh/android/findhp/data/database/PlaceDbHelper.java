package com.huynhtinh.android.findhp.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.huynhtinh.android.findhp.data.database.PlaceContract.PlaceEntry;

/**
 * Created by TINH HUYNH on 3/16/2018.
 */

public class PlaceDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SavedPlace.db";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PlaceEntry.TABLE_NAME + " (" +
                    PlaceEntry._ID + " INTEGER PRIMARY KEY," +
                    PlaceEntry.COL_PLACE_ID + " TEXT," +
                    PlaceEntry.COL_NAME + " TEXT," +
                    PlaceEntry.COL_ADDRESS + " TEXT," +
                    PlaceEntry.COL_PLACE_TYPE + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PlaceEntry.TABLE_NAME;

    public PlaceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}

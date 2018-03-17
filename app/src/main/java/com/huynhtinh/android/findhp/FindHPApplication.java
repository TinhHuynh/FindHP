package com.huynhtinh.android.findhp;

import android.app.Application;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.huynhtinh.android.findhp.data.database.PlaceContract.PlaceEntry;
import com.huynhtinh.android.findhp.data.database.PlaceDbHelper;

/**
 * Created by TINH HUYNH on 3/16/2018.
 */

public class FindHPApplication extends Application {

    private SQLiteDatabase mDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
//        initDatabase();
    }

//    public SQLiteDatabase getDatabase() {
//        return mDatabase;
//    }

    private void initDatabase() {
        PlaceDbHelper mDbHelper = new PlaceDbHelper(getApplicationContext());
        mDatabase = mDbHelper.getWritableDatabase();
        fakeData();
    }

    private void fakeData() {
        ContentValues values = new ContentValues();
        values.put(PlaceEntry.COL_NAME, "Happy Hospital");
        values.put(PlaceEntry.COL_ADDRESS, "Tan Binh District, Viet Nam");
        values.put(PlaceEntry.COL_ADDRESS, PlaceType.HOSPITAL.toString());

        mDatabase.insert(PlaceEntry.TABLE_NAME, null, values);
    }


}

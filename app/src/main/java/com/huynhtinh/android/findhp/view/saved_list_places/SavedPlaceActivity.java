package com.huynhtinh.android.findhp.view.saved_list_places;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.huynhtinh.android.findhp.PlaceType;
import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.database.Place;
import com.huynhtinh.android.findhp.data.database.PlaceContract.PlaceEntry;
import com.huynhtinh.android.findhp.data.database.PlaceDbHelper;

import java.util.ArrayList;
import java.util.List;

public class SavedPlaceActivity extends AppCompatActivity {

    private static final String TAG = "SavedPlaceActivity";

    PlaceDbHelper mDbHelper;

    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, SavedPlaceActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_place);

        mDbHelper = new PlaceDbHelper(getApplicationContext());
        fakeData();
        fetchAllPlaces();
    }

    private void fakeData() {
        ContentValues values = new ContentValues();
        values.put(PlaceEntry.COL_NAME, "Happy Hospital");
        values.put(PlaceEntry.COL_ADDRESS, "Tan Binh District, Viet Nam");
        values.put(PlaceEntry.COL_PLACE_TYPE, PlaceType.HOSPITAL.toString());

        mDbHelper.getWritableDatabase().insert(PlaceEntry.TABLE_NAME, null, values);
    }

    private void fetchAllPlaces() {
        String[] projection = {
                BaseColumns._ID,
                PlaceEntry.COL_NAME,
                PlaceEntry.COL_ADDRESS,
                PlaceEntry.COL_PLACE_TYPE,
        };

        String sortOrder =
                PlaceEntry.COL_NAME + " DESC";

        Cursor cursor = mDbHelper.getWritableDatabase().query(
                PlaceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<Place> places = new ArrayList<>();
        while (cursor.moveToNext()) {
            long id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(PlaceEntry._ID));
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlaceEntry.COL_NAME));
            String address = cursor.getString(
                    cursor.getColumnIndexOrThrow(PlaceEntry.COL_ADDRESS));
            PlaceType placeType = PlaceType.toPlaceType(cursor.getString(
                    cursor.getColumnIndexOrThrow(PlaceEntry.COL_PLACE_TYPE)));

            Place place = new Place(id, name, address, placeType);
            places.add(place);

        }
        cursor.close();

        Log.d(TAG, places.get(0).toString());

    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();

    }
}

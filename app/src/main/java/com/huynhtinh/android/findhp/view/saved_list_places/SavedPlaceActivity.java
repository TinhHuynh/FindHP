package com.huynhtinh.android.findhp.view.saved_list_places;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.*;
import com.huynhtinh.android.findhp.PlaceType;
import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.database.Place;
import com.huynhtinh.android.findhp.data.database.PlaceContract.PlaceEntry;
import com.huynhtinh.android.findhp.data.database.PlaceDbHelper;
import com.huynhtinh.android.findhp.view.place_detail.PlaceDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class SavedPlaceActivity extends ListActivity {

    private static final String TAG = "SavedPlaceActivity";

    PlaceDbHelper mDbHelper;

    public static Intent getIntent(Context packageContext) {
        return new Intent(packageContext, SavedPlaceActivity.class);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        TextView placeId = v.findViewById(R.id.place_id);
        TextView placeType = v.findViewById(R.id.text2);

        goToPlaceDetailScreen(placeId.getText().toString(), placeType.getText().toString());
    }

    private void goToPlaceDetailScreen(String placeId, String placeType) {
        Intent intent = PlaceDetailActivity.getIntent(this, placeId, placeType);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_place);

        mDbHelper = new PlaceDbHelper(getApplicationContext());
//        fakeData();
//        fetchAllPlaces();

        Cursor mCursor = getDatabaseCursor();
        startManagingCursor(mCursor);

        ListAdapter adapter = new SimpleCursorAdapter(
            this,
            R.layout.row_layout,
            mCursor,
            new String[] { PlaceEntry.COL_NAME, PlaceEntry.COL_PLACE_TYPE, PlaceEntry.COL_ADDRESS, PlaceEntry.COL_PLACE_ID },
            new int[] { R.id.text1, R.id.text2, R.id.text3, R.id.place_id }
        );

        setListAdapter(adapter);
    }

//    private void fakeData() {
//        ContentValues values = new ContentValues();
//        values.put(PlaceEntry.COL_PLACE_ID, "1");
//        values.put(PlaceEntry.COL_NAME, "Happy Hospital");
//        values.put(PlaceEntry.COL_ADDRESS, "Tan Binh District, Viet Nam");
//        values.put(PlaceEntry.COL_PLACE_TYPE, PlaceType.HOSPITAL.toString());
//
//        mDbHelper.getWritableDatabase().insert(PlaceEntry.TABLE_NAME, null, values);
//    }

//    private void fetchAllPlaces() {
//        String[] projection = {
//                BaseColumns._ID,
//                PlaceEntry.COL_PLACE_ID,
//                PlaceEntry.COL_NAME,
//                PlaceEntry.COL_ADDRESS,
//                PlaceEntry.COL_PLACE_TYPE,
//        };
//
//        String sortOrder =
//                PlaceEntry.COL_NAME + " DESC";
//
//        Cursor cursor = mDbHelper.getWritableDatabase().query(
//                PlaceEntry.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                sortOrder
//        );
//
//        List<Place> places = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            long id = cursor.getLong(
//                    cursor.getColumnIndexOrThrow(PlaceEntry._ID));
//            String name = cursor.getString(
//                    cursor.getColumnIndexOrThrow(PlaceEntry.COL_NAME));
//            String address = cursor.getString(
//                    cursor.getColumnIndexOrThrow(PlaceEntry.COL_ADDRESS));
//            PlaceType placeType = PlaceType.toPlaceType(cursor.getString(
//                    cursor.getColumnIndexOrThrow(PlaceEntry.COL_PLACE_TYPE)));
//
//            Place place = new Place(id, name, address, placeType);
//            places.add(place);
//
//        }
//        cursor.close();
//
//        Log.d(TAG, places.get(0).toString());
//
//    }

    private Cursor getDatabaseCursor() {
        String[] projection = {
            BaseColumns._ID,
            PlaceEntry.COL_PLACE_ID,
            PlaceEntry.COL_NAME,
            PlaceEntry.COL_ADDRESS,
            PlaceEntry.COL_PLACE_TYPE,
        };

        String sortOrder = PlaceEntry.COL_NAME + " DESC";

        return mDbHelper.getWritableDatabase().query(
            PlaceEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        );
    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }
}

package com.huynhtinh.android.findhp.view.saved_list_places;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.database.PlaceContract.PlaceEntry;
import com.huynhtinh.android.findhp.data.database.PlaceDbHelper;
import com.huynhtinh.android.findhp.view.place_detail.PlaceDetailActivity;

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
        TextView placeType = v.findViewById(R.id.place_type);

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

        Cursor mCursor = getDatabaseCursor();
        startManagingCursor(mCursor);

        ListAdapter adapter = new SimpleCursorAdapter(
            this,
            R.layout.row_layout,
            mCursor,
            new String[] { PlaceEntry.COL_NAME, PlaceEntry.COL_PLACE_TYPE, PlaceEntry.COL_ADDRESS, PlaceEntry.COL_PLACE_ID },
            new int[] { R.id.place_name, R.id.place_type, R.id.place_address, R.id.place_id }
        );

        setListAdapter(adapter);
    }

    private Cursor getDatabaseCursor() {
        String[] projection = {
            BaseColumns._ID,
            PlaceEntry.COL_PLACE_ID,
            PlaceEntry.COL_NAME,
            PlaceEntry.COL_ADDRESS,
            PlaceEntry.COL_PLACE_TYPE,
        };

        String sortOrder = PlaceEntry.COL_NAME + " ASC";

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

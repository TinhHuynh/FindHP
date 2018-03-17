package com.huynhtinh.android.findhp.data.database;

import android.provider.BaseColumns;

/**
 * Created by TINH HUYNH on 3/16/2018.
 */

public class PlaceContract {
    private PlaceContract() {
    }

    public static class PlaceEntry implements BaseColumns {
        public static final String TABLE_NAME = "saved_place";
        public static final String COL_NAME = "name";
        public static final String COL_ADDRESS = "address";
        public static final String COL_PLACE_TYPE = "place_type";
    }

}

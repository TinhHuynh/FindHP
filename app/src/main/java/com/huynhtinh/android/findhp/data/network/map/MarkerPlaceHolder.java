package com.huynhtinh.android.findhp.data.network.map;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by TINH HUYNH on 3/10/2018.
 */

public class MarkerPlaceHolder {
    private Marker mMarker;
    private Place mPlace;

    public MarkerPlaceHolder(Marker marker, Place place) {
        mMarker = marker;
        mPlace = place;
    }

    public Marker getMarker() {
        return mMarker;
    }

    public void setMarker(Marker marker) {
        mMarker = marker;
    }

    public Place getPlace() {
        return mPlace;
    }

    public void setPlace(Place place) {
        mPlace = place;
    }
}

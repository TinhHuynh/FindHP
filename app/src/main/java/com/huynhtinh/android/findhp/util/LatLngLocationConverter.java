package com.huynhtinh.android.findhp.util;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by TINH HUYNH on 3/10/2018.
 */

public class LatLngLocationConverter {

    public static LatLng convertLccationToLatLng(Location location) {
        if (location == null)
            return new LatLng(0, 0);
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        return new LatLng(lat, lng);
    }

    public static Location convertLatLngToLocation(LatLng latLng) {
        Location location = new Location("LatLngLocationConverter");
        if (latLng == null)
            return location;
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        return location;
    }

}

package com.huynhtinh.android.findhp.data.util;

import com.google.android.gms.maps.model.LatLng;
import com.huynhtinh.android.findhp.data.HPLocation;

/**
 * Created by TINH HUYNH on 3/12/2018.
 */

public class HPLocationUtils {
    public static LatLng convertHPLocationToLatLng(HPLocation location) {
        if (location == null)
            return new LatLng(0, 0);
        double lat = location.getLat();
        double lng = location.getLng();
        return new LatLng(lat, lng);
    }
}

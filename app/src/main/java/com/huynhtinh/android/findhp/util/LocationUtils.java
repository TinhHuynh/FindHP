package com.huynhtinh.android.findhp.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Locale;

/**
 * Created by TINH HUYNH on 3/10/2018.
 */

public class LocationUtils {

    public static String getFormattedAddressFromLocation(Context context, double lat, double lng) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                int maxAddressLineIndex = returnedAddress.getMaxAddressLineIndex();

                for (int i = 0; i <= maxAddressLineIndex - 1; i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(", ");
                }
                strReturnedAddress.append(returnedAddress.getAddressLine(maxAddressLineIndex))
                        .append(".");

                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    public static String parseParameterString(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }
}

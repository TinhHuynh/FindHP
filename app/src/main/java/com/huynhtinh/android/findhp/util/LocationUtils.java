package com.huynhtinh.android.findhp.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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

    public static LatLngBounds getLatLngBounds(Location location, int distanceInMeters) {
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = distanceInMeters / 1000.0 / degLatKm;
        double deltaLong = distanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;

        return new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong));
    }

    public static LatLngBounds getLatLngBounds(LatLng latLng, int distanceInMeters) {
        Location location = LatLngLocationConverter.convertLatLngToLocation(latLng);
        return getLatLngBounds(location, distanceInMeters);
    }


    public static String parseParameterString(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }
}

package com.huynhtinh.android.findhp.data.database;

import com.huynhtinh.android.findhp.data.PlaceType;

/**
 * Created by TINH HUYNH on 3/7/2018.
 */

public class Place {

    private String name;
    private String address;
    private PlaceType placeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }
}

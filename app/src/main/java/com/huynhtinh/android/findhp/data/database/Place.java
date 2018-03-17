package com.huynhtinh.android.findhp.data.database;

import android.support.annotation.NonNull;

import com.huynhtinh.android.findhp.PlaceType;

/**
 * Created by TINH HUYNH on 3/7/2018.
 */

public class Place {

    private long id;
    private String name;
    private String address;
    private PlaceType placeType;

    public Place(long id, String name, String address, @NonNull PlaceType placeType) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.placeType = placeType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return name + " // " + address + " // " + placeType;
    }
}

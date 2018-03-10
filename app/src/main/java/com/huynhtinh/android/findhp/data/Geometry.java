
package com.huynhtinh.android.findhp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.huynhtinh.android.findhp.data.Location;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}

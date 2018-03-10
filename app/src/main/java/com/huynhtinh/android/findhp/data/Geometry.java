
package com.huynhtinh.android.findhp.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    @Expose
    private HPLocation location;

    public HPLocation getLocation() {
        return location;
    }

    public void setLocation(HPLocation location) {
        this.location = location;
    }

}

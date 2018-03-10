
package com.huynhtinh.android.findhp.data.network.place_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlacesDetailResponse {

    @SerializedName("result")
    @Expose
    private Place place;

    public Place getResult() {
        return place;
    }

    public void setResult(Place result) {
        this.place = result;
    }

}

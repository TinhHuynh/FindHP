
package com.huynhtinh.android.findhp.data.network.map;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PlacesResponse {

    @SerializedName("results")
    @Expose
    private List<Place> results = null;

    public List<Place> getResults() {
        return results;
    }

    public void setResults(List<Place> results) {
        this.results = results;
    }

}

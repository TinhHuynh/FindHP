package com.huynhtinh.android.findhp.data.network.api;

import com.huynhtinh.android.findhp.data.network.map.PlacesResponse;
import com.huynhtinh.android.findhp.data.network.Configs;
import com.huynhtinh.android.findhp.data.network.place_detail.PlacesDetailResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by TINH HUYNH on 3/7/2018.
 */

public interface GoogleMapService {
    // Places
    @GET("place/nearbysearch/json?key="+ Configs.PLACES_SEARCH_API_KEY)
    Call<PlacesResponse> fetchPlaces(@Query("location") String location,
                                   @Query("radius") int radius,
                                   @Query("type") String type);

    @GET("/place/details/json?key=" + Configs.PLACE_DETAILS_API_KEY)
    Call<PlacesDetailResponse> fetchPlaceDetail(@Query("placeid") String placeId,
                                           @Query("radius") int radius,
                                           @Query("type") String type);

}

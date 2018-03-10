package com.huynhtinh.android.findhp.data.network.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by TINH HUYNH on 3/7/2018.
 */

public class GMapsClient {

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/";

    public static final GoogleMapService getClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapService service = retrofit.create(GoogleMapService.class);
        return service;
    }

}

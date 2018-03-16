package com.huynhtinh.android.findhp.view.place_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.network.api.GMapsClient;
import com.huynhtinh.android.findhp.data.network.api.GoogleMapService;
import com.huynhtinh.android.findhp.data.network.place_detail.Place;
import com.huynhtinh.android.findhp.data.network.place_detail.PlaceDetailResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailActivity extends AppCompatActivity {

    private static final String KEY_PLACE_ID = "place_id";
    private static final String TAG = "PlaceDetailActivity";

    private GoogleMapService mGoogleMapService = GMapsClient.getClient();
    private Place mPlace;

    public static Intent getIntent(Context packageContext, String placeId) {
        Intent intent = new Intent(packageContext, PlaceDetailActivity.class);
        intent.putExtra(KEY_PLACE_ID, placeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);
        String placeId = getIntent().getStringExtra(KEY_PLACE_ID);
        fetchPlaceByPlaceId(placeId);
    }

    private void fetchPlaceByPlaceId(String id) {
        Call<PlaceDetailResponse> call = mGoogleMapService.fetchPlaceDetail(id);
        onFetchPlaceByIdCall(call);
    }

    private void onFetchPlaceByIdCall(Call<PlaceDetailResponse> call) {
        call.enqueue(new Callback<PlaceDetailResponse>() {
            @Override
            public void onResponse(Call<PlaceDetailResponse> call, Response<PlaceDetailResponse> response) {
                if (response.isSuccessful()) {
                    mPlace = response.body().getResult();
                    Log.d(TAG, mPlace.toString());
                } else {
                    Log.d(TAG, response.message());
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailResponse> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }


}

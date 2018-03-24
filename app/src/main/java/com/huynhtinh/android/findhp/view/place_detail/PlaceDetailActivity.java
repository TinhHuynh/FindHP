package com.huynhtinh.android.findhp.view.place_detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.Photo;
import com.huynhtinh.android.findhp.data.network.Configs;
import com.huynhtinh.android.findhp.data.network.api.GMapsClient;
import com.huynhtinh.android.findhp.data.network.api.GoogleMapService;
import com.huynhtinh.android.findhp.data.network.place_detail.PhotoAdapter;
import com.huynhtinh.android.findhp.data.network.place_detail.Place;
import com.huynhtinh.android.findhp.data.network.place_detail.PlaceDetailResponse;
import com.huynhtinh.android.findhp.data.network.place_detail.ReviewsAdapter;
import com.huynhtinh.android.findhp.data.network.place_detail.WorkingDaysAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceDetailActivity extends AppCompatActivity {

    private static final String KEY_PLACE_ID = "place_id";
    private static final String TAG = "PlaceDetailActivity";

    private GoogleMapService mGoogleMapService = GMapsClient.getClient();
    private Place mPlace;

    private ImageView hpImageView;
    private TextView hpNameTv;
    private TextView hpAddressTv;
    private RatingBar hpRatingBar;
    private TextView rateIndexTv;
    private TextView phoneTv;
    private RecyclerView photoRv;
    private RecyclerView workingDaysRv;
    private TextView openNowTv;
    private RecyclerView reviewsRv;
    private LinearLayout contactLayout;

    public static Intent getIntent(Context packageContext, String placeId) {
        Intent intent = new Intent(packageContext, PlaceDetailActivity.class);
        intent.putExtra(KEY_PLACE_ID, placeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_place_detail);
        String placeId = getIntent().getStringExtra(KEY_PLACE_ID);
        fetchPlaceByPlaceId(placeId);

        hpImageView = (ImageView) findViewById(R.id.hp_image_view);
        hpNameTv = (TextView) findViewById(R.id.hp_name_text_view);
        hpAddressTv = (TextView) findViewById(R.id.hp_address_text_view);
        hpRatingBar = (RatingBar) findViewById(R.id.hp_rating_bar);
        rateIndexTv = (TextView) findViewById(R.id.rate_index_text_view);
        phoneTv = (TextView) findViewById(R.id.phone_number_text_view);
        photoRv = (RecyclerView) findViewById(R.id.photos_recycler_view);
        workingDaysRv = (RecyclerView) findViewById(R.id.working_days_recycler_view);
        openNowTv = (TextView) findViewById(R.id.openNowTv);
        reviewsRv = (RecyclerView) findViewById(R.id.reviews_recycler_view);
        contactLayout = (LinearLayout) findViewById(R.id.contact_linear_out);
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
                    if(mPlace.getPhotos() != null) {
                        Photo titlePhoto = mPlace.getPhotos().get(0);
                        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo"
                                + "?key=" + Configs.PLACE_API_KEY
                                + "&photoreference=" + titlePhoto.getPhotoReference()
                                + "&maxwidth=" + titlePhoto.getWidth()
                                + "&maxheight=" + titlePhoto.getHeight();
                        Picasso.with(PlaceDetailActivity.this).load(photoUrl).into(hpImageView);
                    }

                    hpNameTv.setText(mPlace.getName());
                    hpAddressTv.setText(mPlace.getAddress());
                    hpRatingBar.setEnabled(false);
                    hpRatingBar.setMax(5);
                    hpRatingBar.setStepSize(0.1f);
                    float rating = (mPlace.getRating() != null?Float.parseFloat(mPlace.getRating().toString()):0);
                    hpRatingBar.setRating(rating);
                    rateIndexTv.setText(String.valueOf(rating));

                    if(mPlace.getPhoneNumber() != null) {
                        phoneTv.setText(mPlace.getPhoneNumber());
                        contactLayout.setClickable(true);
                        contactLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPlace.getPhoneNumber()));
                                startActivity(intent);
                            }
                        });
                    }

                    if(mPlace.getPhotos() != null) {
                        ArrayList<Photo> photoList = new ArrayList<>();
                        photoList.addAll(mPlace.getPhotos());
                        PhotoAdapter adapter = new PhotoAdapter(PlaceDetailActivity.this, photoList);
                        photoRv.setHasFixedSize(true);
                        photoRv.setAdapter(adapter);
                        photoRv.setLayoutManager(new LinearLayoutManager(PlaceDetailActivity.this, LinearLayout.HORIZONTAL, false));
                    }

                    List<String> daytimeList = mPlace.getOpeningHours().getWeekdayText();
                    if(daytimeList != null) {
                        if(mPlace.getOpeningHours().getOpenNow()) {
                            openNowTv.setText("Now Opening");
                            openNowTv.setTextColor(Color.BLUE);
                        } else {
                            openNowTv.setText("Now Closing");
                            openNowTv.setTextColor(Color.RED);
                        }
                        WorkingDaysAdapter adapter = new WorkingDaysAdapter(PlaceDetailActivity.this, daytimeList);
                        workingDaysRv.setHasFixedSize(true);
                        workingDaysRv.setAdapter(adapter);
                        workingDaysRv.setLayoutManager(new LinearLayoutManager(PlaceDetailActivity.this));
                    }
                    if(mPlace.getReviews() != null) {
                        ReviewsAdapter adapter = new ReviewsAdapter(PlaceDetailActivity.this, mPlace.getReviews());
                        reviewsRv.setHasFixedSize(true);
                        reviewsRv.setAdapter(adapter);
                        reviewsRv.setLayoutManager(new LinearLayoutManager(PlaceDetailActivity.this));
                    }

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

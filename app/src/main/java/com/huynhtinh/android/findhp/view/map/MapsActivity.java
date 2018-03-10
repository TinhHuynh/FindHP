package com.huynhtinh.android.findhp.view.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.data.HPLocation;
import com.huynhtinh.android.findhp.data.PlaceType;
import com.huynhtinh.android.findhp.data.network.api.GMapsClient;
import com.huynhtinh.android.findhp.data.network.api.GoogleMapService;
import com.huynhtinh.android.findhp.data.network.map.MarkerPlaceHolder;
import com.huynhtinh.android.findhp.data.network.map.Place;
import com.huynhtinh.android.findhp.data.network.map.PlacesResponse;
import com.huynhtinh.android.findhp.util.LatLngLocationConverter;
import com.huynhtinh.android.findhp.util.LocationUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnSuccessListener<Location>,
        GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    private static final int LOCATION_PERMISSION_RC = 101;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 5 * 1000;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int MIN_DISTANCE_TO_UPDATE_LOCATION = 100; // meter
    private static final int DEFAULT_ZOOM_LEVEL = 13;
    private static final int DEFAULT_RADIUS = 5000;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng mCurrentLccation;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private String mCurrentAddresss;
    private Marker mTargetMarker;
    private GoogleMapService mGoogleMapService = GMapsClient.getClient();

    private List<MarkerPlaceHolder> mMarkerPlaceHolders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        grantLocationPermissionsIfNeeded();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();
        initLocationRequest();
        initLocationCallBack();
        getCurrentLocationSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMarkerClickListener(this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_RC: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length == 0 ||
                        grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showExplanationLocationPermissionDialog();
                }
            }
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            onLocationUpdated(location);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        showShortToast(mCurrentAddresss);
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    startLocationUpdates();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_CHECK_SETTINGS:
                    showCannotListenToLocationUpdates();
                    break;
            }
        }
    }

    private void onLocationUpdated(Location location) {
        updateRelatedCurrentLocationData(location);
        showShortToast(mCurrentAddresss);
        moveCamera(mCurrentLccation, DEFAULT_ZOOM_LEVEL);
        updateTargetMarker(mCurrentLccation);
        fetchPlaces(PlaceType.HOSPITAL);
    }

    private void fetchPlaces(PlaceType placeType) {
        String location = LocationUtils.parseParameterString(mCurrentLccation);

        Call<PlacesResponse> placesResponseCall = mGoogleMapService.fetchPlaces(
                location, DEFAULT_RADIUS, placeType.toString());
        placesResponseCall.request().url();
        onFetchPlacesCall(placesResponseCall);
    }

    private void onFetchPlacesCall(Call<PlacesResponse> call) {
        call.enqueue(new Callback<PlacesResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacesResponse> call, @NonNull Response<PlacesResponse> response) {
                handlePlacesList(response.body().getResults());
            }

            @Override
            public void onFailure(@NonNull Call<PlacesResponse> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void handlePlacesList(List<Place> places) {
        if (mMarkerPlaceHolders == null) {
            mMarkerPlaceHolders = new ArrayList<>();
        } else {
            mMarkerPlaceHolders.clear();
        }
        for (Place place : places) {
            Marker placeMarker = addPlaceMarker(place.getGeometry().getLocation());
            MarkerPlaceHolder holder = new MarkerPlaceHolder(placeMarker, place);
            mMarkerPlaceHolders.add(holder);
        }
    }

    private Marker addPlaceMarker(HPLocation location) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .position(new LatLng(location.getLat(), location.getLng()));
        return mMap.addMarker(options);
    }

    private void updateTargetMarker(LatLng latLng) {
        if (mTargetMarker != null) {
            mTargetMarker.remove();
        }
        MarkerOptions options = new MarkerOptions();
        options.position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mTargetMarker = mMap.addMarker(options);

    }

    private void moveCamera(LatLng latLng, int zoomLevel) {
        CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
        mMap.animateCamera(camera);
    }


    private void initLocationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }

                Location lastLocation = locationResult.getLastLocation();
                if (needUpdateCurrentLocation(lastLocation)) {
                    onLocationUpdated(lastLocation);
                }
            }
        };
    }

    private void updateRelatedCurrentLocationData(Location location) {
        mCurrentLccation = LatLngLocationConverter.convertLccationToLatLng(location);
        mLastLocation = location;
        mCurrentAddresss = LocationUtils.
                getFormattedAddressFromLocation(this,
                        location.getLatitude(), location.getLongitude());
    }

    private boolean needUpdateCurrentLocation(Location location) {
        return mLastLocation.distanceTo(location) >= MIN_DISTANCE_TO_UPDATE_LOCATION;
    }


    private void getLastKnownLocation() {
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this);
    }

    private void initLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void getCurrentLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                startLocationUpdates();
            }
        });
        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(MapsActivity.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }

            }
        });
    }

    private void showCannotListenToLocationUpdates() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_permission_not_granted)
                .setCancelable(true)
                .setMessage(R.string.msg_can_not_listen_update_changes)
                .create().show();
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
    }

    private void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    private void grantLocationPermissionsIfNeeded() {
        if (!checkLocationPermissionGranted()) {
            if (userAlreadyDeniedLocationPermission()) {
                showExplanationLocationPermissionDialog();
            } else {
                requestLocationPermission();
            }

        }
    }

    private boolean checkLocationPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    private boolean userAlreadyDeniedLocationPermission() {
        return ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private void showExplanationLocationPermissionDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_permission_not_granted)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestLocationPermission();
                    }
                })
                .setMessage(R.string.msg_permission_not_granted)
                .create().show();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},
                LOCATION_PERMISSION_RC);
    }

    private void showShortToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLongToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}

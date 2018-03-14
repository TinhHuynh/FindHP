package com.huynhtinh.android.findhp.view.map;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.huynhtinh.android.findhp.PlaceAutoCompleteAdapter;
import com.huynhtinh.android.findhp.PlaceType;
import com.huynhtinh.android.findhp.R;
import com.huynhtinh.android.findhp.SearchType;
import com.huynhtinh.android.findhp.data.HPLocation;
import com.huynhtinh.android.findhp.data.network.api.GMapsClient;
import com.huynhtinh.android.findhp.data.network.api.GoogleMapService;
import com.huynhtinh.android.findhp.data.network.map.MarkerPlaceHolder;
import com.huynhtinh.android.findhp.data.network.map.Place;
import com.huynhtinh.android.findhp.data.network.map.PlacesResponse;
import com.huynhtinh.android.findhp.data.util.HPLocationUtils;
import com.huynhtinh.android.findhp.route.AbstractRouting;
import com.huynhtinh.android.findhp.route.Route;
import com.huynhtinh.android.findhp.route.RouteException;
import com.huynhtinh.android.findhp.route.Routing;
import com.huynhtinh.android.findhp.route.RoutingListener;
import com.huynhtinh.android.findhp.util.BloodRouteDrawer;
import com.huynhtinh.android.findhp.util.LatLngLocationConverter;
import com.huynhtinh.android.findhp.util.LocationUtils;
import com.huynhtinh.android.findhp.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.AdapterView.OnItemClickListener;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnSuccessListener<Location>,
        GoogleMap.OnMarkerClickListener, RoutingListener, GoogleMap.OnMarkerDragListener, View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnItemClickListener {

    private static final int LOCATION_PERMISSION_RC = 101;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 5 * 1000;
    private static final int REQUEST_CHECK_SETTINGS = 102;
    private static final int MIN_DISTANCE_TO_UPDATE_LOCATION = 200; // meter
    private static final int DEFAULT_ZOOM_LEVEL = 12;
    private static final int DEFAULT_RADIUS = 5000;
    private static final int DEFAULT_ZOOM_BOUND_PADDING = 0;
    private static final float DEFAULT_POLYLINE_WIDTH = 7;
    private static final int DEFAULT_AUTO_COMPLETE_RADIUS = 50 * 1000; // 50 km
    private static final String LOG_TAG = "MapsActivity";
    private static final int DEFAULT_SELECTED_OPTION_MENU_ITEM = 3; // hospital


    @BindView(R.id.fab_current_location)
    FloatingActionButton mFabCurrentLocation;
    @BindView(R.id.fab_view_map)
    FloatingActionButton mFabViewMap;
    @BindView(R.id.fab_view_place)
    FloatingActionButton mFabViewPlace;
    @BindView(R.id.txt_search_location)
    AutoCompleteTextView mTxtSearchLocation;
    @BindView(R.id.btn_clear_auto_complete)
    ImageButton mBtnClearAutoComplete;
    @BindView(R.id.card_distance_duration)
    CardView mCardDistanceDuration;
    @BindView(R.id.txt_distance_duration)
    TextView mTxtDistanceDuration;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private LatLng mCurrentLocation;
    private LatLng mTargetLocation;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private String mTargetAddress;
    private Marker mTargetMarker;
    private GoogleMapService mGoogleMapService = GMapsClient.getClient();
    private GoogleApiClient mGoogleApiClient;

    private SearchType mCurrentSearchType = SearchType.CURRENT_LOCATION;
    private PlaceType mCurrentPlaceType = PlaceType.HOSPITAL;
    private List<MarkerPlaceHolder> mMarkerPlaceHolders;
    private List<Polyline> mPolylines;
    private MarkerPlaceHolder mSelectedHolder;
    private BloodRouteDrawer mBloodRouteDrawer = new BloodRouteDrawer();

    private PlaceAutoCompleteAdapter mAutoCompleteAdapter;

    private boolean alreadyAnimateDirectionComponents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

//        grantLocationPermissionsIfNeeded();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mTxtSearchLocation.setOnItemClickListener(this);
        mFabCurrentLocation.setOnClickListener(this);
        mBtnClearAutoComplete.setOnClickListener(this);
        mFabViewMap.setOnClickListener(this);

        initGoogleApiClient();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();
        initLocationRequest();
        initLocationCallBack();
        getCurrentLocationSettings();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_maps, menu);
        menu.getItem(DEFAULT_SELECTED_OPTION_MENU_ITEM).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        item.setChecked(!item.isChecked());
        clearPreviousSearchData();
        switch (item.getItemId()) {
            case R.id.item_find_dentist:
                checkPlaceTypeAndFetchPlaces(PlaceType.DENTIST);
                break;
            case R.id.item_find_doctor:
                checkPlaceTypeAndFetchPlaces(PlaceType.DOCTOR);
                break;
            case R.id.item_find_gym:
                checkPlaceTypeAndFetchPlaces(PlaceType.GYM);
                break;
            case R.id.item_find_hospital:
                checkPlaceTypeAndFetchPlaces(PlaceType.HOSPITAL);
                break;
            case R.id.item_find_pharmacy:
                checkPlaceTypeAndFetchPlaces(PlaceType.PHARMARCY);
                break;
            case R.id.item_find_spa:
                checkPlaceTypeAndFetchPlaces(PlaceType.SPA);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

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
            updateCurrentLocationAndFetchPlaces(location);
            initPlaceAutoCompleteAdapter();
        }
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.equals(mTargetMarker) && !mMarkerPlaceHolders.isEmpty()) {
            for (MarkerPlaceHolder holder : mMarkerPlaceHolders) {
                if (holder.getMarker().equals(marker)) {
                    mSelectedHolder = holder;
                    updatePlaceBoundUI();
                    break;
                }
            }
        }
        return true;
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

    @Override
    public void onMarkerDragEnd(Marker marker) {
        if (mTargetMarker.equals(marker)) {
            Location location = LatLngLocationConverter.convertLatLngToLocation(marker.getPosition());
            if (checkLocationIsFarEnoughFromLastLocaton(location)) {
                clearPreviousSearchData();
                mCurrentSearchType = SearchType.DRAG_LOCATION;
                mTargetLocation = marker.getPosition();
                updateTargetAddressMarker(true);
                fetchPlaces();
            } else {
                showShortToast(getString(R.string.msg_target_location_is_not_far));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_current_location:
                if (mCurrentSearchType != SearchType.CURRENT_LOCATION) {
                    clearPreviousSearchData();
                    mCurrentSearchType = SearchType.CURRENT_LOCATION;
                    startLocationUpdates();
                } else {
                    moveCamera(mCurrentLocation, DEFAULT_ZOOM_LEVEL);
//                    animatePlaceDirectionsComponentsComeOut();

                }
                break;
            case R.id.btn_clear_auto_complete:
                mTxtSearchLocation.setText("");
                break;
            case R.id.fab_view_map:
                openGoogleMapDirection();
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final PlaceAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
        final String placeId = String.valueOf(item.placeId);

        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                .getPlaceById(mGoogleApiClient, placeId);
        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
            @Override
            public void onResult(PlaceBuffer places) {
                if (!places.getStatus().isSuccess()) {
                    Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                    places.release();
                    return;
                }

                final com.google.android.gms.location.places.Place place = places.get(0);
                Location location = LatLngLocationConverter.convertLatLngToLocation(place.getLatLng());
                if (checkLocationIsFarEnoughFromLastLocaton(location)) {
                    clearPreviousSearchData();
                    mCurrentSearchType = SearchType.SEARCH_LOCATION;

                    mTargetLocation = place.getLatLng();
                    mTargetAddress = place.getAddress().toString();
                    updateTargetAddressMarker(false);
                    fetchPlaces();
                } else {
                    showShortToast(getString(R.string.msg_target_location_is_not_far));
                }
            }
        });
    }

    private void openGoogleMapDirection() {
        String startLocation = LocationUtils.getFormattedAddressFromLocation(this,
                mTargetLocation.latitude, mTargetLocation.longitude);


        String endLocation = LocationUtils.getFormattedAddressFromLocation(this,
                mSelectedHolder.getPlace().getGeometry().getLocation().getLat(),
                mSelectedHolder.getPlace().getGeometry().getLocation().getLng());

        Uri uri = Uri.parse("https://www.google.com/maps/dir/?api=1" +
                "&origin=" + startLocation +
                "&destination=" + endLocation +
                "&travelmode=driving");

        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    private void initPlaceAutoCompleteAdapter() {
        LatLngBounds latLngBounds = LocationUtils.getLatLngBounds(mCurrentLocation, DEFAULT_AUTO_COMPLETE_RADIUS);

        mAutoCompleteAdapter = new PlaceAutoCompleteAdapter(this, mGoogleApiClient,
                latLngBounds, null);

        mTxtSearchLocation.setAdapter(mAutoCompleteAdapter);
    }

    private void checkPlaceTypeAndFetchPlaces(PlaceType placeType) {
        if (mCurrentPlaceType != placeType) {
            mCurrentPlaceType = placeType;

            fetchPlaces();
        }
    }

    private void clearPreviousSearchData() {
        animatePlaceDirectionsComponentsComeOut();
        refreshPolylines();
        refreshPlaceMarkerHolders();
        switch (mCurrentSearchType) {
            case CURRENT_LOCATION:
                stopLocationUpdates();
                break;
            case DRAG_LOCATION:
                break;
            case SEARCH_LOCATION:
                clearSearchLocationText();
                break;
        }

    }

    private void clearSearchLocationText() {
        mTxtSearchLocation.setText("");
    }

    private void updatePlaceBoundUI() {
        if (mSelectedHolder == null) {
            return;
        }
        Marker destMarker = mSelectedHolder.getMarker();
        Place place = mSelectedHolder.getPlace();
        moveZoomCameraBound(destMarker);
        customizePlaceMarker(destMarker, place);

        animatePlaceDirectionsComponentsComeIn();

        LatLng placeLatLng = HPLocationUtils
                .convertHPLocationToLatLng(place.getGeometry()
                        .getLocation());

        fetchDirectionTo(placeLatLng);
    }

    private void animatePlaceDirectionsComponentsComeIn() {
        if (!alreadyAnimateDirectionComponents) {

            float cardStartValue = ScreenUtils.convertDpToPixel(60, this);
            float fabMapStartValue = ScreenUtils.convertDpToPixel(75, this);

            ObjectAnimator cardAnimation = ObjectAnimator.ofFloat(mCardDistanceDuration,
                    "translationY", cardStartValue, 0);

            ObjectAnimator fabMapAnimator = ObjectAnimator.ofFloat(mFabViewMap,
                    "translationX", fabMapStartValue, 0);

            ObjectAnimator fabViewAnimator = ObjectAnimator.ofFloat(mFabViewPlace,
                    "translationX", fabMapStartValue, 0);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.playTogether(cardAnimation, fabMapAnimator, fabViewAnimator);
            animatorSet.start();
            alreadyAnimateDirectionComponents = true;
        }
    }

    private void animatePlaceDirectionsComponentsComeOut() {
        if (alreadyAnimateDirectionComponents) {

            float cardEndValue = ScreenUtils.convertDpToPixel(60, this);
            float fabMapEndValue = ScreenUtils.convertDpToPixel(75, this);

            ObjectAnimator cardAnimation = ObjectAnimator.ofFloat(mCardDistanceDuration,
                    "translationY", 0, cardEndValue);

            ObjectAnimator fabMapAnimator = ObjectAnimator.ofFloat(mFabViewMap,
                    "translationX", 0, fabMapEndValue);

            ObjectAnimator fabViewAnimator = ObjectAnimator.ofFloat(mFabViewPlace,
                    "translationX", 0, fabMapEndValue);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(300);
            animatorSet.setInterpolator(new AccelerateInterpolator());
            animatorSet.playTogether(cardAnimation, fabMapAnimator, fabViewAnimator);
            animatorSet.start();
            alreadyAnimateDirectionComponents = false;
        }
    }


    private void moveZoomCameraBound(Marker marker) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mTargetLocation);
        builder.include(marker.getPosition());
        LatLngBounds bounds = builder.build();

        //padding
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        int padding = (int) (displayMetrics.widthPixels * 0.3);

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);

    }

    private void moveZoomCameraBound(List<Marker> markers) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mTargetLocation);
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_ZOOM_BOUND_PADDING);
        mMap.animateCamera(cu);
    }

    private void customizePlaceMarker(Marker marker, Place place) {
        marker.setTitle(place.getName());
        marker.setSnippet(place.getVicinity());
        marker.showInfoWindow();
    }

    private void fetchDirectionTo(LatLng destLatLng) {
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(mTargetLocation, destLatLng)
                .build();
        routing.execute();
    }

    private void updateCurrentLocationAndFetchPlaces(Location location) {
        mCurrentLocation = LatLngLocationConverter.convertLocationToLatLng(location);
        mTargetLocation = mCurrentLocation;
        moveCamera(mCurrentLocation, DEFAULT_ZOOM_LEVEL);
        updateTargetAddressMarker(true);
        fetchPlaces();
    }

    private void updateTargetAddressMarker(boolean needToUpdateAddress) {
        if (needToUpdateAddress)
            mTargetAddress = LocationUtils.
                    getFormattedAddressFromLocation(this,
                            mTargetLocation.latitude, mTargetLocation.longitude);
        showShortToast(mTargetAddress);
        updateTargetMarker(mTargetLocation);
    }

    private void fetchPlaces() {
        if (mTargetLocation == null) {
            return;
        }

        String location = LocationUtils.parseParameterString(mTargetLocation);

        Call<PlacesResponse> placesResponseCall = mGoogleMapService.fetchPlaces(
                location, DEFAULT_RADIUS, mCurrentPlaceType.toString());
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
        refreshPlaceMarkerHolders();
        List<Marker> markers = new ArrayList<>();
        int iconRes = getIconResForMarker();
        for (Place place : places) {
            Marker placeMarker = addPlaceMarker(place.getGeometry().getLocation(), iconRes);
            MarkerPlaceHolder holder = new MarkerPlaceHolder(placeMarker, place);
            mMarkerPlaceHolders.add(holder);
            markers.add(placeMarker);
        }
        moveZoomCameraBound(markers);
    }


    private Marker addPlaceMarker(HPLocation location, @DrawableRes int resIcon) {
        MarkerOptions options = new MarkerOptions();
        options.icon(BitmapDescriptorFactory
                .fromResource(resIcon))
                .position(new LatLng(location.getLat(), location.getLng()));
        return mMap.addMarker(options);
    }

    private int getIconResForMarker() {
        switch (mCurrentPlaceType) {
            case DENTIST:
                return R.drawable.ic_dentist;
            case DOCTOR:
                return R.drawable.ic_doctor;
            case GYM:
                return R.drawable.ic_gym;
            case HOSPITAL:
                return R.drawable.ic_hospital;
            case PHARMARCY:
                return R.drawable.ic_pharmacy;
            case SPA:
                return R.drawable.ic_spa;
            default:
                return R.drawable.ic_hospital;
        }
    }

    private void updateTargetMarker(LatLng latLng) {
        if (mTargetMarker != null) {
            mTargetMarker.remove();
        }
        MarkerOptions options = new MarkerOptions();
        options.position(latLng)
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mTargetMarker = mMap.addMarker(options);

    }

    private void moveCamera(LatLng latLng, int zoomLevel) {
        if (latLng != null) {
            CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel);
            mMap.animateCamera(camera);
        }
    }


    private void initLocationCallBack() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }
                if (mCurrentSearchType == SearchType.CURRENT_LOCATION) {
                    Location location = locationResult.getLastLocation();
                    if (checkLocationIsFarEnoughFromLastLocaton(location)) {
                        updateCurrentLocationAndFetchPlaces(location);
                    }
                }
            }
        };
    }

    private boolean checkLocationIsFarEnoughFromLastLocaton(Location location) {
        Location targetLocation = LatLngLocationConverter.convertLatLngToLocation(mTargetLocation);
        return targetLocation.distanceTo(location) >= MIN_DISTANCE_TO_UPDATE_LOCATION;
    }


    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            grantLocationPermissionsIfNeeded();
            return;
        }
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
                .create()
                .show();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
//            grantLocationPermissionsIfNeeded();
            return;
        }
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
                        dialog.dismiss();
                    }
                })
                .setMessage(R.string.msg_permission_not_granted)
                .create()
                .show();
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

    private void refreshPlaceMarkerHolders() {
        if (mMarkerPlaceHolders == null) {
            mMarkerPlaceHolders = new ArrayList<>();
        } else {
            if (!mMarkerPlaceHolders.isEmpty()) {
                for (MarkerPlaceHolder holder : mMarkerPlaceHolders) {
                    holder.getMarker().remove();
                }
            }
            mMarkerPlaceHolders.clear();

        }
    }

    private void refreshPolylines() {
//        if (mPolylines == null) {
//            mPolylines = new ArrayList<>();
//        } else {
//            if (mPolylines.size() > 0) {
//                for (Polyline poly : mPolylines) {
//                    poly.remove();
//                }
//            }
//            mPolylines.clear();
//        }
        mBloodRouteDrawer.clearAnimate();
    }

    @Override
    public void onRoutingFailure(RouteException e) {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
        refreshPolylines();

        String distanceAndDuration = route.get(0).getDistanceText() + " - " +
                route.get(0).getDurationText();

        mTxtDistanceDuration.setText(distanceAndDuration);
        drawRoute(route.get(0));

    }

    private void drawRoute(Route route) {

        mBloodRouteDrawer.animateRoute(mMap, route.getPoints(), ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryDark), (int) DEFAULT_POLYLINE_WIDTH);
//        PolylineOptions polyOptions = new PolylineOptions();
//        polyOptions.color(ContextCompat.getColor(this, R.color.md_blue_300));
//        polyOptions.width(DEFAULT_POLYLINE_WIDTH);
//        polyOptions.addAll(route.getPoints());
//        Polyline polyline = mMap.addPolyline(polyOptions);
//        mPolylines.add(polyline);
    }


    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        refreshPolylines();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


}

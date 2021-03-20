package com.example.go4lunch.views.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.R;
import com.example.go4lunch.models.NearbySearch.NearbySearch;
import com.example.go4lunch.models.NearbySearch.Result;
import com.example.go4lunch.models.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.models.PlaceAutocomplete.Prediction;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;
import com.example.go4lunch.presenters.Go4LunchStreams;
import com.example.go4lunch.views.activities.RestaurantDetailActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;


public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationSource.OnLocationChangedListener {

    private static final String TAG = MapsFragment.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;

    // The entry point to the Places API.

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;


    @BindString(R.string.type)
    public String type;
    @BindInt(R.integer.radius)
    public int radius;

    private Disposable mDisposable;
    private List<Result> mRestaurants = new ArrayList<>();
    private Marker mMarker;
    private String mLocation;
    private List<Prediction> mPredictionList;
    int SEARCH_QUERY_THRESHOLD = 3;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Build the map.
        SupportMapFragment mMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);

    }
    @OnClick(R.id.FloatingLocationButton)
    public void displayLocation(){
        getDeviceLocation();
    }



    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_view, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() >= SEARCH_QUERY_THRESHOLD){
                    // executeAutocompleteHttpRequestWithRetrofit(query);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= SEARCH_QUERY_THRESHOLD){
                    //executeAutocompleteHttpRequestWithRetrofit(newText);

                }
                return true;
            }
        });
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            mLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                            Log.e("TAG", "Location/devicelocation" + mLocation);
                            executeHttpRequestWithRetrofit(mLocation);
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                Log.e("TAG", "Location/devicelocation" + mLocation);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }




    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                map.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity().getApplicationContext(),R.raw.style_json));
                map.getUiSettings().setMapToolbarEnabled(false);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
        getDeviceLocation();
    }



    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void displayNearbyRestaurantMarker(List<Result> results){
        if (map != null) {
            map.clear();
            for (Result result : results) {
                 MarkerOptions options = new MarkerOptions().position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                        .title(result.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.background_lunch));
                 map.addMarker(options).setTag(result.getPlaceId());
                 Log.e("testId","testid" + result.getPlaceId());
                Log.e("testmarker","loc" + result.getGeometry().getLocation().getLat()+ result.getGeometry().getLocation().getLng());
            }

        }else {
            for (Result result : results) {
                MarkerOptions options = new MarkerOptions().position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                        .title(result.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.background_lunch));
                map.addMarker(options).setTag(result.getId());
            }
        }
        map.setOnInfoWindowClickListener(marker -> {
            String placeId = (String) marker.getTag();
            Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
            intent.putExtra("placeID",placeId);
            startActivity(intent);
        });

    }



    public void executeAutocompleteHttpRequestWithRetrofit(String query){
        Disposable disposable = Go4LunchStreams.streamFetchAutocomplete(query,mLocation,radius).subscribeWith(new DisposableObserver<PlaceAutocomplete>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull PlaceAutocomplete placeAutocomplete) {
                mPredictionList = placeAutocomplete.getPredictions();
                Log.e("Tag",query + "test" + placeAutocomplete.getPredictions().get(0).getDescription());
                String placeID = placeAutocomplete.getPredictions().get(0).getPlaceId();
                executeHttpRequestForRetrievePlaceDetailData(placeID);

            }
            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    public void executeHttpRequestWithRetrofit(String location){
        Log.e("TAG","LocationOnHttpRequest" + location);
        Disposable disposable = Go4LunchStreams.streamFetchRestaurants(location,radius,type).subscribeWith(new DisposableObserver<NearbySearch>() {
            @Override
            public void onNext(@NotNull NearbySearch nearbySearch) {
                Log.e("MapFragmentNearbyList","sizeList :" + nearbySearch.getResults().size());
                mRestaurants.addAll(nearbySearch.getResults());
                displayNearbyRestaurantMarker(mRestaurants);
                Log.e("TAG","RestaurantListInHttpRequest" + mRestaurants.size());
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void executeHttpRequestForRetrievePlaceDetailData(String placeID){
        Disposable disposable = Go4LunchStreams.streamFetchDetails(placeID).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(@io.reactivex.rxjava3.annotations.NonNull PlaceDetail placeDetail) {
                Double latitude = placeDetail.getResult().getGeometry().getLocation().getLat();
                Double longitude = placeDetail.getResult().getGeometry().getLocation().getLng();
                String searchLocation = (latitude + "," + longitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude),DEFAULT_ZOOM));
                executeHttpRequestWithRetrofit(searchLocation);

            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.e("error","error" + e);

            }

            @Override
            public void onComplete() {

            }
        });

    }




    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        //executeHttpRequestWithRetrofit(mLocation);
        Log.e("test","onmapready");





        // Get the current location of the device and set the position of the map.

    }

    @Override
    public void onLocationChanged(Location location) {



        Log.e("onLocationChanged", "test" + location);

    }
}
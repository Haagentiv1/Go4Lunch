package com.example.go4lunch.views.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.adapters.PlacesAdapter;
import com.example.go4lunch.models.NearbySearch.NearbySearch;
import com.example.go4lunch.models.NearbySearch.Result;
import com.example.go4lunch.presenters.Go4LunchStreams;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.views.activities.RestaurantDetailActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

import static android.content.ContentValues.TAG;

/**
 * A fragment representing a list of Items.
 */
public class ItemRestaurant extends Fragment implements LocationSource.OnLocationChangedListener {




    private static final String key = BuildConfig.API_KEY;

    @BindString(R.string.type)
    public String type;


    public Double mLatitude = 48.85661;


    public Double mLongitude = 2.35222;

    @BindInt(R.integer.radius)
    public int radius;

    @BindView(R.id.list_restaurant)
    RecyclerView mRecyclerView;

    private PlacesAdapter mAdapter;

    private List<Result> mRestaurants = new ArrayList<>();

    private String mLocation =   mLatitude + "," + mLongitude;

    private Disposable mDisposable;

    private Location lastKnownLocation;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemRestaurant() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemRestaurant newInstance(int columnCount) {
        ItemRestaurant fragment = new ItemRestaurant();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_restaurant, container, false);
        ButterKnife.bind(this,view);
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getDeviceLocation();
        executeHttpRequestWithRetrofitDetail();
        return view;
    }

    public void configureRecyclerView(){
        mRestaurants = new ArrayList<>();
        mAdapter = new PlacesAdapter(this.mRestaurants, Glide.with(this),mLatitude,mLongitude);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL));
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView,R.layout.fragment_item_list_restaurant)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        String placeId = mAdapter.getResult(position);
                        Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                        intent.putExtra("placeID", placeId);
                        startActivity(intent);
                    }
                });
    }

    private void executeHttpRequestWithRetrofitDetail(){
        mDisposable = Go4LunchStreams.streamFetchRestaurants(mLocation,radius,type).subscribeWith(new DisposableObserver<NearbySearch>() {

            @Override
            public void onNext(@NonNull NearbySearch nearbySearch) {
                Log.e("Tag","detailResultListInHttpRequest" + nearbySearch.getResults().size());
                updateUiWithPlaceDetail(nearbySearch.getResults());

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("Tag","Error"+e);

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void updateUiWithPlaceDetail(List<Result> placeDetails){
        mRestaurants.clear();
        mRestaurants.addAll(placeDetails);
        mAdapter.notifyDataSetChanged();

    }
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            mLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                            Log.e("TAG", "Location/devicelocation" + mLocation);
                            executeHttpRequestWithRetrofitDetail();
                            if (lastKnownLocation != null) {

                                Log.e("TAG", "Location/devicelocation" + mLocation);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }









    @Override
    public void onLocationChanged(Location location) {
        mLocation = location.getLatitude() + "," + location.getLongitude();
        Log.e("TAG","onLocationChanged" + mLocation);


    }
}
package com.example.go4lunch.views.fragments;

import android.content.Intent;
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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.adapters.PlacesAdapter;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;
import com.example.go4lunch.models.User;
import com.example.go4lunch.presenters.Go4LunchStreams;
import com.example.go4lunch.utils.ItemClickSupport;
import com.example.go4lunch.views.activities.RestaurantDetailActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;

import static android.content.ContentValues.TAG;

/**
 * A fragment representing a list of Items.
 */
public class ItemRestaurant extends Fragment implements LocationSource.OnLocationChangedListener {


    @BindString(R.string.type)
    public String type;
    public Double mLatitude;
    public Double mLongitude;
    @BindInt(R.integer.radius)
    public int radius;
    @BindView(R.id.list_restaurant)
    RecyclerView mRecyclerView;
    int SEARCH_QUERY_THRESHOLD = 3;
    private PlacesAdapter mAdapter;
    private List<PlaceDetail> mRestaurants = new ArrayList<>();
    private String mLocation =   mLatitude + "," + mLongitude;
    private Disposable mDisposable;
    private Location lastKnownLocation;
    private final boolean locationPermissionGranted = true;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private List<User> usersList = new ArrayList<>();
    @BindView(R.id.list_restaurant_swipe)
    public SwipeRefreshLayout swipeRefreshLayout;
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
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list_restaurant, container, false);
        ButterKnife.bind(this,view);
        setHasOptionsMenu(true);
        getDeviceLocation();
        getUserList();
        this.configureRecyclerView();
        this.configureOnClickRecyclerView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.search_view,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() >= SEARCH_QUERY_THRESHOLD){
                    executeHttpRequestWithRetrofitAutocompleteAndPlaceDetail(query);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= SEARCH_QUERY_THRESHOLD) {
                    executeHttpRequestWithRetrofitAutocompleteAndPlaceDetail(newText);
                }
                return true;
            }
        });
    }

    public void configureRecyclerView(){
        mRestaurants = new ArrayList<>();
        mAdapter = new PlacesAdapter(this.mRestaurants,usersList, Glide.with(this),mLatitude,mLongitude);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(),DividerItemDecoration.VERTICAL));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                executeHttpRequestWithRetrofitNearbyDetailRestaurant(mLocation);
            }
        });
    }

    private void configureOnClickRecyclerView(){
        ItemClickSupport.addTo(mRecyclerView,R.layout.fragment_item_list_restaurant)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    String placeId = mAdapter.getResult(position);
                    Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                    intent.putExtra("placeID", placeId);
                    startActivity(intent);
                });
    }

    private void executeHttpRequestWithRetrofitNearbyDetailRestaurant(String mLocation){
        mDisposable = Go4LunchStreams.streamFetchRestaurantsDetails(mLocation,radius,type).subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {
            @Override
            public void onSuccess(@NonNull List<PlaceDetail> placeDetails) {
                updateUiWithPlaceDetail(placeDetails);
                Log.e("tag",placeDetails.get(0).getResult().getAddressComponents().get(0).getLongName());
                if (swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("Tag","error nearbydetail" + e);
            }
        });
    }

    private void executeHttpRequestWithRetrofitAutocompleteAndPlaceDetail(String input){
        mDisposable = Go4LunchStreams.streamFetchAutoCompleteRestaurantDetails(input,mLocation,radius).subscribeWith(new DisposableSingleObserver<List<PlaceDetail>>() {
            @Override
            public void onSuccess(@NonNull List<PlaceDetail> placeDetails) {
                updateUiWithPlaceDetail(placeDetails);
            }
            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getUserList() {
        UserHelper.getUsers().addOnCompleteListener(task -> {
            usersList = task.getResult().toObjects(User.class);
            mAdapter.setUserList(usersList);
        });
    }

    private void updateUiWithPlaceDetail(List<PlaceDetail> placeDetails){
        mRestaurants.clear();
        mRestaurants.addAll(placeDetails);
        mAdapter.notifyDataSetChanged();

    }

    private void getDeviceLocation() {
        Log.e("tag","location getdevicelocation");
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        mLocation = lastKnownLocation.getLatitude() + "," + lastKnownLocation.getLongitude();
                        mLatitude = lastKnownLocation.getLatitude();
                        mLongitude = lastKnownLocation.getLongitude();
                        mAdapter.setUserLatitude(mLatitude);
                        mAdapter.setUserLongitude(mLongitude);
                        executeHttpRequestWithRetrofitNearbyDetailRestaurant(mLocation);
                        Log.e("TAG", "Location/devicelocation" + mLocation);

                        if (lastKnownLocation != null) {

                            Log.e("TAG", "Location/devicelocation" + mLocation);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());

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
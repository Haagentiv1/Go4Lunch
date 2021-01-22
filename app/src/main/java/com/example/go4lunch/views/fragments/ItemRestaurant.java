package com.example.go4lunch.views.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.adapters.PlacesAdapter;
import com.example.go4lunch.models.NearbySearch.NearbySearch;
import com.example.go4lunch.models.NearbySearch.Result;
import com.example.go4lunch.presenters.Go4LunchStreams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

/**
 * A fragment representing a list of Items.
 */
public class ItemRestaurant extends Fragment  {




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
        executeHttpRequestWithRetrofit();
        return view;
    }

    public void configureRecyclerView(){
        mRestaurants = new ArrayList<>();
        mRecyclerView.setAdapter(new PlacesAdapter(mRestaurants));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    private void executeHttpRequestWithRetrofit(){
        mDisposable = Go4LunchStreams.streamFetchRestaurants(mLocation,radius,type).subscribeWith(new DisposableObserver<NearbySearch>() {

            @Override
            public void onNext(@NonNull NearbySearch nearbySearch) {
                Log.e("Tag", "OnNext"+nearbySearch.getStatus());

                mRestaurants.addAll(nearbySearch.getResults());
                mRecyclerView.setAdapter(new PlacesAdapter(mRestaurants));
                Log.e("Tag","Result"+mRestaurants.size());

            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {


            }
        });
    }



    
}
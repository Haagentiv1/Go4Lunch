package com.example.go4lunch.presenters;

import com.example.go4lunch.apiService.ApiService;
import com.example.go4lunch.apiService.RetrofitService;
import com.example.go4lunch.models.NearbySearch.NearbySearch;
import com.example.go4lunch.models.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Go4LunchStreams {


    public static Observable<NearbySearch> streamFetchRestaurants(String location, int radius, String type){
        ApiService apiService = RetrofitService.retrofit.create(ApiService.class);
        return apiService.getRestaurants(location, radius, type).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10 ,TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetail> streamFetchDetails(String placeId){
        ApiService apiService = RetrofitService.retrofit.create(ApiService.class);
        return apiService.getPlaceDetails(placeId).
                observeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10,TimeUnit.SECONDS);
    }

    public static Observable<PlaceAutocomplete> streamFetchAutocomplete(String input,String location, String radius, String type){
        ApiService apiService = RetrofitService.retrofit.create(ApiService.class);
        return apiService.getPlaceAutoComplete(input, location, radius, type).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10,TimeUnit.SECONDS);
    }









}

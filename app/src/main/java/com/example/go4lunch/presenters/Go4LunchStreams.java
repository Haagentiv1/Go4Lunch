package com.example.go4lunch.presenters;

import com.example.go4lunch.WebService.ApiService;
import com.example.go4lunch.WebService.RetrofitService;
import com.example.go4lunch.models.NearbySearch.NearbySearch;
import com.example.go4lunch.models.NearbySearch.Result;
import com.example.go4lunch.models.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.models.PlaceAutocomplete.Prediction;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class Go4LunchStreams {


    public static Observable<NearbySearch> streamFetchRestaurants(String location, int radius, String type) {
        ApiService apiService = RetrofitService.retrofit.create(ApiService.class);
        return apiService.getRestaurants(location, radius, type).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetail> streamFetchDetails(String placeId) {
        ApiService apiService = RetrofitService.retrofit.create(ApiService.class);
        return apiService.getPlaceDetails(placeId).
                observeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceAutocomplete> streamFetchAutocomplete(String input, String location, int radius) {
        ApiService apiService = RetrofitService.retrofit.create(ApiService.class);
        return apiService.getPlaceAutoComplete(input, location, radius).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                timeout(10, TimeUnit.SECONDS);
    }

    public static  Single<List<PlaceDetail>> streamFetchRestaurantsDetails(String location, int radius, String type) {
        return streamFetchRestaurants(location, radius, type).flatMapIterable(new Function<NearbySearch, List<Result>>() {

            @Override
            public List<Result> apply(NearbySearch nearbySearch) throws Throwable {
                return nearbySearch.getResults();
            }
        }).flatMap(new Function<Result, Observable<PlaceDetail>>() {
            @Override
            public Observable<PlaceDetail> apply(Result result) throws Throwable {
                return streamFetchDetails(result.getPlaceId());
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<List<PlaceDetail>> streamFetchAutoCompleteRestaurantDetails(String input, String location, int radius){
        return streamFetchAutocomplete(input, location, radius).flatMapIterable(new Function<PlaceAutocomplete, List<Prediction>>() {
            @Override
            public List<Prediction> apply(PlaceAutocomplete placeAutocomplete) throws Throwable {
                return placeAutocomplete.getPredictions();
            }
        }).flatMap(new Function<Prediction, ObservableSource<PlaceDetail>>() {
            @Override
            public ObservableSource<PlaceDetail> apply(Prediction prediction) throws Throwable {
                return streamFetchDetails(prediction.getPlaceId());
            }
        }).toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers
                        .mainThread());
    }
}


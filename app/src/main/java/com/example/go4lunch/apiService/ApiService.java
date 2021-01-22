package com.example.go4lunch.apiService;

import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.models.NearbySearch.NearbySearch;
import com.example.go4lunch.models.PlaceAutocomplete.PlaceAutocomplete;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    String key = BuildConfig.API_KEY;


    @GET("nearbysearch/json?key="+key)
     Observable <NearbySearch> getRestaurants(@Query("location") String location,
                                                   @Query("radius") Integer radius,
                                                   @Query("type") String type);
    @GET("details/json?key="+key)
    Observable<PlaceDetail> getPlaceDetails(@Query("place_id")String placeId);

    @GET("autocomplete/json?key="+key)
    Observable<PlaceAutocomplete> getPlaceAutoComplete(@Query("input")String input,
                                                       @Query("location")String location,
                                                       @Query("radius")String radius,
                                                       @Query("type")String type);



}



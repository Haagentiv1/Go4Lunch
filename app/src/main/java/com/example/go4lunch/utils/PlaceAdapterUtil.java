package com.example.go4lunch.utils;

import android.location.Location;
import android.util.Log;

import com.example.go4lunch.R;
import com.example.go4lunch.models.PlaceDetail.Result;
import com.example.go4lunch.models.User;

import java.util.List;

public class PlaceAdapterUtil {

    public static String getPositionBetweenPlaceAndUSer(Location userLocation, Result restaurant){
        Location placeLocation = new Location("");
        placeLocation.setLatitude(restaurant.getGeometry().getLocation().getLat());
        placeLocation.setLongitude(restaurant.getGeometry().getLocation().getLng());
        float DistanceInMeter = userLocation.distanceTo(placeLocation);
        Log.e("Tag", String.valueOf(DistanceInMeter));
        return String.valueOf(Math.round(DistanceInMeter));
    }

    public static float divideRestaurantRatingBy3(List<User> userList,Result restaurant){
        Double restaurantRating = restaurant.getRating();
        if (restaurantRating == null){
            return 0;
        }
        else return (float) ((restaurantRating / 5) * 3);
    }

    public static int howManyWorkmatesEatAtThisRestaurant(List<User>userList,Result restaurant){
        int howManyWorkmates = 0;
        for (User user : userList){
            if (user.getChosenRestaurant() != null){
                if (user.getChosenRestaurant().equals(restaurant.getPlaceId())){
                    howManyWorkmates += 1;
                }
            }
        }return howManyWorkmates;
    }

    public static int getOpeningHourString(Result restaurant){
        if (restaurant.getOpeningHours() != null){
            if (restaurant.getOpeningHours().getOpenNow()){
                return R.string.restaurant_open;
            }
            else return R.string.restaurant_close;
        }
        return R.string.no_hour_data;
    }

}

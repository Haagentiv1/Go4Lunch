package com.example.go4lunch.adapters;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;
import com.example.go4lunch.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private final List<PlaceDetail> placeDetailList;
    private List<User> userList;
    private final RequestManager glide;
    private Double userLatitude;
    private Double userLongitude;
    private final Location userLocation = new Location("");
    private final Location placeLocation = new Location("");

    public PlacesAdapter(@NonNull List<PlaceDetail> places, List<User> userList, RequestManager glide, Double userLatitude, Double userLongitude) {
        this.placeDetailList = places;
        this.glide = glide;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
        this.userList = userList;
    }
    public void setUserList(List<User> userList){
        this.userList = userList;
    }

    public void setUserLatitude(Double userLatitude){
        this.userLatitude = userLatitude;
    }

    public void setUserLongitude(Double userLongitude) {
        this.userLongitude = userLongitude;
    }




    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    public String getResult(int position){
        return this.placeDetailList.get(position).getResult().getPlaceId();
    }

    public float divideRating(Double rating){
        if (rating == null){
            return 0;
        }
        else return (float) ((rating / 5) * 3);
    }

    public int getHowManyWorkmatesForRestaurant(int position){
        int howManyWorkmates = 0;
        for (User user : userList){
            if (user.getChosenRestaurant() != null){
            if (user.getChosenRestaurant().equals(placeDetailList.get(position).getResult().getPlaceId())){
                howManyWorkmates += 1;
            }
            }
        }return howManyWorkmates;
    }
    public void setOpeningHour(int position) {
        if (placeDetailList.get(position).getResult().getOpeningHours() != null) {
            Date date = new Date();
            Log.e("Tag", String.valueOf(placeDetailList.get(position).getResult().getOpeningHours().getPeriods().get(0).getOpen().getTime()));
            Log.e("TAG", placeDetailList.get(position).getResult().getOpeningHours().getWeekdayText().get(6));
        }
    }




    public String getDistanceBetweenUserLocationAndPlace(int adapterPosition){
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);
        placeLocation.setLatitude(placeDetailList.get(adapterPosition).getResult().getGeometry().getLocation().getLat());
        placeLocation.setLongitude(placeDetailList.get(adapterPosition).getResult().getGeometry().getLocation().getLng());
        float distanceInMeter = userLocation.distanceTo(placeLocation);
        return String.valueOf(Math.round(distanceInMeter));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        setOpeningHour(position);
        holder.mName.setText(placeDetailList.get(position).getResult().getName());
        holder.mAddress.setText(placeDetailList.get(position).getResult().getFormattedAddress());
        holder.mRating.setRating(divideRating(placeDetailList.get(position).getResult().getRating()));
        holder.mRestaurantDistance.setText(String.format("%sm", getDistanceBetweenUserLocationAndPlace(position)));
        holder.mRestaurantWorkmates.setText("(" + getHowManyWorkmatesForRestaurant(position) + ")");
        //holder.mRestaurantOpenHour.setText(mPlaces.get(position).getOpeningHours().getOpenNow().toString());
        if (placeDetailList.get(position).getResult().getPhotos() != null && !placeDetailList.get(position).getResult().getPhotos().isEmpty()){
            glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + placeDetailList.get(position).getResult().getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY).into(holder.mRestaurantPicture);
             }else {holder.mRestaurantPicture.setImageResource(R.drawable.image_not_available);}

    }
    //https://stackoverflow.com/questions/13524834/google-place-api-placedetails-photo-reference


    @Override
    public int getItemCount() {
        return placeDetailList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.text_restaurant_name)
        TextView mName;

        @BindView(R.id.text_restaurant_address)
        TextView mAddress;

        @BindView(R.id.restaurant_ratingBar)
        RatingBar mRating;

        @BindView(R.id.picture_restaurant)
        ImageView mRestaurantPicture;

        @BindView(R.id.text_restaurant_distance)
        TextView mRestaurantDistance;

        @BindView(R.id.text_restaurant_openHour)
        TextView mRestaurantOpenHour;

        @BindView(R.id.restaurant_workmates)
        TextView mRestaurantWorkmates;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
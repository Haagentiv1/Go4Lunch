package com.example.go4lunch.adapters;

import android.annotation.SuppressLint;
import android.location.Location;
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
import com.example.go4lunch.models.PlaceDetail.Result;
import com.example.go4lunch.models.User;
import com.example.go4lunch.utils.PlaceAdapterUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private final List<PlaceDetail> placeDetailList;
    private List<User> userList;
    private final RequestManager glide;
    private Location userLocation;

    public PlacesAdapter(@NonNull List<PlaceDetail> places, List<User> userList, RequestManager glide, Location userLocation) {
        this.placeDetailList = places;
        this.glide = glide;
        this.userList = userList;
        this.userLocation = userLocation;
    }
    public void setUserList(List<User> userList){
        this.userList = userList;
    }

    public void setUserLocation(Location location){
        this.userLocation = location;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Result placeDetail = placeDetailList.get(position).getResult();
        holder.mName.setText(placeDetailList.get(position).getResult().getName());
        holder.mAddress.setText(placeDetailList.get(position).getResult().getFormattedAddress());
        holder.mRating.setRating(PlaceAdapterUtil.divideRestaurantRatingBy3(userList,placeDetail));
        holder.mRestaurantDistance.setText( String.format("%sm",PlaceAdapterUtil.getPositionBetweenPlaceAndUSer(userLocation,placeDetail)));
        holder.mRestaurantWorkmates.setText(String.format("(%s)", PlaceAdapterUtil.howManyWorkmatesEatAtThisRestaurant(userList, placeDetail)));
        holder.mRestaurantOpenHour.setText(PlaceAdapterUtil.getOpeningHourString(placeDetail));
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
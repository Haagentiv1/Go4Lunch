package com.example.go4lunch.adapters;

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
import com.example.go4lunch.R;
import com.example.go4lunch.models.NearbySearch.Result;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private List<Result> mPlaces;
    private RequestManager glide;
    private Double userLatitude;
    private Double userLongitude;
    private Location userLocation = new Location("");
    private Location placeLocation = new Location("");
    public PlacesAdapter(@NonNull List<Result> places, RequestManager glide,Double userLatitude, Double userLongitude) {
        this.mPlaces = places;
        this.glide = glide;
        this.userLatitude = userLatitude;
        this.userLongitude = userLongitude;
    }

    public void setUserLatitude(Double userLatitude){
        this.userLatitude = userLatitude;
    }

    public void setUserLongitude(Double userLongitude) {
        this.userLongitude = userLongitude;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item_restaurant, parent, false);
        return new ViewHolder(view);
    }

    public String getResult(int position){
        return this.mPlaces.get(position).getPlaceId();
    }

    public float divideRating(Double rating){
        if (rating == null){
            return 0;
        }
        else return (float) ((rating / 5) * 3);
    }


    public String getDistanceBetweenUserLocationAndPlace(int adapterPosition){
        userLocation.setLatitude(userLatitude);
        userLocation.setLongitude(userLongitude);
        placeLocation.setLatitude(mPlaces.get(adapterPosition).getGeometry().getLocation().getLat());
        placeLocation.setLongitude(mPlaces.get(adapterPosition).getGeometry().getLocation().getLng());
        float distanceInMeter = userLocation.distanceTo(placeLocation);
        return String.valueOf(Math.round(distanceInMeter));
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mName.setText(mPlaces.get(position).getName());
        holder.mAddress.setText(mPlaces.get(position).getVicinity());
        holder.mRating.setRating(divideRating(mPlaces.get(position).getRating()));
        holder.mRestaurantDistance.setText(String.format("%sm", getDistanceBetweenUserLocationAndPlace(position)));
        //holder.mRestaurantOpenHour.setText(mPlaces.get(position).getOpeningHours().getOpenNow().toString());
        //glide.load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + mPlaces.get(position).getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY).into(holder.mRestaurantPicture);


    }
    //https://stackoverflow.com/questions/13524834/google-place-api-placedetails-photo-reference


    @Override
    public int getItemCount() {
        return mPlaces.size();
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this,view);

        }
    }
}
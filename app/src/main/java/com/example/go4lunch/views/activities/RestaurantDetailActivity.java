package com.example.go4lunch.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;
import com.example.go4lunch.models.PlaceDetail.PlaceDetailResult;
import com.example.go4lunch.presenters.Go4LunchStreams;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;

public class RestaurantDetailActivity extends AppCompatActivity {

    private String restaurantId;
    private Disposable disposable;

    @BindView(R.id.detail_restaurant_picture)
    public ImageView restaurantPicture;
    @BindView(R.id.detail_restaurant_name)
    public TextView restaurantName;
    @BindView(R.id.detail_restaurant_address)
    public TextView restaurantAddress;

    private String phone;
    private String website;


    @OnClick(R.id.detail_restaurant_callButton)
    public void callActivity(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone,null));
        startActivity(intent);
    }

    @OnClick(R.id.detail_restaurant_websiteButton)
    public void websiteActivity(){
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(website)));

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);
        recoverDetailRestaurant();


    }

    public void recoverDetailRestaurant(){
        Intent intent = getIntent();
        if (intent != null){
            restaurantId = intent.getStringExtra("placeID");
            Log.e("restaurant id", "id" + restaurantId);
            executeHttpRequestForRetrievePlaceDetailData();
        }
    }

    public void bindPlaceDetailDataToView(PlaceDetailResult placeDetailResult){
        Glide.with(this).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + placeDetailResult.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY).into(restaurantPicture);
        restaurantName.setText(placeDetailResult.getName());
        restaurantAddress.setText(placeDetailResult.getFormattedAddress());
        phone = placeDetailResult.getFormattedPhoneNumber();
        website = placeDetailResult.getWebsite();
        Log.e("website", "websiteurl" + website);

    }

    private void executeHttpRequestForRetrievePlaceDetailData(){
        Log.e("httpid","id" + restaurantId);
        disposable = Go4LunchStreams.streamFetchDetails(restaurantId).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(@NonNull PlaceDetail placeDetail) {
                Log.e("detailHttp","detailObject" + placeDetail.getStatus());
                PlaceDetailResult placeDetailResult = placeDetail.getResult();
                bindPlaceDetailDataToView(placeDetailResult);

            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("error","error" + e);

            }

            @Override
            public void onComplete() {

            }
        });

    }
}
package com.example.go4lunch.views.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;
import com.example.go4lunch.models.PlaceDetail.Result;
import com.example.go4lunch.models.User;
import com.example.go4lunch.presenters.Go4LunchStreams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
    @BindView(R.id.detail_like_image)
    public ImageView likeImageView;


    private String phone;
    private String website;
    private List<String> userLikes = new ArrayList<>();
    private User currentUser;


    @OnClick(R.id.detail_restaurant_callButton)
    public void callActivity(){
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone,null));
        startActivity(intent);
    }

    @OnClick(R.id.detail_restaurant_websiteButton)
    public void websiteActivity(){
        if (website != null){
        startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(website)));
        }else {
            Toast.makeText(this,"Website non disponible",Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.detail_restaurant_likeButton)
    public void onClickLike(){
        if (userLikes.contains(restaurantId)){
            UserHelper.deleteLike(getCurrentUserUid(),restaurantId);
    }else{
          UserHelper.updateUserLikes(getCurrentUserUid(),restaurantId);
        }
    }
    @OnClick(R.id.Reserv_Restaurant_btn)
    public void onCLickOnReserveRestaurantButton(){
        UserHelper.updateChosenRestaurant(getCurrentUserUid(),restaurantId);
    }


    @Nullable
    protected String getCurrentUserUid(){ return FirebaseAuth.getInstance().getCurrentUser().getUid(); }


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

    private void setRestaurantPicture(Result placeDetailResult){
        if (placeDetailResult.getPhotos() != null && !placeDetailResult.getPhotos().isEmpty()){
            Glide.with(this).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + placeDetailResult.getPhotos().get(0).getPhotoReference() + "&key=" + BuildConfig.API_KEY).into(restaurantPicture);
        }else {restaurantPicture.setImageResource(R.drawable.image_not_available);}

    }

    public void bindPlaceDetailDataToView(Result placeDetailResult){
        restaurantName.setText(placeDetailResult.getName());
        restaurantAddress.setText(placeDetailResult.getFormattedAddress());
        phone = placeDetailResult.getFormattedPhoneNumber();
        website = placeDetailResult.getUrl();
        likeImageView.setBackgroundResource(R.drawable.ic_baseline_star_rate_24);
        Log.e("website", "websiteurl" + website);
        Log.e("Tag","restoid" + restaurantId);

    }

    private void executeHttpRequestForRetrievePlaceDetailData(){
        Log.e("httpid","id" + restaurantId);
        disposable = Go4LunchStreams.streamFetchDetails(restaurantId).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                Log.e("detailHttp","detailObject" + placeDetail.getStatus());
                Log.e("test","test" + placeDetail.getResult().getPlaceId());
                Result placeDetailResult = placeDetail.getResult();
                bindPlaceDetailDataToView(placeDetailResult);
                setRestaurantPicture(placeDetailResult);
                Log.e("Tag","getType" + placeDetail.getResult().getTypes());

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

    public void getUserFromFirebaseDatabase(){
        UserHelper.getUser(getCurrentUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                currentUser = documentSnapshot.toObject(User.class);
                userLikes = currentUser.getLikes();
            }
        });
    }

    public void getUserLikes(){

    }

    public boolean checkLikes(@Nullable List<String> userLikes){
        for (String userLike : userLikes){
            if (userLike.equals(restaurantId)){
                return true;
            }
        }return false;
    }


}
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.BuildConfig;
import com.example.go4lunch.R;
import com.example.go4lunch.adapters.WorkMatesAdapter;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.PlaceDetail.PlaceDetail;
import com.example.go4lunch.models.PlaceDetail.Result;
import com.example.go4lunch.models.User;
import com.example.go4lunch.presenters.Go4LunchStreams;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
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
    @BindView(R.id.workmates_recycler)
    public RecyclerView recyclerView;


    private String phone;
    private String website;
    private List<String> userLikes = new ArrayList<>();
    private User currentUser;
    private WorkMatesAdapter workMatesAdapter;
    private List<User> userList = new ArrayList<>();
    private List<User> usersListRestaurant = new ArrayList<>();
    private PlaceDetail currentPlaceDetail;


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
            userLikes.remove(restaurantId);
            UserHelper.deleteLike(getCurrentUserUid(),restaurantId);
            likeImageView.setImageResource(R.drawable.ic_baseline_star_rate_24);
    }else{
          UserHelper.updateUserLikes(getCurrentUserUid(),restaurantId);
          likeImageView.setImageResource(R.drawable.ic_liked_star);
          userLikes.add(restaurantId);
        }
    }
    @OnClick(R.id.Reserv_Restaurant_btn)
    public void onCLickOnReserveRestaurantButton(){
        UserHelper.updateChosenRestaurant(getCurrentUserUid(),restaurantId);
        Date date = new Date();
        Timestamp restaurantReservationCreationTimestamp= new Timestamp(date);
        UserHelper.updateChosenRestaurantTimestamp(getCurrentUserUid(),restaurantReservationCreationTimestamp);
        UserHelper.updateChosenRestaurantName(getCurrentUserUid(),currentPlaceDetail.getResult().getName());
    }


    @Nullable
    protected String getCurrentUserUid(){ return FirebaseAuth.getInstance().getCurrentUser().getUid(); }

    public void getUsersWithThisRestaurant(){
        Log.e("Tag","test");
        UserHelper.getAllUsersByRestaurant(restaurantId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.e("Tag","wrkm" + String.valueOf(queryDocumentSnapshots.getDocuments().size()));
                List<User> userList1 =queryDocumentSnapshots.toObjects(User.class);
            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        ButterKnife.bind(this);
        getUserFromFirebaseDatabase();
        recoverDetailRestaurant();
        getUsersWithThisRestaurant();
        configureRecyclerView();

    }


    @NotNull
    private FirestoreRecyclerOptions<User> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .setLifecycleOwner(this)
                .build();
    }



    private void configureRecyclerView(){
        this.workMatesAdapter = new WorkMatesAdapter(generateOptionsForAdapter(UserHelper.getAllUsersByRestaurant(restaurantId)), Glide.with(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(workMatesAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUsersList();
    }

    public void recoverDetailRestaurant(){
        Intent intent = getIntent();
        if (intent != null){
            restaurantId = intent.getStringExtra("placeID");
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
        updateDrawableLikes();
    }

    private void executeHttpRequestForRetrievePlaceDetailData(){
        disposable = Go4LunchStreams.streamFetchDetails(restaurantId).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                currentPlaceDetail = placeDetail;
                Result placeDetailResult = placeDetail.getResult();
                bindPlaceDetailDataToView(placeDetailResult);
                setRestaurantPicture(placeDetailResult);
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
                updateDrawableLikes();
            }
        });
    }

    public void getUsersWithTheSameRestaurant(){
        for (User user : userList){
            if (user.getChosenRestaurant().equals(restaurantId)){
                usersListRestaurant.add(user);
            }
        }
    }

    public void getUsersList(){
        UserHelper.getUsers().addOnSuccessListener(queryDocumentSnapshots ->
                userList = queryDocumentSnapshots.toObjects(User.class));
        getUsersWithTheSameRestaurant();
    }



    public void updateDrawableLikes(){
        if (userLikes.contains(restaurantId)){
            likeImageView.setImageResource(R.drawable.ic_liked_star);
        }else likeImageView.setImageResource(R.drawable.ic_baseline_star_rate_24);
    }





}
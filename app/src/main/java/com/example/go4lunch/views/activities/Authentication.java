package com.example.go4lunch.views.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.multidex.MultiDex;

import com.example.go4lunch.BaseActivity;
import com.example.go4lunch.MainActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Authentication extends  BaseActivity {

    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 123;
    private List<User> userList = new ArrayList<>();

    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build()
        );
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.ic_hot_food_in_a_bowl__2_)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            Log.e("Tag","SignIn");

            if (resultCode == RESULT_OK) {
                Log.e("Tag","ResultOK");
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                createUserInFirestore();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private void createUserInFirestore(){
        if (!checkUserInFirestore()){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ?
                    this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            List<String> likes = new ArrayList<>();
            Date date = new Date();
            Timestamp userCreationTimestamp = new Timestamp(date);
            String chosenRestaurant = null;
            Timestamp chosenRestaurantTimestamp = null;
            String chosenRestaurantName = null;


            UserHelper.createUser(uid, username, urlPicture,likes,userCreationTimestamp, null,null ,null).addOnFailureListener(this.onFailureListener());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.authentication_activity);
        mAuth = FirebaseAuth.getInstance();
        createSignInIntent();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    public Boolean checkUserInFirestore(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        for (User user : userList){
            if (user.getUid().equals(currentUser.getUid())) return true;
        }
        return false;
    }

    public void getUsersFromFirestore(){
        UserHelper.getUsers().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                  userList = queryDocumentSnapshots.toObjects(User.class);
            }
        });

    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        getUsersFromFirestore();
        //updateUI(currentUser);
    }
}

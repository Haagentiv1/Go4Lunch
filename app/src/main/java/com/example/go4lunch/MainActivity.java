package com.example.go4lunch;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.example.go4lunch.views.activities.Authentication;
import com.example.go4lunch.views.activities.RestaurantDetailActivity;
import com.example.go4lunch.views.activities.SettingsActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView mNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigationView;
    private User currentUser;


    // 2 - Identify each Http Request
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;
    private static final int RC_SIGN_IN = 30;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode,resultCode,data);
    }

    @Nullable
    protected String getCurrentUserUid(){ return FirebaseAuth.getInstance().getCurrentUser().getUid(); }

    private void getUserInfoFromFirebase(){
    UserHelper.getUser(getCurrentUserUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
        @Override
        public void onSuccess(DocumentSnapshot documentSnapshot) {
           currentUser = documentSnapshot.toObject(User.class);
        }
    });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        setContentView(R.layout.activity_main);
        Log.e("Tag", String.valueOf(LocalDateTime.now()));

        Places.initialize(getApplicationContext(), String.valueOf(R.string.google_api_key));
        configureToolbar();
        configureNavigationVieW();
        configureDrawerLayout();
        configureNavHeader();
        configureBottomNavigation();
        getUserInfoFromFirebase();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

    }

    public void configureToolbar() {
        this.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
    }




    public void configureDrawerLayout() {
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


    }

    public void configureNavigationVieW() {
        this.mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    public void configureBottomNavigation() {
        BottomNavigationView BottomNavView = findViewById(R.id.bottom_navigation);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.bottom_nav_map, R.id.bottom_nav_list, R.id.bottom_nav_workmates)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(BottomNavView, navController);


    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void configureNavHeader() {
        View headerView = mNavigationView.getHeaderView(0);
        ImageView mUserPicture = headerView.findViewById(R.id.user_picture);
        if (this.getCurrentUser() != null) {
            createUserInFirestore();

            //Get picture URL from Firebase
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(mUserPicture);
            }
            //Get username & email from Firebase
            String email = TextUtils.isEmpty(this.getCurrentUser().getEmail()) ?
                    getString(R.string.info_no_email_found) : this.getCurrentUser().getEmail();
            String userName = TextUtils.isEmpty(this.getCurrentUser().getDisplayName()) ?
                    getString(R.string.info_no_username_found) : this.getCurrentUser().getDisplayName();

            //update views with data
            TextView mUserName = (TextView) headerView.findViewById(R.id.user_name_nav);
            TextView mUserEmail = (TextView) headerView.findViewById(R.id.user_email_nav);
            mUserName.setText(userName);
            mUserEmail.setText(email);
        }


    }

    private void createUserInFirestore(){
        if (this.getCurrentUser() != null){
            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ?
                    this.getCurrentUser().getPhotoUrl().toString() : null;
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            List<String> likes = new ArrayList<>();
            Date date = new Date();
            Timestamp userCreationTimestamp = new Timestamp(date);
            String chosenRestaurant = null;
            Timestamp chosenRestaurantTimestamp = null;


            UserHelper.createUser(uid, username, urlPicture,likes,userCreationTimestamp, null, null).addOnFailureListener(this.onFailureListener());
        }
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK){
                this.createUserInFirestore();
            }
        }
    }

    private void signOutUserFromFirebase() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    private Boolean checkUserRestaurant(){
        if (currentUser.getChosenRestaurant() != null){
            return true;
        }else return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_your_lunch:
                if (checkUserRestaurant()){
                Intent intent = new Intent(this, RestaurantDetailActivity.class);
                startActivity(intent);
                    }else Toast.makeText(this,"Vous n'avez pas encore choisit de restaurant",Toast.LENGTH_LONG).show();
                break;


            case R.id.nav_settings:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                break;

            case R.id.nav_logout:
                signOutUserFromFirebase();
                Intent intent2 = new Intent(this, Authentication.class);
                startActivity(intent2);
                break;
            default:
                break;


        }

        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // 3 - Create OnCompleteListener called after tasks ended
    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin) {
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin) {
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}



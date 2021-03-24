package com.example.go4lunch.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.stream.Collectors;

public class AlertReceiver extends BroadcastReceiver {

    private User currentUser;
    private List<User> userList;
    private Context context;
    private String userListString;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        getCurrentUserFromFirebase();
        Log.e("Tag","OnReceive");
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    public void getCurrentUserFromFirebase(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            currentUser = documentSnapshot.toObject(User.class);
            UserHelper.getAllUsersByRestaurant(currentUser.getChosenRestaurant()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    userList = queryDocumentSnapshots.toObjects(User.class);
                    Log.e("Tag","Userlist" + userList.size());
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        userListString = userList.stream().map(User::getUsername).collect(Collectors.joining(","));
                    }
                    NotificationHelper notificationHelper = new NotificationHelper(context);
                    NotificationCompat.Builder nb = notificationHelper.getChannelNotification(currentUser,userListString);
                    notificationHelper.getManager().notify(1,nb.build());

                }
            });
            Log.e("Tag",currentUser.getUsername());
        });
    }

    public void sentVisualNotification(){}
}

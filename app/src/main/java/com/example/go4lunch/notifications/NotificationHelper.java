package com.example.go4lunch.notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class NotificationHelper extends ContextWrapper{

    public static final String channelID = "channelID";
    public static final String channelName = "channelName";

    private NotificationManager mManager;
    private User currentUser;

    public NotificationHelper(Context base) {
        super(base);
         getCurrentUserFromFirebase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    public void getCurrentUserFromFirebase(){
        UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
            currentUser = documentSnapshot.toObject(User.class);
            Log.e("Tag",currentUser.getUsername());
        });
    }



    public NotificationCompat.Builder getChannelNotification(){
        Log.e("Tag","channelNotif");
        return new NotificationCompat.Builder(getApplicationContext(),channelID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Le Zinc")
                .setSmallIcon(R.drawable.ic_hot_food_in_a_bowl__2_);
    }
}

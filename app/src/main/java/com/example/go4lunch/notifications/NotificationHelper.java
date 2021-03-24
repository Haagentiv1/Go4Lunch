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
import com.example.go4lunch.models.User;

public class NotificationHelper extends ContextWrapper{

    public static final String channelID = "channelID";
    public static final String channelName = "channelName";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
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





    public NotificationCompat.Builder getChannelNotification(User user,@Nullable String userList){
        Log.e("Tag","channelNotif");
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle(user.getChosenRestaurantName())
                .addLine(user.getChosenRestaurantAddress())
                .addLine(userList);
        return new NotificationCompat.Builder(getApplicationContext(),channelID)
                .setContentTitle(getString(R.string.notification_title))
                .setStyle(inboxStyle)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_hot_food_in_a_bowl__2_);
    }
}

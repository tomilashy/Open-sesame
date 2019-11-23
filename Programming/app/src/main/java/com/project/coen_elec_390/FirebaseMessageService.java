package com.project.coen_elec_390;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseMessageService extends FirebaseMessagingService {

    private SharedPreferences sharedPreference;

    private final String TAG = "MESSAGESERVICE";

    @Override
    public void onNewToken(String token) {
        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        String prevTopic = sharedPreference.getString("topic", "DEFAULT");
        final int doorID = sharedPreference.getInt("doorID", 0);
        final String sDoorID = Integer.toString(doorID);
        if (prevTopic.equals("DEFAULT")) {
            FirebaseMessaging.getInstance().subscribeToTopic(sDoorID)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            SharedPreferences.Editor editor = sharedPreference.edit();
                            editor.remove("topic");
                            editor.putString("topic", sDoorID);
                            editor.commit();

                            Log.d(TAG, "Subscribed to door: " + doorID);
                        }
                    });
        } else {
            if (!prevTopic.equals(sDoorID)) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(prevTopic);
                FirebaseMessaging.getInstance().subscribeToTopic(sDoorID)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SharedPreferences.Editor editor = sharedPreference.edit();
                                editor.remove("topic");
                                editor.putString("topic", sDoorID);
                                editor.commit();

                                Log.d(TAG, "Subscribed to door: " + doorID);
                            }
                        });
            }
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage.getData().get("body"));
        }
    }

    private void sendNotification(String messageBody) {

        sharedPreference = getSharedPreferences("ProfilePreference", this.MODE_PRIVATE);
        final int doorID = sharedPreference.getInt("doorID", 0);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Door " + doorID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Peek Door")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }


}
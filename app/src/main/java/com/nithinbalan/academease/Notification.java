package com.nithinbalan.academease;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.academease.R;
import com.nithinbalan.academease.activities.MainActivity;

public class Notification extends BroadcastReceiver {

    // Method called when the broadcast is received
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent yesIntent=new Intent(context, MainActivity.class);
        Intent noIntent=new Intent(context, MainActivity.class);
        PendingIntent yesPI=PendingIntent.getActivity(context, 0, yesIntent, PendingIntent.FLAG_IMMUTABLE);
        PendingIntent noPI=PendingIntent.getActivity(context, 0, noIntent, PendingIntent.FLAG_IMMUTABLE);
        // Build the notification using NotificationCompat.Builder
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "academease")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(intent.getStringExtra("title"))
                .setContentText(intent.getStringExtra("msg"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setChannelId("academease")
                .addAction(R.drawable.icon, "Yes", yesPI)
                .addAction(R.drawable.icon, "No", noPI);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            return;
        }
        notificationManager.notify(420, notificationBuilder.build());
    }
}

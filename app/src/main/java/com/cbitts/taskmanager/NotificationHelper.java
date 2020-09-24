package com.cbitts.taskmanager;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class NotificationHelper {
    public static final String CHANNEL_ID = "TaskMaster";
    public static final String CHANNEL_NAME = "TaskMaster";
    public static final String CHANNEL_DESCRIPTION = "Update for tasks";


    public static void displayNotification(Context context, String title, String body){

        Intent intent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                100,
                intent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        NotificationCompat.Builder mbuilder =
                new NotificationCompat.Builder(context,CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                        .setContentIntent(pendingIntent)
//                        .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat mNotificationmgr = NotificationManagerCompat.from(context);
        mNotificationmgr.notify(1,mbuilder.build());
    }
}

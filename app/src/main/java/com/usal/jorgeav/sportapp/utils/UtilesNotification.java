package com.usal.jorgeav.sportapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.MyNotification;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

public class UtilesNotification {

    private static final int NOTIFICATION_ID = 1;

    // TODO: 06/07/2017 si las notificaciones son diferentes se necesitaran mas metodos
    // TODO: 06/07/2017 para referencias ver proyecto water reminder

    public static void createNotification(Context context, MyNotification fNotification) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_action_check)
                .setContentTitle(fNotification.getMessage())
                .setContentText(fNotification.getExtra_data())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getExtra_data()))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private static PendingIntent contentIntent(Context context) {
        return null;
    }
}

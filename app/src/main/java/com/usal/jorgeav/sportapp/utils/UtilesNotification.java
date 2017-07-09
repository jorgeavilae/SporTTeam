package com.usal.jorgeav.sportapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

public class UtilesNotification {

    private static final int NOTIFICATION_ID = 1;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void createNotification(Context context, MyNotification fNotification, User user) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getMessage())
                    .setContentText(user.getmName())
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getExtra_data()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent(context))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }
    private static NotificationCompat.Action drinkWaterAction(Context context) {
//        Intent incrementWaterCountIntent = new Intent(context, WaterReminderIntentService.class);
//        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);
//        PendingIntent incrementWaterPendingIntent = PendingIntent.getService(
//                context,
//                ACTION_DRINK_PENDING_INTENT_ID,
//                incrementWaterCountIntent,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        NotificationCompat.Action drinkWaterAction = new NotificationCompat.Action(R.drawable.ic_local_drink_black_24px,
//                "I did it!",
//                incrementWaterPendingIntent);
//        return drinkWaterAction;
        return null;
    }

    private static PendingIntent contentIntent(Context context) {
//        Intent startActivityIntent = new Intent(context, MainActivity.class);
//        return PendingIntent.getActivity(
//                context,
//                WATER_REMINDER_PENDING_INTENT_ID,
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        return null;
    }

    public static void createNotification(Context context, MyNotification fNotification, Event event) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getMessage())
                    .setContentText(event.getOwner() + " invite you to play " + event.getSport_id())
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getExtra_data()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent(context))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}

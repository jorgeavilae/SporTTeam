package com.usal.jorgeav.sportapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.mainactivities.FriendsActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

public class UtilesNotification {
    public static final String TAG = UtilesNotification.class.getSimpleName();

    private static final int PROFILE_PENDING_INTENT_ID = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOTIFICATION_ID_ERROR,
            NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED, NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED,
            NOTIFICATION_ID_EVENT_INVITATION_RECEIVED, NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED,
            NOTIFICATION_ID_EVENT_INVITATION_DECLINED, NOTIFICATION_ID_EVENT_REQUEST_RECEIVED,
            NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED, NOTIFICATION_ID_EVENT_REQUEST_DECLINED,
            NOTIFICATION_ID_EVENT_COMPLETE, NOTIFICATION_ID_EVENT_SOMEONE_QUIT,
            NOTIFICATION_ID_EVENT_EDIT, NOTIFICATION_ID_EVENT_DELETE, NOTIFICATION_ID_ALARM_EVENT})
    public @interface NotificationType {}
    public static final int NOTIFICATION_ID_ERROR = 0;
    public static final int NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED = 1;
    public static final int NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED = 2;
    public static final int NOTIFICATION_ID_EVENT_INVITATION_RECEIVED = 3;
    public static final int NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED = 4;
    public static final int NOTIFICATION_ID_EVENT_INVITATION_DECLINED = 5;
    public static final int NOTIFICATION_ID_EVENT_REQUEST_RECEIVED = 6;
    public static final int NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED = 7;
    public static final int NOTIFICATION_ID_EVENT_REQUEST_DECLINED = 8;
    public static final int NOTIFICATION_ID_EVENT_COMPLETE = 9;
    public static final int NOTIFICATION_ID_EVENT_SOMEONE_QUIT = 10;
    public static final int NOTIFICATION_ID_EVENT_EDIT = 11;
    public static final int NOTIFICATION_ID_EVENT_DELETE = 12;
    public static final int NOTIFICATION_ID_ALARM_EVENT = 13;

    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void createNotification(Context context, MyNotification fNotification) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
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
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    public static void createNotification(Context context, MyNotification fNotification, User user) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(user.getmName())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentProfileIntent(context, user.getmId()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
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
//        Intent startActivityIntent = new Intent(context, BaseActivity.class);
//        return PendingIntent.getActivity(
//                context,
//                WATER_REMINDER_PENDING_INTENT_ID,
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        return null;
    }

    private static PendingIntent contentProfileIntent(Context context, String userId) {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, FriendsActivity.class));
        startActivityIntent.putExtra(FriendsActivity.USERID_PENDING_INTENT_EXTRA, userId);
        return PendingIntent.getActivity(
                context,
                PROFILE_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static void createNotification(Context context, MyNotification fNotification, Event event) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getMessage())
                    .setContentText(event.getOwner() + " invite you to play " + event.getSport_id()) // TODO: 09/07/2017  cambiar msg
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent(context))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    public static void createNotification(Context context, MyNotification fNotification, Alarm alarm) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getMessage())
                    .setContentText(alarm.getmId()) //// TODO: 09/07/2017  cambiar msg
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent(context))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification and notificationBuilder.build()
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }
}

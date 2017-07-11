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
import com.usal.jorgeav.sportapp.mainactivities.AlarmsActivity;
import com.usal.jorgeav.sportapp.mainactivities.EventsActivity;
import com.usal.jorgeav.sportapp.mainactivities.FriendsActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

// TODO: 11/07/2017 Updates mesages in notifications with Users/Event/Alarm data
public class UtilesNotification {
    public static final String TAG = UtilesNotification.class.getSimpleName();

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

    //Creates and display a notification with an User information
    public static void createNotification(Context context, MyNotification fNotification, User user) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentProfileIntent(context, user.getmId()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    //Creates a PendingIntent to open user's ProfileFragment
    private static PendingIntent contentProfileIntent(Context context, String userId) {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, FriendsActivity.class));
        startActivityIntent.putExtra(FriendsActivity.USERID_PENDING_INTENT_EXTRA, userId);
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //Creates and display a notification with an Event information
    public static void createNotification(Context context, MyNotification fNotification, Event event) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage()) // TODO: 09/07/2017  cambiar msg
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentEventIntent(context, event.getEvent_id()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    //Creates a PendingIntent to open event's DetailEventFragment
    private static PendingIntent contentEventIntent(Context context, String eventId) {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
        startActivityIntent.putExtra(EventsActivity.EVENTID_PENDING_INTENT_EXTRA, eventId);
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //Creates and display a notification with an Alarm information
    public static void createNotification(Context context, MyNotification fNotification, Alarm alarm) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage()) //// TODO: 09/07/2017  cambiar msg
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentAlarmIntent(context, alarm.getmId()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    //Creates a PendingIntent to open alarm's DetailAlarmFragment
    private static PendingIntent contentAlarmIntent(Context context, String alarmId) {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, AlarmsActivity.class));
        startActivityIntent.putExtra(AlarmsActivity.ALARMID_PENDING_INTENT_EXTRA, alarmId);
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //Creates and display a notification without information (event deleted)
    public static void createNotification(Context context, MyNotification fNotification) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentIntent(context))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }

    //Creates a PendingIntent to open alarm's DetailAlarmFragment
    private static PendingIntent contentIntent(Context context) {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
        return PendingIntent.getActivity(
                context,
                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // TODO: 11/07/2017 delete or use
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
}

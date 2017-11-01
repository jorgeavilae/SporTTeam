package com.usal.jorgeav.sportapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
                    .setContentIntent(contentProfileIntent(context, user.getUid()))
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
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentDetailEventIntent(context, event.getEvent_id()))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

            // Pass in a unique ID of your choosing for the notification
            notificationManager.notify(fNotification.getNotification_type(), notificationBuilder.build());
        }
    }


    //Creates a PendingIntent to open EventFragment
    public static PendingIntent contentEventIntent(Context context) {
        /* https://stackoverflow.com/a/24927301/4235666 */
//        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
//        return PendingIntent.getActivity(
//                context,
//                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, EventsActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(EventsActivity.class);
        taskStackBuilder.addNextIntent(intent);

        return taskStackBuilder.getPendingIntent(
                (int) System.currentTimeMillis(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //Creates a PendingIntent to open event's DetailEventFragment
    private static PendingIntent contentDetailEventIntent(Context context, String eventId) {
        /* https://stackoverflow.com/a/24927301/4235666 */
//        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
//        startActivityIntent.putExtra(EventsActivity.EVENTID_PENDING_INTENT_EXTRA, eventId);
//        return PendingIntent.getActivity(
//                context,
//                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, EventsActivity.class);
        intent.putExtra(EventsActivity.EVENTID_PENDING_INTENT_EXTRA, eventId);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(EventsActivity.class);
        taskStackBuilder.addNextIntent(intent);

        return taskStackBuilder.getPendingIntent(
                (int) System.currentTimeMillis(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //Creates and display a notification with an Alarm information
    public static void createNotification(Context context, MyNotification fNotification, Alarm alarm) {
        if (!fNotification.getChecked()) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setSmallIcon(R.drawable.ic_logo_white) /* https://stackoverflow.com/a/30795471/4235666 */
                    .setContentTitle(fNotification.getTitle())
                    .setContentText(fNotification.getMessage())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(fNotification.getMessage()))
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    .setContentIntent(contentAlarmIntent(context, alarm.getId()))
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

    //Creates a PendingIntent to open EventsActivity
    private static PendingIntent contentIntent(Context context) {
        /* https://stackoverflow.com/a/24927301/4235666 */
//        Intent startActivityIntent = Intent.makeRestartActivityTask(new ComponentName(context, EventsActivity.class));
//        return PendingIntent.getActivity(
//                context,
//                (int) System.currentTimeMillis(), /* To ensure every PendingIntent is unique */
//                startActivityIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent intent = new Intent(context, EventsActivity.class);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(EventsActivity.class);
        taskStackBuilder.addNextIntent(intent);

        return taskStackBuilder.getPendingIntent(
                (int) System.currentTimeMillis(),
                PendingIntent.FLAG_UPDATE_CURRENT);
    }
}

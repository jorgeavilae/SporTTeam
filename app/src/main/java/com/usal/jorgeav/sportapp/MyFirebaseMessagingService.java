package com.usal.jorgeav.sportapp;

import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.AlarmsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @SuppressWarnings("unused")
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0)
            notify(remoteMessage.getData());
    }

    private void notify(Map<String, String> dataMap) {
        MyNotification notification = parseNotificationFromMap(dataMap);
        String notificationID = "";
        if (dataMap.containsKey("notificationID"))
            notificationID = dataMap.get("notificationID");
        if (TextUtils.isEmpty(notificationID)) return;

        @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
        switch (type) {
            case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                User user = UtilesContentProvider.getUserFromContentProvider(notification.getExtra_data_one());
                if (user == null) {
                    UsersFirebaseSync.loadAProfileAndNotify(notificationID, notification);
                    return;
                }
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, user);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                if (event == null) {
                    EventsFirebaseSync.loadAnEventAndNotify(notificationID, notification);
                    return;
                }
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, event);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                if (alarm == null || eventCoincidence == null) {
                    AlarmsFirebaseSync.loadAnAlarmAndNotify(notificationID, notification);
                    return;
                }
                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, alarm);
                break;
            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                break;
        }
        NotificationsFirebaseActions.checkNotification(notificationID);
    }

    /**
     * Convert map into a MyNotification object.
     * @param data map from Firebase Cloud Functions, should look like this:
        data: {
            notificationID: notificationID,
            title: title,
            message: message,
            notification_type: notification_type,
            checked: checked,
            data_type: data_type,
            extra_data_one: extra_data_one,
            date: date
        }
     * @return MyNotification object from Database.
     */
    private MyNotification parseNotificationFromMap(Map<String, String> data) {
        Long notification_type = null;
        if (data.containsKey(FirebaseDBContract.Notification.NOTIFICATION_TYPE))
            notification_type = Long.parseLong(data.get(FirebaseDBContract.Notification.NOTIFICATION_TYPE));

        Boolean checked = null;
        if (data.containsKey(FirebaseDBContract.Notification.CHECKED))
            checked = Boolean.parseBoolean(data.get(FirebaseDBContract.Notification.CHECKED));

        String title = null;
        if (data.containsKey(FirebaseDBContract.Notification.TITLE))
            title = data.get(FirebaseDBContract.Notification.TITLE);

        String message = null;
        if (data.containsKey(FirebaseDBContract.Notification.MESSAGE))
            message = data.get(FirebaseDBContract.Notification.MESSAGE);

        String extra_data_one = null;
        if (data.containsKey(FirebaseDBContract.Notification.EXTRA_DATA_ONE))
            extra_data_one = data.get(FirebaseDBContract.Notification.EXTRA_DATA_ONE);

        String extra_data_two = null;
        if (data.containsKey(FirebaseDBContract.Notification.EXTRA_DATA_TWO))
            extra_data_two = data.get(FirebaseDBContract.Notification.EXTRA_DATA_TWO);

        Long data_type = null;
        if (data.containsKey(FirebaseDBContract.Notification.DATA_TYPE))
            data_type = Long.parseLong(data.get(FirebaseDBContract.Notification.DATA_TYPE));

        Long date = null;
        if (data.containsKey(FirebaseDBContract.Notification.DATE))
            date = Long.parseLong(data.get(FirebaseDBContract.Notification.DATE));

        return new MyNotification(notification_type, checked, title, message,
                extra_data_one, extra_data_two, data_type, date);
    }
}

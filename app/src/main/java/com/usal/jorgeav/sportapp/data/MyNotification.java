package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

public class MyNotification {
    Long notification_type;
    Boolean checked;
    String message;
    String extra_data;
    Long data_type;
    Long date;
    // TODO: 06/07/2017 necesita mas parametros, hay que ponerlos tambien en FirebaseActions.java

    public MyNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(MyNotification.class)
    }

    public MyNotification(Long notification_type, Boolean checked, String message, String extra_data, Long data_type, Long date) {
        this.notification_type = notification_type;
        this.checked = checked;
        this.message = message;
        this.extra_data = extra_data;
        this.data_type = data_type;
        this.date = date;
    }

    @UtilesNotification.NotificationType
    public int getNotification_type() {
        return longToNotificationType(notification_type);
    }

    @UtilesNotification.NotificationType
    private int longToNotificationType(Long type) {
        if (type == (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED)
            return UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED)
            return UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_RECEIVED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_RECEIVED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_DECLINED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_DECLINED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_RECEIVED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_RECEIVED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_DECLINED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_DECLINED;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_COMPLETE)
            return UtilesNotification.NOTIFICATION_ID_EVENT_COMPLETE;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_SOMEONE_QUIT)
            return UtilesNotification.NOTIFICATION_ID_EVENT_SOMEONE_QUIT;
        return UtilesNotification.NOTIFICATION_ID_ERROR;
    }

    public Boolean getChecked() {
        return checked;
    }

    public String getMessage() {
        return message;
    }

    public String getExtra_data() {
        return extra_data;
    }

    @FirebaseDBContract.NotificationDataTypes
    public int getData_type() {
        return longToNotificationDataType(data_type);
    }

    @FirebaseDBContract.NotificationDataTypes
    private int longToNotificationDataType(Long type) {
        if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_NONE)
            return FirebaseDBContract.NOTIFICATION_TYPE_NONE;
        if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_USER)
            return FirebaseDBContract.NOTIFICATION_TYPE_USER;
        if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT)
            return FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_ALARM)
            return FirebaseDBContract.NOTIFICATION_TYPE_ALARM;
        return FirebaseDBContract.NOTIFICATION_TYPE_ERROR;
    }

    public Long getDate() {
        return date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Notification.NOTIFICATION_TYPE, notification_type);
        result.put(FirebaseDBContract.Notification.CHECKED, checked);
        result.put(FirebaseDBContract.Notification.MESSAGE, message);
        result.put(FirebaseDBContract.Notification.EXTRA_DATA, extra_data);
        result.put(FirebaseDBContract.Notification.DATA_TYPE, data_type);
        result.put(FirebaseDBContract.Notification.DATE, date);
        return result;
    }
}

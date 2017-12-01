package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class MyNotification {
    private Long notification_type;
    private Boolean checked;
    private String title;
    private String message;
    private String extra_data_one;
    private String extra_data_two;
    private Long data_type;
    private Long date;

    public MyNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(MyNotification.class)
    }

    public MyNotification(Long notification_type, Boolean checked, String title, String message,
                          String extra_data_one, String extra_data_two, Long data_type, Long date) {
        this.notification_type = notification_type;
        this.checked = checked;
        this.title = title;
        this.message = message;
        this.extra_data_one = extra_data_one;
        this.extra_data_two = extra_data_two;
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
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_EDIT)
            return UtilesNotification.NOTIFICATION_ID_EVENT_EDIT;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_DELETE)
            return UtilesNotification.NOTIFICATION_ID_EVENT_DELETE;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_ALARM_EVENT)
            return UtilesNotification.NOTIFICATION_ID_ALARM_EVENT;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_CREATE)
            return UtilesNotification.NOTIFICATION_ID_EVENT_CREATE;
        if (type == (long) UtilesNotification.NOTIFICATION_ID_EVENT_EXPELLED)
            return UtilesNotification.NOTIFICATION_ID_EVENT_EXPELLED;
        return UtilesNotification.NOTIFICATION_ID_ERROR;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getExtra_data_one() {
        return extra_data_one;
    }

    public String getExtra_data_two() {
        return extra_data_two;
    }

    @FirebaseDBContract.NotificationDataTypes
    public int getData_type() {
        return longToNotificationDataType(data_type);
    }

    @FirebaseDBContract.NotificationDataTypes
    private int longToNotificationDataType(Long type) {
        if (type != null) {
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_NONE)
                return FirebaseDBContract.NOTIFICATION_TYPE_NONE;
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_USER)
                return FirebaseDBContract.NOTIFICATION_TYPE_USER;
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT)
                return FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
            if (type == (long) FirebaseDBContract.NOTIFICATION_TYPE_ALARM)
                return FirebaseDBContract.NOTIFICATION_TYPE_ALARM;
        }
        return FirebaseDBContract.NOTIFICATION_TYPE_ERROR;
    }

    public Long getDate() {
        return date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Notification.NOTIFICATION_TYPE, notification_type);
        result.put(FirebaseDBContract.Notification.CHECKED, checked);
        result.put(FirebaseDBContract.Notification.TITLE, title);
        result.put(FirebaseDBContract.Notification.MESSAGE, message);
        result.put(FirebaseDBContract.Notification.EXTRA_DATA_ONE, extra_data_one);
        result.put(FirebaseDBContract.Notification.EXTRA_DATA_TWO, extra_data_two);
        result.put(FirebaseDBContract.Notification.DATA_TYPE, data_type);
        result.put(FirebaseDBContract.Notification.DATE, date);
        return result;
    }

    @Override
    public String toString() {
        return "MyNotification{" +
                "notification_type=" + notification_type +
                ", checked=" + checked +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", extra_data_one='" + extra_data_one + '\'' +
                ", extra_data_two='" + extra_data_two + '\'' +
                ", data_type=" + data_type +
                ", date=" + date +
                '}';
    }
}

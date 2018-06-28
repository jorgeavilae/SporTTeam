package com.usal.jorgeav.sportapp.network.firebase.actions;


import android.text.TextUtils;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class NotificationsFirebaseActions {
    @SuppressWarnings("unused")
    private static final String TAG = NotificationsFirebaseActions.class.getSimpleName();

    // Notification
    public static void checkNotification(String ref) {
        FirebaseDatabase.getInstance().getReferenceFromUrl(ref)
                .child(FirebaseDBContract.Notification.CHECKED).setValue(true);
    }

    public static void deleteNotification(String myUserID, String notificationId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS)
                .child(notificationId).removeValue();
    }

    public static void deleteAllNotifications(String myUserID) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS)
                .removeValue();
    }

    public static void eventCompleteNotifications(boolean isComplete, Event event) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        // Notification object
        String notificationTitle;
        String notificationMessage;
        @UtilesNotification.NotificationType
        Long notificationType;
        if (isComplete) {
            notificationTitle = MyApplication.getAppContext()
                    .getString(R.string.notification_title_event_complete);
            notificationMessage = MyApplication.getAppContext()
                    .getString(R.string.notification_msg_event_complete);
            notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_COMPLETE;
        } else {
            notificationTitle = MyApplication.getAppContext()
                    .getString(R.string.notification_title_event_someone_quit);
            notificationMessage = MyApplication.getAppContext()
                    .getString(R.string.notification_msg_event_someone_quit);
            notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_SOMEONE_QUIT;
        }
        long currentTime = System.currentTimeMillis();
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, event.getEvent_id(), null, type, currentTime);

        //Set Event complete/incomplete MyNotification in participant
        String notificationId = event.getEvent_id() + FirebaseDBContract.Event.EMPTY_PLAYERS;

        Map<String, Object> childUpdates = new HashMap<>();
        if (event.getParticipants() != null && event.getParticipants().size() > 0)
            for (Map.Entry<String, Boolean> entry : event.getParticipants().entrySet())
                if (entry.getValue() && !entry.getKey().equals(myUserID)) {
                    String eventCompleteNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                            + entry.getKey() + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
                    childUpdates.put(eventCompleteNotification, n.toMap());
                }

        if (!event.getOwner().equals(myUserID)) {
            String eventCompleteNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                    + event.getOwner() + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
            childUpdates.put(eventCompleteNotification, n.toMap());
        }

        if (!childUpdates.isEmpty())
            FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public static void checkAlarmsForNotifications() {
        List<Alarm> alarms = UtilesContentProvider.getAllAlarmsFromContentProvider(MyApplication.getAppContext());
        if (alarms == null || alarms.size() < 1) return;

        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;
        final String finalMyUserID = myUserID;

        for (Alarm a : alarms) {
            final Alarm finalAlarm = a;
            final String eventId = UtilesContentProvider.eventsCoincidenceAlarmFromContentProvider(a, myUserID);
            if (eventId != null) {
                //Check if notification already exists
                FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                        .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS)
                        .child(a.getId() + FirebaseDBContract.User.ALARMS)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // Alarm a has some Event Coincidence (alarm and event in ContentProvider)
                                    // and notification doesn't exists

                                    // Create MyNotification
                                    long currentTime = System.currentTimeMillis();
                                    String notificationTitle = MyApplication.getAppContext()
                                            .getString(R.string.notification_title_alarm_event);
                                    String notificationMessage = MyApplication.getAppContext()
                                            .getString(R.string.notification_msg_alarm_event);
                                    @FirebaseDBContract.NotificationDataTypes
                                    Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_ALARM;
                                    @UtilesNotification.NotificationType
                                    Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_ALARM_EVENT;
                                    MyNotification n = new MyNotification(
                                            notificationType, true, notificationTitle,
                                            notificationMessage, finalAlarm.getId(), eventId,
                                            type, currentTime);

                                    // Store on Firebase
                                    FirebaseDatabase.getInstance().getReference().child(FirebaseDBContract.TABLE_USERS)
                                            .child(finalMyUserID).child(FirebaseDBContract.User.NOTIFICATIONS)
                                            .child(finalAlarm.getId() + FirebaseDBContract.User.ALARMS)
                                            .setValue(n.toMap());

                                    // Notify
                                    n.setChecked(false); /* storing true in Firebase but changing false for show in StatusBar */
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), n, finalAlarm);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
            }
        }
    }
}

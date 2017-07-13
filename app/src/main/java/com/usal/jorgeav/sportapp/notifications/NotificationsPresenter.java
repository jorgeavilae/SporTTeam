package com.usal.jorgeav.sportapp.notifications;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.HashMap;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class NotificationsPresenter implements NotificationsContract.Presenter {

    NotificationsContract.View mView;

    public NotificationsPresenter(NotificationsContract.View view) {
        this.mView = view;
    }

    @Override
    public void loadNotifications() {
        FirebaseSync.loadMyNotifications(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    HashMap<String, MyNotification> result = new HashMap<String, MyNotification>();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MyNotification notification = data.getValue(MyNotification.class);
                        if (notification == null) return;

                        @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
                        switch (type) {
                            case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                User user = Utiles.getUserFromContentProvider(notification.getExtra_data());
                                if (user == null) {
                                    FirebaseSync.loadAProfile(data.getRef().toString());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                Event event = Utiles.getEventFromContentProvider(notification.getExtra_data());
                                if (event == null) {
                                    FirebaseSync.loadAnEvent(data.getRef().toString());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                Alarm alarm = Utiles.getAlarmFromContentProvider(notification.getExtra_data());
                                if (alarm == null) {
                                    FirebaseSync.loadAnAlarm(data.getRef().toString());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                break;
                        }
                    }
                    mView.showNotifications(result);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
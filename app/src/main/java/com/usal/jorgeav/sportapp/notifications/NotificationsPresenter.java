package com.usal.jorgeav.sportapp.notifications;

import android.text.TextUtils;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.HashMap;

class NotificationsPresenter implements NotificationsContract.Presenter {
    @SuppressWarnings("unused")
    private static final String TAG = NotificationsPresenter.class.getSimpleName();

    private NotificationsContract.View mView;

    NotificationsPresenter(NotificationsContract.View view) {
        this.mView = view;
    }

    @Override
    public void loadNotifications() {
        FirebaseSync.loadMyNotifications(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, MyNotification> result = new HashMap<>();
                if (dataSnapshot.exists()) {
                    // Populate a list of notifications
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MyNotification notification = data.getValue(MyNotification.class);
                        if (notification == null) return;

                        // Make sure data in notification is loaded in Content Provider
                        @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
                        switch (type) {
                            case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                User user = UtilesContentProvider.getUserFromContentProvider(notification.getExtra_data_one());
                                if (user == null) {
                                    FirebaseSync.loadAProfile(notification.getExtra_data_one(), false);
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                                if (event == null) {
                                    FirebaseSync.loadAnEvent(notification.getExtra_data_one());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                                Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                                if (alarm == null) {
                                    FirebaseSync.loadAnAlarm(notification.getExtra_data_one());
                                }
                                if (eventCoincidence == null) {
                                    FirebaseSync.loadAnEvent(notification.getExtra_data_two());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                break;
                        }
                    }
                }

                // Show notifications
                mView.showNotifications(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mView.getActivityContext(), R.string.toast_notification_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void deleteNotification(String key) {
        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID) && key != null && !TextUtils.isEmpty(key))
            NotificationsFirebaseActions.deleteNotification(myUserID, key);
    }

    @Override
    public void deleteAllNotifications() {
        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID))
            NotificationsFirebaseActions.deleteAllNotifications(myUserID);

        // Reload
        loadNotifications();
    }
}
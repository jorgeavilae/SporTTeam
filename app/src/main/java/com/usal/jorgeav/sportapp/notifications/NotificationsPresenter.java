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
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.AlarmsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
                LinkedHashMap<String, MyNotification> result = new LinkedHashMap<>();
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
                                    UsersFirebaseSync.loadAProfile(null, notification.getExtra_data_one(), false);
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                                if (event == null) {
                                    EventsFirebaseSync.loadAnEvent(notification.getExtra_data_one());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                                Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                                if (alarm == null) {
                                    AlarmsFirebaseSync.loadAnAlarm(notification.getExtra_data_one());
                                }
                                if (eventCoincidence == null) {
                                    EventsFirebaseSync.loadAnEvent(notification.getExtra_data_two());
                                }
                                result.put(data.getKey(), notification);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                break;
                        }
                    }
                }
                // Show notifications
                mView.showNotifications(reverseLinkedHashMap(result));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mView.getActivityContext(), R.string.toast_notification_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private LinkedHashMap<String,MyNotification> reverseLinkedHashMap(LinkedHashMap<String, MyNotification> map) {
        LinkedHashMap<String, MyNotification> result = new LinkedHashMap<>(map.size());

        List<Map.Entry<String, MyNotification>> list = new ArrayList<>(map.entrySet());

        for( int i = list.size() -1; i >= 0 ; i --){
            Map.Entry<String, MyNotification> entry = list.get(i);
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
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
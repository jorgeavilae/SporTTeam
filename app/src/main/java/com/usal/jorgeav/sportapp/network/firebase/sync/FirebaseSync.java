package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

public class FirebaseSync {
    private static final String TAG = FirebaseSync.class.getSimpleName();

    private static HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();

    public static void syncFirebaseDatabase(LoginActivity loginActivity) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && listenerMap.isEmpty()) {
            String myUserID = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserID)) return;

            // Load current user profile and sports
            UsersFirebaseSync.loadAProfile(loginActivity, myUserID, true);

            // Load friends list and user data
            loadUsersFromFriends();

            // Load friends request sent list and user data
            loadUsersFromFriendsRequestsSent();

            // Load friends request received list and user data
            loadUsersFromFriendsRequestsReceived();

            // Load events created with data, users participants with data, users invited with data
            // and user requests received with data
            loadEventsFromMyOwnEvents();

            // Load participation events with data
            loadEventsFromEventsParticipation();

            // Load events with data from invitations received by current user
            loadEventsFromInvitationsReceived();

            // Load events with data from participation requests sent by current user
            loadEventsFromEventsRequests();

            // Load alarms with data created by current user
            loadAlarmsFromMyAlarms();

            // Load notification and data
            loadMyNotifications(null);
        }
    }

    public static void detachListeners() {
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : listenerMap.entrySet()) {
            Log.i(TAG, "detachListeners: ref " + entry.getKey());
            entry.getKey().removeEventListener(entry.getValue());
        }
        listenerMap.clear();
    }

    public static void loadUsersFromFriends() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.FRIENDS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriends();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadUsersFromFriendsRequestsSent() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriendsRequestsSent();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadUsersFromFriendsRequestsReceived() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriendsRequestsReceived();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadEventsFromMyOwnEvents() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_CREATED);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromMyOwnEvents();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadEventsFromEventsParticipation() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_PARTICIPATION);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromEventsParticipation();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadUsersFromInvitationsSent(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.Event.INVITATIONS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromInvitationsSent();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadEventsFromInvitationsReceived() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromInvitationsReceived();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadUsersFromUserRequests(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.Event.USER_REQUESTS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromUserRequests();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadEventsFromEventsRequests() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_REQUESTS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromEventsRequests();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadAlarmsFromMyAlarms() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = AlarmsFirebaseSync.getListenerToLoadAlarmsFromMyAlarms();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    public static void loadFieldsFromCity(String city, boolean shouldResetFieldsData) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Field.CITY;

        // Should reset Fields table because it's first load or there was a city change.
        if (shouldResetFieldsData) {
            // Remove listener from DatabaseReference and from listenerMap
            if (listenerMap.get(fieldsRef) != null) {
                fieldsRef.removeEventListener(listenerMap.get(fieldsRef));
                listenerMap.remove(fieldsRef);
            }

            // Remove Fields and FieldsSport from tables
            MyApplication.getAppContext().getContentResolver()
                    .delete(SportteamContract.FieldEntry.CONTENT_FIELD_URI, null, null);
            MyApplication.getAppContext().getContentResolver()
                    .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI, null, null);
        }

        if (!listenerMap.containsKey(fieldsRef)) {
            ExecutorChildEventListener childEventListener = FieldsFirebaseSync.getListenerToLoadFieldsFromCity();
            fieldsRef.orderByChild(filter).equalTo(city).addChildEventListener(childEventListener);
            listenerMap.put(fieldsRef, childEventListener);
            Log.i(TAG, "attachListener ref " + fieldsRef);
        }
    }

    public static void loadMyNotifications(ValueEventListener listener) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS);

        if (listener == null)
            listener = new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                @Override
                public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            MyNotification notification = data.getValue(MyNotification.class);
                            if (notification == null) continue;

                            @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
                            switch (type) {
                                case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                    User user = UtilesContentProvider.getUserFromContentProvider(notification.getExtra_data_one());
                                    if (user == null) {
                                        UsersFirebaseSync.loadAProfileAndNotify(data.getRef().toString(), notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, user);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                    Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                                    if (event == null) {
                                        EventsFirebaseSync.loadAnEventAndNotify(data.getRef().toString(), notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, event);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                    Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                                    Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                                    if (alarm == null || eventCoincidence == null) {
                                        AlarmsFirebaseSync.loadAnAlarmAndNotify(data.getRef().toString(), notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, alarm);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                    break;
                            }
                            NotificationsFirebaseActions.checkNotification(data.getRef().toString());
                        }
                }

                @Override
                public void onCancelledExecutor(DatabaseError databaseError) {

                }
            };
        ref.orderByChild(FirebaseDBContract.Notification.DATE).limitToLast(20)
                .addListenerForSingleValueEvent(listener);
    }


}

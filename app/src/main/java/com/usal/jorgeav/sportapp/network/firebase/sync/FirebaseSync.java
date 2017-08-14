package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public static final String TAG = FirebaseSync.class.getSimpleName();

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
            Log.d(TAG, "detachListeners: ref "+entry.getKey());
            entry.getKey().removeEventListener(entry.getValue());
        }
        listenerMap.clear();
    }

    public static void loadUsersFromFriends() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = "";
        if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS);

        ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriends();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }
    public static void loadUsersFromFriendsRequestsSent() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT);

        ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriendsRequestsSent();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }
    public static void loadUsersFromFriendsRequestsReceived() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED);

        ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriendsRequestsReceived();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    public static void loadEventsFromMyOwnEvents() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_CREATED);

        ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromMyOwnEvents();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }
    public static void loadEventsFromEventsParticipation() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION);

        ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromEventsParticipation();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    // Retrieve all invitations, sent by the current user or others, in order to not
    // invite same user twice.
    public static void loadUsersFromInvitationsSent(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId + "/" + FirebaseDBContract.Event.INVITATIONS);

        ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromInvitationsSent();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }
    public static void loadEventsFromInvitationsReceived() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED);

        ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromInvitationsReceived();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    public static void loadUsersFromUserRequests(String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(key + "/" + FirebaseDBContract.Event.USER_REQUESTS);

        ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromUserRequests();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }
    public static void loadEventsFromEventsRequests() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_REQUESTS);

        ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromEventsRequests();
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    public static void loadAlarmsFromMyAlarms() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS);

        ExecutorChildEventListener childEventListener = AlarmsFirebaseSync.getListenerToLoadAlarmsFromMyAlarms();

        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    public static void loadFieldsFromCity(String city, boolean shouldResetFieldsData) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Field.CITY;

        // Should reset Fields table because it's first load or it's change city.
        if (shouldResetFieldsData) {
            /* remove listener from DatabaseReference and from listenerMap */
            if (listenerMap.get(fieldsRef) != null) {
                fieldsRef.removeEventListener(listenerMap.get(fieldsRef));
                listenerMap.remove(fieldsRef);
            }

            /* remove Fields and FieldsSport from tables */
            MyApplication.getAppContext().getContentResolver()
                    .delete(SportteamContract.FieldEntry.CONTENT_FIELD_URI, null, null);
            MyApplication.getAppContext().getContentResolver()
                    .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI, null, null);
        }

        ExecutorChildEventListener childEventListener = FieldsFirebaseSync.getListenerToLoadFieldsFromCity();
        if (!listenerMap.containsKey(fieldsRef)) {
            fieldsRef.orderByChild(filter).equalTo(city).addChildEventListener(childEventListener);
            listenerMap.put(fieldsRef, childEventListener);
            Log.d(TAG, "attachListener ref " + fieldsRef);
        }
    }

    public static void loadMyNotifications(ValueEventListener listener) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS);
        ExecutorValueEventListener defaultListener = new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MyNotification notification = data.getValue(MyNotification.class);
                        if (notification == null) return;

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

        if (listener == null) listener = defaultListener;
        myUserRef.addListenerForSingleValueEvent(listener);
        Log.d(TAG, "loadMyNotifications");
    }


}

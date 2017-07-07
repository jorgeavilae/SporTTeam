package com.usal.jorgeav.sportapp.network.firebase;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesDataSnapshot;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseSync {
    public static final String TAG = FirebaseSync.class.getSimpleName();

    private static HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();

    /*public static void syncFirebaseDatabase() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && listenerMap.isEmpty()) {
            // Load current user profile and sports
            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
            String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();

            loadAProfile(myUserID);

            // Load fields from user city
            loadFieldsFromCity(Utiles.getCurrentCity(MyApplication.getAppContext(), myUserID));

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
        }
    }*/
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

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = "";
                    if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                loadAProfile(dataSnapshot.getKey());
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = "";
                if (fUser != null) myUserID = fUser.getUid();
                ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                MyApplication.getAppContext().getContentResolver()
                        .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);

            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = "";
                if (fUser != null) myUserID = fUser.getUid();
                String userId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND "
                                + SportteamContract.FriendsEntry.USER_ID + " = ? ",
                        new String[]{myUserID, userId});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadUsersFromFriendsRequestsSent() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String senderId = ""; if (fUser != null) senderId = fUser.getUid();
                String receiverId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.SENDER_ID + " = ? ",
                        new String[]{receiverId, senderId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadUsersFromFriendsRequestsReceived() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesDataSnapshot.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String receiverId = ""; if (fUser != null) receiverId = fUser.getUid();
                String senderId = dataSnapshot.getKey();
                Log.d(TAG, "onChildRemoved: sender "+senderId);
                Log.d(TAG, "onChildRemoved: receiver "+receiverId);
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                                SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ? AND "
                                        + SportteamContract.FriendRequestEntry.SENDER_ID + " = ? ",
                                new String[]{receiverId, senderId});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }

    public static void loadEventsFromMyOwnEvents() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_CREATED);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    // Load user requests received with data
                    loadUsersFromUserRequests(dataSnapshot.getKey());
                    // Load users invited with data
                    loadUsersFromInvitationsSent(dataSnapshot.getKey());
                }

            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadUsersFromUserRequests(String key) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(key + "/" + FirebaseDBContract.Event.USER_REQUESTS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventsRequestsToContentValues(dataSnapshot, eventId, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                String senderId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventRequestsEntry.SENDER_ID + " = ? ",
                        new String[]{eventId, senderId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadUsersFromInvitationsSent(String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(key + "/" + FirebaseDBContract.Event.INVITATIONS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey());

                    String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventInvitationsToContentValues(dataSnapshot, eventId, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                String userId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventsInvitationEntry.USER_ID + " = ? ",
                        new String[]{eventId, userId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }

    public static void loadEventsFromEventsParticipation() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventsParticipationToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cvData);
                }

            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventsParticipationEntry.USER_ID + " = ? ",
                        new String[]{eventId, myUserID});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadEventsFromInvitationsReceived() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventInvitationsToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI, cvData);
                }

            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventsInvitationEntry.USER_ID + " = ? ",
                        new String[]{eventId, myUserID});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadEventsFromEventsRequests() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_REQUESTS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesDataSnapshot
                            .dataSnapshotEventsRequestsToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI, cvData);
                }

            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                String eventId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ? AND "
                                +SportteamContract.EventRequestsEntry.SENDER_ID + " = ? ",
                        new String[]{eventId, myUserID});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }

    public static void loadAlarmsFromMyAlarms() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Alarm a = UtilesDataSnapshot.dataSnapshotToAlarm(dataSnapshot);
                    ContentValues cv = UtilesDataSnapshot.alarmToContentValues(a);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                    if (a.getmField() != null)
                        loadAField(a.getmField());
                }

            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String alarmId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                        SportteamContract.AlarmEntry.ALARM_ID + " = ? ",
                        new String[]{alarmId});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(myUserRef)) {
            myUserRef.addChildEventListener(childEventListener);
            listenerMap.put(myUserRef, childEventListener);
            Log.d(TAG, "loadUsersFromFriends: attachListener ref " + myUserRef);
        }
    }
    public static void loadMyNotifications() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS);

        myUserRef.addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        MyNotification notification = data.getValue(MyNotification.class);
                        if (notification == null) return;
                        if (!notification.getChecked()) {
                            @FirebaseDBContract.NotificationTypes int type = notification.getType();
                            switch (type) {
                                case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                    User user = Utiles.getUserFromContentProvider(notification.getExtra_data());
                                    if (user == null) {
                                        loadAProfileAndNotify(notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, user);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                    Event event = Utiles.getEventFromContentProvider(notification.getExtra_data());
                                    if (event == null) {
                                        loadAnEventAndNotify(notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, event);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                    break;
                            }
                            FirebaseActions.checkNotification(data.getRef().toString());
                        }
                    }
            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        });
        Log.d(TAG, "loadMyNotifications");
    }

    public static void loadAProfile(String userID) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(userID)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = UtilesDataSnapshot.dataSnapshotToUser(dataSnapshot);

                            ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getmId()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadAProfileAndNotify(final MyNotification notification) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(notification.getExtra_data())
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = UtilesDataSnapshot.dataSnapshotToUser(dataSnapshot);

                            ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getmId()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));

                            //Notify
                            UtilesNotification.createNotification(MyApplication.getAppContext(), notification, anUser);
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAnAlarm(String alarmId) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.ALARMS);

        myUserRef.child(alarmId)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Alarm a = UtilesDataSnapshot.dataSnapshotToAlarm(dataSnapshot);
                            ContentValues cv = UtilesDataSnapshot.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                            if (a.getmField() != null)
                                loadAField(a.getmField());
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAnEvent(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        eventRef.child(eventId)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                            if (e == null) return;
                            e.setEvent_id(dataSnapshot.getKey());

                            ContentValues cv = UtilesDataSnapshot.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            loadAField(e.getField_id());

                            // Load users participants with data
                            if (e.getParticipants() != null)
                                loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadAnEventAndNotify(final MyNotification notification) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        eventRef.child(notification.getExtra_data())
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                            if (e == null) return;
                            e.setEvent_id(dataSnapshot.getKey());

                            ContentValues cv = UtilesDataSnapshot.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            loadAField(e.getField_id());

                            // Load users participants with data
                            if (e.getParticipants() != null)
                                loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());

                            //Notify
                            UtilesNotification.createNotification(MyApplication.getAppContext(), notification, e);
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAField(String fieldId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);

        myUserRef.child(fieldId)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ContentValues> cvArray = new ArrayList<>();
                            List<Field> fields = UtilesDataSnapshot.dataSnapshotToFieldList(dataSnapshot);
                            cvArray.addAll(UtilesDataSnapshot.fieldsArrayToContentValues(fields));

                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                            cvArray.toArray(new ContentValues[cvArray.size()]));
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadUsersFromParticipants(String eventId, Map<String, Boolean> participants) {
        MyApplication.getAppContext().getContentResolver()
                .delete(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});

        for (Map.Entry<String, Boolean> entry : participants.entrySet()) {
            loadAProfile(entry.getKey());

            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.EventsParticipationEntry.USER_ID, entry.getKey());
            cv.put(SportteamContract.EventsParticipationEntry.EVENT_ID, eventId);
            cv.put(SportteamContract.EventsParticipationEntry.PARTICIPATES, entry.getValue() ? 1 : 0);
            MyApplication.getAppContext().getContentResolver()
                    .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cv);
        }
    }

    public static void loadFieldsFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Field.CITY;

        fieldsRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<ContentValues> cvArray = new ArrayList<>();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                List<Field> fields = UtilesDataSnapshot.dataSnapshotToFieldList(data);
                                cvArray.addAll(UtilesDataSnapshot.fieldsArrayToContentValues(fields));
                            }
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                            cvArray.toArray(new ContentValues[cvArray.size()]));
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadEventsFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventsRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        eventsRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Event e = UtilesDataSnapshot.dataSnapshotToEvent(data);
                                ContentValues cv = UtilesDataSnapshot.eventToContentValues(e);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                                loadAField(e.getField_id());
                            }
                            // TODO: 16/06/2017 comparar evento con alarmas para que se muestren notificaciones
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadUsersFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        usersRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = UtilesDataSnapshot.dataSnapshotToUser(data);

                                ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[cvSports.size()]));
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadUsersWithName(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;

        /* https://stackoverflow.com/a/40633692/4235666
         * https://firebase.google.com/docs/database/admin/retrieve-data */
        usersRef.orderByChild(filter).startAt(username).endAt(username+"\uf8ff")
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = UtilesDataSnapshot.dataSnapshotToUser(data);
                                ContentValues cvData = UtilesDataSnapshot.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesDataSnapshot.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[cvSports.size()]));
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
}

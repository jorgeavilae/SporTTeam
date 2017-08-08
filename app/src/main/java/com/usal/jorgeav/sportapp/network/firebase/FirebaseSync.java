package com.usal.jorgeav.sportapp.network.firebase;

import android.content.ContentValues;
import android.net.Uri;
import android.support.annotation.NonNull;
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
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseSync {
    public static final String TAG = FirebaseSync.class.getSimpleName();

    private static HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();
    private static LoginActivity mLoginActivity;

    public static void syncFirebaseDatabase(LoginActivity loginActivity) {
        mLoginActivity = loginActivity;
        if (FirebaseAuth.getInstance().getCurrentUser() != null && listenerMap.isEmpty()) {
            String myUserID = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserID)) return;

            // Load current user profile and sports
            loadAProfile(myUserID, true);

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

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(dataSnapshot.getKey(), false);
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = "";
                    if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                loadAProfile(dataSnapshot.getKey(), false);
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = "";
                if (fUser != null) myUserID = fUser.getUid();
                ContentValues cvData = UtilesContentValues.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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
                    loadAProfile(dataSnapshot.getKey(), false);

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, true);
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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
                    loadAProfile(dataSnapshot.getKey(), false);

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, false);
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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
                        SportteamContract.SimulatedParticipantEntry.CONTENT_SIMULATED_PARTICIPANT_URI,
                        SportteamContract.SimulatedParticipantEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ? ",
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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

                    // Load users invited with data if I participate
                    Boolean participation = dataSnapshot.getValue(Boolean.class);
                    if (participation != null && participation)
                        loadUsersFromInvitationsSent(dataSnapshot.getKey());

                    String myUserID = Utiles.getCurrentUserId();
                    if (TextUtils.isEmpty(myUserID)) return;
                    ContentValues cvData = UtilesContentValues
                            .dataSnapshotEventsParticipationToContentValues(dataSnapshot, myUserID);
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
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    // Retrieve all invitations, sent by the current user or others, in order to not
    // invite same user twice.
    public static void loadUsersFromInvitationsSent(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId + "/" + FirebaseDBContract.Event.INVITATIONS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Invitation invitation = dataSnapshot.getValue(Invitation.class);
                    if (invitation == null) {
                        Log.e(TAG, "loadUsersFromInvitationsSent: onChildAddedExecutor: parse Invitation null");
                        return;
                    }

                    // Load receiver. Necessary cause it could be a sender's friend not mine.
                    loadAProfile(invitation.getReceiver(), false);

                    // Load sender. It could be the current user. It could be other user, in such
                    // case that user would be load in loadParticipants or loadOwner.

                    // Load event. Not needed cause this invitation is for one of the current
                    // user's events or participation ones.

                    ContentValues cvData = UtilesContentValues.invitationToContentValues(invitation);
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
                                + SportteamContract.EventsInvitationEntry.RECEIVER_ID + " = ? ",
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
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }
    public static void loadEventsFromInvitationsReceived() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Invitation invitation = dataSnapshot.getValue(Invitation.class);
                    if (invitation == null) {
                        Log.e(TAG, "loadEventsFromInvitationsReceived: onChildAddedExecutor: parse Invitation null");
                        return;
                    }
                    // Load Event
                    loadAnEvent(invitation.getEvent());

                    // Load User who send me this invitation. Load in loadFriends

                    ContentValues cvData = UtilesContentValues.invitationToContentValues(invitation);
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
                                + SportteamContract.EventsInvitationEntry.RECEIVER_ID + " = ? ",
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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
                    loadAProfile(dataSnapshot.getKey(), false);

                    String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                    ContentValues cvData = UtilesContentValues
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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
                    ContentValues cvData = UtilesContentValues
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
            Log.d(TAG, "attachListener ref " + myUserRef);
        }
    }

    public static void loadAlarmsFromMyAlarms() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS);

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Alarm a = dataSnapshot.getValue(Alarm.class);
                    if (a == null) {
                        Log.e(TAG, "loadAlarmsFromMyAlarms: onChildAddedExecutor: Error parsing alarm");
                        return;
                    }
                    a.setId(dataSnapshot.getKey());

                    ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                    if (a.getField_id() != null)
                        loadAField(a.getField_id());
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
            Log.d(TAG, "attachListener ref " + myUserRef);
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
                                    loadAProfileAndNotify(data.getRef().toString(), notification);
                                    continue;
                                }
                                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, user);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                                if (event == null) {
                                    loadAnEventAndNotify(data.getRef().toString(), notification);
                                    continue;
                                }
                                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, event);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                                Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                                if (alarm == null || eventCoincidence == null) {
                                    loadAnAlarmAndNotify(data.getRef().toString(), notification);
                                    continue;
                                }
                                UtilesNotification.createNotification(MyApplication.getAppContext(), notification, alarm);
                                break;
                            case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                break;
                        }
                        FirebaseActions.checkNotification(data.getRef().toString());
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

    public static void loadAProfile(@NonNull String userID, final boolean shouldUpdateCityPrefs) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(userID)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (anUser == null) {
                                Log.e(TAG, "loadAProfile: onDataChangeExecutor: Error parsing user");
                                return;
                            }
                            anUser.setUid(dataSnapshot.getKey());

                            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                            String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                            if (!TextUtils.isEmpty(myUserID) && myUserID.equals(anUser.getUid()))
                                // anUser is the current User so check email address in case
                                // it was recently changed and later cancel that change.
                                Utiles.checkEmailFromDatabaseIsCorrect(fUser, anUser);

                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getUid()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));

                            if (shouldUpdateCityPrefs) {
                                UtilesPreferences.setCurrentUserCity(MyApplication.getAppContext());
                                UtilesPreferences.setCurrentUserCityCoords(MyApplication.getAppContext());

                                // Load fields from user city
                                loadFieldsFromCity(UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext()), true);
                                // Load events from user city
                                loadEventsFromCity(UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext()));

                                // Updates in prefs came from new login in so if the mLoginActivity
                                // isn't null, ends that Activity and start BaseActivity with the
                                // User data in ContentProvider.
                                if (mLoginActivity != null)
                                    mLoginActivity.finishLoadMyProfile();
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadAProfileAndNotify(final String notificationRef, final MyNotification notification) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        if (notification.getExtra_data_one() != null)
        myUserRef.child(notification.getExtra_data_one())
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (anUser == null) {
                                Log.e(TAG, "loadAProfileAndNotify: onDataChangeExecutor: Error parsing user");
                                return;
                            }
                            anUser.setUid(dataSnapshot.getKey());

                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getUid()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));

                            //Notify
                            UtilesNotification.createNotification(MyApplication.getAppContext(), notification, anUser);
                            FirebaseActions.checkNotification(notificationRef);
                        } else {
                            Log.e(TAG, "loadAProfileAndNotify: onDataChangeExecutor: User "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAnAlarm(@NonNull String alarmId) {
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
                            Alarm a = dataSnapshot.getValue(Alarm.class);
                            if (a == null) {
                                Log.e(TAG, "loadAnAlarm: onChildAddedExecutor: Error parsing alarm");
                                return;
                            }
                            a.setId(dataSnapshot.getKey());

                            ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                            if (a.getField_id() != null)
                                loadAField(a.getField_id());
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAnAlarmAndNotify(final String notificationRef, final MyNotification notification) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.ALARMS);

        if (notification.getExtra_data_one() != null)
        myUserRef.child(notification.getExtra_data_one())
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Alarm a = dataSnapshot.getValue(Alarm.class);
                            if (a == null) {
                                Log.e(TAG, "loadAnAlarmAndNotify: onChildAddedExecutor: Error parsing alarm");
                                return;
                            }
                            a.setId(dataSnapshot.getKey());

                            ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                            if (a.getField_id() != null)
                                loadAField(a.getField_id());


                            String eventId = notification.getExtra_data_two();
                            if (eventId != null)
                                database.getReference(FirebaseDBContract.TABLE_EVENTS)
                                        .child(eventId).addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()){
                                @Override
                                protected void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                                        if (e == null) {
                                            Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Error parsing Event");
                                            return;
                                        }
                                        e.setEvent_id(dataSnapshot.getKey());

                                        ContentValues cv = UtilesContentValues.eventToContentValues(e);
                                        MyApplication.getAppContext().getContentResolver()
                                                .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                                        loadAProfile(e.getOwner(), false);
                                        loadAField(e.getField_id());

                                        //Notify
                                        UtilesNotification.createNotification(MyApplication.getAppContext(), notification, a);
                                        FirebaseActions.checkNotification(notificationRef);

                                    } else {
                                        Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Event "
                                                + notification.getExtra_data_two() + " doesn't exist");
                                        FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                                    }
                                }

                                @Override
                                protected void onCancelledExecutor(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Alarm "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAnEvent(@NonNull String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        eventRef.child(eventId)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                            if (e == null) {
                                Log.e(TAG, "loadAnEvent: onDataChangeExecutor: Error parsing Event");
                                return;
                            }
                            e.setEvent_id(dataSnapshot.getKey());

                            //If the current user is the owner and it is a past event
                            // delete user requests and invitations
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null && e.getOwner().equals(currentUser.getUid())
                                    && System.currentTimeMillis() > e.getDate()) {
                                for (DataSnapshot d : dataSnapshot.child(FirebaseDBContract.Event.USER_REQUESTS).getChildren())
                                    FirebaseActions.cancelEventRequest(d.getKey(), e.getEvent_id(), e.getOwner());
                                for (DataSnapshot d : dataSnapshot.child(FirebaseDBContract.Event.INVITATIONS).getChildren()) {
                                    Invitation invitation = d.getValue(Invitation.class);
                                    if (invitation != null)
                                        FirebaseActions.deleteInvitationToThisEvent(invitation.getSender(), e.getEvent_id(), invitation.getReceiver());
                                }
                            }

                            ContentValues cv = UtilesContentValues.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            loadAProfile(e.getOwner(), false);
                            loadAField(e.getField_id());

                            // Load users participants with data
                            loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());

                            // Load simulated users participants with data
                            loadSimulatedParticipants(e.getEvent_id(), e.getSimulated_participants());
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    private static void loadAnEventAndNotify(final String notificationRef, final MyNotification notification) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        if (notification.getExtra_data_one() != null)
            eventRef.child(notification.getExtra_data_one())
                    .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                            if (e == null) {
                                Log.e(TAG, "loadAnEventAndNotify: onDataChangeExecutor: Error parsing Event");
                                return;
                            }
                            e.setEvent_id(dataSnapshot.getKey());

                            ContentValues cv = UtilesContentValues.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            loadAProfile(e.getOwner(), false);
                            loadAField(e.getField_id());

                            // Load users participants with data
                            loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());

                            // Load simulated users participants with data
                            loadSimulatedParticipants(e.getEvent_id(), e.getSimulated_participants());

                            //Notify
                            UtilesNotification.createNotification(MyApplication.getAppContext(), notification, e);
                            FirebaseActions.checkNotification(notificationRef);
                        } else {
                            Log.e(TAG, "loadAnEventAndNotify: onDataChangeExecutor: Event "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }
    public static void loadAField(String fieldId) {
        if (fieldId == null || TextUtils.isEmpty(fieldId)) return;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);

        myUserRef.child(fieldId)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Field field = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Field.class);
                            if (field == null) {
                                Log.e(TAG, "loadAField: onDataChangeExecutor: Error parsing Field");
                                return;
                            }
                            field.setId(dataSnapshot.getKey());

                            ContentValues cvData = UtilesContentValues.fieldToContentValues(field);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, cvData);

                            List<ContentValues> cvSports = UtilesContentValues.fieldSportToContentValues(field);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                            SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                            new String[]{field.getId()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));
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

        if (participants != null)
            for (Map.Entry<String, Boolean> entry : participants.entrySet()) {
                loadAProfile(entry.getKey(), false);

                ContentValues cv = new ContentValues();
                cv.put(SportteamContract.EventsParticipationEntry.USER_ID, entry.getKey());
                cv.put(SportteamContract.EventsParticipationEntry.EVENT_ID, eventId);
                cv.put(SportteamContract.EventsParticipationEntry.PARTICIPATES, entry.getValue() ? 1 : 0);
                MyApplication.getAppContext().getContentResolver()
                        .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cv);
            }
    }
    private static void loadSimulatedParticipants(String eventId, Map<String, SimulatedUser> simulatedParticipants) {
        MyApplication.getAppContext().getContentResolver()
                .delete(SportteamContract.SimulatedParticipantEntry.CONTENT_SIMULATED_PARTICIPANT_URI,
                        SportteamContract.SimulatedParticipantEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});

        if (simulatedParticipants != null)
            for (Map.Entry<String, SimulatedUser> entry : simulatedParticipants.entrySet()) {
                ContentValues cv = new ContentValues();
                cv.put(SportteamContract.SimulatedParticipantEntry.EVENT_ID, eventId);
                cv.put(SportteamContract.SimulatedParticipantEntry.SIMULATED_USER_ID, entry.getKey());
                cv.put(SportteamContract.SimulatedParticipantEntry.ALIAS, entry.getValue().getAlias());
                cv.put(SportteamContract.SimulatedParticipantEntry.PROFILE_PICTURE, entry.getValue().getProfile_picture());
                cv.put(SportteamContract.SimulatedParticipantEntry.AGE, entry.getValue().getAge());
                cv.put(SportteamContract.SimulatedParticipantEntry.OWNER, entry.getValue().getOwner());
                MyApplication.getAppContext().getContentResolver()
                        .insert(SportteamContract.SimulatedParticipantEntry.CONTENT_SIMULATED_PARTICIPANT_URI, cv);
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

        ExecutorChildEventListener childEventListener = new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Field field = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Field.class);
                    if (field == null) {
                        Log.e(TAG, "loadFieldsFromCity: onDataChangeExecutor: Error parsing Field from "
                                + dataSnapshot.child(FirebaseDBContract.DATA).getRef());
                        return;
                    }
                    field.setId(dataSnapshot.getKey());

                    ContentValues cvData = UtilesContentValues.fieldToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, cvData);

                    List<ContentValues> cvSports = UtilesContentValues.fieldSportToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .bulkInsert(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    cvSports.toArray(new ContentValues[cvSports.size()]));
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String fieldId = dataSnapshot.getKey();
                Log.d(TAG, "onChildRemovedExecutor: "+fieldId);
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                                new String[]{fieldId});
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                new String[]{fieldId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
        if (!listenerMap.containsKey(fieldsRef)) {
            fieldsRef.orderByChild(filter).equalTo(city).addChildEventListener(childEventListener);
            listenerMap.put(fieldsRef, childEventListener);
            Log.d(TAG, "attachListener ref " + fieldsRef);
        }
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
                                Event e = data.child(FirebaseDBContract.DATA).getValue(Event.class);
                                if (e == null) {
                                    Log.e(TAG, "loadEventsFromCity: onDataChangeExecutor: Error parsing Event");
                                    return;
                                }
                                e.setEvent_id(data.getKey());
                                String myUserId = Utiles.getCurrentUserId();

                                // Check if I am participant or owner
                                if (!TextUtils.isEmpty(myUserId) && !myUserId.equals(e.getOwner())
                                        && (e.getParticipants() == null || !e.getParticipants().containsKey(myUserId))) {
                                    ContentValues cv = UtilesContentValues.eventToContentValues(e);
                                    MyApplication.getAppContext().getContentResolver()
                                            .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                                    loadAProfile(e.getOwner(), false);
                                    loadAField(e.getField_id());

                                    // Load users participants with data
                                    loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());

                                    // Load simulated users participants with data
                                    loadSimulatedParticipants(e.getEvent_id(), e.getSimulated_participants());
                                }
                            }
                            FirebaseActions.checkAlarmsForNotifications();
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
                                User anUser = data.child(FirebaseDBContract.DATA).getValue(User.class);
                                if (anUser == null) {
                                    Log.e(TAG, "loadUsersFromCity: onDataChangeExecutor: Error parsing user");
                                    return;
                                }
                                anUser.setUid(data.getKey());

                                ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
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
        usersRef.orderByChild(filter).startAt(username).endAt(username+"\uf8ff").limitToFirst(40)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = data.child(FirebaseDBContract.DATA).getValue(User.class);
                                if (anUser == null) {
                                    Log.e(TAG, "loadUsersWithName: onDataChangeExecutor: Error parsing user");
                                    return;
                                }
                                anUser.setUid(data.getKey());

                                ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
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

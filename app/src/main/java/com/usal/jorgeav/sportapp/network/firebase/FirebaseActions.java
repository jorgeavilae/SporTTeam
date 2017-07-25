package com.usal.jorgeav.sportapp.network.firebase;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseActions {
    public static final String TAG = FirebaseActions.class.getSimpleName();

    // Add User
    public static Query getUserEmailReferenceEqualTo(String email) {
        String userEmailPath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.EMAIL;
        return FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .orderByChild(userEmailPath).equalTo(email);
    }
    public static Query getUserNameReferenceEqualTo(String name) {
        String userNamePath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;
        return FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .orderByChild(userNamePath).equalTo(name);
    }
    public static void addUser(User user){
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(user.getUid()).setValue(user.toMap());
    }
    public static void updateSports(String myUid, HashMap<String, Float> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.SPORTS_PRACTICED).setValue(sportsMap);
    }

    // Add Event
    public static void addEvent(Event event) {
        //Create eventId
        DatabaseReference eventTable = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS);
        event.setEvent_id(eventTable.push().getKey());

        //Set Event in Event Table
        String eventInEventTable = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + event.getEvent_id()
                + "/" + FirebaseDBContract.DATA;

        //Set Event created in ownerId
        String userEventCreated = "/" + FirebaseDBContract.TABLE_USERS + "/" + event.getOwner() + "/"
                + FirebaseDBContract.User.EVENTS_CREATED + "/" + event.getEvent_id();

        //Set next Event in fieldId
        String fieldNextEvent = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + event.getField_id() + "/"
                + FirebaseDBContract.Field.NEXT_EVENTS + "/" + event.getEvent_id();

        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventInEventTable, event.toMap());
        // Listener is attached to this reference so it doesn't need to reload
        childUpdates.put(userEventCreated, currentTime);
        childUpdates.put(fieldNextEvent, currentTime);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
    public static void editEvent(Event event) {
        //Set Event in Event Table
        String eventInEventTable = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + event.getEvent_id()
                + "/" + FirebaseDBContract.DATA;

        //Set Event created in ownerId
        String userEventCreated = "/" + FirebaseDBContract.TABLE_USERS + "/" + event.getOwner() + "/"
                + FirebaseDBContract.User.EVENTS_CREATED + "/" + event.getEvent_id();

        //Set next Event in fieldId
        String fieldNextEvent = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + event.getField_id() + "/"
                + FirebaseDBContract.Field.NEXT_EVENTS + "/" + event.getEvent_id();

        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventInEventTable, event.toMap());
        // Listener is attached to this reference so it doesn't need to reload
        childUpdates.put(userEventCreated, currentTime);
        childUpdates.put(fieldNextEvent, currentTime);

        //Notify participants the event has changed
        if (event.getParticipants() != null && event.getParticipants().size() > 0) {
            // Notification object
            MyNotification n;
            String notificationTitle = MyApplication.getAppContext()
                    .getString(R.string.notification_title_event_edit);
            String notificationMessage = MyApplication.getAppContext()
                    .getString(R.string.notification_msg_event_edit);
            @UtilesNotification.NotificationType
            Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_EDIT;
            @FirebaseDBContract.NotificationDataTypes
            Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
            n = new MyNotification(notificationType, false, notificationTitle,
                    notificationMessage, event.getEvent_id(), null, type, currentTime);

            //Set Event edited MyNotification in participant
            String notificationId = event.getEvent_id() + FirebaseDBContract.Event.OWNER;
            for (Map.Entry<String, Boolean> entry : event.getParticipants().entrySet())
                if (entry.getValue()) {
                    String eventEditNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                            + entry.getKey() + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
                    childUpdates.put(eventEditNotification, n.toMap());
                }
            //The current user is the owner since is the only one who can edit this event
        }

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    // Add Event
    public static void addField(Field field) {
        if(TextUtils.isEmpty(field.getId())) {
            //Create fieldId
            DatabaseReference fieldTable = FirebaseDatabase.getInstance()
                    .getReference(FirebaseDBContract.TABLE_FIELDS);
            field.setId(fieldTable.push().getKey());
        }

        //Set Field in Field Table
        String fieldInFieldTable = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + field.getId();

        //Set Field created in creatorId
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(fieldInFieldTable, field.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
    public static void updateFieldSports(String fieldId, Map<String, Object> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT).setValue(sportsMap);
    }
    public static void addFieldSport(String fieldId, SportCourt sport) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT).child(sport.getSport_id()).setValue(sport.toMap());
    }

    // Add Alarm
    public static void addAlarm(Alarm alarm, String myUserId) {
        DatabaseReference myUserAlarmsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserId)
                .child(FirebaseDBContract.User.ALARMS);

        if (TextUtils.isEmpty(alarm.getId()))
            alarm.setId(myUserAlarmsRef.push().getKey());

        // Listener is attached to this reference so it doesn't need to reload
        myUserAlarmsRef.child(alarm.getId()).setValue(alarm.toMap());
    }

    // It couldn't check if children exists, but if it set a value on non-existent child
    // wouldn't be added to the content provider
    public static void sendFriendRequest(String myUid, String otherUid) {
        //Set Friend Request Sent in my User
        String userFriendRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + otherUid;

        //Set Friend Request Received in other User
        String userFriendRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + myUid;

        //Set Friend Request Received MyNotification in other User
        String notificationId = myUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        //Notification object
        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_friend_request_received);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_friend_request_received);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_USER;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, myUid, null, type, currentTime);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, currentTime);
        childUpdates.put(userFriendRequestReceived, currentTime);
        childUpdates.put(userFriendRequestReceivedNotification, n.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
    public static void cancelFriendRequest(String myUid, String otherUid) {
        //Delete Friend Request Sent in my User
        String userFriendRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + otherUid;

        //Delete Friend Request Received in other User
        String userFriendRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + myUid;

        //Delete Friend Request Received MyNotification in other User
        String notificationId = myUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, null);
        childUpdates.put(userFriendRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);

        database.updateChildren(childUpdates);
    }
    public static void acceptFriendRequest(String myUid, String otherUid) {
        //Add Friend to my User
        String myUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + otherUid;

        //Add Friend to other User
        String otherUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + myUid;

        //Set Friend new MyNotification in other User
        String notificationFriendId = myUid + FirebaseDBContract.User.FRIENDS;
        String userFriendNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendId;

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Received MyNotification in my User
        String notificationFriendRequestId = otherUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendRequestId;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_friend_request_accepted);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_friend_request_accepted);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_USER;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, myUid, null, type, currentTime);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriends, currentTime);
        childUpdates.put(otherUserFriends, currentTime);
        childUpdates.put(userFriendNotification, n.toMap());
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        database.updateChildren(childUpdates);
    }
    public static void declineFriendRequest(String myUid, String otherUid) {
        //No need to notify otherUid that myUid decline his friend request

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Received MyNotification in my User
        String notificationId = otherUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        database.updateChildren(childUpdates);
    }
    public static void deleteFriend(String myUid, String otherUid) {
        //Delete Friend new MyNotification in other User
        String notificationFriendId = myUid + FirebaseDBContract.User.FRIENDS;
        String userFriendNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendId;

        //Delete Friend to my User
        String myUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + otherUid;

        //Delete Friend to other User
        String otherUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriends, null);
        childUpdates.put(otherUserFriends, null);
        childUpdates.put(userFriendNotification, null);

        database.updateChildren(childUpdates);
    }

    // User otherUid receive an invitation to the event eventId, from user myUid
    public static void sendInvitationToThisEvent(String myUid, String eventId, String otherUid) {
        //Set Invitation Sent in myUid
        String userInvitationSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

        //Set Invitation Sent in Event
        String eventInvitationSent =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + otherUid;

        //Set Invitation Received in otherUid
        String userInvitationReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;

        //Set Invitation Received MyNotification in otherUid
        String notificationId = eventId + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED;
        String userInvitationReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        // Invitation object
        long currentTime = System.currentTimeMillis();
        Invitation invitation = new Invitation(myUid, otherUid, eventId, currentTime);

        // Notification object
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_event_invitation_received);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_event_invitation_received);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_RECEIVED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, eventId, null, type, currentTime);

        // Updates
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userInvitationSent, invitation.toMap());
        childUpdates.put(eventInvitationSent, invitation.toMap());
        childUpdates.put(userInvitationReceived, invitation.toMap());
        childUpdates.put(userInvitationReceivedNotification, n.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
    public static void deleteInvitationToThisEvent(String myUid, String eventId, String otherUid) {
        //Delete Invitation Sent in myUid
        String userInvitationSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

        //Delete Invitation Sent in Event
        String eventInvitationSent =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + otherUid;

        //Delete Invitation Received in otherUid
        String userInvitationReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;

        //Delete Invitation Received MyNotification in otherUid
        String notificationId = eventId + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED;
        String userInvitationReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userInvitationSent, null);
        childUpdates.put(eventInvitationSent, null);
        childUpdates.put(userInvitationReceived, null);
        childUpdates.put(userInvitationReceivedNotification, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public static void acceptEventInvitation(final String myUid, final String eventId, final String sender) {
        //Add Assistant User to that Event
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToParticipants(myUid, true);
                    if (e.getEmpty_players() == 0) eventCompleteNotifications(true, e);
                }

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.setValue(e);

                //Add Assistant Event to my User
                String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                        + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

                //Delete Invitation Received in my User
                String userInvitationReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                        + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;

                //Delete Invitation Sent in Event
                String eventInvitationSent =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                        + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + myUid;

                //Delete Invitation Sent in other User
                String userInvitationSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + sender
                        + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

                //Delete Invitation Received MyNotification in other User
                String notificationId = eventId + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED;
                String userInvitationReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

                //Set Invitation Accept MyNotification in other User
                String notificationAcceptedId = myUid + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + eventId;
                String userInvitationAcceptedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + sender + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationAcceptedId;

                // Notification object
                long currentTime = System.currentTimeMillis();
                String notificationTitle = MyApplication.getAppContext()
                        .getString(R.string.notification_title_event_invitation_accepted);
                String notificationMessage = MyApplication.getAppContext()
                        .getString(R.string.notification_msg_event_invitation_accepted);
                @FirebaseDBContract.NotificationDataTypes
                Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
                @UtilesNotification.NotificationType
                Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_ACCEPTED;
                MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                        notificationMessage, eventId, null, type, currentTime);

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(userParticipationEvent, true);
                childUpdates.put(userInvitationReceived, null);
                childUpdates.put(userInvitationSent, null);
                childUpdates.put(eventInvitationSent, null);
                childUpdates.put(userInvitationReceivedNotification, null);
                childUpdates.put(userInvitationAcceptedNotification, n.toMap());

                database.updateChildren(childUpdates);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }
    public static void declineEventInvitation(String myUid, String eventId, String sender) {
        //Delete Invitation Received in my User
        String userInvitationReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;

        //Delete Invitation Sent in Event
        String eventInvitationSent =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + myUid;

        //Delete Invitation Sent in other User
        String userInvitationSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + sender
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

        //Delete Invitation Received MyNotification in my User
        String notificationId = eventId + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED;
        String userInvitationReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        //Set Invitation Declined MyNotification in sender User
        String notificationDeclinedId = myUid + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + eventId;
        String userInvitationDeclinedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + sender + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationDeclinedId;

        // Notification object
        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_event_invitation_declined);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_event_invitation_declined);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_INVITATION_DECLINED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, eventId, null, type, currentTime);

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userInvitationReceived, null);
        childUpdates.put(eventInvitationSent, null);
        childUpdates.put(userInvitationSent, null);
        childUpdates.put(userInvitationReceivedNotification, null);
        childUpdates.put(userInvitationDeclinedNotification, n.toMap());

        database.updateChildren(childUpdates);
    }
    public static void quitEvent(final String uid, final String eventId) {
        //Delete Assistant User to that Event (uid can be another user, not the current one)
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.child(FirebaseDBContract.DATA).getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                e.setEmpty_players(e.getEmpty_players() + 1);
                e.deleteParticipant(uid);
                // The event isn't complete because this quit
                if (e.getEmpty_players() == 1) eventCompleteNotifications(false, e);

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.child(FirebaseDBContract.DATA).setValue(e);

                //Delete Assistant Event to User
                FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                        .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).removeValue();

                // Delete invitations sent by User UID
                for (MutableData mutableInvitation : mutableData.child(FirebaseDBContract.Event.INVITATIONS).getChildren()) {
                    Invitation invitation = mutableInvitation.getValue(Invitation.class);
                    if (invitation != null) {
                        if (invitation.getSender().equals(uid)) {
                            deleteInvitationToThisEvent(invitation.getSender(), invitation.getEvent(), invitation.getReceiver());
                        }
                    }
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }

    public static void sendEventRequest(String uid, String eventId, String ownerId) {
        //Set Event Request Sent in my User
        String userRequestsEventSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        //Set User Request in that Event
        String eventRequestsUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + uid;

        //Set Event Request Received in ownerId
        // Do I really need this? The event requests are listed in NotificationsActivity
        // and Events with one of these are marked in EventsActivity.

        //Set User Request MyNotification in ownerId
        String notificationId = uid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
        String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + ownerId + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        // Notification object
        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_event_request_received);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_event_request_received);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_RECEIVED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, eventId, null, type, currentTime);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userRequestsEventSent, currentTime);
        childUpdates.put(eventRequestsUser, currentTime);
        childUpdates.put(userRequestsEventReceivedNotification, n.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
    public static void cancelEventRequest(String myUid, String eventId, String ownerId) {
        // Delete Event Request in that my User
        String userRequestsSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        // Delete User Request in that Event
        String eventRequestsUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + myUid;

        // Delete Event Request Received in ownerId
        // Do I really need this? The event requests are listed in NotificationsActivity
        // and Events with one of these are marked in EventsActivity.

        // Delete User Request MyNotification in ownerId
        String notificationId = myUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
        String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + ownerId + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userRequestsSent, null);
        childUpdates.put(eventRequestsUser, null);
        childUpdates.put(userRequestsEventReceivedNotification, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
    public static void acceptUserRequestToThisEvent(final String otherUid, final String eventId) {
        //Add Assistant User to my Event
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.child(FirebaseDBContract.DATA).getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToParticipants(otherUid, true);
                    if (e.getEmpty_players() == 0) eventCompleteNotifications(true, e);
                }

                mutableData.child(FirebaseDBContract.DATA).setValue(e);

                /* This actions must be performed in here https://stackoverflow.com/a/39608139/4235666 */

                //Add Assistant Event to that User
                String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                        + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

                // Add Assistant MyNotification in otherUid
                String notificationParticipationId = eventId + FirebaseDBContract.User.EVENTS_PARTICIPATION;
                String userParticipationNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationParticipationId;

                // Notification object
                long currentTime = System.currentTimeMillis();
                String notificationTitle = MyApplication.getAppContext()
                        .getString(R.string.notification_title_event_request_accepted);
                String notificationMessage = MyApplication.getAppContext()
                        .getString(R.string.notification_msg_event_request_accepted);
                @FirebaseDBContract.NotificationDataTypes
                Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
                @UtilesNotification.NotificationType
                Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_ACCEPTED;
                MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                        notificationMessage, eventId, null, type, currentTime);

                //Delete Event Request Sent in that User
                String userEventRequestsSentEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                        + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

                //Delete Event Request Received in my Event
                String eventUserRequestsUser  =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                        + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + otherUid;

                // Delete User Request MyNotification in ownerId
                String notificationId = otherUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
                String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + e.getOwner() + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(userParticipationEvent, true);
                childUpdates.put(userEventRequestsSentEvent, null);
                childUpdates.put(eventUserRequestsUser, null);
                childUpdates.put(userRequestsEventReceivedNotification, null);
                childUpdates.put(userParticipationNotification, n.toMap());

                database.updateChildren(childUpdates);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    public static void declineUserRequestToThisEvent(String otherUid, String eventId, String myUserID) {
        //Add Not Assistant Event to that User
        String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

        //Add Not Assistant User to my Event
        String eventParticipationUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.PARTICIPANTS + "/" + otherUid;

        // Add Assistant MyNotification in otherUid
        String notificationParticipationId = eventId + FirebaseDBContract.User.EVENTS_PARTICIPATION;
        String userParticipationNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationParticipationId;

        // Notification object
        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_event_request_declined);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_event_request_declined);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_REQUEST_DECLINED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, eventId, null, type, currentTime);

        //Delete Event Request Sent in that User
        String userRequestsSentEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        //Delete Event Request Received in my Event
        String eventRequestsReceivedUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + otherUid;

        // Delete User Request MyNotification in ownerId
        // The current user is the owner cause is the only user who can accept/decline user requests
        String notificationId = otherUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
        String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUserID + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userParticipationEvent, false);
        childUpdates.put(eventParticipationUser, false);
        childUpdates.put(userRequestsSentEvent, null);
        childUpdates.put(eventRequestsReceivedUser, null);
        childUpdates.put(userRequestsEventReceivedNotification, null);
        childUpdates.put(userParticipationNotification, n.toMap());

        database.updateChildren(childUpdates);
    }
    public static void unblockUserParticipationRejectedToThisEvent(String uid, String eventId) {
        //Delete Assistant Event to that User
        String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

        //Delete Assistant User to my Event
        String eventParticipationUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.PARTICIPANTS + "/" + uid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userParticipationEvent, null);
        childUpdates.put(eventParticipationUser, null);

        database.updateChildren(childUpdates);
    }

    private static void eventCompleteNotifications(boolean isComplete, Event event) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();

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
        MyNotification n;
        long currentTime = System.currentTimeMillis();
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
        n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, event.getEvent_id(), null, type, currentTime);

        //Set Event complete/incomplete MyNotification in participant
        String notificationId = event.getEvent_id() + FirebaseDBContract.Event.EMPTY_PLAYERS;

        Map<String, Object> childUpdates = new HashMap<>();
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

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public static void voteField(String fieldId, String sportId, final float vote) {
        //Add vote to count and recalculate average rating
        Log.e(TAG, "voteField: NOt implemented"); //TODO Implementar votar field
//        DatabaseReference fieldSportRef = FirebaseDatabase.getInstance()
//                .getReference(FirebaseDBContract.TABLE_FIELDS)
//                .child(fieldId)
//                .child(FirebaseDBContract.DATA)
//                .child(FirebaseDBContract.Field.SPORT)
//                .child(sportId);
//        fieldSportRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData mutableData) {
//                Sport s = mutableData.getValue(Sport.class); //TODO que es esto??
//                if (s == null) return Transaction.success(mutableData);
//
//                float newRating = (s.getPunctuation()*s.getVotes() + vote) / (s.getVotes()+1);
//                s.setVotes(s.getVotes() + 1);
//                s.setPunctuation((float) (Math.round(newRating * 2) / 2.0));
//
//                mutableData.setValue(s);
//
//                return Transaction.success(mutableData);
//            }
//
//            @Override
//            public void onComplete(DatabaseError databaseError, boolean b,
//                                   DataSnapshot dataSnapshot) {
//                // Transaction completed
//                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
//            }
//        });
    }

    public static void deleteAlarm(BaseFragment baseFragment, String userId, String alarmId) {
        //Delete Alarm in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(userId).child(FirebaseDBContract.User.ALARMS).child(alarmId).removeValue();
        baseFragment.resetBackStack();
    }

    public static void deleteEvent(final BaseFragment baseFragment, String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        eventRef.child(eventId).addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                    if (e == null) return;
                    e.setEvent_id(dataSnapshot.getKey());

                    ArrayList<String> participantsUserId = new ArrayList<>();
                    if (e.getParticipants() != null)
                        participantsUserId.addAll(new ArrayList<>(e.getParticipants().keySet()));

                    ArrayList<String> invitationsSentUserId = new ArrayList<>();
                    ArrayList<String> invitationsReceivedUserId = new ArrayList<>();
                    DataSnapshot dataInvitations = dataSnapshot.child(FirebaseDBContract.Event.INVITATIONS);
                    for (DataSnapshot data : dataInvitations.getChildren()) {
                        invitationsReceivedUserId.add(data.getKey());
                        invitationsSentUserId.add(data.child(FirebaseDBContract.Invitation.SENDER).getValue(String.class));
                    }

                    ArrayList<String> requestsUserId = new ArrayList<>();
                    DataSnapshot dataRequests = dataSnapshot.child(FirebaseDBContract.Event.USER_REQUESTS);
                    for (DataSnapshot data : dataRequests.getChildren()) requestsUserId.add(data.getKey());

                    DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                    Map<String, Object> childDeletes = new HashMap<>();

                    //Delete Event
                    String event =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + e.getEvent_id();
                    childDeletes.put(event, null);

                    //Delete Event in Field next events
                    String eventInFieldNextEvent = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + e.getField_id()
                            + "/" + FirebaseDBContract.Field.NEXT_EVENTS + "/" + e.getEvent_id();
                    childDeletes.put(eventInFieldNextEvent, null);

                    //Delete Event in User events created
                    String eventInUserEventsCreated = "/" + FirebaseDBContract.TABLE_USERS + "/" + e.getOwner()
                            + "/" + FirebaseDBContract.User.EVENTS_CREATED + "/" + e.getEvent_id();
                    childDeletes.put(eventInUserEventsCreated, null);

                    // Notification object
                    long currentTime = System.currentTimeMillis();
                    String notificationTitle = MyApplication.getAppContext()
                            .getString(R.string.notification_title_event_delete);
                    String notificationMessage = MyApplication.getAppContext()
                            .getString(R.string.notification_msg_event_delete,
                                    e.getName(), e.getCity(),
                                    UtilesTime.millisToDateTimeString(e.getDate()));
                    @FirebaseDBContract.NotificationDataTypes
                    Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_NONE;
                    @UtilesNotification.NotificationType
                    Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_DELETE;
                    MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                            notificationMessage, null, null, type, currentTime);

                    //Delete Event in User participation
                    for (String userParticipation : participantsUserId) {
                        String eventInUserParticipation = "/" + FirebaseDBContract.TABLE_USERS + "/" + userParticipation
                                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserParticipation, null);

                        //Set Event Deleted MyNotification in participants User
                        String notificationDeleteId = e.getEvent_id() + FirebaseDBContract.Event.OWNER;
                        String eventDeletedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                                + userParticipation + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationDeleteId;
                        childDeletes.put(eventDeletedNotification, n.toMap());
                    }

                    //Delete Event in User invitations received
                    for (String userInvitation : invitationsReceivedUserId) {
                        String eventInUserInvitationReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + userInvitation
                                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserInvitationReceived, null);
                    }

                    //Delete Event in User invitations sent
                    for (String userInvitation : invitationsSentUserId) {
                        String eventInUserInvitationSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + userInvitation
                                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserInvitationSent, null);
                    }

                    //Delete Event in User event requests send
                    for (String userRequest : requestsUserId) {
                        String eventInUserRequest = "/" + FirebaseDBContract.TABLE_USERS + "/" + userRequest
                                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserRequest, null);
                    }

                    //No need to delete notifications since is automatic when it can not found eventId
                    database.updateChildren(childDeletes);

                    baseFragment.resetBackStack();
                }
            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        });
    }

    static void checkNotification(String ref) {
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

    static void checkAlarmsForNotifications() {
        List<Alarm> alarms = UtilesContentProvider.getAllAlarmsFromContentProvider(MyApplication.getAppContext());
        if (alarms == null || alarms.size() < 1) return;

        String myUserID = Utiles.getCurrentUserId();
        if(TextUtils.isEmpty(myUserID)) return;
        final String finalMyUserID = myUserID;

        for (final Alarm a : alarms) {
            final String eventId = UtilesContentProvider.eventsCoincidenceAlarmFromContentProvider(a, myUserID);
            if (eventId != null) {
                FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                        .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS)
                        .child(a.getId() + FirebaseDBContract.User.ALARMS)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // Alarm a has some Event Coincidence and
                                    // notification doesn't exists. Notify
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
                                            notificationType, false, notificationTitle,
                                            notificationMessage, a.getId(), eventId,
                                            type, currentTime);

                                    FirebaseDatabase.getInstance().getReference().child(FirebaseDBContract.TABLE_USERS)
                                            .child(finalMyUserID).child(FirebaseDBContract.User.NOTIFICATIONS)
                                            .child(a.getId() + FirebaseDBContract.User.ALARMS)
                                            .setValue(n.toMap());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) { }
                        });

            }
        }
    }

    public static void storePhotoOnFirebase(Uri photo, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference photoRef = storage.getReferenceFromUrl(Utiles.getFirebaseStorageRootReference())
                .child(FirebaseDBContract.Storage.PROFILE_PICTURES).child(photo.getLastPathSegment());

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        // Upload file and metadata to the path
        UploadTask uploadTask = photoRef.putFile(photo, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "storePhotoOnFirebase:putFile:onFailure: ", exception);
            }
        }).addOnSuccessListener(listener);
    }

    public static void addSimulatedParticipant(final BaseFragment fragment, final String eventId, final SimulatedUser su) {
        //Add Assistant User to that Event
        final DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                String simulatedParticipantKey = eventRef
                        .child(FirebaseDBContract.Event.PARTICIPANTS).push().getKey();
                if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToSimulatedParticipants(simulatedParticipantKey, su);
                    if (e.getEmpty_players() == 0) eventCompleteNotifications(true, e);
                    if (fragment != null)
                        if (fragment instanceof SimulateParticipantFragment)
                            ((SimulateParticipantFragment) fragment).showResult(null);
                } else
                    if (fragment != null)
                        if (fragment instanceof SimulateParticipantFragment)
                            ((SimulateParticipantFragment) fragment).showResult(
                                    "There isn't empty slots for Simulated User");

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.setValue(e);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                if (b)
                    Log.d(TAG, "addSimulatedParticipant: onComplete: Transaction completed");
                else
                    Log.e(TAG, "addSimulatedParticipant: onComplete: Transaction error "+databaseError);
            }
        });
    }
    public static void deleteSimulatedParticipant(final String simulatedUid, final String eventId) {
        //Delete Assistant User to that Event (uid can be another user, not the current one)
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                e.setEmpty_players(e.getEmpty_players() + 1);
                e.deleteSimulatedParticipant(simulatedUid);
                // The event isn't complete because this quit
                if (e.getEmpty_players() == 1) eventCompleteNotifications(false, e);

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.setValue(e);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                if (b)
                    Log.d(TAG, "deleteSimulatedParticipant: onComplete: Transaction completed");
                else
                    Log.e(TAG, "deleteSimulatedParticipant: onComplete: Transaction error "+databaseError);
            }
        });
    }

}

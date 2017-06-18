package com.usal.jorgeav.sportapp.network;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.User;

import java.util.HashMap;

/**
 * Created by Jorge Avila on 18/05/2017.
 */

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
                .child(user.getmId()).setValue(user.toMap());
    }
    public static void updateSports(String myUid, HashMap<String, Float> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
            .child(myUid).child(FirebaseDBContract.User.SPORTS_PRACTICED).setValue(sportsMap);
    }

    // Add Event
    public static void addEvent(Event event) {
        DatabaseReference eventsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS);
        DatabaseReference myUserEventCreatedRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS)
                .child(event.getmOwner())
                .child(FirebaseDBContract.User.EVENTS_CREATED);
        DatabaseReference fieldsNextEventsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(event.getmField())
                .child(FirebaseDBContract.Field.NEXT_EVENTS);

        long currentTime = System.currentTimeMillis();
        event.setmId(eventsRef.push().getKey());

        eventsRef.child(event.getmId()).setValue(event.toMap());
        myUserEventCreatedRef.child(event.getmId()).setValue(currentTime);
        fieldsNextEventsRef.child(event.getmId()).setValue(currentTime);

        FirebaseData.loadEventsFromMyOwnEvents();
    }

    // Add Alarm
    public static void addAlarm(Alarm alarm, String myUserId) {
        DatabaseReference myUserAlarmsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserId)
                .child(FirebaseDBContract.User.ALARMS);

        alarm.setmId(myUserAlarmsRef.push().getKey());

        myUserAlarmsRef.child(alarm.getmId()).setValue(alarm.toMap());

        FirebaseData.loadAlarmsFromMyAlarms();
    }

    //TODO checks if childs exists
    public static void sendFriendRequest(String myUid, String otherUid) {
        long currentTime = System.currentTimeMillis();

        //Set Friend Request Sent in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT)
                .child(otherUid).setValue(currentTime);

        //Set Friend Request Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED)
                .child(myUid).setValue(currentTime);
    }
    public static void cancelFriendRequest(String myUid, String otherUid) {
        //Delete Friend Request Sent in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).child(otherUid).removeValue();

        //Delete Friend Request Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).child(myUid).removeValue();
    }
    public static void acceptFriendRequest(String myUid, String otherUid) {
        long currentTime = System.currentTimeMillis();

        //Add Friend to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS).child(otherUid).setValue(currentTime);

        //Add Friend to other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS).child(myUid).setValue(currentTime);

        //Delete Friend Request Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).child(otherUid).removeValue();

        //Delete Friend Request Sent in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).child(myUid).removeValue();
    }
    public static void declineFriendRequest(String myUid, String otherUid) {
        //Delete Friend Request Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).child(otherUid).removeValue();

        //Delete Friend Request Sent in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).child(myUid).removeValue();
    }
    public static void deleteFriend(String myUid, String otherUid) {
        //Delete Friend to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUid)
                .child(FirebaseDBContract.User.FRIENDS).child(otherUid).removeValue();

        //Delete Friend to other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(otherUid)
                .child(FirebaseDBContract.User.FRIENDS).child(myUid).removeValue();

    }

    //TODO checks if childs exists and empty player and total player counts
    //TODO COMPROBAR SI FUNCIONAN
    public static void sendInvitationToThisEvent(String eventId, String uid) {
        long currentTime = System.currentTimeMillis();

        //Set Invitation Sent in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS)
                .child(uid).setValue(currentTime);

        //Set Invitation Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS)
                .child(eventId).setValue(currentTime);
    }
    public static void deleteInvitationToThisEvent(String eventId, String uid) {
        //Delete Invitation Sent in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS)
                .child(uid).removeValue();

        //Set Invitation Received in other User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS)
                .child(eventId).removeValue();
    }
    public static void sendEventRequest(String uid, String eventId) {
        long currentTime = System.currentTimeMillis();

        //Set User Request in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS)
                .child(uid).setValue(currentTime);

        //Set Event Request in that my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS)
                .child(eventId).setValue(currentTime);
    }
    public static void cancelEventRequest(String uid, String eventId) {
        //Delete User Request in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS)
                .child(uid).removeValue();

        //Delete Event Request in that my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS)
                .child(eventId).removeValue();
    }
    public static void acceptEventInvitation(String uid, String eventId) {
        //Add Assistant Event to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).setValue(true);

        //Add Assistant User to that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).setValue(true);
        //TODO update empty Players with Transaction

        //Delete Event Invitation Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS).child(eventId).removeValue();

        //Delete Event Invitation Sent in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS).child(uid).removeValue();
    }
    public static void declineEventInvitation(String uid, String eventId) {
        //Delete Event Invitation Received in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_INVITATIONS).child(eventId).removeValue();

        //Delete Event Invitation Sent in that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.INVITATIONS).child(uid).removeValue();
    }
    public static void quitEvent(String uid, String eventId) {
        //Delete Assistant Event to my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).removeValue();

        //Delete Assistant User to that Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).removeValue();
        //TODO update empty Players with Transaction
    }
    public static void acceptUserRequestToThisEvent(String uid, String eventId) {
        //Add Assistant Event to that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).setValue(true);

        //Add Assistant User to my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).setValue(true);
        //TODO update empty Players with Transaction

        //Delete Event Request Sent in that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS).child(eventId).removeValue();

        //Delete Event Request Received in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS).child(uid).removeValue();

        FirebaseData.loadAnEvent(eventId);
    }
    public static void declineUserRequestToThisEvent(String uid, String eventId) {
        //Add Not Assistant Event to that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).setValue(false);

        //Add Not Assistant User to my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).setValue(false);
        //TODO update empty Players with Transaction

        //Delete Event Request Sent in that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_REQUESTS).child(eventId).removeValue();

        //Delete Event Request Received in my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.USER_REQUESTS).child(uid).removeValue();

        FirebaseData.loadAnEvent(eventId);
    }
    public static void unblockUserParticipationRejectedToThisEvent(String uid, String eventId) {
        //Delete Assistant Event to that User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).removeValue();

        //Delete Assistant User to my Event
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId)
                .child(FirebaseDBContract.Event.PARTICIPANTS).child(uid).removeValue();

        FirebaseData.loadAnEvent(eventId);
    }
}

package com.usal.jorgeav.sportapp.network.firebase;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                .child(event.getOwner())
                .child(FirebaseDBContract.User.EVENTS_CREATED);
        DatabaseReference fieldsNextEventsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(event.getField_id())
                .child(FirebaseDBContract.Field.NEXT_EVENTS);

        long currentTime = System.currentTimeMillis();

        if (TextUtils.isEmpty(event.getEvent_id()))
            event.setEvent_id(eventsRef.push().getKey());

        eventsRef.child(event.getEvent_id()).setValue(event.toMap());
        // Listener is attached to this reference so it doesn't need to reload
        myUserEventCreatedRef.child(event.getEvent_id()).setValue(currentTime);
        fieldsNextEventsRef.child(event.getEvent_id()).setValue(currentTime);
    }

    // Add Alarm
    public static void addAlarm(Alarm alarm, String myUserId) {
        DatabaseReference myUserAlarmsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserId)
                .child(FirebaseDBContract.User.ALARMS);

        if (TextUtils.isEmpty(alarm.getmId()))
            alarm.setmId(myUserAlarmsRef.push().getKey());

        // Listener is attached to this reference so it doesn't need to reload
        myUserAlarmsRef.child(alarm.getmId()).setValue(alarm.toMap());
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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, currentTime);
        childUpdates.put(userFriendRequestReceived, currentTime);

        database.updateChildren(childUpdates);
    }
    public static void cancelFriendRequest(String myUid, String otherUid) {
        //Delete Friend Request Sent in my User
        String userFriendRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + otherUid;

        //Delete Friend Request Received in other User
        String userFriendRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, null);
        childUpdates.put(userFriendRequestReceived, null);

        database.updateChildren(childUpdates);
    }
    public static void acceptFriendRequest(String myUid, String otherUid) {
        //Add Friend to my User
        String myUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + otherUid;

        //Add Friend to other User
        String otherUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + myUid;

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriends, currentTime);
        childUpdates.put(otherUserFriends, currentTime);
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        database.updateChildren(childUpdates);
    }
    public static void declineFriendRequest(String myUid, String otherUid) {
        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        database.updateChildren(childUpdates);
    }
    public static void deleteFriend(String myUid, String otherUid) {
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

        database.updateChildren(childUpdates);
    }

    public static void sendInvitationToThisEvent(String eventId, String uid) {
        //Set Invitation Sent in my Event
        String eventInvitationSentUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + uid;

        //Set Invitation Received in other User
        String userInvitationReceivedEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS + "/" + eventId;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventInvitationSentUser, currentTime);
        childUpdates.put(userInvitationReceivedEvent, currentTime);

        database.updateChildren(childUpdates);
    }
    public static void deleteInvitationToThisEvent(String eventId, String uid) {
        //Delete Invitation Sent in my Event
        String eventInvitationSentUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + uid;

        //Delete Invitation Received in other User
        String userInvitationReceivedEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS + "/" + eventId;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventInvitationSentUser, null);
        childUpdates.put(userInvitationReceivedEvent, null);

        database.updateChildren(childUpdates);
    }
    public static void sendEventRequest(String uid, String eventId) {
        //Set User Request in that Event
        String eventRequestsUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + uid;

        //Set Event Request in my User
        String userRequestsEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventRequestsUser, currentTime);
        childUpdates.put(userRequestsEvent, currentTime);

        database.updateChildren(childUpdates);
    }
    public static void cancelEventRequest(String uid, String eventId) {
        //Delete User Request in that Event
        String eventRequestsUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + uid;

        //Delete Event Request in that my User
        String userRequestsEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventRequestsUser, null);
        childUpdates.put(userRequestsEvent, null);

        database.updateChildren(childUpdates);
    }
    public static void acceptEventInvitation(final String uid, final String eventId) {
        //Add Assistant User to that Event
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);

                // Doesn't work, fortunately it doesn't needed
                // e.setEvent_id(mutableData.getKey());

                if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToParticipants(uid, true);
                }

                mutableData.setValue(e);

                //Add Assistant Event to my User
                String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                        + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

                //Delete Event Invitation Received in my User
                String userInvitationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                        + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS + "/" + eventId;

                //Delete Event Invitation Sent in that Event
                String eventInvitationUser =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                        + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + uid;

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(userParticipationEvent, true);
                childUpdates.put(userInvitationEvent, null);
                childUpdates.put(eventInvitationUser, null);

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
    public static void declineEventInvitation(String uid, String eventId) {
        //Delete Event Invitation Received in my User
        String userInvitationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS + "/" + eventId;

        //Delete Event Invitation Sent in that Event
        String eventInvitationUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + uid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userInvitationEvent, null);
        childUpdates.put(eventInvitationUser, null);

        database.updateChildren(childUpdates);
    }
    public static void quitEvent(final String uid, final String eventId) {
        //Delete Assistant User to that Event
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);

                // Doesn't work, fortunately it doesn't needed
                // e.setEvent_id(mutableData.getKey());

                e.setEmpty_players(e.getEmpty_players() + 1);
                e.deleteParticipant(uid);

                mutableData.setValue(e);

                //Delete Assistant Event to my User
                FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                        .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).removeValue();

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
            }
        });
    }
    public static void acceptUserRequestToThisEvent(final String uid, final String eventId) {
        //Add Assistant User to my Event
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.child(FirebaseDBContract.DATA).getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);

                // Doesn't work, fortunately it doesn't needed here, but needed below
                // e.setEvent_id(mutableData.getKey());

                if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToParticipants(uid, true);
                }

                mutableData.child(FirebaseDBContract.DATA).setValue(e);

                /* This actions must be performed in here https://stackoverflow.com/a/39608139/4235666*/

                //Add Assistant Event to that User
                String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                        + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

                //Delete Event Request Sent in that User
                String userEventRequestsEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                        + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

                //Delete Event Request Received in my Event
                String eventUserRequestsUser  =  "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                        + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + uid;

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(userParticipationEvent, true);
                childUpdates.put(userEventRequestsEvent, null);
                childUpdates.put(eventUserRequestsUser, null);

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
    public static void declineUserRequestToThisEvent(String uid, String eventId) {
        //Add Not Assistant Event to that User
        String userParticipationEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

        //Add Not Assistant User to my Event
        String eventParticipationUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.PARTICIPANTS + "/" + uid;

        //Delete Event Request Sent in that User
        String userRequestsSentEvent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        //Delete Event Request Received in my Event
        String eventRequestsReceivedUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + uid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userParticipationEvent, false);
        childUpdates.put(eventParticipationUser, false);
        childUpdates.put(userRequestsSentEvent, null);
        childUpdates.put(eventRequestsReceivedUser, null);

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

    public static void voteField(String fieldId, String sportId, final float vote) {
        //Add vote to count and recalculate average rating
        DatabaseReference fieldSportRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId)
                .child(FirebaseDBContract.Field.SPORTS)
                .child(sportId);
        fieldSportRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Sport s = mutableData.getValue(Sport.class);
                if (s == null) return Transaction.success(mutableData);

                float newRating = (s.getPunctuation()*s.getVotes() + vote) / (s.getVotes()+1);
                s.setVotes(s.getVotes() + 1);
                s.setPunctuation((float) (Math.round(newRating * 2) / 2.0));

                mutableData.setValue(s);

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

    public static void deleteAlarm(BaseFragment baseFragment, String userId, String alarmId) {
        //Delete Alarm in my User
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(userId).child(FirebaseDBContract.User.ALARMS).child(alarmId).removeValue();
        baseFragment.resetBackStack();
    }

    public static void deleteEvent(final BaseFragment baseFragment, String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS);

        eventRef.child(eventId).addListenerForSingleValueEvent(new ExecutorValueEventListener() {
            @Override
            public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                    e.setEvent_id(dataSnapshot.getKey());

                    ArrayList<String> participantsUserId = new ArrayList<String>();
                    if (e.getParticipants() != null)
                        participantsUserId.addAll(new ArrayList<String>(e.getParticipants().keySet()));

                    ArrayList<String> invitationsUserId = new ArrayList<String>();
                    DataSnapshot dataInvitations = dataSnapshot.child(FirebaseDBContract.Event.INVITATIONS);
                    for (DataSnapshot data : dataInvitations.getChildren()) invitationsUserId.add(data.getKey());

                    ArrayList<String> requestsUserId = new ArrayList<String>();
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

                    //Delete Event in User participation
                    for (String userParticipation : participantsUserId) {
                        String eventInUserParticipation = "/" + FirebaseDBContract.TABLE_USERS + "/" + userParticipation
                                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserParticipation, null);
                    }

                    //Delete Event in User invitations received
                    for (String userInvitation : invitationsUserId) {
                        String eventInUserInvitation = "/" + FirebaseDBContract.TABLE_USERS + "/" + userInvitation
                                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserInvitation, null);
                    }

                    //Delete Event in User event requests send
                    for (String userRequest : requestsUserId) {
                        String eventInUserRequest = "/" + FirebaseDBContract.TABLE_USERS + "/" + userRequest
                                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + e.getEvent_id();
                        childDeletes.put(eventInUserRequest, null);
                    }

                    database.updateChildren(childDeletes);

                    baseFragment.resetBackStack();
                }
            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        });
    }
}

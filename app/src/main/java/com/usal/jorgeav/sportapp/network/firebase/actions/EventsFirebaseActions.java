package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.usal.jorgeav.sportapp.BaseFragment;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.eventdetail.simulateparticipant.SimulateParticipantContract;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;
import com.usal.jorgeav.sportapp.utils.UtilesTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventsFirebaseActions {
    private static final String TAG = EventsFirebaseActions.class.getSimpleName();

    public static void addEvent(Event event, ArrayList<String> friendsId) {
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

        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventInEventTable, event.toMap());
        childUpdates.put(userEventCreated, currentTime);

        /* If sport need a Field */
        if (event.getField_id() != null) {
            //Set next Event in fieldId
            String fieldNextEvent = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + event.getField_id() + "/"
                    + FirebaseDBContract.Field.NEXT_EVENTS + "/" + event.getEvent_id();
            childUpdates.put(fieldNextEvent, currentTime);
        }

        //Notify friend's owner that he has created an event
        if (friendsId != null && friendsId.size() > 0) {
            // Notification object
            MyNotification n;
            String notificationTitle = MyApplication.getAppContext()
                    .getString(R.string.notification_title_event_create);
            String notificationMessage = MyApplication.getAppContext()
                    .getString(R.string.notification_msg_event_create);
            @UtilesNotification.NotificationType
            Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_CREATE;
            @FirebaseDBContract.NotificationDataTypes
            Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
            n = new MyNotification(notificationType, false, notificationTitle,
                    notificationMessage, event.getEvent_id(), null, type, currentTime);

            //Set Event creation MyNotification in friend
            String notificationId = event.getEvent_id() + FirebaseDBContract.Event.OWNER;
            for (String friendID : friendsId) {
                String eventCreateNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + friendID + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
                childUpdates.put(eventCreateNotification, n.toMap());
            }
        }

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public static void editEvent(Event event) {
        //Set Event in Event Table
        String eventInEventTable = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + event.getEvent_id()
                + "/" + FirebaseDBContract.DATA;

        //Set Event created in ownerId
        String userEventCreated = "/" + FirebaseDBContract.TABLE_USERS + "/" + event.getOwner() + "/"
                + FirebaseDBContract.User.EVENTS_CREATED + "/" + event.getEvent_id();

        long currentTime = System.currentTimeMillis();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(eventInEventTable, event.toMap());
        childUpdates.put(userEventCreated, currentTime);

        /* If sport need a Field */
        if (event.getField_id() != null) {
            //Set next Event in fieldId
            String fieldNextEvent = "/" + FirebaseDBContract.TABLE_FIELDS + "/" + event.getField_id() + "/"
                    + FirebaseDBContract.Field.NEXT_EVENTS + "/" + event.getEvent_id();
            childUpdates.put(fieldNextEvent, currentTime);
        }

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
        }

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
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
                        .child(FirebaseDBContract.Event.SIMULATED_PARTICIPANTS).push().getKey();

                // If no teams needed, empty players doesn't count
                if (!Utiles.sportNeedsTeams(e.getSport_id())) {
                    e.addToSimulatedParticipants(simulatedParticipantKey, su);
                    displayMessage(-1);
                } else if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToSimulatedParticipants(simulatedParticipantKey, su);
                    if (e.getEmpty_players() == 0)
                        NotificationsFirebaseActions.eventCompleteNotifications(true, e);
                    displayMessage(-1);
                } else if (e.getEmpty_players() == 0)
                    displayMessage(R.string.no_empty_players_for_sim_user);

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.setValue(e);
                return Transaction.success(mutableData);
            }

            private void displayMessage(int msgResource) {
                if (fragment != null && fragment instanceof SimulateParticipantContract.View)
                    ((SimulateParticipantContract.View) fragment).showMsgFromBackgroundThread(msgResource);
                else
                    Log.e(TAG, "addSimulatedParticipant: doTransaction: " +
                            "fragment not instanceof SimulateParticipantContract.View");
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.i(TAG, "addSimulatedParticipant: onComplete:" + databaseError);
            }
        });
    }

    public static void deleteSimulatedParticipant(final String simulatedUid, final String eventId) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                UserFirebaseActions.deleteOldUserPhoto(e.getSimulated_participants().get(simulatedUid).getProfile_picture());
                e.deleteSimulatedParticipant(simulatedUid);

                // If teams needed, empty players must be restored
                if (Utiles.sportNeedsTeams(e.getSport_id())) {
                    e.setEmpty_players(e.getEmpty_players() + 1);
                    // The event isn't complete because this quit
                    if (e.getEmpty_players() == 1)
                        NotificationsFirebaseActions.eventCompleteNotifications(false, e);
                }

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
                    Log.e(TAG, "deleteSimulatedParticipant: onComplete: Transaction error " + databaseError);
                EventsFirebaseSync.loadAnEvent(eventId);
            }
        });
    }

    public static void quitEvent(String uid, String eventId, boolean deleteSimulatedUsers) {
        quitEvent(uid, eventId, deleteSimulatedUsers, false);
    }
    public static void quitEvent(final String uid, final String eventId,
                                 final boolean deleteSimulatedUsers, final boolean notifyUser) {
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

                //Delete Assistant Event to User
                FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(uid)
                        .child(FirebaseDBContract.User.EVENTS_PARTICIPATION).child(eventId).removeValue();

                // Delete invitations sent by User UID
                Iterable<MutableData> mutableInvitationsList = mutableData.child(FirebaseDBContract.Event.INVITATIONS).getChildren();
                for (MutableData mutableInvitation : mutableInvitationsList) {
                    Invitation invitation = mutableInvitation.getValue(Invitation.class);
                    if (invitation != null && invitation.getSender().equals(uid))
                        InvitationFirebaseActions.deleteInvitationToThisEvent(
                                invitation.getSender(), invitation.getEvent(), invitation.getReceiver());
                }

                int usersLeaving = 0;
                // Delete simulated participants added by User UID
                if (deleteSimulatedUsers && e.getSimulated_participants() != null) {
                    Map<String, SimulatedUser> map = new HashMap<>(e.getSimulated_participants());
                    for (Map.Entry<String, SimulatedUser> entry : map.entrySet()) {
                        if (entry.getValue().getOwner().equals(uid)) {
                            usersLeaving++;
                            UserFirebaseActions.deleteOldUserPhoto(entry.getValue().getProfile_picture());
                            e.deleteSimulatedParticipant(entry.getKey());
                        }
                    }
                }

                // Delete participant
                usersLeaving++;
                e.deleteParticipant(uid);

                // If teams needed, empty players must be restored
                if (Utiles.sportNeedsTeams(e.getSport_id())) {
                    e.setEmpty_players(e.getEmpty_players() + usersLeaving);
                    // The event isn't complete because this quits
                    if (e.getEmpty_players() == usersLeaving)
                        NotificationsFirebaseActions.eventCompleteNotifications(false, e);
                }

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.child(FirebaseDBContract.DATA).setValue(e);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                if (notifyUser) {
                    // Notification object
                    MyNotification n;
                    String notificationTitle = MyApplication.getAppContext()
                            .getString(R.string.notification_title_event_expelled);
                    String notificationMessage = MyApplication.getAppContext()
                            .getString(R.string.notification_msg_event_expelled);
                    @UtilesNotification.NotificationType
                    Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_EVENT_EXPELLED;
                    @FirebaseDBContract.NotificationDataTypes
                    Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_EVENT;
                    long currentTime = System.currentTimeMillis();
                    n = new MyNotification(notificationType, false, notificationTitle,
                            notificationMessage, eventId, null, type, currentTime);

                    //Set Event MyNotification in user expelled
                    String notificationId = eventId + FirebaseDBContract.Event.PARTICIPANTS + false;
                    String eventExpelledNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                            + uid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
                    FirebaseDatabase.getInstance().getReference()
                            .child(eventExpelledNotification).setValue(n.toMap());
                }

                EventsFirebaseSync.loadAnEvent(eventId);
            }
        });
    }

    public static void deleteEvent(final BaseFragment baseFragment, String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId);

        eventRef.addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
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
                    for (DataSnapshot data : dataRequests.getChildren())
                        requestsUserId.add(data.getKey());

                    Map<String, Object> childDeletes = new HashMap<>();

                    //Delete Event
                    String event = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + e.getEvent_id();
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

                    FirebaseDatabase.getInstance().getReference().updateChildren(childDeletes);

                    if (baseFragment != null)
                        baseFragment.resetBackStack();
                }
            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        });
    }
}

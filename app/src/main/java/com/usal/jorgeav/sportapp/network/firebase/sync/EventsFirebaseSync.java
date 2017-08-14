package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventRequestFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.InvitationFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.Map;

/**
 * Created by Jorge Avila on 14/08/2017.
 */

public class EventsFirebaseSync {
    public static final String TAG = EventsFirebaseSync.class.getSimpleName();

    // Events
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
                                Log.e(FirebaseSync.TAG, "loadAnEvent: onDataChangeExecutor: Error parsing Event");
                                return;
                            }
                            e.setEvent_id(dataSnapshot.getKey());

                            //If the current user is the owner and it is a past event
                            // delete user requests and invitations
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (currentUser != null && e.getOwner().equals(currentUser.getUid())
                                    && System.currentTimeMillis() > e.getDate()) {
                                for (DataSnapshot d : dataSnapshot.child(FirebaseDBContract.Event.USER_REQUESTS).getChildren())
                                    EventRequestFirebaseActions.cancelEventRequest(d.getKey(), e.getEvent_id(), e.getOwner());
                                for (DataSnapshot d : dataSnapshot.child(FirebaseDBContract.Event.INVITATIONS).getChildren()) {
                                    Invitation invitation = d.getValue(Invitation.class);
                                    if (invitation != null)
                                        InvitationFirebaseActions.deleteInvitationToThisEvent(invitation.getSender(), e.getEvent_id(), invitation.getReceiver());
                                }
                            }

                            ContentValues cv = UtilesContentValues.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            UsersFirebaseSync.loadAProfile(null, e.getOwner(), false);
                            FieldsFirebaseSync.loadAField(e.getField_id());

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

    public static void loadAnEventAndNotify(final String notificationRef, final MyNotification notification) {
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
                                Log.e(FirebaseSync.TAG, "loadAnEventAndNotify: onDataChangeExecutor: Error parsing Event");
                                return;
                            }
                            e.setEvent_id(dataSnapshot.getKey());

                            ContentValues cv = UtilesContentValues.eventToContentValues(e);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                            UsersFirebaseSync.loadAProfile(null, e.getOwner(), false);
                            FieldsFirebaseSync.loadAField(e.getField_id());

                            // Load users participants with data
                            loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());

                            // Load simulated users participants with data
                            loadSimulatedParticipants(e.getEvent_id(), e.getSimulated_participants());

                            //Notify
                            UtilesNotification.createNotification(MyApplication.getAppContext(), notification, e);
                            NotificationsFirebaseActions.checkNotification(notificationRef);
                        } else {
                            Log.e(FirebaseSync.TAG, "loadAnEventAndNotify: onDataChangeExecutor: Event "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadUsersFromParticipants(String eventId, Map<String, Boolean> participants) {
        MyApplication.getAppContext().getContentResolver()
                .delete(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ? ",
                        new String[]{eventId});

        if (participants != null)
            for (Map.Entry<String, Boolean> entry : participants.entrySet()) {
                UsersFirebaseSync.loadAProfile(null, entry.getKey(), false);

                ContentValues cv = new ContentValues();
                cv.put(SportteamContract.EventsParticipationEntry.USER_ID, entry.getKey());
                cv.put(SportteamContract.EventsParticipationEntry.EVENT_ID, eventId);
                cv.put(SportteamContract.EventsParticipationEntry.PARTICIPATES, entry.getValue() ? 1 : 0);
                MyApplication.getAppContext().getContentResolver()
                        .insert(SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI, cv);
            }
    }

    public static void loadSimulatedParticipants(String eventId, Map<String, SimulatedUser> simulatedParticipants) {
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
                                    Log.e(FirebaseSync.TAG, "loadEventsFromCity: onDataChangeExecutor: Error parsing Event");
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
                                    UsersFirebaseSync.loadAProfile(null, e.getOwner(), false);
                                    FieldsFirebaseSync.loadAField(e.getField_id());

                                    // Load users participants with data
                                    loadUsersFromParticipants(e.getEvent_id(), e.getParticipants());

                                    // Load simulated users participants with data
                                    loadSimulatedParticipants(e.getEvent_id(), e.getSimulated_participants());
                                }
                            }
                            NotificationsFirebaseActions.checkAlarmsForNotifications();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static ExecutorChildEventListener getListenerToLoadEventsFromMyOwnEvents() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    // Load user requests received with data
                    FirebaseSync.loadUsersFromUserRequests(dataSnapshot.getKey());
                    // Load users invited with data
                    FirebaseSync.loadUsersFromInvitationsSent(dataSnapshot.getKey());
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
    }

    public static ExecutorChildEventListener getListenerToLoadEventsFromEventsParticipation() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAnEvent(dataSnapshot.getKey());

                    // Load users invited with data if I participate
                    Boolean participation = dataSnapshot.getValue(Boolean.class);
                    if (participation != null && participation)
                        FirebaseSync.loadUsersFromInvitationsSent(dataSnapshot.getKey());

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
    }

    public static ExecutorChildEventListener getListenerToLoadEventsFromInvitationsReceived() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Invitation invitation = dataSnapshot.getValue(Invitation.class);
                    if (invitation == null) {
                        Log.e(FirebaseSync.TAG, "loadEventsFromInvitationsReceived: onChildAddedExecutor: parse Invitation null");
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
    }

    public static ExecutorChildEventListener getListenerToLoadEventsFromEventsRequests() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
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
    }
}

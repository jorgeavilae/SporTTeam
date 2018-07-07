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
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.eventdetail.userrequests.UsersRequestsContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

public class EventRequestFirebaseActions {
    private static final String TAG = EventRequestFirebaseActions.class.getSimpleName();

    public static void sendEventRequest(String uid, String eventId, String ownerId) {
        //Set Event Request Sent in my User
        String userRequestsEventSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        //Set User Request in that Event
        String eventRequestsUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + uid;

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
        String eventRequestsUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + myUid;

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

    public static void acceptUserRequestToThisEvent(final BaseFragment fragment, final String otherUid, final String eventId) {
        //Add Assistant User to my Event
        DatabaseReference eventRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.DATA);
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Event e = mutableData.getValue(Event.class);
                if (e == null) return Transaction.success(mutableData);
                e.setEvent_id(eventId);

                // If no teams needed, empty players doesn't count
                if (!Utiles.sportNeedsTeams(e.getSport_id())) {
                    e.addToParticipants(otherUid, true);
                } else if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToParticipants(otherUid, true);
                    if (e.getEmpty_players() == 0)
                        NotificationsFirebaseActions.eventCompleteNotifications(true, e);
                } else if (e.getEmpty_players() == 0) {
                    displayMessage(R.string.no_empty_players_for_user);
                    //Ignore retry and abort transaction
                    return Transaction.abort();
                }

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.setValue(e);

                /* This actions must be performed in here https://stackoverflow.com/a/39608139/4235666 */

                //Add Assistant Event to that User
                String userParticipationEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
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
                String userEventRequestsSentEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                        + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

                //Delete Event Request Received in my Event
                String eventUserRequestsUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                        + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + otherUid;

                // Delete User Request MyNotification in ownerId
                String notificationId = otherUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
                String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + e.getOwner() + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(userParticipationEvent, true);
                childUpdates.put(userEventRequestsSentEvent, null);
                childUpdates.put(eventUserRequestsUser, null);
                childUpdates.put(userRequestsEventReceivedNotification, null);
                childUpdates.put(userParticipationNotification, n.toMap());

                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

                return Transaction.success(mutableData);
            }

            private void displayMessage(int msgResource) {
                if (fragment != null && fragment instanceof UsersRequestsContract.View)
                    ((UsersRequestsContract.View) fragment).showMsgFromBackgroundThread(msgResource);
                else
                    Log.e(TAG, "acceptUserRequestToThisEvent: doTransaction: " +
                            "fragment not instanceof UsersRequestsContract.View");
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot dataSnapshot) {

                if (databaseError == null && !committed) { // Transaction aborted
                    Map<String, Object> childUpdates = new HashMap<>();

                    //Delete Event Request Sent in that User
                    String userEventRequestsSentEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                            + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;
                    childUpdates.put(userEventRequestsSentEvent, null);

                    //Delete Event Request Received in my Event
                    String eventUserRequestsUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                            + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + otherUid;
                    childUpdates.put(eventUserRequestsUser, null);

                    // Delete User Request MyNotification in ownerId
                    Event e = dataSnapshot.getValue(Event.class);
                    if (e != null) {
                        String notificationId = otherUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
                        String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                                + e.getOwner() + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
                        childUpdates.put(userRequestsEventReceivedNotification, null);
                    }

                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                }

                Log.i(TAG, "acceptUserRequestToThisEvent: onComplete:" + databaseError);
            }
        });
    }

    public static void declineUserRequestToThisEvent(String otherUid, String eventId, String myUserID) {
        //Add Not Assistant Event to that User
        String userParticipationEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

        //Add Not Assistant User to my Event
        String eventParticipationUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.PARTICIPANTS + "/" + otherUid;

        // Add Not Assistant MyNotification in otherUid
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
        String userRequestsSentEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        //Delete Event Request Received in my Event
        String eventRequestsReceivedUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + otherUid;

        // Delete User Request MyNotification in ownerId
        // The current user is the owner cause is the only user who can accept/decline user requests
        String notificationId = otherUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
        String userRequestsEventReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUserID + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userParticipationEvent, false);
        childUpdates.put(eventParticipationUser, false);
        childUpdates.put(userRequestsSentEvent, null);
        childUpdates.put(eventRequestsReceivedUser, null);
        childUpdates.put(userRequestsEventReceivedNotification, null);
        childUpdates.put(userParticipationNotification, n.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    public static void unblockUserParticipationRejectedToThisEvent(String uid, String eventId) {
        //Delete Assistant Event to that User
        String userParticipationEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + uid
                + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

        //Delete Assistant User to my Event
        String eventParticipationUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.PARTICIPANTS + "/" + uid;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userParticipationEvent, null);
        childUpdates.put(eventParticipationUser, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
}

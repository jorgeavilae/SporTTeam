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
import com.usal.jorgeav.sportapp.eventdetail.DetailEventContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a las invitaciones a
 * partidos que puede recibir o enviar el usuario actual.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class InvitationFirebaseActions {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = InvitationFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar la invitación en la base de datos del servidor. Obtiene las
     * referencias a las ramas del usuario emisor, receptor y del partido al que refiere para
     * insertar la invitación en las tres. Además, crea e inserta una notificación en el usuario
     * receptor para avisarle de que tiene una invitación nueva. Todas las inserciones se llevan a
     * cabo de forma atómica {@link DatabaseReference#updateChildren(Map)}.
     *
     * @param myUid    identificador del usuario que envía la invitación, el usuario actual
     * @param eventId  identificador del partido al que va referida la invitación
     * @param otherUid identificador del usuario que recibe la invitación
     */
    public static void sendInvitationToThisEvent(String myUid, String eventId, String otherUid) {
        //Set Invitation Sent in myUid
        String userInvitationSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

        //Set Invitation Sent in Event
        String eventInvitationSent = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + otherUid;

        //Set Invitation Received in otherUid
        String userInvitationReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
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

    /**
     * Invocado para borrar una invitación enviada en la base de datos del servidor. Obtiene las
     * referencias a las ramas del usuario emisor, receptor y del partido al que refiere para
     * borrar la invitación en las tres. Además, borra la notificación en el usuario receptor.
     * Todas las eliminaciones se llevan a cabo de forma atómica
     * {@link DatabaseReference#updateChildren(Map)}.
     *
     * @param myUid    identificador del usuario que envía la invitación, el usuario actual
     * @param eventId  identificador del partido al que va referida la invitación
     * @param otherUid identificador del usuario que recibe la invitación
     */
    public static void deleteInvitationToThisEvent(String myUid, String eventId, String otherUid) {
        //Delete Invitation Sent in myUid
        String userInvitationSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

        //Delete Invitation Sent in Event
        String eventInvitationSent = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + otherUid;

        //Delete Invitation Received in otherUid
        String userInvitationReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
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

    /**
     * Invocado para aceptar una invitación de la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama del partido al que refiere para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre el partido. Con ella, se
     * consultan los datos del partido, se modifican y se reinsertan en la base de datos de forma
     * atómica. Esto es necesario para aumentar el número de participantes, que sumará uno debido
     * a la aceptación de esta invitación.
     * <p>
     * Además de sumar la participación y añadir al participante, inserta el partido en dicho rama
     * del participante nuevo. Inserta también, una notificación para avisar al emisor de la
     * invitación de que fue aceptada. Por último, borra la invitación, ya respondida, de la base
     * de datos del servidor.
     * <p>
     * En caso de que el partido esté lleno, se aborta la transacción y se borra la invitación de la
     * base de datos del servidor.
     *
     * @param fragment referencial al Fragmento que invoca este método para los casos en los que
     *                 sea necesario mostrar un mensaje de error en la interfaz.
     * @param myUid    identificador del usuario que recibe la invitación, el usuario actual
     * @param eventId  identificador del partido al que va referida la invitación
     * @param sender   identificador del usuario que envía la invitación
     */
    public static void acceptEventInvitation(final BaseFragment fragment, final String myUid,
                                             final String eventId, final String sender) {
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

                // If no teams needed, empty players doesn't count
                if (!Utiles.sportNeedsTeams(e.getSport_id())) {
                    e.addToParticipants(myUid, true);
                } else if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToParticipants(myUid, true);
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

                //Add Assistant Event to my User
                String userParticipationEvent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                        + "/" + FirebaseDBContract.User.EVENTS_PARTICIPATION + "/" + eventId;

                //Add Invitation Accept MyNotification in other User
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

                //Delete Invitation Received in my User
                String userInvitationReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                        + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;

                //Delete Invitation Sent in Event
                String eventInvitationSent = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                        + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + myUid;

                //Delete Invitation Sent in other User
                String userInvitationSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + sender
                        + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;

                //Delete Invitation Received MyNotification in other User
                String notificationId = eventId + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED;
                String userInvitationReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                        + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(userParticipationEvent, true);
                childUpdates.put(userInvitationReceived, null);
                childUpdates.put(userInvitationSent, null);
                childUpdates.put(eventInvitationSent, null);
                childUpdates.put(userInvitationReceivedNotification, null);
                childUpdates.put(userInvitationAcceptedNotification, n.toMap());

                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

                return Transaction.success(mutableData);
            }

            @SuppressWarnings("SameParameterValue")
            private void displayMessage(int msgResource) {
                if (fragment instanceof DetailEventContract.View)
                    ((DetailEventContract.View) fragment).showMsgFromBackgroundThread(msgResource);
                else
                    Log.e(TAG, "acceptEventInvitation: doTransaction: " +
                            "fragment not instanceof DetailEventContract.View");
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean committed,
                                   DataSnapshot dataSnapshot) {

                if (databaseError == null && !committed) { // Transaction aborted
                    Map<String, Object> childUpdates = new HashMap<>();

                    //Delete Invitation Received in my User
                    String userInvitationReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                            + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;
                    childUpdates.put(userInvitationReceived, null);

                    //Delete Invitation Sent in Event
                    String eventInvitationSent = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                            + "/" + FirebaseDBContract.Event.INVITATIONS + "/" + myUid;
                    childUpdates.put(eventInvitationSent, null);

                    //Delete Invitation Sent in other User
                    String userInvitationSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + sender
                            + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_SENT + "/" + eventId;
                    childUpdates.put(userInvitationSent, null);

                    //Delete Invitation Received MyNotification in other User
                    String notificationId = eventId + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED;
                    String userInvitationReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                            + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;
                    childUpdates.put(userInvitationReceivedNotification, null);

                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
                }

                Log.i(TAG, "acceptUserRequestToThisEvent: onComplete:" + databaseError);
            }
        });
    }

    /**
     * Invocado para rechazar una invitación de la base de datos del servidor. Borra la invitación,
     * ya respondida, de la base de datos del servidor. También inserta una notificación para
     * avisar al emisor de la invitación de que fue rechazada.
     *
     * @param myUid   identificador del usuario que recibe la invitación, el usuario actual
     * @param eventId identificador del partido al que va referida la invitación
     * @param sender  identificador del usuario que envía la invitación
     */
    public static void declineEventInvitation(String myUid, String eventId, String sender) {
        //Delete Invitation Received in my User
        String userInvitationReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED + "/" + eventId;

        //Delete Invitation Sent in Event
        String eventInvitationSent = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
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

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userInvitationReceived, null);
        childUpdates.put(eventInvitationSent, null);
        childUpdates.put(userInvitationSent, null);
        childUpdates.put(userInvitationReceivedNotification, null);
        childUpdates.put(userInvitationDeclinedNotification, n.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
}

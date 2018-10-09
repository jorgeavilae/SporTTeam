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

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a las peticiones de
 * participación del usuario actual.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class EventRequestFirebaseActions {
    /**
     * Nombre de la clase
     */
    private static final String TAG = EventRequestFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar una petición de participación en la base de datos del servidor.
     * Obtiene las referencias a las ramas del usuario emisor y del partido al que refiere para
     * insertar la petición en ambas. Además, crea e inserta una notificación en el usuario
     * creador del partido para avisarle de que tiene una petición de participación que necesita
     * respuesta. Todas las inserciones se llevan a cabo de forma atómica
     * {@link DatabaseReference#updateChildren(Map)}.
     *
     * @param myUid   identificador del usuario que envía la petición, el usuario actual
     * @param eventId identificador del partido al que va referida la petición
     * @param ownerId identificador del usuario que recibe la petición, el creador del partido
     */
    public static void sendEventRequest(String myUid, String eventId, String ownerId) {
        //Set Event Request Sent in my User
        String userRequestsEventSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.EVENTS_REQUESTS + "/" + eventId;

        //Set User Request in that Event
        String eventRequestsUser = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + eventId
                + "/" + FirebaseDBContract.Event.USER_REQUESTS + "/" + myUid;

        //Set User Request MyNotification in ownerId
        String notificationId = myUid + FirebaseDBContract.User.EVENTS_REQUESTS + eventId;
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

    /**
     * Invocado para borrar una petición de participación de la base de datos del servidor. Obtiene
     * las referencias a las ramas del usuario emisor y del partido al que refiere para borrar la
     * petición en ambas. Además, borra la notificación en el usuario creador del partido. Todas las
     * eliminaciones se llevan a cabo de forma atómica {@link DatabaseReference#updateChildren(Map)}.
     *
     * @param myUid   identificador del usuario que envía la petición, el usuario actual
     * @param eventId identificador del partido al que va referida la petición
     * @param ownerId identificador del usuario que recibe la petición, el creador del partido
     */
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

    /**
     * Invocado para aceptar una petición de participación de la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama del partido al que refiere para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre el partido. Con ella, se
     * consultan los datos del partido, se modifican y se reinsertan en la base de datos de forma
     * atómica. Esto es necesario para aumentar el número de participantes, que sumará uno debido
     * a la aceptación de esta petición.
     * <p>
     * Además de sumar la participación y añadir al participante, inserta el partido en dicho rama
     * del participante nuevo. Inserta también, una notificación para avisar al emisor de la
     * petición de que fue aceptada y es participante en un nuevo partido. Por último, borra la
     * petición, ya respondida, de la base de datos del servidor.
     * <p>
     * En caso de que el partido esté lleno, se aborta la transacción y se borra la petición de la
     * base de datos del servidor.
     *
     * @param fragment referencial al Fragmento que invoca este método para los casos en los que
     *                 sea necesario mostrar un mensaje de error en la interfaz.
     * @param otherUid identificador del usuario que envió la petición y va a ser aceptado
     * @param eventId  identificador del partido al que va referida la petición
     */
    public static void acceptUserRequestToThisEvent(final BaseFragment fragment,
                                                    final String otherUid, final String eventId) {
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

            @SuppressWarnings("SameParameterValue")
            private void displayMessage(int msgResource) {
                if (fragment instanceof UsersRequestsContract.View)
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

    /**
     * Invocado para rechazar una petición de participación de la base de datos del servidor.
     * Añade al emisor como participante bloqueado y no podrá enviar más peticiones. También
     * inserta el partido en la correspondiente rama del participante bloqueado nuevo. Borra la
     * petición, ya respondida, de la base de datos del servidor. Por último, inserta una
     * notificación para avisar al emisor de la invitación de que fue rechazada.
     *
     * @param otherUid identificador del usuario que envía la petición
     * @param eventId  identificador del partido al que va referida la petición
     * @param myUserID identificador del usuario que reciba la petición, el usuario actual
     */
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

    /**
     * Invocado para desbloquear a un participante cuya petición de participación fue rechazada.
     * Borra al participante bloqueado de la lista del partido y borra el partido de la lista de
     * participaciones del usuario. En este caso no se envía ninguna notificación.
     *
     * @param uid     identificador del usuario que se va a desbloquear
     * @param eventId identificador del partido en el que el usuario esta bloqueado
     */
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

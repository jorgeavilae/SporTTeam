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

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a los partidos.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class EventsFirebaseActions {
    /**
     * Nombre de la clase
     */
    private static final String TAG = EventsFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar el partido especificada en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece el partido
     * con {@link Event#toMap()}
     * <p>
     * Además, crea e inserta (obteniendo las referencias correspondientes) notificaciones en los
     * amigos del usuario creador del partido para avisarles de que uno de sus amigos creó un
     * partido nuevo.
     *
     * @param event     objeto {@link Event} listo para ser añadido
     * @param friendsId lista de identificadores de los usuarios amigos del usuario creador
     */
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
            childUpdates.put(fieldNextEvent, event.getDate());
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

    /**
     * Invocado para actualizar el partido especificada en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece el partido
     * con {@link Event#toMap()}
     * <p>
     * Además, crea e inserta (obteniendo las referencias correspondientes) notificaciones en los
     * participantes del partido para avisarles de que fue editado.
     *
     * @param event objeto {@link Event} listo para ser añadido
     */
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
            childUpdates.put(fieldNextEvent, event.getDate());
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

    /**
     * Invocado para añadir un usuario simulado al partido de la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama del partido para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre él. Con esa transacción,
     * se consultan los datos del partido, se modifican y se reinsertan en la base de datos de forma
     * atómica. Esto es necesario para aumentar el número de participantes, que sumará uno debido
     * al nuevo usuario simulado. Además, inserta los datos del usuario simulado en el partido.
     * <p>
     * En caso de que el partido esté lleno, muestra un mensaje de error en la interfaz a través
     * de la referencia al Fragmento.
     *
     * @param fragment referencial al Fragmento que invoca este método para los casos en los que
     *                 sea necesario mostrar un mensaje de error en la interfaz.
     * @param eventId  identificador del partido al que se añade el usuario simulado
     * @param su       objeto {@link SimulatedUser} que se pretende insertar
     */
    public static void addSimulatedParticipant(final BaseFragment fragment, final String eventId,
                                               final SimulatedUser su) {
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
                } else if (e.getEmpty_players() > 0) {
                    e.setEmpty_players(e.getEmpty_players() - 1);
                    e.addToSimulatedParticipants(simulatedParticipantKey, su);
                    if (e.getEmpty_players() == 0)
                        NotificationsFirebaseActions.eventCompleteNotifications(true, e);
                } else if (e.getEmpty_players() == 0)
                    displayMessage(R.string.no_empty_players_for_sim_user);

                // Set ID to null to not store ID under data in Event's tree in Firebase.
                e.setEvent_id(null);
                mutableData.setValue(e);
                return Transaction.success(mutableData);
            }

            @SuppressWarnings("SameParameterValue")
            private void displayMessage(int msgResource) {
                if (fragment instanceof SimulateParticipantContract.View)
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

    /**
     * Invocado para eliminar un usuario simulado del partido de la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama del partido para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre él. Con esa transacción,
     * se consultan los datos del partido, se modifican y se reinsertan en la base de datos de forma
     * atómica. Esto es necesario para disminuir el número de participantes, que restará uno debido
     * a la eliminación del usuario simulado. Además, borra los datos del usuario simulado.
     * <p>
     * En caso de que el partido ya no esté lleno, inserta notificaciones en los participantes con
     * {@link NotificationsFirebaseActions#eventCompleteNotifications(boolean, Event)}
     *
     * @param eventId      identificador del partido del que se borra el usuario simulado
     * @param simulatedUid identificador del usuario simulado que se pretende eliminar
     */
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

    /**
     * Invoca {@link #quitEvent(String, String, boolean, boolean)} de tal forma que no notifique
     * al usuario.
     *
     * @param uid                  identificador de usuario que se va a expulsar
     * @param eventId              identificador del partido del que se va a expulsar
     * @param deleteSimulatedUsers true si se deben borrar también los usuario simulados creado por
     *                             el usuario que se va a expulsar
     */
    public static void quitEvent(String uid, String eventId, boolean deleteSimulatedUsers) {
        quitEvent(uid, eventId, deleteSimulatedUsers, false);
    }

    /**
     * Invocado para eliminar un usuario participante del partido de la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama del partido para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre él. Con esa transacción,
     * se consultan los datos del partido, se modifican y se reinsertan en la base de datos de forma
     * atómica. Esto es necesario para borrar el participante, borrar quizás a los usuarios
     * simulados creados por él, borrar las invitaciones enviadas por él y añadir tantos puestos
     * vacantes al partido como participantes se hayan borrado.
     * <p>
     * En caso de que el partido ya no esté lleno, inserta notificaciones en los participantes con
     * {@link NotificationsFirebaseActions#eventCompleteNotifications(boolean, Event)}. También
     * notifica al usuario expulsado si <var>notifyUser</var> es true.
     *
     * @param uid                  identificador de usuario que se va a expulsar
     * @param eventId              identificador del partido del que se va a expulsar
     * @param deleteSimulatedUsers true si se deben borrar también los usuario simulados creado por
     *                             el usuario que se va a expulsar
     * @param notifyUser           true si debe notificarse la expulsión al usuario que se ha
     *                             expulsado, false en caso contrario
     */
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

    /**
     * Invocado para borrar el partido de la base de datos del servidor. Obtiene las referencias
     * a los usuarios participantes, invitados, que han enviado peticiones de participación, etc;
     * para poder eliminar este partido de sus respectivas ramas de datos.
     * <p>
     * Por último, inserta notificaciones en los participantes para avisarles de que el partido se
     * canceló.
     *
     * @param baseFragment referencia al Fragmento para avisar cuando acabe la ejecución
     * @param eventId      identificador del partido que se va a eliminar
     */
    public static void deleteEvent(final BaseFragment baseFragment, String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference eventRef = database.getReference(FirebaseDBContract.TABLE_EVENTS).child(eventId);

        eventRef.addListenerForSingleValueEvent(
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

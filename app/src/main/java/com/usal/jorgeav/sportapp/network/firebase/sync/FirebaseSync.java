package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase se utiliza para sincronizar todas las ramas necesarias de Firebase Realtime Database.
 * Se encarga de establecer sobre estos datos los Listeners necesarios para almacenar dichos datos
 * en el Proveedor de Contenido cada vez que cambien.
 * <p>
 * Estos métodos obtienen los Listeners del resto de clases del paquete y los establecen sobre la
 * rama adecuada del árbol JSON de la base de datos del servidor. Además, para no establecer
 * múltiples Listeners sobre la misma rama, mantiene una referencia a todos ellos en una variable
 * estática {@link HashMap}. Cada par de esta variables estará formado con el
 * {@link ExecutorChildEventListener} como valor y la referencia a la rama del árbol JSON en la que
 * está escuchando como referencia.
 * <p>
 * Estos Listeners se vinculan al principio de la ejecución de la aplicación y se desvinculan al
 * finalizarla.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 * @see <a href= "https://stackoverflow.com/questions/33776195/how-to-keep-track-of-listeners-in-firebase-on-android">
 * How to keep track of listeners in Firebase on Android?</a>
 */
public class FirebaseSync {
    /**
     * Nombre de la clase
     */
    private static final String TAG = FirebaseSync.class.getSimpleName();

    /**
     * Mapa de Listeners para mantener una referencia a ellos con el objetivo de no duplicarlos
     * bajo la misma rama del servidor y para poder desvincularlos al finalizar la ejecución.
     */
    private static HashMap<DatabaseReference, ChildEventListener> listenerMap = new HashMap<>();

    /**
     * Método invocado para establecer todos los Listeners necesarios para mantener todos los
     * datos actualizados: los datos del usuario actual, los partidos con los que mantiene relación,
     * alarmas, amigos, etcétera. Es invocado al inicio de la aplicación o al iniciar sesión.
     * <p>
     * Se le pasa una referencia a la {@link LoginActivity} para que, en el caso de ser invocado al
     * iniciar sesión, pueda avisar a dicha Actividad cuando los datos del usuario actual estén en
     * el Proveedor de Contenido para que pueda mostrarlos en la interfaz.
     *
     * @param loginActivity referencia a la Actividad de inicio de sesión en caso de que este método
     *                      haya sido invocado desde ella, o null.
     */
    public static void syncFirebaseDatabase(LoginActivity loginActivity) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null && listenerMap.isEmpty()) {
            String myUserID = Utiles.getCurrentUserId();
            if (TextUtils.isEmpty(myUserID)) return;

            // Add current device token if needed
            UserFirebaseActions.updateUserToken(myUserID, FirebaseInstanceId.getInstance().getToken());
            // Load current user profile and sports
            UsersFirebaseSync.loadAProfile(loginActivity, myUserID, true);

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

    /**
     * Método invocado para desvincular y borrar todos los Listeners utilizados para mantener los
     * datos actualizados, limpiando {@link #listenerMap}. Es invocado al finalizar la aplicación o
     * al cerrar sesión.
     */
    public static void detachListeners() {
        for (Map.Entry<DatabaseReference, ChildEventListener> entry : listenerMap.entrySet()) {
            Log.i(TAG, "detachListeners: ref " + entry.getKey());
            entry.getKey().removeEventListener(entry.getValue());
        }
        listenerMap.clear();
    }

    /**
     * Usa {@link UsersFirebaseSync#getListenerToLoadUsersFromFriends()} para obtener y almacenar en
     * {@link #listenerMap} el Listener que manejará los datos de la lista de amigos del usuario
     * actual.
     */
    public static void loadUsersFromFriends() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.FRIENDS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriends();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link UsersFirebaseSync#getListenerToLoadUsersFromFriendsRequestsSent()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * peticiones de amistad enviadas por el usuario actual.
     */
    public static void loadUsersFromFriendsRequestsSent() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriendsRequestsSent();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link UsersFirebaseSync#getListenerToLoadUsersFromFriendsRequestsReceived()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * peticiones de amistad recibidas por el usuario actual.
     */
    public static void loadUsersFromFriendsRequestsReceived() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromFriendsRequestsReceived();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link EventsFirebaseSync#getListenerToLoadEventsFromMyOwnEvents()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * partidos creados por el usuario actual.
     */
    public static void loadEventsFromMyOwnEvents() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_CREATED);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromMyOwnEvents();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link EventsFirebaseSync#getListenerToLoadEventsFromEventsParticipation()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * partidos en los que participa el usuario actual.
     */
    public static void loadEventsFromEventsParticipation() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_PARTICIPATION);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromEventsParticipation();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link UsersFirebaseSync#getListenerToLoadUsersFromInvitationsSent()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * usuarios que han recibido una invitación para un partido del usuario actual.
     *
     * @param eventId identificador del partido al que referencia la invitación
     */
    public static void loadUsersFromInvitationsSent(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.Event.INVITATIONS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromInvitationsSent();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link EventsFirebaseSync#getListenerToLoadEventsFromInvitationsReceived()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * partidos para los que el usuario actual ha recibido una invitación.
     */
    public static void loadEventsFromInvitationsReceived() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromInvitationsReceived();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link UsersFirebaseSync#getListenerToLoadUsersFromUserRequests()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * los usuarios que han mandado una petición de participación a algún partido del usuario actual.
     *
     * @param eventId identificador del partido para el que es la petición de participación
     */
    public static void loadUsersFromUserRequests(String eventId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_EVENTS)
                .child(eventId).child(FirebaseDBContract.Event.USER_REQUESTS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = UsersFirebaseSync.getListenerToLoadUsersFromUserRequests();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link EventsFirebaseSync#getListenerToLoadEventsFromEventsRequests()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * partidos para los que el usuario actual ha mandado una petición de participación.
     */
    public static void loadEventsFromEventsRequests() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.EVENTS_REQUESTS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = EventsFirebaseSync.getListenerToLoadEventsFromEventsRequests();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link AlarmsFirebaseSync#getListenerToLoadAlarmsFromMyAlarms()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * alarmas del usuario actual.
     */
    public static void loadAlarmsFromMyAlarms() {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS);

        if (!listenerMap.containsKey(ref)) {
            ExecutorChildEventListener childEventListener = AlarmsFirebaseSync.getListenerToLoadAlarmsFromMyAlarms();
            ref.addChildEventListener(childEventListener);
            listenerMap.put(ref, childEventListener);
            Log.i(TAG, "attachListener ref " + ref);
        }
    }

    /**
     * Usa {@link FieldsFirebaseSync#getListenerToLoadFieldsFromCity()} para obtener
     * y almacenar en {@link #listenerMap} el Listener que manejará los datos de la lista de
     * instalaciones de la ciudad del usuario actual.
     * <p>
     * Primero, si lo indica el parámetro <var>shouldResetFieldsData</var>, borra el Listener
     * anterior y las instalaciones de la ciudad antigua del Proveedor de Contenido. Esto ocurre si
     * es la primera vez que se invoca este método o bien porque la ciudad del usuario actual acaba
     * de ser actualizada.
     *
     * @param city                  ciudad del usuario actual
     * @param shouldResetFieldsData true si se debe actualizar la lista de instalaciones, false en
     *                              otro caso
     */
    public static void loadFieldsFromCity(String city, boolean shouldResetFieldsData) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference fieldsRef = database.getReference(FirebaseDBContract.TABLE_FIELDS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Field.CITY;

        // Should reset Fields table because it's first load or there was a city change.
        if (shouldResetFieldsData) {
            // Remove listener from DatabaseReference and from listenerMap
            if (listenerMap.get(fieldsRef) != null) {
                fieldsRef.removeEventListener(listenerMap.get(fieldsRef));
                listenerMap.remove(fieldsRef);
            }

            // Remove Fields and FieldsSport from tables
            MyApplication.getAppContext().getContentResolver()
                    .delete(SportteamContract.FieldEntry.CONTENT_FIELD_URI, null, null);
            MyApplication.getAppContext().getContentResolver()
                    .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI, null, null);
        }

        if (!listenerMap.containsKey(fieldsRef)) {
            ExecutorChildEventListener childEventListener = FieldsFirebaseSync.getListenerToLoadFieldsFromCity();
            fieldsRef.orderByChild(filter).equalTo(city).addChildEventListener(childEventListener);
            listenerMap.put(fieldsRef, childEventListener);
            Log.i(TAG, "attachListener ref " + fieldsRef);
        }
    }

    /**
     * Realiza una única consulta sobre la rama de notificaciones del usuario actual. El Listener
     * establecido puede ser pasado por parámetros o puede vincularse uno por defecto. El Listener
     * por defecto comprueba la lista de notificaciones, comprueba que los datos a los que hace
     * referencia se encuentren en el Proveedor de Contenido y si hay alguna sin comprobar la muestra
     * mediante {@link UtilesNotification}.
     * <p>
     * Este método se invoca desde {@link #syncFirebaseDatabase(LoginActivity)}, de esta forma las
     * notificaciones son comprobadas al iniciar sesión.
     * <p>
     * Este Listener no es necesario guardarlo sobre {@link #listenerMap} porque sólo se vincula
     * una vez ya que las notificaciones son comprobadas desde el servidor mediante Firebase Cloud
     * Functions y Firebase Cloud Messaging.
     *
     * @param listener Listener que se vincula a la rama de notificaciones para una única consulta,
     *                 o null.
     * @see <a href= "https://firebase.google.com/docs/functions/">Firebase Cloud Functions</a>
     * @see <a href= "https://firebase.google.com/docs/cloud-messaging/">Firebase Cloud Messaging</a>
     */
    public static void loadMyNotifications(ValueEventListener listener) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.NOTIFICATIONS);

        if (listener == null)
            listener = new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                @Override
                public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            MyNotification notification = data.getValue(MyNotification.class);
                            if (notification == null) continue;

                            @FirebaseDBContract.NotificationDataTypes int type = notification.getData_type();
                            switch (type) {
                                case FirebaseDBContract.NOTIFICATION_TYPE_NONE:
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_USER:
                                    User user = UtilesContentProvider.getUserFromContentProvider(notification.getExtra_data_one());
                                    if (user == null) {
                                        UsersFirebaseSync.loadAProfileAndNotify(data.getRef().toString(), notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, user);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_EVENT:
                                    Event event = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_one());
                                    if (event == null) {
                                        EventsFirebaseSync.loadAnEventAndNotify(data.getRef().toString(), notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, event);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ALARM:
                                    Alarm alarm = UtilesContentProvider.getAlarmFromContentProvider(notification.getExtra_data_one());
                                    Event eventCoincidence = UtilesContentProvider.getEventFromContentProvider(notification.getExtra_data_two());
                                    if (alarm == null || eventCoincidence == null) {
                                        AlarmsFirebaseSync.loadAnAlarmAndNotify(data.getRef().toString(), notification);
                                        continue;
                                    }
                                    UtilesNotification.createNotification(MyApplication.getAppContext(), notification, alarm);
                                    break;
                                case FirebaseDBContract.NOTIFICATION_TYPE_ERROR:
                                    break;
                            }
                            NotificationsFirebaseActions.checkNotification(data.getRef().toString());
                        }
                }

                @Override
                public void onCancelledExecutor(DatabaseError databaseError) {

                }
            };
        ref.orderByChild(FirebaseDBContract.Notification.DATE).limitToLast(20)
                .addListenerForSingleValueEvent(listener);
    }


}

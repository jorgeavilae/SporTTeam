package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import java.util.List;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para sincronizar los datos de
 * Firebase Realtime Database con el Proveedor de Contenido. Concretamente, los datos relativos a
 * usuarios.
 * <p>
 * Proporciona tanto métodos para sincronizar datos en una sola consulta, como métodos para obtener
 * los Listeners que se vincularán a los datos que necesiten una escucha continuada, en
 * {@link FirebaseSync#syncFirebaseDatabase(LoginActivity)}
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class UsersFirebaseSync {
    /**
     * Nombre de la clase
     */
    private static final String TAG = UsersFirebaseSync.class.getSimpleName();

    /**
     * Sincroniza los datos de un usuario, incluidos sus deportes practicados. Si el usuario es el
     * usuario actual, quizás necesite actualizar los valores de ciudad y coordenadas en
     * {@link android.content.SharedPreferences}.
     * <p>
     * Esta carga del usuario actual es necesaria al iniciar sesión para tener, al menos, los datos
     * del usuario en el Proveedor de Contenido y poder mostrarlos en la interfaz. Como la
     * consulta es asíncrona, se pasa una referencia a {@link LoginActivity} para avisar a
     * esta Actividad cuando la sincronización de los datos del usuario finaliza.
     *
     * @param loginActivity         referencia a la Actividad {@link LoginActivity} si este método
     *                              fue invocado desde ella, o null.
     * @param userID                identificador del usuario que se debe sincronizar
     * @param shouldUpdateCityPrefs true si se deben actualizar los valores de la ciudad de
     *                              {@link android.content.SharedPreferences}
     */
    public static void loadAProfile(final LoginActivity loginActivity,
                                    @NonNull String userID,
                                    final boolean shouldUpdateCityPrefs) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS).child(userID);

        ref.addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (anUser == null) {
                                Log.e(TAG, "loadAProfile: onDataChangeExecutor: Error parsing user");
                                return;
                            }
                            anUser.setUid(dataSnapshot.getKey());

                            String myUserID = Utiles.getCurrentUserId();
                            if (!TextUtils.isEmpty(myUserID) && myUserID.equals(anUser.getUid()))
                                // anUser is the current User so check email address in case
                                // it was recently changed and later cancel that change,
                                // in such case FirebaseAuth.user.email != FirebaseDatabase.user.email
                                Utiles.checkEmailFromDatabaseIsCorrect(
                                        FirebaseAuth.getInstance().getCurrentUser(), anUser);

                            // Store user in ContentProvider
                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            // Delete sports in case some of them was deleted
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getUid()});

                            // Store sports in ContentProvider
                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[0]));

                            // This is the current user and it is first load or city was change
                            if (shouldUpdateCityPrefs) {
                                UtilesPreferences.setCurrentUserCity(MyApplication.getAppContext());
                                UtilesPreferences.setCurrentUserCityCoords(MyApplication.getAppContext());

                                // Load fields from user city
                                FirebaseSync.loadFieldsFromCity(UtilesPreferences.getCurrentUserCity(
                                        MyApplication.getAppContext()), true);
                                // Load events from user city
                                EventsFirebaseSync.loadEventsFromCity(
                                        UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext()));

                                // If loginActivity isn't null means this came from that Activity so,
                                // ends that Activity and start BaseActivity with the User data already
                                // stored in ContentProvider.
                                if (loginActivity != null) loginActivity.finishLoadMyProfile();
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Sincroniza los datos de un usuario cuyo identificador ha venido insertado en una notificación
     * desde Firebase Cloud Messaging. Para mostrar los datos de dicho usuario al pulsar la
     * notificación, primero deben estar en el Proveedor de Contenido, por lo que se sincronizan.
     * A continuación, se invoca
     * {@link UtilesNotification#createNotification(Context, MyNotification, User)}
     *
     * @param notificationRef identificador de la notificación
     * @param notification    objeto notificación del que extraer el identificador de usuario
     */
    public static void loadAProfileAndNotify(final String notificationRef,
                                             final MyNotification notification) {
        if (notification.getExtra_data_one() == null) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(notification.getExtra_data_one());

        ref.addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (anUser == null) {
                                Log.e(TAG, "loadAProfileAndNotify: onDataChangeExecutor: Error parsing user");
                                return;
                            }
                            anUser.setUid(dataSnapshot.getKey());

                            // Store user in ContentProvider
                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            // Delete sports in case some of them was deleted
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getUid()});

                            // Store sports in ContentProvider
                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[0]));

                            //Notify
                            UtilesNotification.createNotification(
                                    MyApplication.getAppContext(), notification, anUser);
                            NotificationsFirebaseActions.checkNotification(notificationRef);
                        } else {
                            Log.e(TAG, "loadAProfileAndNotify: onDataChangeExecutor: User "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Sincroniza los datos de los usuarios de una ciudad dada.
     *
     * @param city ciudad
     */
    public static void loadUsersFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.CITY;

        ref.orderByChild(filter).equalTo(city).limitToFirst(40).addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = data.child(FirebaseDBContract.DATA).getValue(User.class);
                                if (anUser == null) {
                                    Log.e(TAG, "loadUsersFromCity: onDataChangeExecutor: Error parsing user");
                                    continue;
                                }
                                anUser.setUid(data.getKey());

                                // Store user in ContentProvider
                                ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                // Delete sports in case some of them was deleted
                                MyApplication.getAppContext().getContentResolver()
                                        .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                                new String[]{anUser.getUid()});

                                // Store sports in ContentProvider
                                List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[0]));
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Sincroniza los datos de los usuarios cuyo nombre coincida con uno dado.
     *
     * @param username nombre a buscar
     */
    public static void loadUsersWithName(String username) {
        if (username != null && !TextUtils.isEmpty(username) && username.length() > 3) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
            String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;

            /* https://stackoverflow.com/a/40633692/4235666
             * https://firebase.google.com/docs/database/admin/retrieve-data */
            usersRef.orderByChild(filter).startAt(username).endAt(username + "\uf8ff").limitToFirst(40)
                    .addListenerForSingleValueEvent(
                            new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                                @Override
                                public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            User anUser = data.child(FirebaseDBContract.DATA).getValue(User.class);
                                            if (anUser == null) {
                                                Log.e(TAG, "loadUsersWithName: onDataChangeExecutor: Error parsing user");
                                                continue;
                                            }
                                            anUser.setUid(data.getKey());

                                            // Store user in ContentProvider
                                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                                            MyApplication.getAppContext().getContentResolver()
                                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                            // Delete sports in case some of them was deleted
                                            MyApplication.getAppContext().getContentResolver()
                                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                                            new String[]{anUser.getUid()});

                                            // Store sports in ContentProvider
                                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                                            MyApplication.getAppContext().getContentResolver()
                                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                            cvSports.toArray(new ContentValues[0]));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelledExecutor(DatabaseError databaseError) {

                                }
                            });
        }
    }

    /**
     * Crea un Listener para vincularlo sobre la lista de amigos del usuario actual y sincronizarlos
     * con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadUsersFromFriends()
     */
    static ExecutorChildEventListener getListenerToLoadUsersFromFriends() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);

                    String myUserID = Utiles.getCurrentUserId();
                    if (TextUtils.isEmpty(myUserID)) return;

                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String myUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUserID)) return;

                String userId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND "
                                + SportteamContract.FriendsEntry.USER_ID + " = ? ",
                        new String[]{myUserID, userId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Crea un Listener para vincularlo sobre la lista de peticiones de amistad enviadas por el
     * usuario actual y sincronizarlas con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadUsersFromFriendsRequestsSent()
     */
    static ExecutorChildEventListener getListenerToLoadUsersFromFriendsRequestsSent() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);

                    String myUserID = Utiles.getCurrentUserId();
                    if (TextUtils.isEmpty(myUserID)) return;

                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, true);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String senderId = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(senderId)) return;

                String receiverId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.SENDER_ID + " = ? ",
                        new String[]{receiverId, senderId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Crea un Listener para vincularlo sobre la lista de peticiones de amistad recibidas por el
     * usuario actual y sincronizarlas con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadUsersFromFriendsRequestsReceived()
     */
    static ExecutorChildEventListener getListenerToLoadUsersFromFriendsRequestsReceived() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);

                    String myUserID = Utiles.getCurrentUserId();
                    if (TextUtils.isEmpty(myUserID)) return;

                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendRequestToContentValues(dataSnapshot, myUserID, false);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String receiverId = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(receiverId)) return;

                String senderId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                                SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ? AND "
                                        + SportteamContract.FriendRequestEntry.SENDER_ID + " = ? ",
                                new String[]{receiverId, senderId});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Crea un Listener para vincularlo sobre la lista de invitaciones enviadas por el usuario
     * actual y sincronizarlas con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadUsersFromInvitationsSent(String)
     */
    static ExecutorChildEventListener getListenerToLoadUsersFromInvitationsSent() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Invitation invitation = dataSnapshot.getValue(Invitation.class);
                    if (invitation == null) {
                        Log.e(TAG, "loadUsersFromInvitationsSent: onChildAddedExecutor: parse Invitation null");
                        return;
                    }

                    // Load receiver. Necessary cause it could be a sender's friend not mine.
                    loadAProfile(null, invitation.getReceiver(), false);

                    // Load sender. It could be the current user. It could be other user, in such
                    // case that user would be load in loadParticipants or loadOwner.

                    // Load event. Not needed cause this invitation is for one of the current
                    // user's events or participation ones.

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
                String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                String userId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventsInvitationEntry.RECEIVER_ID + " = ? ",
                        new String[]{eventId, userId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Crea un Listener para vincularlo sobre la lista de peticiones de participación recibidas por
     * el usuario actual y sincronizarlas con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadUsersFromUserRequests(String)
     */
    static ExecutorChildEventListener getListenerToLoadUsersFromUserRequests() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);

                    String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                    ContentValues cvData = UtilesContentValues
                            .dataSnapshotEventsRequestsToContentValues(dataSnapshot, eventId, false);
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
                String eventId = Uri.parse(dataSnapshot.getRef().getParent().getParent().toString()).getLastPathSegment();
                String senderId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ? AND "
                                + SportteamContract.EventRequestsEntry.SENDER_ID + " = ? ",
                        new String[]{eventId, senderId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Realiza una única consulta a la base de datos de Firebase buscando un email concreto
     * de usuario y aplica el {@link ValueEventListener} proporcionado.
     * Utilizado para comprobar la existencia de ese email en la base de datos.
     *
     * @param email              email buscado
     * @param valueEventListener listener con las acciones a realizar al recibir los resultados
     *                           de la consulta
     */
    public static void queryUserEmail(String email, ValueEventListener valueEventListener) {
        String userEmailPath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.EMAIL;
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .orderByChild(userEmailPath).equalTo(email)
                .addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * Realiza una única consulta a la base de datos de Firebase buscando un nombre concreto
     * de usuario y aplica el {@link ValueEventListener} proporcionado.
     * Utilizado para comprobar la existencia de ese nombre en la base de datos.
     *
     * @param name               nombre buscado
     * @param valueEventListener listener con las acciones a realizar al recibir los resultados
     *                           de la consulta
     */
    public static void queryUserName(String name, ValueEventListener valueEventListener) {
        String userNamePath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .orderByChild(userNamePath).equalTo(name)
                .addListenerForSingleValueEvent(valueEventListener);
    }
}

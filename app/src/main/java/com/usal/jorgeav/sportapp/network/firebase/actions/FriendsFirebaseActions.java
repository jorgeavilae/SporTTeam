package com.usal.jorgeav.sportapp.network.firebase.actions;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

import java.util.HashMap;
import java.util.Map;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a las peticiones de amistad
 * y amigos del usuario actual.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class FriendsFirebaseActions {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = FriendsFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar una petición de amistad en la base de datos del servidor. Obtiene las
     * referencias a las ramas del usuario emisor y receptor para insertar la petición en las dos.
     * Además, crea e inserta una notificación en el usuario receptor para avisarle de que tiene
     * una petición de amistad nueva. Todas las inserciones se llevan a
     * cabo de forma atómica {@link DatabaseReference#updateChildren(Map)}.
     *
     * @param myUid    identificador del usuario que envía la petición, el usuario actual
     * @param otherUid identificador del usuario que recibe la petición
     */
    public static void sendFriendRequest(String myUid, String otherUid) {
        //Set Friend Request Sent in my User
        String userFriendRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + otherUid;

        //Set Friend Request Received in other User
        String userFriendRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + myUid;

        //Set Friend Request Received MyNotification in other User
        String notificationId = myUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        //Notification object
        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_friend_request_received);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_friend_request_received);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_USER;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_RECEIVED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, myUid, null, type, currentTime);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, currentTime);
        childUpdates.put(userFriendRequestReceived, currentTime);
        childUpdates.put(userFriendRequestReceivedNotification, n.toMap());

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    /**
     * Invocado para borrar una petición de amistad de la base de datos del servidor. Obtiene las
     * referencias a las ramas del usuario emisor y receptor para insertar la petición en las dos.
     * Además, borra la notificación en el usuario receptor. Todas las eliminaciones se llevan a
     * cabo de forma atómica {@link DatabaseReference#updateChildren(Map)}.
     *
     * @param myUid    identificador del usuario que envía la petición, el usuario actual
     * @param otherUid identificador del usuario que recibe la petición
     */
    public static void cancelFriendRequest(String myUid, String otherUid) {
        //Delete Friend Request Sent in my User
        String userFriendRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + otherUid;

        //Delete Friend Request Received in other User
        String userFriendRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid + "/"
                + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + myUid;

        //Delete Friend Request Received MyNotification in other User
        String notificationId = myUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, null);
        childUpdates.put(userFriendRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    /**
     * Invocado para aceptar una petición de amistad de la base de datos del servidor. Establece
     * mutuamente a cada usuario en la rama de amigos del otro. Además, inserta una notificación
     * para avisar al emisor de la petición de que fue aceptada y tiene un nuevo amigo. Por último,
     * borra la petición, ya respondida, de la base de datos del servidor.
     *
     * @param myUid    identificador del usuario que recibe la petición, el usuario actual
     * @param otherUid identificador del usuario que envía la petición
     */
    public static void acceptFriendRequest(String myUid, String otherUid) {
        //Add Friend to my User
        String myUserFriends = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + otherUid;

        //Add Friend to other User
        String otherUserFriends = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + myUid;

        //Set Friend new MyNotification in other User
        String notificationFriendId = myUid + FirebaseDBContract.User.FRIENDS;
        String userFriendNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendId;

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Received MyNotification in my User
        String notificationFriendRequestId = otherUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendRequestId;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        long currentTime = System.currentTimeMillis();
        String notificationTitle = MyApplication.getAppContext()
                .getString(R.string.notification_title_friend_request_accepted);
        String notificationMessage = MyApplication.getAppContext()
                .getString(R.string.notification_msg_friend_request_accepted);
        @FirebaseDBContract.NotificationDataTypes
        Long type = (long) FirebaseDBContract.NOTIFICATION_TYPE_USER;
        @UtilesNotification.NotificationType
        Long notificationType = (long) UtilesNotification.NOTIFICATION_ID_FRIEND_REQUEST_ACCEPTED;
        MyNotification n = new MyNotification(notificationType, false, notificationTitle,
                notificationMessage, myUid, null, type, currentTime);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriends, currentTime);
        childUpdates.put(otherUserFriends, currentTime);
        childUpdates.put(userFriendNotification, n.toMap());
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    /**
     * Invocado para rechazar una petición de amistad de la base de datos del servidor. Borra la
     * petición, ya respondida, de la base de datos del servidor. En este caso, no notifica al
     * emisor de que su petición de amistad fue rechazada.
     *
     * @param myUid    identificador del usuario que recibe la petición, el usuario actual
     * @param otherUid identificador del usuario que envía la petición
     */
    public static void declineFriendRequest(String myUid, String otherUid) {
        //No need to notify otherUid that myUid decline his friend request

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Received MyNotification in my User
        String notificationId = otherUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }

    /**
     * Invocado para borrar una amistad entre dos usuarios de la base de datos del servidor. Borra
     * a cada usuario de la lista de amigos del otro en cada rama correspondiente.
     *
     * @param myUid    identificador del usuario que recibe la petición, el usuario actual
     * @param otherUid identificador del usuario que envía la petición
     */
    public static void deleteFriend(String myUid, String otherUid) {
        //Delete Friend new MyNotification in other User
        String notificationFriendId = myUid + FirebaseDBContract.User.FRIENDS;
        String userFriendNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendId;

        //Delete Friend to my User
        String myUserFriends = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + otherUid;

        //Delete Friend to other User
        String otherUserFriends = "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + myUid;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriends, null);
        childUpdates.put(otherUserFriends, null);
        childUpdates.put(userFriendNotification, null);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);
    }
}

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
 * Created by Jorge Avila on 14/08/2017.
 */

public class FriendsFirebaseActions {
    public static final String TAG = FriendsFirebaseActions.class.getSimpleName();

    // Friend
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

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userFriendRequestSent, null);
        childUpdates.put(userFriendRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);

        database.updateChildren(childUpdates);
    }

    public static void acceptFriendRequest(String myUid, String otherUid) {
        //Add Friend to my User
        String myUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + otherUid;

        //Add Friend to other User
        String otherUserFriends =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS + "/" + myUid;

        //Set Friend new MyNotification in other User
        String notificationFriendId = myUid + FirebaseDBContract.User.FRIENDS;
        String userFriendNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendId;

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Received MyNotification in my User
        String notificationFriendRequestId = otherUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendRequestId;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
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

        database.updateChildren(childUpdates);
    }

    public static void declineFriendRequest(String myUid, String otherUid) {
        //No need to notify otherUid that myUid decline his friend request

        //Delete Friend Request Received in my User
        String myUserFriendsRequestReceived =  "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED + "/" + otherUid;

        //Delete Friend Request Received MyNotification in my User
        String notificationId = otherUid + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT;
        String userFriendRequestReceivedNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + myUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationId;

        //Delete Friend Request Sent in other User
        String otherUserFriendsRequestSent =  "/" + FirebaseDBContract.TABLE_USERS + "/" + otherUid
                + "/" + FirebaseDBContract.User.FRIENDS_REQUESTS_SENT + "/" + myUid;

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(myUserFriendsRequestReceived, null);
        childUpdates.put(userFriendRequestReceivedNotification, null);
        childUpdates.put(otherUserFriendsRequestSent, null);

        database.updateChildren(childUpdates);
    }

    public static void deleteFriend(String myUid, String otherUid) {
        //Delete Friend new MyNotification in other User
        String notificationFriendId = myUid + FirebaseDBContract.User.FRIENDS;
        String userFriendNotification = "/" + FirebaseDBContract.TABLE_USERS + "/"
                + otherUid + "/" + FirebaseDBContract.User.NOTIFICATIONS + "/" + notificationFriendId;

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
        childUpdates.put(userFriendNotification, null);

        database.updateChildren(childUpdates);
    }
}

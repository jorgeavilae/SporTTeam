package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.net.Uri;
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
 * Created by Jorge Avila on 14/08/2017.
 */

public class UsersFirebaseSync {
    public static final String TAG = UsersFirebaseSync.class.getSimpleName();

    // User
    public static void loadAProfile(final LoginActivity loginActivity, @NonNull String userID, final boolean shouldUpdateCityPrefs) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        myUserRef.child(userID)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (anUser == null) {
                                Log.e(FirebaseSync.TAG, "loadAProfile: onDataChangeExecutor: Error parsing user");
                                return;
                            }
                            anUser.setUid(dataSnapshot.getKey());

                            FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                            String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
                            if (!TextUtils.isEmpty(myUserID) && myUserID.equals(anUser.getUid()))
                                // anUser is the current User so check email address in case
                                // it was recently changed and later cancel that change.
                                Utiles.checkEmailFromDatabaseIsCorrect(fUser, anUser);

                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getUid()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));

                            if (shouldUpdateCityPrefs) {
                                UtilesPreferences.setCurrentUserCity(MyApplication.getAppContext());
                                UtilesPreferences.setCurrentUserCityCoords(MyApplication.getAppContext());

                                // Load fields from user city
                                FirebaseSync.loadFieldsFromCity(UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext()), true);
                                // Load events from user city
                                EventsFirebaseSync.loadEventsFromCity(UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext()));

                                // Updates in prefs came from new login in so if the mLoginActivity
                                // isn't null, ends that Activity and start BaseActivity with the
                                // User data in ContentProvider.
                                if (loginActivity != null)
                                    loginActivity.finishLoadMyProfile();
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadAProfileAndNotify(final String notificationRef, final MyNotification notification) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS);

        if (notification.getExtra_data_one() != null)
        myUserRef.child(notification.getExtra_data_one())
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User anUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                            if (anUser == null) {
                                Log.e(FirebaseSync.TAG, "loadAProfileAndNotify: onDataChangeExecutor: Error parsing user");
                                return;
                            }
                            anUser.setUid(dataSnapshot.getKey());

                            ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                            List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            SportteamContract.UserSportEntry.USER_ID + " = ? ",
                                            new String[]{anUser.getUid()});
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                            cvSports.toArray(new ContentValues[cvSports.size()]));

                            //Notify
                            UtilesNotification.createNotification(MyApplication.getAppContext(), notification, anUser);
                            NotificationsFirebaseActions.checkNotification(notificationRef);
                        } else {
                            Log.e(FirebaseSync.TAG, "loadAProfileAndNotify: onDataChangeExecutor: User "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadUsersFromCity(String city) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.Event.CITY;

        usersRef.orderByChild(filter).equalTo(city)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = data.child(FirebaseDBContract.DATA).getValue(User.class);
                                if (anUser == null) {
                                    Log.e(FirebaseSync.TAG, "loadUsersFromCity: onDataChangeExecutor: Error parsing user");
                                    return;
                                }
                                anUser.setUid(data.getKey());

                                ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[cvSports.size()]));
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadUsersWithName(String username) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference(FirebaseDBContract.TABLE_USERS);
        String filter = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;

        /* https://stackoverflow.com/a/40633692/4235666
         * https://firebase.google.com/docs/database/admin/retrieve-data */
        usersRef.orderByChild(filter).startAt(username).endAt(username+"\uf8ff").limitToFirst(40)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                User anUser = data.child(FirebaseDBContract.DATA).getValue(User.class);
                                if (anUser == null) {
                                    Log.e(FirebaseSync.TAG, "loadUsersWithName: onDataChangeExecutor: Error parsing user");
                                    return;
                                }
                                anUser.setUid(data.getKey());

                                ContentValues cvData = UtilesContentValues.dataUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cvData);

                                List<ContentValues> cvSports = UtilesContentValues.sportUserToContentValues(anUser);
                                MyApplication.getAppContext().getContentResolver()
                                        .bulkInsert(SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                                                cvSports.toArray(new ContentValues[cvSports.size()]));
                            }
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static ExecutorChildEventListener getListenerToLoadUsersFromFriends() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);
                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = "";
                    if (fUser != null) myUserID = fUser.getUid();
                    ContentValues cvData = UtilesContentValues.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                loadAProfile(null, dataSnapshot.getKey(), false);
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = "";
                if (fUser != null) myUserID = fUser.getUid();
                ContentValues cvData = UtilesContentValues.dataSnapshotFriendToContentValues(dataSnapshot, myUserID);
                MyApplication.getAppContext().getContentResolver()
                        .insert(SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI, cvData);

            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUserID = "";
                if (fUser != null) myUserID = fUser.getUid();
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

    public static ExecutorChildEventListener getListenerToLoadUsersFromFriendsRequestsSent() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
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
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String senderId = ""; if (fUser != null) senderId = fUser.getUid();
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

    public static ExecutorChildEventListener getListenerToLoadUsersFromFriendsRequestsReceived() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    loadAProfile(null, dataSnapshot.getKey(), false);

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
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
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String receiverId = ""; if (fUser != null) receiverId = fUser.getUid();
                String senderId = dataSnapshot.getKey();
                Log.d(FirebaseSync.TAG, "onChildRemoved: sender "+senderId);
                Log.d(FirebaseSync.TAG, "onChildRemoved: receiver "+receiverId);
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

    public static ExecutorChildEventListener getListenerToLoadUsersFromInvitationsSent() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
                    Invitation invitation = dataSnapshot.getValue(Invitation.class);
                    if (invitation == null) {
                        Log.e(FirebaseSync.TAG, "loadUsersFromInvitationsSent: onChildAddedExecutor: parse Invitation null");
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

    public static ExecutorChildEventListener getListenerToLoadUsersFromUserRequests() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.exists()) {
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
                                +SportteamContract.EventRequestsEntry.SENDER_ID + " = ? ",
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
}

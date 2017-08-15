package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.preferences.SettingsFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.HashMap;
import java.util.Map;

public class UserFirebaseActions {
    private static final String TAG = UserFirebaseActions.class.getSimpleName();

    public static Query getUserEmailReferenceEqualTo(String email) {
        String userEmailPath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.EMAIL;
        return FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .orderByChild(userEmailPath).equalTo(email);
    }

    public static Query getUserNameReferenceEqualTo(String name) {
        String userNamePath = FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.ALIAS;
        return FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .orderByChild(userNamePath).equalTo(name);
    }

    public static void addUser(User user) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(user.getUid()).setValue(user.toMap());
    }

    public static void updateSports(String myUid, HashMap<String, Float> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.SPORTS_PRACTICED).setValue(sportsMap);
    }

    public static void updateUserName(String myUid, String name) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.ALIAS).setValue(name);
    }

    public static void updateUserAge(String myUid, Integer age) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.AGE).setValue(age);
    }

    public static void updateUserPhoto(String myUid, String photo) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.PROFILE_PICTURE).setValue(photo);
    }

    public static void updateUserCityAndReload(final String myUid, String citySelectedName, LatLng citySelectedCoord) {
        //Set City
        String city = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.CITY;

        //Set Latitude
        String latitude = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.COORD_LATITUDE;

        //Set Longitude
        String longitude = "/" + FirebaseDBContract.TABLE_USERS + "/" + myUid + "/"
                + FirebaseDBContract.DATA + "/" + FirebaseDBContract.User.COORD_LONGITUDE;

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(city, citySelectedName);
        childUpdates.put(latitude, citySelectedCoord.latitude);
        childUpdates.put(longitude, citySelectedCoord.longitude);

        FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    // Passing true makes update sharedPreferences and
                    // perform loadEventsFromCity and loadFieldsFromCity
                    UsersFirebaseSync.loadAProfile(null, myUid, true);
            }
        });
    }

    public static void updateUserEmail(String myUid, String email) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUid).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.User.EMAIL).setValue(email);
    }

    public static void deleteCurrentUser(String myUserId, final SettingsFragment settingsFragment, final boolean deleteUser) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS).child(myUserId);

        userRef.addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User myUser = dataSnapshot.child(FirebaseDBContract.DATA).getValue(User.class);
                    if (myUser == null) return;
                    myUser.setUid(dataSnapshot.getKey());

                    //Delete User in Friends
                    for (DataSnapshot friendUid : dataSnapshot.child(FirebaseDBContract.User.FRIENDS).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: deleteFriend " + friendUid.getKey());
                        FriendsFirebaseActions.deleteFriend(myUser.getUid(), friendUid.getKey());
                    }

                    //Delete User in Friends who Received a friendRequest
                    for (DataSnapshot friendRequestSent : dataSnapshot.child(FirebaseDBContract.User.FRIENDS_REQUESTS_SENT).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: cancelFriendRequest " + friendRequestSent.getKey());
                        FriendsFirebaseActions.cancelFriendRequest(myUser.getUid(), friendRequestSent.getKey());
                    }

                    //Delete User in Friends who Send me a friendRequest
                    for (DataSnapshot friendRequestReceived : dataSnapshot.child(FirebaseDBContract.User.FRIENDS_REQUESTS_RECEIVED).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: declineFriendRequest " + friendRequestReceived.getKey());
                        FriendsFirebaseActions.declineFriendRequest(myUser.getUid(), friendRequestReceived.getKey());
                    }

                    //Delete Events from User events created
                    for (DataSnapshot eventCreated : dataSnapshot.child(FirebaseDBContract.User.EVENTS_CREATED).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: deleteEvent " + eventCreated.getKey());
                        EventsFirebaseActions.deleteEvent(null, eventCreated.getKey());
                    }

                    //Delete participant from User events participation
                    for (DataSnapshot eventParticipation : dataSnapshot.child(FirebaseDBContract.User.EVENTS_PARTICIPATION).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: quitEvent " + eventParticipation.getKey());
                        EventsFirebaseActions.quitEvent(myUser.getUid(), eventParticipation.getKey(), true);
                    }

                    //Delete Invitation Sent from User event invitations received
                    for (DataSnapshot eventInvitationReceived : dataSnapshot.child(FirebaseDBContract.User.EVENTS_INVITATIONS_RECEIVED).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: declineEventInvitation " + eventInvitationReceived.getKey());
                        String sender = eventInvitationReceived.child(FirebaseDBContract.Invitation.SENDER).getValue(String.class);
                        InvitationFirebaseActions.declineEventInvitation(myUser.getUid(), eventInvitationReceived.getKey(), sender);
                    }

                    //Delete User in Events with a userRequest from me
                    for (DataSnapshot eventUserRequestsSent : dataSnapshot.child(FirebaseDBContract.User.EVENTS_REQUESTS).getChildren()) {
                        Log.i(TAG, "deleteCurrentUser: onDataChangeExecutor: cancelEventRequest " + eventUserRequestsSent.getKey());
                        Event e = UtilesContentProvider.getEventFromContentProvider(eventUserRequestsSent.getKey());
                        if (e != null)
                            EventRequestFirebaseActions.cancelEventRequest(myUser.getUid(), eventUserRequestsSent.getKey(), e.getOwner());
                    }

                    //Delete all notifications
                    NotificationsFirebaseActions.deleteAllNotifications(myUser.getUid());

                    //Delete all alarms
                    AlarmFirebaseActions.deleteAllAlarms(myUser.getUid());

                    //Delete User
                    if (settingsFragment != null)
                        settingsFragment.userDataDeleted(myUser.getUid(), deleteUser);
                }
            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        });
    }

    public static void storePhotoOnFirebase(Uri photo, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference photoRef = storage.getReferenceFromUrl(Utiles.getFirebaseStorageRootReference())
                .child(FirebaseDBContract.Storage.PROFILE_PICTURES).child(photo.getLastPathSegment());

        // Create the file metadata
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();

        // Upload file and metadata to the path
        UploadTask uploadTask = photoRef.putFile(photo, metadata);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.e(TAG, "storePhotoOnFirebase:putFile:onFailure: ", exception);
            }
        }).addOnSuccessListener(listener);
    }
}

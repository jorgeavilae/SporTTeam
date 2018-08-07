package com.usal.jorgeav.sportapp.profile;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.network.firebase.actions.FriendsFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.UsersFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

class ProfilePresenter implements ProfileContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = ProfilePresenter.class.getSimpleName();

    ProfileContract.View mUserView;
    private ContentObserver mContentObserver;

    ProfilePresenter(ProfileContract.View userView) {
        mUserView = userView;
        mContentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                getRelationTypeBetweenThisUserAndI();
            }
        };
    }

    @Override
    public void openUser(LoaderManager loaderManager, Bundle b) {
        String userId = b.getString(ProfileFragment.BUNDLE_INSTANCE_UID);
        if (userId != null) UsersFirebaseSync.loadAProfile(null, userId, false);
        FirebaseSync.loadUsersFromFriendsRequestsSent();
        loaderManager.initLoader(SportteamLoader.LOADER_PROFILE_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_PROFILE_SPORTS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String userId = args.getString(ProfileFragment.BUNDLE_INSTANCE_UID);
        switch (id) {
            case SportteamLoader.LOADER_PROFILE_ID:
                return SportteamLoader
                        .cursorLoaderOneUser(mUserView.getActivityContext(), userId);
            case SportteamLoader.LOADER_PROFILE_SPORTS_ID:
                return SportteamLoader
                        .cursorLoaderSportsUser(mUserView.getActivityContext(), userId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_PROFILE_ID:
                showUser(data);
                break;
            case SportteamLoader.LOADER_PROFILE_SPORTS_ID:
                mUserView.showSports(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_PROFILE_ID:
                showUser(null);
                break;
            case SportteamLoader.LOADER_PROFILE_SPORTS_ID:
                mUserView.showSports(null);
                break;
        }
    }

    private void showUser(Cursor data) {
        if(data != null && data.moveToFirst()) {
            mUserView.showUserImage(data.getString(SportteamContract.UserEntry.COLUMN_PHOTO));
            mUserView.showUserName(data.getString(SportteamContract.UserEntry.COLUMN_NAME));
            mUserView.showUserCity(data.getString(SportteamContract.UserEntry.COLUMN_CITY));
            mUserView.showUserAge(data.getInt(SportteamContract.UserEntry.COLUMN_AGE));
            mUserView.showContent();
        } else {
            mUserView.clearUI();
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATION_TYPE_ERROR, RELATION_TYPE_ME, RELATION_TYPE_NONE, RELATION_TYPE_FRIENDS,
            RELATION_TYPE_I_SEND_REQUEST, RELATION_TYPE_I_RECEIVE_REQUEST})
    @interface UserRelationType {}
    static final int RELATION_TYPE_ERROR = -1;
    static final int RELATION_TYPE_ME = 0;
    static final int RELATION_TYPE_NONE = 1;
    static final int RELATION_TYPE_FRIENDS = 2;
    static final int RELATION_TYPE_I_SEND_REQUEST = 3;
    static final int RELATION_TYPE_I_RECEIVE_REQUEST = 4;
    @Override
    public void getRelationTypeBetweenThisUserAndI() {
        new MyAsyncTask(mUserView).execute();
    }

    // Use custom static AsyncTask class instead of create AsyncTask inside
    // getRelationTypeBetweenThisUserAndI() to avoid memory leak problem
    private static class MyAsyncTask extends AsyncTask<Void, Void, Integer> {

        // https://developer.android.com/reference/java/lang/ref/WeakReference
        // The View with a weak reference could be collected by GC.
        private WeakReference<ProfileContract.View> mView;

        MyAsyncTask(ProfileContract.View view) {
            mView = new WeakReference<>(view);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            // Check if ProfileView still exists
            ProfileContract.View usersView = mView.get();
            if (usersView == null) return RELATION_TYPE_ERROR;

            try {
                String myUid = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(myUid)) return RELATION_TYPE_ERROR;

                //Me?
                if (myUid.equals(usersView.getUserID())) return RELATION_TYPE_ME;

                //Friends?
                Cursor cursorFriends = usersView.getActivityContext().getContentResolver().query(
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        new String[]{SportteamContract.FriendsEntry.USER_ID_TABLE_PREFIX},
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND "
                                + SportteamContract.FriendsEntry.USER_ID + " = ?",
                        new String[]{myUid, usersView.getUserID()},
                        null);
                if (cursorFriends != null) {
                    if (cursorFriends.getCount() > 0) {
                        cursorFriends.close();
                        return RELATION_TYPE_FRIENDS;
                    }
                    cursorFriends.close();
                }

                //I have received a FriendRequest?
                Cursor cursorReceiver = usersView.getActivityContext().getContentResolver().query(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        new String[]{SportteamContract.FriendRequestEntry.SENDER_ID_TABLE_PREFIX},
                        SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                        new String[]{usersView.getUserID(), myUid},
                        null);
                if (cursorReceiver != null) {
                    if (cursorReceiver.getCount() > 0) {
                        cursorReceiver.close();
                        return RELATION_TYPE_I_RECEIVE_REQUEST;
                    }
                    cursorReceiver.close();
                }

                //I have sent a FriendRequest?
                Cursor cursorSender = usersView.getActivityContext().getContentResolver().query(
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        new String[]{SportteamContract.FriendRequestEntry.RECEIVER_ID_TABLE_PREFIX},
                        SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND "
                                + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                        new String[]{myUid, usersView.getUserID()},
                        null);
                if (cursorSender != null) {
                    if (cursorSender.getCount() > 0) {
                        cursorSender.close();
                        return RELATION_TYPE_I_SEND_REQUEST;
                    }
                    cursorSender.close();
                }

                //No relation
                return RELATION_TYPE_NONE;
            } catch (NullPointerException e) {
                e.printStackTrace();
                return RELATION_TYPE_ERROR;
            }
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            mView.get().uiSetupForUserRelation(integer);
        }
    }

    @Override
    public void sendFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.sendFriendRequest(myUid, uid);
        }
    }

    @Override
    public void cancelFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.cancelFriendRequest(myUid, uid);
        }

    }

    @Override
    public void acceptFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.acceptFriendRequest(myUid, uid);
        }

    }

    @Override
    public void declineFriendRequest(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.declineFriendRequest(myUid, uid);
        }

    }

    @Override
    public void deleteFriend(String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(uid)) {
            FriendsFirebaseActions.deleteFriend(myUid, uid);
        }
    }

    @Override
    public void checkUserName(String name, ValueEventListener listener) {
        if (name != null && !TextUtils.isEmpty(name))
            UserFirebaseActions.getUserNameReferenceEqualTo(name)
                    .addListenerForSingleValueEvent(listener);
    }

    @Override
    public void updateUserName(String name) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fUser == null) return;

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();
        fUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                if (mUserView.getActivityContext() instanceof ActivityContracts.ActionBarIconManagement)
                    ((ActivityContracts.ActionBarIconManagement)mUserView.getActivityContext()).setUserInfoInNavigationDrawer();
            }
        });

        UserFirebaseActions.updateUserName(fUser.getUid(), name);
        UsersFirebaseSync.loadAProfile(null, fUser.getUid(), false);
    }

    @Override
    public void updateUserAge(int age) {
        String myUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserId)) return;

        UserFirebaseActions.updateUserAge(myUserId, age);
        UsersFirebaseSync.loadAProfile(null, myUserId, false);
    }

    @Override
    public void updateUserPhoto(Uri photoCroppedUri) {
        UserFirebaseActions.storePhotoOnFirebase(photoCroppedUri, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    String oldPhotoUrl = Utiles.getCurrentUserPhoto();

                    FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (fUser == null) return;

                    // Update photo URL in Firebase Auth profile
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setPhotoUri(downloadUrl)
                            .build();
                    fUser.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update NavigationDrawer header
                            if (mUserView.getActivityContext() instanceof ActivityContracts.ActionBarIconManagement)
                                ((ActivityContracts.ActionBarIconManagement) mUserView.getActivityContext()).setUserInfoInNavigationDrawer();
                        }
                    });

                    // Update photo URL in Firebase Database
                    UserFirebaseActions.updateUserPhoto(fUser.getUid(), downloadUrl.toString());
                    UsersFirebaseSync.loadAProfile(null, fUser.getUid(), false);

                    // Delete old photo in Firebase Storage
                    UserFirebaseActions.deleteOldUserPhoto(oldPhotoUrl);
                }
            }
        });
    }

    @Override
    public void registerUserRelationObserver() {
        mUserView.getActivityContext().getContentResolver().registerContentObserver(
                SportteamContract.UserEntry.CONTENT_USER_RELATION_USER_URI, false, mContentObserver);
    }

    @Override
    public void unregisterUserRelationObserver() {
        mUserView.getActivityContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }
}

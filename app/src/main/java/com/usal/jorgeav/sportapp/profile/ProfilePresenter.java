package com.usal.jorgeav.sportapp.profile;

import android.database.ContentObserver;
import android.database.Cursor;
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
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = ProfilePresenter.class.getSimpleName();

    private ProfileContract.View mUserView;
    ContentObserver mContentObserver;

    public ProfilePresenter(ProfileContract.View userView) {
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
        FirebaseSync.loadAProfile(userId, false);
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
            String photoUrl = data.getString(SportteamContract.UserEntry.COLUMN_PHOTO);
            String name = data.getString(SportteamContract.UserEntry.COLUMN_NAME);
            String city = data.getString(SportteamContract.UserEntry.COLUMN_CITY);
            int age = data.getInt(SportteamContract.UserEntry.COLUMN_AGE);
            mUserView.showUserImage(photoUrl);
            mUserView.showUserName(name);
            mUserView.showUserCity(city);
            mUserView.showUserAge(age);
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
        AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
            @Override
            protected Integer doInBackground(Void... voids) {
                try {
                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    //Me?
                    if (myUid.equals(mUserView.getUserID())) return RELATION_TYPE_ME;

                    //Friends?
                    Cursor cursorFriends = mUserView.getActivityContext().getContentResolver().query(
                            SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                            SportteamContract.FriendsEntry.FRIENDS_COLUMNS,
                            SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND " + SportteamContract.FriendsEntry.USER_ID + " = ?",
                            new String[]{myUid, mUserView.getUserID()},
                            null);
                    if (cursorFriends != null) {
                        if(cursorFriends.getCount() > 0) {
                            cursorFriends.close();
                            return RELATION_TYPE_FRIENDS;
                        }
                        cursorFriends.close();
                    }

                    //I have received a FriendRequest?
                    Cursor cursorReceiver = mUserView.getActivityContext().getContentResolver().query(
                            SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                            SportteamContract.FriendRequestEntry.FRIEND_REQUESTS_COLUMNS,
                            SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND " + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                            new String[]{mUserView.getUserID(), myUid},
                            null);
                    if (cursorReceiver != null) {
                        if(cursorReceiver.getCount() > 0) {
                            cursorReceiver.close();
                            return RELATION_TYPE_I_RECEIVE_REQUEST;
                        }
                        cursorReceiver.close();
                    }

                    //I have sent a FriendRequest?
                    Cursor cursorSender = mUserView.getActivityContext().getContentResolver().query(
                            SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                            SportteamContract.FriendRequestEntry.FRIEND_REQUESTS_COLUMNS,
                            SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND " + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                            new String[]{myUid, mUserView.getUserID()},
                            null);
                    if (cursorSender != null) {
                        if(cursorSender.getCount() > 0) {
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
                mUserView.uiSetupForUserRelation(integer);
            }
        };

        task.execute();
    }

    @Override
    public void sendFriendRequest(String uid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(uid)) {
            FirebaseActions.sendFriendRequest(myUid, uid);
        }
    }

    @Override
    public void cancelFriendRequest(String uid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(uid)) {
            FirebaseActions.cancelFriendRequest(myUid, uid);
        }

    }

    @Override
    public void acceptFriendRequest(String uid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(uid)) {
            FirebaseActions.acceptFriendRequest(myUid, uid);
        }

    }

    @Override
    public void declineFriendRequest(String uid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(uid)) {
            FirebaseActions.declineFriendRequest(myUid, uid);
        }

    }

    @Override
    public void deleteFriend(String uid) {
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (!TextUtils.isEmpty(uid)) {
            FirebaseActions.deleteFriend(myUid, uid);
        }
    }

    @Override
    public void checkUserName(String name, ValueEventListener listener) {
        if (name != null && !TextUtils.isEmpty(name))
            FirebaseActions.getUserNameReferenceEqualTo(name)
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

        FirebaseActions.updateUserName(fUser.getUid(), name);
        FirebaseSync.loadAProfile(fUser.getUid(), false);
    }

    @Override
    public void updateUserAge(int age) {
        String myUserId = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserId)) return;

        FirebaseActions.updateUserAge(myUserId, age);
        FirebaseSync.loadAProfile(myUserId, false);
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

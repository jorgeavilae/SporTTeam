package com.usal.jorgeav.sportapp.profile;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseActions;
import com.usal.jorgeav.sportapp.network.FirebaseData;

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
                mUserView.uiSetupForUserRelation();
            }
        };
    }

    @Override
    public void openUser(LoaderManager loaderManager, Bundle b) {
        String userId = b.getString(ProfileFragment.BUNDLE_INSTANCE_UID);
        FirebaseData.loadAProfile(userId);
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
            String ageStr = data.getString(SportteamContract.UserEntry.COLUMN_AGE);
            int age = Integer.valueOf(ageStr);
            mUserView.showUserImage(photoUrl);
            mUserView.showUserName(name);
            mUserView.showUserCity(city);
            mUserView.showUserAge(age);
            mUserView.showContent();
        } else {
            mUserView.showUserImage("");
            mUserView.showUserName("");
            mUserView.showUserCity("");
            mUserView.showUserAge(-1);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({RELATION_TYPE_ERROR, RELATION_TYPE_NONE, RELATION_TYPE_FRIENDS,
            RELATION_TYPE_I_SEND_REQUEST, RELATION_TYPE_I_RECEIVE_REQUEST})
    @interface UserRelationType {}
    static final int RELATION_TYPE_ERROR = -1;
    static final int RELATION_TYPE_NONE = 0;
    static final int RELATION_TYPE_FRIENDS = 1;
    static final int RELATION_TYPE_I_SEND_REQUEST = 2;
    static final int RELATION_TYPE_I_RECEIVE_REQUEST = 3;
    @Override
    @UserRelationType
    public int getRelationTypeBetweenThisUserAndI() {
        try {
            String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d(TAG, "getRelationTypeBetweenThisUserAndI: myUid "+myUid);
            Log.d(TAG, "getRelationTypeBetweenThisUserAndI: otherUid "+mUserView.getUserID());

            //Friends?
            Cursor cursorFriends = mUserView.getActivityContext().getContentResolver().query(
                    SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                    SportteamContract.FriendsEntry.FRIENDS_COLUMNS,
                    SportteamContract.FriendsEntry.MY_USER_ID + " = ? AND " + SportteamContract.FriendsEntry.USER_ID + " = ?",
                    new String[]{myUid, mUserView.getUserID()},
                    null);
            if (cursorFriends != null && cursorFriends.getCount() > 0) {
                cursorFriends.close();
                return RELATION_TYPE_FRIENDS;
            }

            //I have received a FriendRequest?
            Cursor cursorReceiver = mUserView.getActivityContext().getContentResolver().query(
                    SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                    SportteamContract.FriendRequestEntry.FRIEND_REQUESTS_COLUMNS,
                    SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND " + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                    new String[]{mUserView.getUserID(), myUid},
                    null);
            if (cursorReceiver != null && cursorReceiver.getCount() > 0) {
                cursorReceiver.close();
                return RELATION_TYPE_I_RECEIVE_REQUEST;
            }

            //I have sent a FriendRequest?
            Cursor cursorSender = mUserView.getActivityContext().getContentResolver().query(
                    SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                    SportteamContract.FriendRequestEntry.FRIEND_REQUESTS_COLUMNS,
                    SportteamContract.FriendRequestEntry.SENDER_ID + " = ? AND " + SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                    new String[]{myUid, mUserView.getUserID()},
                    null);
            if (cursorSender != null && cursorSender.getCount() > 0) {
                cursorSender.close();
                return RELATION_TYPE_I_SEND_REQUEST;
            }

            //No relation
            return RELATION_TYPE_NONE;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return RELATION_TYPE_ERROR;
        }
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
    public void registerUserRelationObserver() {
        mUserView.getActivityContext().getContentResolver().registerContentObserver(
                SportteamContract.UserEntry.CONTENT_USER_RELATION_USER_URI, false, mContentObserver);
    }

    @Override
    public void unregisterUserRelationObserver() {
        mUserView.getActivityContext().getContentResolver().unregisterContentObserver(mContentObserver);
    }
}

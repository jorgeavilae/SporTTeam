package com.usal.jorgeav.sportapp.profile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class ProfilePresenter implements ProfileContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private ProfileContract.View mUserView;
    private User mUser;

    public ProfilePresenter(ProfileContract.View userView) {
        mUserView = userView;
    }

    @Override
    public void loadUser() {
//        FirebaseDatabaseActions.loadMyProfile(mUserView.getActivityContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (fuser != null)
            switch (id) {
                case ProfileFragment.LOADER_MYPROFILE_ID:
                    return new CursorLoader(
                            this.mUserView.getActivityContext(),
                            SportteamContract.UserEntry.CONTENT_USER_URI,
                            SportteamContract.UserEntry.USER_COLUMNS,
                            SportteamContract.UserEntry.USER_ID + " = ?",
                            new String[]{args.getString(ProfileFragment.BUNDLE_INSTANCE_UID)},
                            null);
                case ProfileFragment.LOADER_MYPROFILE_SPORTS_ID:
                    return new CursorLoader(
                            this.mUserView.getActivityContext(),
                            SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                            SportteamContract.UserSportEntry.USER_SPORT_COLUMNS,
                            SportteamContract.UserSportEntry.USER_ID + " = ?",
                            new String[]{args.getString(ProfileFragment.BUNDLE_INSTANCE_UID)},
                            null);
            }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case ProfileFragment.LOADER_MYPROFILE_ID:
                showUser(data);
                break;
            case ProfileFragment.LOADER_MYPROFILE_SPORTS_ID:
                if(data != null && data.moveToFirst()) //Todos los usuarios tienen al menos un deporte
                    mUserView.showSports(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case ProfileFragment.LOADER_MYPROFILE_ID:
                showUser(null);
                break;
            case ProfileFragment.LOADER_MYPROFILE_SPORTS_ID:
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
        if (!TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.sendFriendRequest(uid);
    }

    @Override
    public void cancelFriendRequest(String uid) {
        if (!TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.cancelFriendRequest(uid);

    }

    @Override
    public void acceptFriendRequest(String uid) {
        if (!TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.acceptFriendRequest(uid);

    }

    @Override
    public void declineFriendRequest(String uid) {
        if (!TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.declineFriendRequest(uid);

    }

    @Override
    public void deleteFriend(String uid) {
        if (!TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.deleteFriend(uid);

    }
}

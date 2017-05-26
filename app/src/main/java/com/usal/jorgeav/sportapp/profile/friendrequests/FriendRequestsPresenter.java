package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendRequestsPresenter implements FriendRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String SENDERID_KEY = "SENDERID_KEY";
    FriendRequestsContract.View mFriendRequestsView;
    //TODO friend requests
    private String[] friendRequestsUserIDs;

    public FriendRequestsPresenter(FriendRequestsContract.View friendRequestsView) {
        this.mFriendRequestsView = friendRequestsView;
    }

    @Override
    public void loadFriendRequests() {
//        FirebaseDatabaseActions.loadUsers(mFriendRequestsView.getActivityContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case FriendRequestsFragment.LOADER_FRIENDS_REQUESTS_ID:
                return new CursorLoader(
                        this.mFriendRequestsView.getActivityContext(),
                        SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_URI,
                        SportteamContract.FriendRequestEntry.FRIEND_REQUESTS_COLUMNS,
                        SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.FriendRequestEntry.DATE + " ASC");

            case FriendRequestsFragment.LOADER_FRIENDS_REQUESTS_USERS_ID:
                return new CursorLoader(
                        this.mFriendRequestsView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.USER_ID + " = ?",
                        args.getStringArray(SENDERID_KEY),
                        SportteamContract.UserEntry.NAME + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case FriendRequestsFragment.LOADER_FRIENDS_REQUESTS_ID:
                String usersId[] = cursorFriendRequestsToSenderStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(SENDERID_KEY, usersId);
                mFriendRequestsView.getThis().getLoaderManager()
                        .initLoader(FriendRequestsFragment.LOADER_FRIENDS_REQUESTS_USERS_ID, args, this);
                break;
            case FriendRequestsFragment.LOADER_FRIENDS_REQUESTS_USERS_ID:
                mFriendRequestsView.showFriendRequests(data);
                break;
        }
    }

    private String[] cursorFriendRequestsToSenderStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            arrayList.add(data.getString(SportteamContract.FriendRequestEntry.COLUMN_SENDER_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFriendRequestsView.showFriendRequests(null);
    }
}

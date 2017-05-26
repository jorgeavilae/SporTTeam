package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendRequestsPresenter implements FriendRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

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
        switch (id) {
            case FriendRequestsFragment.LOADER_FRIENDS_REQUESTS_ID:
            return new CursorLoader(
                        this.mFriendRequestsView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.USER_ID + " = ?",
                        friendRequestsUserIDs,
                    /*Deberia ordenarse por fecha de la peticion de amistad*/
                        SportteamContract.UserEntry.NAME + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFriendRequestsView.showFriendRequests(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFriendRequestsView.showFriendRequests(null);
    }
}

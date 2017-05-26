package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendRequestsPresenter implements FriendRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    FriendRequestsContract.View mFriendRequestsView;

    public FriendRequestsPresenter(FriendRequestsContract.View friendRequestsView) {
        this.mFriendRequestsView = friendRequestsView;
    }

    @Override
    public void loadFriendRequests() {

    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        //TODO friend requests
//        switch (id) {
//            case :
//                return new CursorLoader();
//        }
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

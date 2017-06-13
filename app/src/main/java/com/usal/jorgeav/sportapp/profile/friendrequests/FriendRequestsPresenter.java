package com.usal.jorgeav.sportapp.profile.friendrequests;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

public class FriendRequestsPresenter implements FriendRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FriendRequestsPresenter.class.getSimpleName();

    FriendRequestsContract.View mFriendRequestsView;

    public FriendRequestsPresenter(FriendRequestsContract.View friendRequestsView) {
        this.mFriendRequestsView = friendRequestsView;
    }

    @Override
    public void loadFriendRequests(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_FRIENDS_REQUESTS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SportteamLoader.LOADER_FRIENDS_REQUESTS_ID:
                return SportteamLoader
                        .cursorLoaderFriendRequests(mFriendRequestsView.getActivityContext(), currentUserID);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: "+ DatabaseUtils.dumpCursorToString(data));
        mFriendRequestsView.showFriendRequests(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mFriendRequestsView.showFriendRequests(null);
    }
}

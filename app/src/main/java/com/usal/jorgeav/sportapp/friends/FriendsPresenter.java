package com.usal.jorgeav.sportapp.friends;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

class FriendsPresenter implements FriendsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = FriendsPresenter.class.getSimpleName();

    private FriendsContract.View mFriendsView;

    FriendsPresenter(FriendsContract.View friendsView) {
        this.mFriendsView = friendsView;
    }

    @Override
    public void loadFriend(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadUsersFromFriends();
        loaderManager.initLoader(SportteamLoader.LOADER_FRIENDS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_FRIENDS_ID:
                return SportteamLoader
                        .cursorLoaderFriends(mFriendsView.getActivityContext(), currentUserID);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFriendsView.showFriends(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mFriendsView.showFriends(null);
    }

}

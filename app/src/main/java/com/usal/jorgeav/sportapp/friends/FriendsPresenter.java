package com.usal.jorgeav.sportapp.friends;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendsPresenter implements FriendsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = FriendsPresenter.class.getSimpleName();

    FriendsContract.View mFriendsView;

    public FriendsPresenter(FriendsContract.View friendsView) {
        this.mFriendsView = friendsView;
    }

    @Override
    public void loadFriend(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadUsersFromFriends();
        loaderManager.initLoader(SportteamLoader.LOADER_FRIENDS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

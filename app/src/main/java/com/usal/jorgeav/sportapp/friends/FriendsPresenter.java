package com.usal.jorgeav.sportapp.friends;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;

/**
 * Created by Jorge Avila on 26/05/2017.
 */

public class FriendsPresenter implements FriendsContract.Presenter {

    FriendsContract.View mFriendsView;

    public FriendsPresenter(FriendsContract.View friendsView) {
        this.mFriendsView = friendsView;
    }

    @Override
    public void loadFriends() {

    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return null;
    }
}

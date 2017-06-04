package com.usal.jorgeav.sportapp.friends.searchuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 04/06/2017.
 */

public class SearchUsersPresenter implements SearchUsersContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SearchUsersPresenter.class.getSimpleName();

    SearchUsersContract.View mSearchUsersView;

    public SearchUsersPresenter(SearchUsersContract.View mEventInvitationsView) {
        this.mSearchUsersView = mEventInvitationsView;
    }

    @Override
    public void loadUsers() {

    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SearchUsersFragment.LOADER_USERS_FROM_CITY:
                return new CursorLoader(
                        this.mSearchUsersView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.CITY + " = ?",
                        new String[]{Utiles.getCurrentCity(mSearchUsersView.getActivityContext(), currentUserID)},
                        SportteamContract.UserEntry.NAME + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSearchUsersView.showUsers(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSearchUsersView.showUsers(null);
    }
}

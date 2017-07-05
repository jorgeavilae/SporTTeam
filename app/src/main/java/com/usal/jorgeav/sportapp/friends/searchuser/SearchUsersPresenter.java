package com.usal.jorgeav.sportapp.friends.searchuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

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
    public void loadNearbyUsers(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_WITH_NAME);
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        String city = Utiles.getCurrentCity(mSearchUsersView.getActivityContext(), myUserID);
        FirebaseSync.loadUsersFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_FROM_CITY, b, this);
    }

    @Override
    public void loadNearbyUsersWithName(LoaderManager loaderManager, Bundle b) {
        String username = b.getString(SearchUsersFragment.BUNDLE_USERNAME);
        FirebaseSync.loadUsersWithName(username);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_WITH_NAME);
        loaderManager.restartLoader(SportteamLoader.LOADER_USERS_WITH_NAME, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SportteamLoader.LOADER_USERS_FROM_CITY:
                String city = Utiles.getCurrentCity(mSearchUsersView.getActivityContext(), currentUserID);
                return SportteamLoader
                        .cursorLoaderUsersFromCity(mSearchUsersView.getActivityContext(), currentUserID, city);
            case SportteamLoader.LOADER_USERS_WITH_NAME:
                if (args.containsKey(SearchUsersFragment.BUNDLE_USERNAME)) {
                    String username = args.getString(SearchUsersFragment.BUNDLE_USERNAME);
                    return SportteamLoader
                            .cursorLoaderUsersWithName(mSearchUsersView.getActivityContext(), currentUserID, username);
                }
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

package com.usal.jorgeav.sportapp.friends.searchuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;


class SearchUsersPresenter implements SearchUsersContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    @SuppressWarnings("unused")
    private static final String TAG = SearchUsersPresenter.class.getSimpleName();

    private SearchUsersContract.View mSearchUsersView;

    SearchUsersPresenter(SearchUsersContract.View mEventInvitationsView) {
        this.mSearchUsersView = mEventInvitationsView;
    }

    @Override
    public void loadNearbyUsers(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_WITH_NAME);

        String city = UtilesPreferences.getCurrentUserCity(mSearchUsersView.getActivityContext());
        FirebaseSync.loadUsersFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_FROM_CITY, b, this);
    }

    @Override
    public void loadUsersWithName(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_USERS_WITH_NAME);

        String username = b.getString(SearchUsersFragment.BUNDLE_USERNAME);
        FirebaseSync.loadUsersWithName(username);
        loaderManager.restartLoader(SportteamLoader.LOADER_USERS_WITH_NAME, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
        switch (id) {
            case SportteamLoader.LOADER_USERS_FROM_CITY:
                String city = UtilesPreferences.getCurrentUserCity(mSearchUsersView.getActivityContext());
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

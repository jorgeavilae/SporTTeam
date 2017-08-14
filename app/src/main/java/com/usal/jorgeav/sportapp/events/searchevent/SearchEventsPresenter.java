package com.usal.jorgeav.sportapp.events.searchevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;


class SearchEventsPresenter implements SearchEventsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = SearchEventsPresenter.class.getSimpleName();

    private SearchEventsContract.View mSearchEventsView;

    SearchEventsPresenter(SearchEventsContract.View mSearchEventsView) {
        this.mSearchEventsView = mSearchEventsView;
    }

    @Override
    public void loadNearbyEvents(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_WITH_SPORT);

        String city = UtilesPreferences.getCurrentUserCity(mSearchEventsView.getActivityContext());
        if (city != null && !TextUtils.isEmpty(city))
            FirebaseSync.loadEventsFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY, b, this);
    }

    @Override
    public void loadNearbyEventsWithSport(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY);
        loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_WITH_SPORT);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_WITH_SPORT, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        String city = UtilesPreferences.getCurrentUserCity(mSearchEventsView.getActivityContext());
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FROM_CITY:
                return SportteamLoader
                        .cursorLoaderEventsFromCity(mSearchEventsView.getActivityContext(), currentUserID, city);
            case SportteamLoader.LOADER_EVENTS_WITH_SPORT:
                if (args.containsKey(SearchEventsFragment.BUNDLE_SPORT)) {
                    String sportId = args.getString(SearchEventsFragment.BUNDLE_SPORT);
                    return SportteamLoader
                            .cursorLoaderEventsWithSport(mSearchEventsView.getActivityContext(), currentUserID, city, sportId);
                }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSearchEventsView.showEvents(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSearchEventsView.showEvents(null);
    }
}

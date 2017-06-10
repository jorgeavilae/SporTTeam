package com.usal.jorgeav.sportapp.events.searchevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

/**
 * Created by Jorge Avila on 06/06/2017.
 */

public class SearchEventsPresenter implements SearchEventsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SearchEventsPresenter.class.getSimpleName();

    SearchEventsContract.View mSearchEventsView;

    public SearchEventsPresenter(SearchEventsContract.View mSearchEventsView) {
        this.mSearchEventsView = mSearchEventsView;
    }

    @Override
    public void loadNearbyEvents(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY, b, this);
    }

    @Override
    public void loadNearbyEventsWithSport(LoaderManager loaderManager, Bundle b) {
        loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_WITH_SPORT, b, this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FROM_CITY:
                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String city = Utiles.getCurrentCity(mSearchEventsView.getActivityContext(), currentUserID);
                return SportteamLoader
                        .cursorLoaderEventsFromCity(mSearchEventsView.getActivityContext(), city);
            case SportteamLoader.LOADER_EVENTS_WITH_SPORT:
                if (args.containsKey(SearchEventsFragment.BUNDLE_SPORT)) {
                    String sportId = args.getString(SearchEventsFragment.BUNDLE_SPORT);
                    return SportteamLoader
                            .cursorLoaderEventsWithSport(mSearchEventsView.getActivityContext(), sportId);
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

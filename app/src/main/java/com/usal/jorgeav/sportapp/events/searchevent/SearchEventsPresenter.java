package com.usal.jorgeav.sportapp.events.searchevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

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
    public void loadEvents() {

    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SearchEventsFragment.LOADER_EVENTS_FROM_CITY:
                return new CursorLoader(
                        this.mSearchEventsView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.CITY + " = ?",
                        new String[]{Utiles.getCurrentCity(mSearchEventsView.getActivityContext(), currentUserID)},
                        SportteamContract.EventEntry.DATE + " ASC");
            case SearchEventsFragment.LOADER_EVENTS_WITH_SPORT:
                if (args.containsKey(SearchEventsFragment.BUNDLE_SPORT)) {
                    return new CursorLoader(
                            this.mSearchEventsView.getActivityContext(),
                            SportteamContract.EventEntry.CONTENT_EVENT_URI,
                            SportteamContract.EventEntry.EVENT_COLUMNS,
                            SportteamContract.EventEntry.SPORT + " = ?",
                            new String[]{args.getString(SearchEventsFragment.BUNDLE_SPORT)},
                            SportteamContract.EventEntry.DATE + " ASC");
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

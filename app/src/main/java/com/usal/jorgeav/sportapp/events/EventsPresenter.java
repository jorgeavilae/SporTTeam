package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsPresenter implements EventsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {

    EventsContract.View mEventsView;

    public EventsPresenter(EventsContract.View eventsView) {
        this.mEventsView = eventsView;
    }

    @Override
    public void loadEvents() {
//        FirebaseDatabaseActions.loadEvents(mEventsView.getContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {return this;}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case EventsFragment.LOADER_EVENTS_ID:
                return new CursorLoader(
                        this.mEventsView.getContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.OWNER + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.EventEntry.COLUMN_DATE + " ASC");
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case EventsFragment.LOADER_EVENTS_ID:
                mEventsView.showMyOwnEvents(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case EventsFragment.LOADER_EVENTS_ID:
                mEventsView.showMyOwnEvents(null);
                break;
        }
    }
}

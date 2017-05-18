package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

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
        FirebaseDatabaseActions.loadEvents(mEventsView.getContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {return this;}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case EventsFragment.LOADER_EVENTS_ID:
                return new CursorLoader(
                        this.mEventsView.getContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        null,
                        null,
                        SportteamContract.EventEntry.COLUMN_DATE + " ASC");
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventsView.showEvents(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mEventsView.showEvents(null);
    }
}

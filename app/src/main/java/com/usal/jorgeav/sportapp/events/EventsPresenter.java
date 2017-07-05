package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsPresenter implements EventsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String EVENTS_KEY = "EVENTS_KEY";

    EventsContract.View mEventsView;

    public EventsPresenter(EventsContract.View eventsView) {
        this.mEventsView = eventsView;
    }

    @Override
    public void loadEvents(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromMyOwnEvents();
        FirebaseSync.loadEventsFromEventsParticipation();
        loaderManager.initLoader(SportteamLoader.LOADER_MY_EVENTS_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_MY_EVENTS_PARTICIPATION_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SportteamLoader.LOADER_MY_EVENTS_ID:
                return SportteamLoader
                        .cursorLoaderMyEvents(mEventsView.getActivityContext(), currentUserID);
            case SportteamLoader.LOADER_MY_EVENTS_PARTICIPATION_ID:
                return SportteamLoader
                        .cursorLoaderMyEventParticipation(mEventsView.getActivityContext(), currentUserID, true);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_MY_EVENTS_ID:
                mEventsView.showMyOwnEvents(data);
                break;
            case SportteamLoader.LOADER_MY_EVENTS_PARTICIPATION_ID:
                mEventsView.showParticipatesEvents(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_MY_EVENTS_ID:
                mEventsView.showMyOwnEvents(null);
                break;
            case SportteamLoader.LOADER_MY_EVENTS_PARTICIPATION_ID:
                mEventsView.showParticipatesEvents(null);
                break;
        }
    }
}

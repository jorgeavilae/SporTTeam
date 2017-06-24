package com.usal.jorgeav.sportapp.events.eventrequest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

/**
 * Created by Jorge Avila on 27/05/2017.
 */

public class EventRequestsPresenter implements EventRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EventRequestsPresenter.class.getSimpleName();

    EventRequestsContract.View mEventRequestsView;

    public EventRequestsPresenter(EventRequestsContract.View mEventRequestsView) {
        this.mEventRequestsView = mEventRequestsView;
    }

    @Override
    public void loadEventRequests(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_REQUESTS_SENT_ID, b, this);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SportteamLoader.LOADER_EVENT_REQUESTS_SENT_ID:
                return SportteamLoader
                        .cursorLoaderEventsForEventRequestsSent(mEventRequestsView.getActivityContext(), currentUserID);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventRequestsView.showEventRequests(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mEventRequestsView.showEventRequests(null);
    }

}

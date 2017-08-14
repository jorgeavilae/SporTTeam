package com.usal.jorgeav.sportapp.events.eventrequest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

class EventRequestsPresenter implements EventRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = EventRequestsPresenter.class.getSimpleName();

    private EventRequestsContract.View mEventRequestsView;

    EventRequestsPresenter(EventRequestsContract.View mEventRequestsView) {
        this.mEventRequestsView = mEventRequestsView;
    }

    @Override
    public void loadEventRequests(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromEventsRequests();
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_REQUESTS_SENT_ID, b, this);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;
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

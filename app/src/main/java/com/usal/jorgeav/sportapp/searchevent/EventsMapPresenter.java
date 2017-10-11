package com.usal.jorgeav.sportapp.searchevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

class EventsMapPresenter implements EventsMapContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = EventsMapPresenter.class.getSimpleName();

    private EventsMapContract.View mEventsMapView;

    EventsMapPresenter(EventsMapContract.View mEventsMapView) {
        this.mEventsMapView = mEventsMapView;
    }

    @Override
    public void loadNearbyEvents(LoaderManager loaderManager, Bundle b) {
        String city = UtilesPreferences.getCurrentUserCity(mEventsMapView.getActivityContext());
        if (city != null && !TextUtils.isEmpty(city)) EventsFirebaseSync.loadEventsFromCity(city);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        String city = UtilesPreferences.getCurrentUserCity(mEventsMapView.getActivityContext());
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FROM_CITY:
                return SportteamLoader
                        .cursorLoaderEventsFromCity(mEventsMapView.getActivityContext(), currentUserID, city);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventsMapView.showEvents(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mEventsMapView.showEvents(null);
    }
}

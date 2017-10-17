package com.usal.jorgeav.sportapp.searchevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.mainactivities.SearchEventsActivity;
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
        loaderManager.restartLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        extractDataFromBundle(args);
        String currentUserID = Utiles.getCurrentUserId();
        String city = UtilesPreferences.getCurrentUserCity(mEventsMapView.getActivityContext());
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FROM_CITY:
                return SportteamLoader
                        .cursorLoaderEventsFromCity(mEventsMapView.getActivityContext(), currentUserID, city);
        }
        return null;
    }

    private void extractDataFromBundle(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_SPORTID_SELECTED))
                Log.d(TAG, "extractDataFromBundle: sport "+args.getString(SearchEventsActivity.INSTANCE_SPORTID_SELECTED));

            if (args.containsKey(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED))
                Log.d(TAG, "extractDataFromBundle: date from "+args.getLong(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED));
            if (args.containsKey(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED))
                Log.d(TAG, "extractDataFromBundle: date to "+args.getLong(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED));

            if (args.containsKey(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED))
                Log.d(TAG, "extractDataFromBundle: total from "+args.getInt(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED));
            if (args.containsKey(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED))
                Log.d(TAG, "extractDataFromBundle: total to "+args.getInt(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED));

            if (args.containsKey(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED))
                Log.d(TAG, "extractDataFromBundle: empty from "+args.getInt(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED));
            if (args.containsKey(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED))
                Log.d(TAG, "extractDataFromBundle: empty to "+args.getInt(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED));
        }
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

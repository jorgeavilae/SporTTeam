package com.usal.jorgeav.sportapp.searchevent;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

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

        if (b.isEmpty()) {
            loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_WITH_PARAMS);
            loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY, b, this);
        } else {
            loaderManager.destroyLoader(SportteamLoader.LOADER_EVENTS_FROM_CITY);
            loaderManager.restartLoader(SportteamLoader.LOADER_EVENTS_WITH_PARAMS, b, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = Utiles.getCurrentUserId();
        String city = UtilesPreferences.getCurrentUserCity(mEventsMapView.getActivityContext());
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FROM_CITY:
                return SportteamLoader
                        .cursorLoaderEventsFromCity(mEventsMapView.getActivityContext(), currentUserID, city);
            case SportteamLoader.LOADER_EVENTS_WITH_PARAMS:
                return SportteamLoader
                        .cursorLoaderEventsWithParams(mEventsMapView.getActivityContext(), currentUserID, city,
                                getBundleSportId(args),
                                getBundleDateFrom(args),
                                getBundleDateTo(args),
                                getBundleTotalFrom(args),
                                getBundleTotalTo(args),
                                getBundleEmptyFrom(args),
                                getBundleEmptyTo(args));
        }
        return null;
    }

    private String getBundleSportId(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_SPORTID_SELECTED))
                return args.getString(SearchEventsActivity.INSTANCE_SPORTID_SELECTED);
        }
        return null;
    }
    private Long getBundleDateFrom(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED))
                return args.getLong(SearchEventsActivity.INSTANCE_DATE_FROM_SELECTED);
        }
        return -1L;
    }
    private Long getBundleDateTo(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED))
                return args.getLong(SearchEventsActivity.INSTANCE_DATE_TO_SELECTED);
        }
        return -1L;
    }
    private int getBundleTotalFrom(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_TOTAL_FROM_SELECTED);
        }
        return -1;
    }
    private int getBundleTotalTo(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_TOTAL_TO_SELECTED);
        }
        return -1;
    }
    private int getBundleEmptyFrom(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_EMPTY_FROM_SELECTED);
        }
        return -1;
    }
    private int getBundleEmptyTo(Bundle args) {
        if (args != null) {
            if (args.containsKey(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED))
                return args.getInt(SearchEventsActivity.INSTANCE_EMPTY_TO_SELECTED);
        }
        return -1;
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

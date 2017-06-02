package com.usal.jorgeav.sportapp.eventdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailEventPresenter implements DetailEventContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailEventPresenter.class.getSimpleName();

    DetailEventContract.View mView;

    public DetailEventPresenter(@NonNull DetailEventContract.View view) {
        this.mView = view;
    }

    @Override
    public void openEvent() {
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DetailEventFragment.LOADER_EVENT_ID:
                return new CursorLoader(
                        this.mView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.EVENT_ID + " = ?",
                        new String[]{args.getString(DetailEventFragment.BUNDLE_EVENT_ID)},
                        null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DetailEventFragment.LOADER_EVENT_ID:
                showEventDetails(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DetailEventFragment.LOADER_EVENT_ID:
                showEventDetails(null);
        }

    }

    private void showEventDetails(Cursor data) {
        if (data != null && data.moveToFirst()) {
            mView.showEventId(data.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID));
            mView.showEventSport(data.getString(SportteamContract.EventEntry.COLUMN_SPORT));
            mView.showEventPlace(data.getString(SportteamContract.EventEntry.COLUMN_FIELD));
            mView.showEventDate(Utiles.millisToDateTimeString(data.getLong(SportteamContract.EventEntry.COLUMN_DATE)));
            mView.showEventOwner(data.getString(SportteamContract.EventEntry.COLUMN_OWNER));
            mView.showEventTotalPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS));
            mView.showEventEmptyPlayers(data.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS));
        } else {
            mView.showEventId("");
            mView.showEventSport("");
            mView.showEventPlace("");
            mView.showEventDate("");
            mView.showEventOwner("");
            mView.showEventTotalPlayers(-1);
            mView.showEventEmptyPlayers(-1);
        }
    }
}

package com.usal.jorgeav.sportapp.eventdetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailEventPresenter implements DetailEventContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailEventPresenter.class.getSimpleName();
    private static final String USERS_KEY = "USERS_KEY";

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
            case DetailEventFragment.LOADER_EVENTS_PARTICIPANTS_ID:
                return new CursorLoader(
                        this.mView.getActivityContext(),
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENTS_PARTICIPATION_COLUMNS,
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " = ?",
                        new String[]{args.getString(DetailEventFragment.BUNDLE_EVENT_ID)},
                        null);
            case DetailEventFragment.LOADER_USER_DATA_FROM_PARTICIPANTS_ID:
                return new CursorLoader(
                        this.mView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.USER_ID + " = ?",
                        args.getStringArray(USERS_KEY),
//                        null,
//                        null,
                        SportteamContract.EventEntry.COLUMN_DATE + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DetailEventFragment.LOADER_EVENT_ID:
                showEventDetails(data);
            case DetailEventFragment.LOADER_EVENTS_PARTICIPANTS_ID:
                String usersId[] = cursorEventsParticipationToUsersStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(USERS_KEY, usersId);
                mView.getThis().getLoaderManager()
                        .initLoader(DetailEventFragment.LOADER_USER_DATA_FROM_PARTICIPANTS_ID, args, this);
                break;
            case DetailEventFragment.LOADER_USER_DATA_FROM_PARTICIPANTS_ID:
                mView.showParticipants(data);
        }
    }

    private String[] cursorEventsParticipationToUsersStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            if (data.getInt(SportteamContract.EventsParticipationEntry.COLUMN_PARTICIPATES) == 1)
                arrayList.add(data.getString(SportteamContract.EventsParticipationEntry.COLUMN_USER_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case DetailEventFragment.LOADER_EVENT_ID:
                showEventDetails(null);
            case DetailEventFragment.LOADER_USER_DATA_FROM_PARTICIPANTS_ID:
                mView.showParticipants(null);
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

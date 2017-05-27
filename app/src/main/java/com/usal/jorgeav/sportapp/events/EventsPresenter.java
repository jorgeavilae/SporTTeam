package com.usal.jorgeav.sportapp.events;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

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
    public void loadEvents() {
//        FirebaseDatabaseActions.loadEvents(mEventsView.getContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {return this;}

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case EventsFragment.LOADER_MY_EVENTS_ID:
                return new CursorLoader(
                        this.mEventsView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.OWNER + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.EventEntry.COLUMN_DATE + " ASC");
            case EventsFragment.LOADER_EVENTS_PARTICIPATION_ID:
                return new CursorLoader(
                        this.mEventsView.getActivityContext(),
                        SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                        SportteamContract.EventsParticipationEntry.EVENTS_PARTICIPATION_COLUMNS,
                        SportteamContract.EventsParticipationEntry.USER_ID + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.EventsParticipationEntry.EVENT_ID + " ASC");
            case EventsFragment.LOADER_EVENTS_DATA_FROM_PARTICIPATION_ID:
                return new CursorLoader(
                        this.mEventsView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.EVENT_ID + " = ?",
                        args.getStringArray(EVENTS_KEY),
//                        null,
//                        null,
                        SportteamContract.EventEntry.COLUMN_DATE + " ASC");
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case EventsFragment.LOADER_MY_EVENTS_ID:
                mEventsView.showMyOwnEvents(data);
                break;
            case EventsFragment.LOADER_EVENTS_PARTICIPATION_ID:
                String evntsId[] = cursorEventsParticipationToEventsStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(EVENTS_KEY, evntsId);
                mEventsView.getThis().getLoaderManager()
                        .initLoader(EventsFragment.LOADER_EVENTS_DATA_FROM_PARTICIPATION_ID, args, this);
                break;
            case EventsFragment.LOADER_EVENTS_DATA_FROM_PARTICIPATION_ID:
                mEventsView.showParticipatesEvents(data);
                break;
        }
    }

    private String[] cursorEventsParticipationToEventsStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            if (data.getInt(SportteamContract.EventsParticipationEntry.COLUMN_PARTICIPATES) == 1)
                arrayList.add(data.getString(SportteamContract.EventsParticipationEntry.COLUMN_EVENT_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case EventsFragment.LOADER_MY_EVENTS_ID:
                mEventsView.showMyOwnEvents(null);
                break;
            case EventsFragment.LOADER_EVENTS_DATA_FROM_PARTICIPATION_ID:
                mEventsView.showParticipatesEvents(null);
                break;
        }
    }
}

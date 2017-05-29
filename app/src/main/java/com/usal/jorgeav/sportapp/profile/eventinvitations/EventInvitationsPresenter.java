package com.usal.jorgeav.sportapp.profile.eventinvitations;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 27/05/2017.
 */

public class EventInvitationsPresenter implements EventInvitationsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EventInvitationsPresenter.class.getSimpleName();
    private static final String EVENTID_KEY = "EVENTID_KEY";

    EventInvitationsContract.View mEventInvitationsView;

    public EventInvitationsPresenter(EventInvitationsContract.View mEventInvitationsView) {
        this.mEventInvitationsView = mEventInvitationsView;
    }

    @Override
    public void loadEventInvitations() {
//        FirebaseDatabaseActions.loadEvent(mEventInvitationsView.getActivityContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case EventInvitationsFragment.LOADER_EVENT_INVITATIONS_ID:
                return new CursorLoader(
                        this.mEventInvitationsView.getActivityContext(),
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_INVITATIONS_COLUMNS,
                        SportteamContract.EventsInvitationEntry.USER_ID + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.EventsInvitationEntry.DATE + " ASC");

            case EventInvitationsFragment.LOADER_EVENT_DATA_FROM_INVITATIONS_ID:
                return new CursorLoader(
                        this.mEventInvitationsView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
//                        SportteamContract.EventEntry.EVENT_ID + " = ?",
//                        args.getStringArray(EVENTID_KEY),
                        null,
                        null,
                        SportteamContract.EventEntry.DATE + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case EventInvitationsFragment.LOADER_EVENT_INVITATIONS_ID:
                String eventsId[] = cursorEventInvitationsToEventsStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(EVENTID_KEY, eventsId);
                mEventInvitationsView.getThis().getLoaderManager()
                        .initLoader(EventInvitationsFragment.LOADER_EVENT_DATA_FROM_INVITATIONS_ID, args, this);
                break;
            case EventInvitationsFragment.LOADER_EVENT_DATA_FROM_INVITATIONS_ID:
                mEventInvitationsView.showEventInvitations(data);
                break;
        }
    }

    private String[] cursorEventInvitationsToEventsStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            arrayList.add(data.getString(SportteamContract.EventsInvitationEntry.COLUMN_EVENT_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mEventInvitationsView.showEventInvitations(null);
    }
}

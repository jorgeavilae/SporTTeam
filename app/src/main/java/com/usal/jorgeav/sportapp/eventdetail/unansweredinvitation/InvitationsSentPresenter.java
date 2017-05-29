package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class InvitationsSentPresenter implements InvitationsSentContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = InvitationsSentPresenter.class.getSimpleName();
    private static final String USERID_KEY = "USERID_KEY";

    InvitationsSentContract.View mEventInvitationsView;

    public InvitationsSentPresenter(InvitationsSentContract.View mEventInvitationsView) {
        this.mEventInvitationsView = mEventInvitationsView;
    }

    @Override
    public void loadEventInvitationsSent() {
//        FirebaseDatabaseActions.loadEvent(mEventInvitationsView.getActivityContext());
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case InvitationsSentFragment.LOADER_EVENT_INVITATIONS_ID:
                return new CursorLoader(
                        this.mEventInvitationsView.getActivityContext(),
                        SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_URI,
                        SportteamContract.EventsInvitationEntry.EVENT_INVITATIONS_COLUMNS,
                        SportteamContract.EventsInvitationEntry.EVENT_ID + " = ?",
                        new String[]{args.getString(InvitationsSentFragment.BUNDLE_EVENT_ID)},
                        SportteamContract.EventsInvitationEntry.DATE + " ASC");

            case InvitationsSentFragment.LOADER_USER_DATA_FROM_INVITATIONS_ID:
                return new CursorLoader(
                        this.mEventInvitationsView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
//                        SportteamContract.EventEntry.EVENT_ID + " = ?",
//                        args.getStringArray(USERID_KEY),
                        null,
                        null,
                        SportteamContract.EventEntry.DATE + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case InvitationsSentFragment.LOADER_EVENT_INVITATIONS_ID:
                String usersId[] = cursorEventInvitationsToUsersStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(USERID_KEY, usersId);
                mEventInvitationsView.getThis().getLoaderManager()
                        .initLoader(InvitationsSentFragment.LOADER_USER_DATA_FROM_INVITATIONS_ID, args, this);
                break;
            case InvitationsSentFragment.LOADER_USER_DATA_FROM_INVITATIONS_ID:
                mEventInvitationsView.showEventInvitationsSent(data);
                break;
        }
    }

    private String[] cursorEventInvitationsToUsersStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            arrayList.add(data.getString(SportteamContract.EventsInvitationEntry.COLUMN_USER_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mEventInvitationsView.showEventInvitationsSent(null);
    }
}

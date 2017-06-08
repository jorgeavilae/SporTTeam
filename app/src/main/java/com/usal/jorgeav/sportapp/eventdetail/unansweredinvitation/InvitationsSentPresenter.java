package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

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
    public void deleteInvitationToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.deleteInvitationToThisEvent(eventId, uid);
    }

    @Override
    public void loadEventInvitationsSent(LoaderManager loaderManager, Bundle bundle) {
        loaderManager.initLoader(InvitationsSentFragment.LOADER_EVENT_INVITATIONS_ID, bundle, this);
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
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.USER_ID + " = ?",
                        args.getStringArray(USERID_KEY),
//                        null, null,
                        SportteamContract.UserEntry.NAME + " ASC");
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

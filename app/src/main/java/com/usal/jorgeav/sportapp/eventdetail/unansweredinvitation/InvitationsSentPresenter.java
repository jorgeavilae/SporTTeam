package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class InvitationsSentPresenter implements InvitationsSentContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = InvitationsSentPresenter.class.getSimpleName();

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
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_INVITATIONS_SENT_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENT_INVITATIONS_SENT_ID:
                String eventId = args.getString(InvitationsSentFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderUsersForEventInvitationsSent(mEventInvitationsView.getActivityContext(), eventId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventInvitationsView.showEventInvitationsSent(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mEventInvitationsView.showEventInvitationsSent(null);
    }
}

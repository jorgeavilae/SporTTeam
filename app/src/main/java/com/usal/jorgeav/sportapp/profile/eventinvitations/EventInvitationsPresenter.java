package com.usal.jorgeav.sportapp.profile.eventinvitations;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

/**
 * Created by Jorge Avila on 27/05/2017.
 */

public class EventInvitationsPresenter implements EventInvitationsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EventInvitationsPresenter.class.getSimpleName();

    EventInvitationsContract.View mEventInvitationsView;

    public EventInvitationsPresenter(EventInvitationsContract.View mEventInvitationsView) {
        this.mEventInvitationsView = mEventInvitationsView;
    }

    @Override
    public void loadEventInvitations(LoaderManager loaderManager, Bundle b) {
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_INVITATIONS_RECEIVED_ID, b, this);

    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SportteamLoader.LOADER_EVENT_INVITATIONS_RECEIVED_ID:
                return SportteamLoader
                        .cursorLoaderEventInvitationsReceived(mEventInvitationsView.getActivityContext(), currentUserID);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mEventInvitationsView.showEventInvitations(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mEventInvitationsView.showEventInvitations(null);
    }

}

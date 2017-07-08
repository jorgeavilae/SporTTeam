package com.usal.jorgeav.sportapp.eventdetail.unansweredinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

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
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = ""; if (fUser != null) myUid = fUser.getUid();
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            FirebaseActions.deleteInvitationToThisEvent(myUid, eventId, uid);
    }

    @Override
    public void loadEventInvitationsSent(LoaderManager loaderManager, Bundle bundle) {
        String eventId = bundle.getString(InvitationsSentFragment.BUNDLE_EVENT_ID);
        FirebaseSync.loadUsersFromInvitationsSent(eventId);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENT_INVITATIONS_SENT_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENT_INVITATIONS_SENT_ID:
                FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
                String myUid = ""; if (fUser != null) myUid = fUser.getUid();
                String eventId = args.getString(InvitationsSentFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderUsersForEventInvitationsSent(mEventInvitationsView.getActivityContext(), eventId, myUid);
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

package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class SendInvitationPresenter implements SendInvitationContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SendInvitationPresenter.class.getSimpleName();

    SendInvitationContract.View mSendInvitationView;

    public SendInvitationPresenter(SendInvitationContract.View mSendIvitationView) {
        this.mSendInvitationView = mSendIvitationView;
    }

    @Override
    public void loadEventsForInvitation() {

    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SendInvitationFragment.LOADER_EVENTS_FOR_INVITATION_ID:
                return new CursorLoader(
                        this.mSendInvitationView.getActivityContext(),
                        SportteamContract.EventEntry.CONTENT_EVENT_URI,
                        SportteamContract.EventEntry.EVENT_COLUMNS,
                        SportteamContract.EventEntry.OWNER + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.EventEntry.DATE + " ASC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSendInvitationView.showEventsForInvitation(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSendInvitationView.showEventsForInvitation(null);
    }
}

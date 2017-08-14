package com.usal.jorgeav.sportapp.profile.sendinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.actions.InvitationFirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

class SendInvitationPresenter implements SendInvitationContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = SendInvitationPresenter.class.getSimpleName();

    private SendInvitationContract.View mSendInvitationView;

    SendInvitationPresenter(SendInvitationContract.View mSendIvitationView) {
        this.mSendInvitationView = mSendIvitationView;
    }

    @Override
    public void loadEventsForInvitation(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromMyOwnEvents();
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FOR_INVITATION_ID, b, this);
    }

    @Override
    public void sendInvitationToThisUser(String eventId, String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            InvitationFirebaseActions.sendInvitationToThisEvent(myUid, eventId, uid);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FOR_INVITATION_ID:
                String currentUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(currentUserID)) return null;
                String otherUserID = args.getString(SendInvitationFragment.BUNDLE_INSTANCE_UID);
                return SportteamLoader
                        .cursorLoaderEventsForInvitation(
                                mSendInvitationView.getActivityContext(), currentUserID, otherUserID);
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

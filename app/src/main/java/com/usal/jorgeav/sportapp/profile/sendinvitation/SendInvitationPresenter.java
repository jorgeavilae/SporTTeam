package com.usal.jorgeav.sportapp.profile.sendinvitation;

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

public class SendInvitationPresenter implements SendInvitationContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SendInvitationPresenter.class.getSimpleName();

    SendInvitationContract.View mSendInvitationView;

    public SendInvitationPresenter(SendInvitationContract.View mSendIvitationView) {
        this.mSendInvitationView = mSendIvitationView;
    }

    @Override
    public void loadEventsForInvitation(LoaderManager loaderManager, Bundle b) {
        FirebaseSync.loadEventsFromMyOwnEvents();
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_FOR_INVITATION_ID, b, this);
    }

    @Override
    public void sendInvitationToThisUser(String eventId, String uid) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = ""; if (fUser != null) myUid = fUser.getUid();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            FirebaseActions.sendInvitationToThisEvent(myUid, eventId, uid);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_EVENTS_FOR_INVITATION_ID:
                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
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

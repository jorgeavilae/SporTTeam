package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class InviteUserPresenter implements InviteUserContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = InviteUserPresenter.class.getSimpleName();


    InviteUserContract.View mSendInvitationView;

    public InviteUserPresenter(InviteUserContract.View mSendInvitationView) {
        this.mSendInvitationView = mSendInvitationView;
    }

    @Override
    public void sendInvitationToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            FirebaseDatabaseActions.sendInvitationToThisEvent(eventId, uid);
    }

    @Override
    public void loadFriends(LoaderManager loaderManager, Bundle bundle) {
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_FOR_INVITE_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_USERS_FOR_INVITE_ID:
                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String eventID = args.getString(InviteUserFragment.BUNDLE_EVENT_ID);
                return SportteamLoader
                        .cursorLoaderUsersForInvite(mSendInvitationView.getActivityContext(), currentUserID, eventID);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSendInvitationView.showFriends(data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSendInvitationView.showFriends(null);
    }
}

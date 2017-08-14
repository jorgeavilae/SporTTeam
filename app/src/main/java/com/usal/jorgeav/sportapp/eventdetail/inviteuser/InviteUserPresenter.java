package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.actions.InvitationFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

class InviteUserPresenter implements InviteUserContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = InviteUserPresenter.class.getSimpleName();

    private InviteUserContract.View mSendInvitationView;

    InviteUserPresenter(InviteUserContract.View mSendInvitationView) {
        this.mSendInvitationView = mSendInvitationView;
    }

    @Override
    public void sendInvitationToThisEvent(String eventId, String uid) {
        String myUid = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUid) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            InvitationFirebaseActions.sendInvitationToThisEvent(myUid, eventId, uid);
    }

    @Override
    public void loadFriends(LoaderManager loaderManager, Bundle bundle) {
        FirebaseSync.loadUsersFromFriends();
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_FOR_INVITE_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SportteamLoader.LOADER_USERS_FOR_INVITE_ID:
                String eventID = args.getString(InviteUserFragment.BUNDLE_EVENT_ID);
                String currentUserID = Utiles.getCurrentUserId();
                if (TextUtils.isEmpty(currentUserID)) return null;
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

package com.usal.jorgeav.sportapp.eventdetail.userrequests;

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

public class UsersRequestsPresenter implements UsersRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = UsersRequestsPresenter.class.getSimpleName();

    private static final String SENDERID_KEY = "SENDERID_KEY";
    UsersRequestsContract.View mUsersRequestsView;

    public UsersRequestsPresenter(UsersRequestsContract.View mUsersRequestsView) {
        this.mUsersRequestsView = mUsersRequestsView;
    }

    @Override
    public void acceptUserRequestToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            FirebaseActions.acceptUserRequestToThisEvent(uid, eventId);
    }

    @Override
    public void declineUserRequestToThisEvent(String eventId, String uid) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        if (!TextUtils.isEmpty(myUserID) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            FirebaseActions.declineUserRequestToThisEvent(uid, eventId, myUserID);
    }

    @Override
    public void unblockUserParticipationRejectedToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId))
            FirebaseActions.unblockUserParticipationRejectedToThisEvent(uid, eventId);
    }

    @Override
    public void loadUsersRequests(LoaderManager loaderManager, Bundle b) {
        String eventId = b.getString(UsersRequestsFragment.BUNDLE_EVENT_ID);
        FirebaseSync.loadUsersFromUserRequests(eventId);
        loaderManager.initLoader(SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID, b, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String eventId = args.getString(UsersRequestsFragment.BUNDLE_EVENT_ID);
        switch (id) {
            case SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID:
                return SportteamLoader
                        .cursorLoaderUsersForEventRequestsReceived(mUsersRequestsView.getActivityContext(), eventId);
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                return SportteamLoader
                        .cursorLoaderEventParticipants(mUsersRequestsView.getActivityContext(), eventId, false);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID:
                mUsersRequestsView.showUsersRequests(data);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mUsersRequestsView.showRejectedUsers(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_USERS_REQUESTS_RECEIVED_ID:
                mUsersRequestsView.showUsersRequests(null);
                break;
            case SportteamLoader.LOADER_EVENTS_PARTICIPANTS_ID:
                mUsersRequestsView.showRejectedUsers(null);
                break;
        }
    }
}

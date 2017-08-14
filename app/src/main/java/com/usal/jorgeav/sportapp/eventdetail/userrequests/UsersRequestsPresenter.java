package com.usal.jorgeav.sportapp.eventdetail.userrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.actions.EventRequestFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;

class UsersRequestsPresenter implements UsersRequestsContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    @SuppressWarnings("unused")
    private static final String TAG = UsersRequestsPresenter.class.getSimpleName();

    private UsersRequestsContract.View mUsersRequestsView;

    UsersRequestsPresenter(UsersRequestsContract.View mUsersRequestsView) {
        this.mUsersRequestsView = mUsersRequestsView;
    }

    @Override
    public void acceptUserRequestToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            EventRequestFirebaseActions.acceptUserRequestToThisEvent(uid, eventId);
    }

    @Override
    public void declineUserRequestToThisEvent(String eventId, String uid) {
        String myUserID = Utiles.getCurrentUserId();
        if (!TextUtils.isEmpty(myUserID) && !TextUtils.isEmpty(eventId) && !TextUtils.isEmpty(uid))
            EventRequestFirebaseActions.declineUserRequestToThisEvent(uid, eventId, myUserID);
    }

    @Override
    public void unblockUserParticipationRejectedToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId))
            EventRequestFirebaseActions.unblockUserParticipationRejectedToThisEvent(uid, eventId);
    }

    @Override
    public void loadUsersRequests(LoaderManager loaderManager, Bundle b) {
        String eventId = b.getString(UsersRequestsFragment.BUNDLE_EVENT_ID);
        if (eventId != null && !TextUtils.isEmpty(eventId))
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

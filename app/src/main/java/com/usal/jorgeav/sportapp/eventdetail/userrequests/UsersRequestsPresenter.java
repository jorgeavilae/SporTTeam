package com.usal.jorgeav.sportapp.eventdetail.userrequests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

import java.util.ArrayList;

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
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.acceptUserRequestToThisEvent(uid, eventId);
    }

    @Override
    public void declineUserRequestToThisEvent(String eventId, String uid) {
        if (!TextUtils.isEmpty(eventId))
            FirebaseDatabaseActions.declineUserRequestToThisEvent(uid, eventId);
    }

    @Override
    public void loadUsersRequests() {

    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case UsersRequestsFragment.LOADER_USERS_REQUESTS_ID:
                return new CursorLoader(
                        this.mUsersRequestsView.getActivityContext(),
                        SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_URI,
                        SportteamContract.EventRequestsEntry.EVENTS_REQUESTS_COLUMNS,
                        SportteamContract.EventRequestsEntry.EVENT_ID + " = ?",
                        new String[]{args.getString(UsersRequestsFragment.BUNDLE_EVENT_ID)},
                        SportteamContract.EventRequestsEntry.DATE + " ASC");

            case UsersRequestsFragment.LOADER_USERS_REQUESTS_DATA_ID:
                return new CursorLoader(
                        this.mUsersRequestsView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
                        SportteamContract.UserEntry.USER_ID + " = ?",
                        args.getStringArray(SENDERID_KEY),
//                        null,null,
                        SportteamContract.UserEntry.NAME + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case UsersRequestsFragment.LOADER_USERS_REQUESTS_ID:
                String usersId[] = cursorUsersRequestsToSenderStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(SENDERID_KEY, usersId);
                mUsersRequestsView.getThis().getLoaderManager()
                        .initLoader(UsersRequestsFragment.LOADER_USERS_REQUESTS_DATA_ID, args, this);
                break;
            case UsersRequestsFragment.LOADER_USERS_REQUESTS_DATA_ID:
                mUsersRequestsView.showUsersRequests(data);
                break;
        }
    }

    private String[] cursorUsersRequestsToSenderStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            arrayList.add(data.getString(SportteamContract.EventRequestsEntry.COLUMN_SENDER_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mUsersRequestsView.showUsersRequests(null);
    }
}

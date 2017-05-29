package com.usal.jorgeav.sportapp.eventdetail.sendinvitation;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class SendInvitationPresenter implements SendInvitationContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String FRIEND_KEY = "FRIEND_KEY";

    SendInvitationContract.View mSendInvitationView;

    public SendInvitationPresenter(SendInvitationContract.View mSendInvitationView) {
        this.mSendInvitationView = mSendInvitationView;
    }

    @Override
    public void loadFriends() {
    }

    @Override
    public LoaderManager.LoaderCallbacks<Cursor> getLoaderInstance() {
        return this;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case SendInvitationFragment.LOADER_FRIENDS_ID:
                return new CursorLoader(
                        this.mSendInvitationView.getActivityContext(),
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        SportteamContract.FriendsEntry.FRIENDS_COLUMNS,
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.FriendsEntry.DATE + " ASC");

            case SendInvitationFragment.LOADER_FRIENDS_AS_USERS_ID:
                return new CursorLoader(
                        this.mSendInvitationView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
//                        SportteamContract.UserEntry.USER_ID + " = ?",
//                        args.getStringArray(FRIEND_KEY),
                        null,
                        null,
                        SportteamContract.UserEntry.NAME + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SendInvitationFragment.LOADER_FRIENDS_ID:
                String usersId[] = cursorFriendsToUsersStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(FRIEND_KEY, usersId);
                mSendInvitationView.getThis().getLoaderManager()
                        .initLoader(SendInvitationFragment.LOADER_FRIENDS_AS_USERS_ID, args, this);
                break;
            case SendInvitationFragment.LOADER_FRIENDS_AS_USERS_ID:
                mSendInvitationView.showFriends(data);
                break;
        }
    }

    private String[] cursorFriendsToUsersStringArray(Cursor data) {
        ArrayList<String> arrayList = new ArrayList<>();
        while (data.moveToNext())
            arrayList.add(data.getString(SportteamContract.FriendsEntry.COLUMN_USER_ID));
        data.close();
        return arrayList.toArray(new String[arrayList.size()]);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mSendInvitationView.showFriends(null);
    }
}

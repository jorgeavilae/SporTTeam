package com.usal.jorgeav.sportapp.eventdetail.inviteuser;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDatabaseActions;

import java.util.ArrayList;

/**
 * Created by Jorge Avila on 29/05/2017.
 */

public class InviteUserPresenter implements InviteUserContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String FRIEND_KEY = "FRIEND_KEY";

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
        loaderManager.initLoader(InviteUserFragment.LOADER_FRIENDS_ID, bundle, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        switch (id) {
            case InviteUserFragment.LOADER_FRIENDS_ID:
                return new CursorLoader(
                        this.mSendInvitationView.getActivityContext(),
                        SportteamContract.FriendsEntry.CONTENT_FRIENDS_URI,
                        SportteamContract.FriendsEntry.FRIENDS_COLUMNS,
                        SportteamContract.FriendsEntry.MY_USER_ID + " = ?",
                        new String[]{currentUserID},
                        SportteamContract.FriendsEntry.DATE + " ASC");

            case InviteUserFragment.LOADER_FRIENDS_AS_USERS_ID:
                return new CursorLoader(
                        this.mSendInvitationView.getActivityContext(),
                        SportteamContract.UserEntry.CONTENT_USER_URI,
                        SportteamContract.UserEntry.USER_COLUMNS,
//                        SportteamContract.UserEntry.USER_ID + " IN (?)",
//                        args.getStringArray(FRIEND_KEY),
                        null, null,
                        SportteamContract.UserEntry.NAME + " DESC");
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case InviteUserFragment.LOADER_FRIENDS_ID:
                String usersId[] = cursorFriendsToUsersStringArray(data);
                Bundle args = new Bundle();
                args.putStringArray(FRIEND_KEY, usersId);
                mSendInvitationView.getThis().getLoaderManager()
                        .initLoader(InviteUserFragment.LOADER_FRIENDS_AS_USERS_ID, args, this);
                break;
            case InviteUserFragment.LOADER_FRIENDS_AS_USERS_ID:
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

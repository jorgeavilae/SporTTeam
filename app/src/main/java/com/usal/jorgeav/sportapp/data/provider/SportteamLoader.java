package com.usal.jorgeav.sportapp.data.provider;

import android.content.Context;
import android.support.v4.content.CursorLoader;

/**
 * Created by Jorge Avila on 07/06/2017.
 */

public final class SportteamLoader {
    public static final int LOADER_FRIENDS_REQUESTS_ID = 5000;
    public static final int LOADER_FRIENDS_REQUESTS_USERS_ID = 5001;

    public static CursorLoader cursorLoaderFriendRequests(Context context, String currentUserID){
        return new CursorLoader(
                context,
                SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                new String[]{currentUserID},
                SportteamContract.FriendRequestEntry.DATE + " ASC");
    }
}

package com.usal.jorgeav.sportapp.data.provider;

import android.content.Context;
import android.support.v4.content.CursorLoader;

/**
 * Created by Jorge Avila on 07/06/2017.
 */

public final class SportteamLoader {
    public static final int LOADER_FRIENDS_REQUESTS_ID = 5000;
    public static CursorLoader cursorLoaderFriendRequests(Context context, String myUserID) {
        // Return user data for all of my friends requests
        return new CursorLoader(
                context,
                SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendRequestEntry.RECEIVER_ID + " = ?",
                new String[]{myUserID},
                SportteamContract.FriendRequestEntry.DATE + " ASC");
    }


    public static final int LOADER_FRIENDS_ID = 6000;
    public static CursorLoader cursorLoaderFriends(Context context, String myUserID) {
        // Return user data for all of my friends
        return new CursorLoader(
                context,
                SportteamContract.FriendsEntry.CONTENT_FRIEND_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendsEntry.MY_USER_ID + " = ?",
                new String[]{myUserID},
                SportteamContract.FriendsEntry.DATE + " ASC");
    }



    public static final int LOADER_EVENTS_FOR_INVITATION_ID = 8000;
    public static CursorLoader cursorLoaderSendInvitation(Context context, String myUserID, String otherUserID) {
        // Return all of my events data in which otherUser has no relation
        /* TODO puede estar
            asistencia true: ya asiste
            asistencia false: esta bloqueado, desbloquear
            invitacion enviada: invitado y esperando que conteste
            peticion participar: envio una peticion para entrar, contestar
            otro caso: enviar invitacion
         */

        return new CursorLoader(
                context,
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.OWNER + " = ? ",
                new String[]{myUserID},
                SportteamContract.EventEntry.DATE + " ASC");
    }
}

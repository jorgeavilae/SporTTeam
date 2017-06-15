package com.usal.jorgeav.sportapp.data.provider;

import android.content.Context;
import android.support.v4.content.CursorLoader;

/**
 * Created by Jorge Avila on 07/06/2017.
 */

public final class SportteamLoader {

    public static final int LOADER_PROFILE_ID = 1010;
    public static final int LOADER_PROFILE_SPORTS_ID = 1011;
    public static CursorLoader cursorLoaderOneUser(Context context, String userId) {
        // Return user data
        return new CursorLoader(
                context,
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{userId},
                null);
    }
    public static CursorLoader cursorLoaderSportsUser(Context context, String userId) {
        // Return sports data for userId
        return new CursorLoader(
                context,
                SportteamContract.UserSportEntry.CONTENT_USER_SPORT_URI,
                SportteamContract.UserSportEntry.USER_SPORT_COLUMNS,
                SportteamContract.UserSportEntry.USER_ID + " = ?",
                new String[]{userId},
                null);
    }


    public static final int LOADER_MY_EVENTS_ID = 2100;
    public static final int LOADER_MY_EVENTS_PARTICIPATION_ID = 2200;
    public static CursorLoader cursorLoaderMyEvents(Context context, String myUserID) {
        // Return my events
        return new CursorLoader(
                context,
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.OWNER + " = ?",
                new String[]{myUserID},
                SportteamContract.EventEntry.COLUMN_DATE + " ASC");
    }
    public static CursorLoader cursorLoaderMyEventParticipation(Context context, String myUserID, boolean participate) {
        // Return event data of my participation events
        return new CursorLoader(
                context,
                SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.EventsParticipationEntry.PARTICIPATES_TABLE_PREFIX + " = ?",
                new String[]{myUserID, String.valueOf(participate?1:0)},
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static final int LOADER_EVENT_ID = 2010;
    public static final int LOADER_EVENTS_PARTICIPANTS_ID = 2011;
    public static CursorLoader cursorLoaderOneEvent(Context context, String eventId) {
        // Return event data
        return new CursorLoader(
                context,
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.EVENT_ID + " = ?",
                new String[]{eventId},
                null);
    }
    public static CursorLoader cursorLoaderEventParticipants(Context context, String eventId, boolean participate) {
        // Return user data for participants in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.EventsParticipationEntry.PARTICIPATES_TABLE_PREFIX + " = ?",
                new String[]{eventId, String.valueOf(participate?1:0)},
                SportteamContract.EventsParticipationEntry.USER_ID_TABLE_PREFIX + " ASC");
    }



    public static final int LOADER_FRIENDS_ID = 3000;
    public static CursorLoader cursorLoaderFriends(Context context, String myUserID) {
        // Return user data for all of my friends
        return new CursorLoader(
                context,
                SportteamContract.FriendsEntry.CONTENT_FRIEND_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendsEntry.MY_USER_ID_TABLE_PREFIX + " = ?",
                new String[]{myUserID},
                SportteamContract.FriendsEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static final int LOADER_FRIENDS_REQUESTS_ID = 3100;
    public static CursorLoader cursorLoaderFriendRequests(Context context, String myUserID) {
        // Return user data for all of my friends requests
        return new CursorLoader(
                context,
                SportteamContract.FriendRequestEntry.CONTENT_FRIEND_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.FriendRequestEntry.RECEIVER_ID_TABLE_PREFIX + " = ?",
                new String[]{myUserID},
                SportteamContract.FriendRequestEntry.DATE_TABLE_PREFIX + " ASC");
    }



    public static final int LOADER_EVENT_INVITATIONS_SENT_ID = 4100;
    public static CursorLoader cursorLoaderUsersForEventInvitationsSent(Context context, String eventId) {
        // Return user data for invitations sent in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.EventsInvitationEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static final int LOADER_EVENT_INVITATIONS_RECEIVED_ID = 4200;
    public static CursorLoader cursorLoaderEventsForEventInvitationsReceived(Context context, String myUserId) {
        // Return event data for invitations received in myUserId
        return new CursorLoader(
                context,
                SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventsInvitationEntry.USER_ID_TABLE_PREFIX + " = ? ",
                new String[]{myUserId},
                SportteamContract.EventsInvitationEntry.DATE_TABLE_PREFIX + " ASC");
    }


    public static final int LOADER_USERS_REQUESTS_ID = 5100;
    public static CursorLoader cursorLoaderUsersForEventRequests(Context context, String eventId) {
        // Return user data for requests received in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.EventRequestsEntry.DATE_TABLE_PREFIX + " ASC");
    }


    public static final int LOADER_USERS_FROM_CITY = 1100;
    public static final int LOADER_USERS_WITH_NAME = 1200;
    public static CursorLoader cursorLoaderUsersFromCity(Context context, String myUserId, String city) {
        // Return user data from users in city
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_NOT_FRIENDS_USERS_FROM_CITY,
                SportteamContract.JoinQueryEntries.queryNotFriendsUsersFromCityArguments(myUserId, city),
                SportteamContract.UserEntry.NAME_TABLE_PREFIX + " ASC");
    }
    public static CursorLoader cursorLoaderUsersWithName(Context context, String myUserId, String username) {
        //Return user data from users with username
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_NOT_FRIENDS_USERS_WITH_NAME,
                SportteamContract.JoinQueryEntries.queryNotFriendsUsersWithNameArguments(myUserId, username),
                SportteamContract.UserEntry.NAME_TABLE_PREFIX + " ASC");
    }


    public static final int LOADER_EVENTS_FROM_CITY = 2300;
    public static final int LOADER_EVENTS_WITH_SPORT = 2400;
    public static CursorLoader cursorLoaderEventsFromCity(Context context, String myUserId, String city) {
        // Return event data from events in city
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME,
                SportteamContract.JoinQueryEntries.queryCityEventsWithoutRelationWithMeArguments(myUserId, city),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static CursorLoader cursorLoaderEventsWithSport(Context context, String myUserId, String city, String sportId) {
        //Return event data from events with sportId
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME,
                SportteamContract.JoinQueryEntries.queryCitySportEventsWithoutRelationWithMeArguments(myUserId, city, sportId),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }



    public static final int LOADER_FIELDS_FROM_CITY = 6100;
    public static CursorLoader cursorLoaderFieldsFromCity(Context context, String city) {
        //Return field data from fields in city
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.CITY + " = ? ",
                new String[]{city},
                SportteamContract.FieldEntry.COLUMN_PUNCTUATION + " DESC");
    }
    public static final int LOADER_FIELDS_WITH_SPORT = 6200;
    public static CursorLoader cursorLoaderFieldsWithSport(Context context, String sportId) {
        // Return field data from fields with sportId
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.SPORT + " = ?",
                new String[]{sportId},
                SportteamContract.FieldEntry.NAME + " ASC");
    }
    public static final int LOADER_FIELD_ID = 6001;
    public static CursorLoader cursorLoaderOneField(Context context, String fieldId, String sportId) {
        // Return field data
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.FIELD_ID + " = ? AND " + SportteamContract.FieldEntry.SPORT +" = ? ",
                new String[]{fieldId, sportId},
                null);
    }


    public static final int LOADER_MY_ALARMS_ID = 7100;
    public static CursorLoader cursorLoaderMyAlarms(Context context) {
        // Return my events
        return new CursorLoader(
                context,
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                null,
                null,
                SportteamContract.AlarmEntry.DATE_FROM_TABLE_PREFIX + " ASC");
    }

    public static final int LOADER_EVENTS_FOR_INVITATION_ID = 8100;
    public static CursorLoader cursorLoaderEventsForInvitation(Context context, String myUserID, String otherUserID) {
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
                SportteamContract.JoinQueryEntries.CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND,
                SportteamContract.JoinQueryEntries.queryMyEventsWithoutRelationWithFriendArguments(myUserID, otherUserID),
                SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static final int LOADER_USERS_FOR_INVITE_ID = 8200;
    public static CursorLoader cursorLoaderUsersForInvite(Context context, String myUserID, String eventId) {
        // Return all of my friends data who has no relation with eventId
        /* TODO puede estar
            asistencia true: ya asiste
            asistencia false: esta bloqueado, desbloquear
            invitacion enviada: invitado y esperando que conteste
            peticion participar: envio una peticion para entrar, contestar
            otro caso: enviar invitacion
         */
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS,
                SportteamContract.JoinQueryEntries.queryMyFriendsWithoutRelationWithMyEventsArguments(myUserID, eventId),
                SportteamContract.FriendsEntry.DATE_TABLE_PREFIX + " ASC");
    }
}

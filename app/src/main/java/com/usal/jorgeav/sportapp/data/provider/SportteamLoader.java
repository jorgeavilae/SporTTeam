package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.utils.UtilesContentProvider;

import java.util.ArrayList;
import java.util.Arrays;


public final class SportteamLoader {
    @SuppressWarnings("unused")
    private static final String TAG = SportteamLoader.class.getSimpleName();

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
    public static final int LOADER_EVENTS_SIMULATED_PARTICIPANTS_ID = 2012;
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
    public static CursorLoader cursorLoaderEventParticipantsNoData(Context context, String eventId) {
        // Return userId for participants in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsParticipationEntry.CONTENT_EVENTS_PARTICIPATION_URI,
                SportteamContract.EventsParticipationEntry.EVENTS_PARTICIPATION_COLUMNS,
                SportteamContract.EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.EventsParticipationEntry.USER_ID_TABLE_PREFIX + " ASC");
    }
    public static CursorLoader cursorLoaderEventSimulatedParticipants(Context context, String eventId) {
        // Return user data for participants in eventId
        return new CursorLoader(
                context,
                SportteamContract.SimulatedParticipantEntry.CONTENT_SIMULATED_PARTICIPANT_URI,
                SportteamContract.SimulatedParticipantEntry.SIMULATED_PARTICIPANTS_COLUMNS,
                SportteamContract.SimulatedParticipantEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.SimulatedParticipantEntry.ALIAS_TABLE_PREFIX + " ASC");
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
    public static CursorLoader cursorLoaderUsersForEventInvitationsSent(Context context, String eventId, String myUserId) {
        // Return user data for invitations sent in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " = ? AND "
                +SportteamContract.EventsInvitationEntry.SENDER_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId, myUserId},
                SportteamContract.EventsInvitationEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static final int LOADER_EVENT_INVITATIONS_RECEIVED_ID = 4200;
    public static CursorLoader cursorLoaderEventsForEventInvitationsReceived(Context context, String myUserId) {
        // Return event data for invitations received in myUserId
        return new CursorLoader(
                context,
                SportteamContract.EventsInvitationEntry.CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX + " = ? ",
                new String[]{myUserId},
                SportteamContract.EventsInvitationEntry.DATE_TABLE_PREFIX + " ASC");
    }


    public static final int LOADER_USERS_REQUESTS_RECEIVED_ID = 5100;
    public static CursorLoader cursorLoaderUsersForEventRequestsReceived(Context context, String eventId) {
        // Return user data for requests received in eventId
        return new CursorLoader(
                context,
                SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " = ? ",
                new String[]{eventId},
                SportteamContract.EventRequestsEntry.DATE_TABLE_PREFIX + " ASC");
    }
    public static final int LOADER_EVENT_REQUESTS_SENT_ID = 5200;
    public static CursorLoader cursorLoaderEventsForEventRequestsSent(Context context, String myUserId) {
        // Return event data for event requests sent by myUserId
        return new CursorLoader(
                context,
                SportteamContract.EventRequestsEntry.CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventRequestsEntry.SENDER_ID_TABLE_PREFIX + " = ? ",
                new String[]{myUserId},
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
                SportteamContract.FieldEntry.COLUMN_NAME + " DESC");
    }
    public static final int LOADER_FIELDS_FROM_CITY_WITH_SPORT = 6200;
    public static CursorLoader cursorLoaderFieldsFromCityWithSport(Context context, String city, String sportId) {
        // Return field data from fields with sportId
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELDS_WITH_FIELD_SPORT_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.CITY_TABLE_PREFIX + " = ? AND "
                        + SportteamContract.FieldSportEntry.SPORT_TABLE_PREFIX +" = ? ",
                new String[]{city, sportId},
                SportteamContract.FieldEntry.NAME + " ASC");
    }
    public static final int LOADER_FIELD_ID = 6010;
    public static final int LOADER_FIELD_SPORTS_ID = 6011;
    public static CursorLoader cursorLoaderOneField(Context context, String fieldId) {
        // Return field data
        return new CursorLoader(
                context,
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }
    public static CursorLoader cursorLoaderFieldSports(Context context, String fieldId) {
        // Return field data
        return new CursorLoader(
                context,
                SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                SportteamContract.FieldSportEntry.FIELD_SPORT_COLUMNS,
                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
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
    public static final int LOADER_ALARM_ID = 7200;
    public static final int LOADER_ALARM_EVENTS_COINCIDENCE_ID = 7210;
    public static CursorLoader cursorLoaderOneAlarm(Context context, String alarmId) {
        // Return event data
        return new CursorLoader(
                context,
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                SportteamContract.AlarmEntry.ALARM_ID + " = ?",
                new String[]{alarmId},
                null);
    }
    public static CursorLoader cursorLoaderAlarmCoincidence(Context context, String alarmId, String myUserId) {
        Alarm alarm = UtilesContentProvider.cursorToSingleAlarm(simpleQueryAlarmId(context, alarmId));

        if (alarm != null) {
            String selection = SportteamContract.JoinQueryEntries.WHERE_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME;
            ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(SportteamContract.JoinQueryEntries.queryCitySportEventsWithoutRelationWithMeArguments(myUserId, alarm.getCity(), alarm.getSport_id())));

            // field could be null
            if (alarm.getField_id() != null) {
                selection += "AND " + SportteamContract.EventEntry.FIELD_TABLE_PREFIX + " = ? ";
                selectionArgs.add(alarm.getField_id());
            }

            // dateFrom must be at least today and dateTo should be greater than dateFrom or null
            selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getDate_from().toString());
            if (alarm.getDate_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getDate_to().toString());
            }

            // totalFrom could be null and totalTo should be greater than totalFrom or null
            if (alarm.getTotal_players_from() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " >= ? ";
                selectionArgs.add(alarm.getTotal_players_from().toString());
            }
            if (alarm.getTotal_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getTotal_players_to().toString());
            }

            // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
            selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getEmpty_players_from().toString());
            if (alarm.getEmpty_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getEmpty_players_to().toString());
            }

            return new CursorLoader(
                    context,
                    SportteamContract.JoinQueryEntries.CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                    SportteamContract.EventEntry.EVENT_COLUMNS,
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
            /*
            SELECT all event columns
            FROM event
                LEFT JOIN eventsParticipation
                    ON (event.eventId = eventsParticipation.eventId AND eventsParticipation.userId = XPs5mf8MZnXDAPjtyHkF0MqbzQ42 )
                LEFT JOIN eventInvitations
                    ON (event.eventId = eventInvitations.eventId AND eventInvitations.receiverId = XPs5mf8MZnXDAPjtyHkF0MqbzQ42 )
                LEFT JOIN eventRequest
                    ON (event.eventId = eventRequest.eventId AND eventRequest.senderId = XPs5mf8MZnXDAPjtyHkF0MqbzQ42 )
            WHERE (
                event.city = ciudad AND
                event.sport = basketball AND
                event.owner <> XPs5mf8MZnXDAPjtyHkF0MqbzQ42 AND
                eventsParticipation.eventId IS NULL AND
                eventInvitations.eventId IS NULL AND
                eventRequest.eventId IS NULL AND

                event.field = -KkaYdeabKspjuzgvzqI AND
                event.date >= 1497823200000 AND event.date <= 1497909600000 AND
                event.totalPlayers >= 22 AND event.totalPlayers <= 22 AND
                event.emptyPlayers >= 2 AND event.emptyPlayers <= 2
                )
             */
        }
        return null;
    }
    public static Cursor cursorAlarmCoincidence(ContentResolver contentResolver, Alarm alarm, String myUserId) {
        if (alarm != null) {
            String selection = SportteamContract.JoinQueryEntries.WHERE_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME;
            ArrayList<String> selectionArgs = new ArrayList<>(Arrays.asList(SportteamContract.JoinQueryEntries.queryCitySportEventsWithoutRelationWithMeArguments(myUserId, alarm.getCity(), alarm.getSport_id())));

            // field could be null
            if (alarm.getField_id() != null) {
                selection += "AND " + SportteamContract.EventEntry.FIELD_TABLE_PREFIX + " = ? ";
                selectionArgs.add(alarm.getField_id());
            }

            // dateFrom must be at least today and dateTo should be greater than dateFrom or null
            selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getDate_from().toString());
            if (alarm.getDate_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.DATE_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getDate_to().toString());
            }

            // totalFrom could be null and totalTo should be greater than totalFrom or null
            if (alarm.getTotal_players_from() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " >= ? ";
                selectionArgs.add(alarm.getTotal_players_from().toString());
            }
            if (alarm.getTotal_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.TOTAL_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getTotal_players_to().toString());
            }

            // emptyFrom must be at least 1 and emptyTo should be greater than emptyFrom or null
            selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " >= ? ";
            selectionArgs.add(alarm.getEmpty_players_from().toString());
            if (alarm.getEmpty_players_to() != null) {
                selection += "AND " + SportteamContract.EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " <= ? ";
                selectionArgs.add(alarm.getEmpty_players_to().toString());
            }

            return contentResolver.query(
                    SportteamContract.JoinQueryEntries.CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI,
                    SportteamContract.EventEntry.EVENT_COLUMNS,
                    selection,
                    selectionArgs.toArray(new String[selectionArgs.size()]),
                    SportteamContract.EventEntry.DATE_TABLE_PREFIX + " ASC");
        }
        return null;
    }

    public static final int LOADER_EVENTS_FOR_INVITATION_ID = 8100;
    public static CursorLoader cursorLoaderEventsForInvitation(Context context, String myUserID, String otherUserID) {
        // Return all of my events data in which otherUser has no relation
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
        return new CursorLoader(
                context,
                SportteamContract.JoinQueryEntries.CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.JoinQueryEntries.WHERE_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS,
                SportteamContract.JoinQueryEntries.queryMyFriendsWithoutRelationWithMyEventsArguments(myUserID, eventId),
                SportteamContract.FriendsEntry.DATE_TABLE_PREFIX + " ASC");
    }

    public static Cursor simpleQueryUserId(Context context, String userId) {
        return context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ? ",
                new String[]{userId},
                null);
    }

    public static Cursor simpleQueryEventId(Context context, String eventId) {
        return context.getContentResolver().query(
                SportteamContract.EventEntry.CONTENT_EVENT_URI,
                SportteamContract.EventEntry.EVENT_COLUMNS,
                SportteamContract.EventEntry.EVENT_ID + " = ? ",
                new String[]{eventId},
                null);
    }

    public static Cursor simpleQueryFieldId(Context context, String fieldId) {
        return context.getContentResolver().query(
                SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                SportteamContract.FieldEntry.FIELDS_COLUMNS,
                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }
    public static Cursor simpleQuerySportsOfFieldId(Context context, String fieldId) {
        return context.getContentResolver().query(
                SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                SportteamContract.FieldSportEntry.FIELD_SPORT_COLUMNS,
                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                new String[]{fieldId},
                null);
    }

    public static Cursor simpleQueryAlarmId(Context context, String alarmId) {
        return context.getContentResolver().query(
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                SportteamContract.AlarmEntry.ALARM_ID + " = ?",
                new String[]{alarmId},
                null);
    }
}

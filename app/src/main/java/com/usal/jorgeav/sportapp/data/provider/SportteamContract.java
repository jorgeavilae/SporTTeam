package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public final class SportteamContract {

    /*
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.usal.jorgeav.sportapp";

    /*
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_USERS = "users";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_USER_RELATION_USER = "user_relation_user";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_USER_RELATION_EVENT = "user_relation_event";
    /* Used internally as the name of our user table. */
    public static final String TABLE_USER = "user";
    /* Inner class that defines the table contents of the user table */
    public static final class UserEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the user table from the content provider */
        public static final Uri CONTENT_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USERS)
                .build();
        /* The base CONTENT_URI used to observe relation between an User and me */
        public static final Uri CONTENT_USER_RELATION_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_RELATION_USER)
                .build();
        /* The base CONTENT_URI used to observe relation between an User and an Event */
        public static final Uri CONTENT_USER_RELATION_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_RELATION_EVENT)
                .build();

        /* Column names */
        public static final String USER_ID = "uid";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String CITY = "city";
        public static final String PHOTO = "photo";

        /* Column names with table prefix*/
        public static final String USER_ID_TABLE_PREFIX = TABLE_USER + "." + USER_ID;
        public static final String EMAIL_TABLE_PREFIX = TABLE_USER + "." + EMAIL;
        public static final String NAME_TABLE_PREFIX = TABLE_USER + "." + NAME;
        public static final String AGE_TABLE_PREFIX = TABLE_USER + "." + AGE;
        public static final String CITY_TABLE_PREFIX = TABLE_USER + "." + CITY;
        public static final String PHOTO_TABLE_PREFIX = TABLE_USER + "." + PHOTO;

        /* All column projection */
        public static final String[] USER_COLUMNS = {
                TABLE_USER+"."+UserEntry._ID,
                USER_ID_TABLE_PREFIX,
                EMAIL_TABLE_PREFIX,
                NAME_TABLE_PREFIX,
                AGE_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                PHOTO_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_USER_ID = 1;
        public static final int COLUMN_EMAIL = 2;
        public static final int COLUMN_NAME = 3;
        public static final int COLUMN_AGE = 4;
        public static final int COLUMN_CITY = 5;
        public static final int COLUMN_PHOTO = 6;

        /* URI for one user */
        public static Uri buildUserUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_USER_URI, id);
        }
    }


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_USER_SPORT = "userSport";
    /* Used internally as the name of our user sports table. */
    public static final String TABLE_USER_SPORTS = "userSport";
    /* Inner class that defines the table contents of the user sport table
     * This table store a row for every sport practiced by every user in the user table */
    public static final class UserSportEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the userSport table from the content provider */
        public static final Uri CONTENT_USER_SPORT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_SPORT)
                .build();

        /* Column names */
        public static final String USER_ID = "uid";
        public static final String SPORT = "sport";
        public static final String LEVEL = "level";

        /* Column names with table prefix*/
        public static final String USER_ID_TABLE_PREFIX = TABLE_USER_SPORTS + "." + USER_ID;
        public static final String SPORT_TABLE_PREFIX = TABLE_USER_SPORTS + "." + SPORT;
        public static final String LEVEL_TABLE_PREFIX = TABLE_USER_SPORTS + "." + LEVEL;

        /* All column projection */
        public static final String[] USER_SPORT_COLUMNS = {
                TABLE_USER_SPORTS + "." + UserSportEntry._ID,
                USER_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                LEVEL_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_USER_ID = 1;
        public static final int COLUMN_SPORT = 2;
        public static final int COLUMN_LEVEL = 3;

        /* URI for one userSport */
        public static Uri buildUserSportUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_USER_SPORT_URI, id);
        }
    }


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_FIELDS = "fields";
    /* Used internally as the name of our field table. */
    public static final String TABLE_FIELD = "field";
    /* Inner class that defines the table contents of the field table */
    public static final class FieldEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the field table from the content provider */
        public static final Uri CONTENT_FIELD_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELDS)
                .build();

        /* Column names */
        public static final String FIELD_ID = "fieldId";
        public static final String NAME = "name";
        public static final String SPORT = "sport";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String PUNCTUATION = "punctuation";
        public static final String VOTES = "votes";
        public static final String OPENING_TIME = "openingTime";
        public static final String CLOSING_TIME = "closingTime";

        /* Column names with table prefix*/
        public static final String FIELD_ID_TABLE_PREFIX = TABLE_FIELD + "." + FIELD_ID;
        public static final String NAME_TABLE_PREFIX = TABLE_FIELD + "." + NAME;
        public static final String SPORT_TABLE_PREFIX = TABLE_FIELD + "." + SPORT;
        public static final String ADDRESS_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS;
        public static final String CITY_TABLE_PREFIX = TABLE_FIELD + "." + CITY;
        public static final String PUNCTUATION_TABLE_PREFIX = TABLE_FIELD + "." + PUNCTUATION;
        public static final String VOTES_TABLE_PREFIX = TABLE_FIELD + "." + VOTES;
        public static final String OPENING_TIME_TABLE_PREFIX = TABLE_FIELD + "." + OPENING_TIME;
        public static final String CLOSING_TIME_TABLE_PREFIX = TABLE_FIELD + "." + CLOSING_TIME;

        /* All column projection */
        public static final String[] FIELDS_COLUMNS = {
                TABLE_FIELD + "." + FieldEntry._ID,
                FIELD_ID_TABLE_PREFIX,
                NAME_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                ADDRESS_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                PUNCTUATION_TABLE_PREFIX,
                VOTES_TABLE_PREFIX,
                OPENING_TIME_TABLE_PREFIX,
                CLOSING_TIME_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_FIELD_ID = 1;
        public static final int COLUMN_NAME = 2;
        public static final int COLUMN_SPORT = 3;
        public static final int COLUMN_ADDRESS = 4;
        public static final int COLUMN_CITY = 5;
        public static final int COLUMN_PUNCTUATION = 6;
        public static final int COLUMN_VOTES = 7;
        public static final int COLUMN_OPENING_TIME = 8;
        public static final int COLUMN_CLOSING_TIME = 9;

        /* URI for one field */
        public static Uri buildFieldUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FIELD_URI, id);
        }
    }


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_ALARMS = "alarms";
    /* Used internally as the name of our alarm table. */
    public static final String TABLE_ALARM = "alarm";
    /* Inner class that defines the table contents of the alarm table */
    public static final class AlarmEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the alarm table from the content provider */
        public static final Uri CONTENT_ALARM_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ALARMS)
                .build();

        /* Column names */
        public static final String ALARM_ID = "alarmId";
        public static final String SPORT = "sport";
        public static final String FIELD = "field";
        public static final String CITY = "city";
        public static final String DATE_FROM = "dateFrom";
        public static final String DATE_TO = "dateTo";
        public static final String TOTAL_PLAYERS_FROM = "totalPlayersFrom";
        public static final String TOTAL_PLAYERS_TO = "totalPlayersTo";
        public static final String EMPTY_PLAYERS_FROM = "emptyPlayersFrom";
        public static final String EMPTY_PLAYERS_TO = "emptyPlayersTo";

        /* Column names with table prefix */
        public static final String ALARM_ID_TABLE_PREFIX = TABLE_ALARM + "." + ALARM_ID;
        public static final String SPORT_TABLE_PREFIX = TABLE_ALARM + "." + SPORT;
        public static final String FIELD_TABLE_PREFIX = TABLE_ALARM + "." + FIELD;
        public static final String CITY_TABLE_PREFIX = TABLE_ALARM + "." + CITY;
        public static final String DATE_FROM_TABLE_PREFIX = TABLE_ALARM + "." + DATE_FROM;
        public static final String DATE_TO_TABLE_PREFIX = TABLE_ALARM + "." + DATE_TO;
        public static final String TOTAL_PLAYERS_FROM_TABLE_PREFIX = TABLE_ALARM + "." + TOTAL_PLAYERS_FROM;
        public static final String TOTAL_PLAYERS_TO_TABLE_PREFIX = TABLE_ALARM + "." + TOTAL_PLAYERS_TO;
        public static final String EMPTY_PLAYERS_FROM_TABLE_PREFIX = TABLE_ALARM + "." + EMPTY_PLAYERS_FROM;
        public static final String EMPTY_PLAYERS_TO_TABLE_PREFIX = TABLE_ALARM + "." + EMPTY_PLAYERS_TO;

        /* All column projection */
        public static final String[] ALARM_COLUMNS = {
                TABLE_ALARM + "." + AlarmEntry._ID,
                ALARM_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                FIELD_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                DATE_FROM_TABLE_PREFIX,
                DATE_TO_TABLE_PREFIX,
                TOTAL_PLAYERS_FROM_TABLE_PREFIX,
                TOTAL_PLAYERS_TO_TABLE_PREFIX,
                EMPTY_PLAYERS_FROM_TABLE_PREFIX,
                EMPTY_PLAYERS_TO_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_ALARM_ID = 1;
        public static final int COLUMN_SPORT = 2;
        public static final int COLUMN_FIELD = 3;
        public static final int COLUMN_CITY = 4;
        public static final int COLUMN_DATE_FROM = 5;
        public static final int COLUMN_DATE_TO = 6;
        public static final int COLUMN_TOTAL_PLAYERS_FROM = 7;
        public static final int COLUMN_TOTAL_PLAYERS_TO = 8;
        public static final int COLUMN_EMPTY_PLAYERS_FROM = 9;
        public static final int COLUMN_EMPTY_PLAYERS_TO = 10;

        /* URI for one alarm */
        public static Uri buildAlarmUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_ALARM_URI, id);
        }
    }


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS = "events";
    /* Used internally as the name of our event table. */
    public static final String TABLE_EVENT = "event";
    /* Inner class that defines the table contents of the event table */
    public static final class EventEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the event table from the content provider */
        public static final Uri CONTENT_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS)
                .build();

        /* Column names */
        public static final String EVENT_ID = "eventId";
        public static final String SPORT = "sport";
        public static final String FIELD = "field";
        public static final String NAME = "name";
        public static final String CITY = "city";
        public static final String DATE = "date";
        public static final String OWNER = "owner";
        public static final String TOTAL_PLAYERS = "totalPlayers";
        public static final String EMPTY_PLAYERS = "emptyPlayers";

        /* Column names with table prefix */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENT + "." + EVENT_ID;
        public static final String SPORT_TABLE_PREFIX = TABLE_EVENT + "." + SPORT;
        public static final String FIELD_TABLE_PREFIX = TABLE_EVENT + "." + FIELD;
        public static final String NAME_TABLE_PREFIX = TABLE_EVENT + "." + NAME;
        public static final String CITY_TABLE_PREFIX = TABLE_EVENT + "." + CITY;
        public static final String DATE_TABLE_PREFIX = TABLE_EVENT + "." + DATE;
        public static final String OWNER_TABLE_PREFIX = TABLE_EVENT + "." + OWNER;
        public static final String TOTAL_PLAYERS_TABLE_PREFIX = TABLE_EVENT + "." + TOTAL_PLAYERS;
        public static final String EMPTY_PLAYERS_TABLE_PREFIX = TABLE_EVENT + "." + EMPTY_PLAYERS;

        /* All column projection */
        public static final String[] EVENT_COLUMNS = {
                TABLE_EVENT + "." + EventEntry._ID,
                EVENT_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                FIELD_TABLE_PREFIX,
                NAME_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                DATE_TABLE_PREFIX,
                OWNER_TABLE_PREFIX,
                TOTAL_PLAYERS_TABLE_PREFIX,
                EMPTY_PLAYERS_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_EVENT_ID = 1;
        public static final int COLUMN_SPORT = 2;
        public static final int COLUMN_FIELD = 3;
        public static final int COLUMN_NAME = 4;
        public static final int COLUMN_CITY = 5;
        public static final int COLUMN_DATE = 6;
        public static final int COLUMN_OWNER = 7;
        public static final int COLUMN_TOTAL_PLAYERS = 8;
        public static final int COLUMN_EMPTY_PLAYERS = 9;

        /* URI for one event */
        public static Uri buildEventUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENT_URI, id);
        }
    }


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_FRIENDS_REQUESTS = "friendRequests";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_FRIENDS_REQUESTS_WITH_USER = "friendRequests_user";
    /* Used internally as the name of our friend requests table. */
    public static final String TABLE_FRIENDS_REQUESTS = "friendRequest";
    /* Inner class that defines the table contents of the friendRequests table
     * This table store a row for every friend request from any user to our logged user */
    public static final class FriendRequestEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the friendRequest table from the content provider */
        public static final Uri CONTENT_FRIEND_REQUESTS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_REQUESTS)
                .build();
        /* The base CONTENT_URI used to query the friendRequest table with user table from the content provider */
        public static final Uri CONTENT_FRIEND_REQUESTS_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_REQUESTS_WITH_USER)
                .build();

        /* Column names */
        public static final String RECEIVER_ID = "receiverId";
        public static final String SENDER_ID = "senderId";
        public static final String DATE = "date";

        /* Column names with table prefix */
        public static final String RECEIVER_ID_TABLE_PREFIX = TABLE_FRIENDS_REQUESTS + "." + RECEIVER_ID;
        public static final String SENDER_ID_TABLE_PREFIX = TABLE_FRIENDS_REQUESTS + "." + SENDER_ID;
        public static final String DATE_TABLE_PREFIX = TABLE_FRIENDS_REQUESTS + "." + DATE;

        /* All column projection */
        public static final String[] FRIEND_REQUESTS_COLUMNS = {
                TABLE_FRIENDS_REQUESTS + "." + FriendRequestEntry._ID,
                RECEIVER_ID_TABLE_PREFIX,
                SENDER_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_RECEIVER_ID = 1;
        public static final int COLUMN_SENDER_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_FRIEND_REQUESTS_WITH_USER_URI */
        public static final String TABLES_FRIENDS_REQUESTS_JOIN_USER =
                TABLE_FRIENDS_REQUESTS + " INNER JOIN " + TABLE_USER + " ON "
                    + FriendRequestEntry.SENDER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /* URI for one friend request */
        public static Uri buildFriendRequestsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FRIEND_REQUESTS_URI, id);
        }
    }

    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_FRIENDS = "friends";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_FRIENDS_WITH_USER = "friends_user";
    /* Used internally as the name of our friends table. */
    public static final String TABLE_FRIENDS = "friends";
    /* Inner class that defines the table contents of the friends table
     * This table store a row for every connection between two users */
    public static final class FriendsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the friends table from the content provider */
        public static final Uri CONTENT_FRIENDS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS)
                .build();
        /* The base CONTENT_URI used to query the friend table with user table from the content provider */
        public static final Uri CONTENT_FRIEND_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_WITH_USER)
                .build();

        /* Column names */
        public static final String MY_USER_ID = "myUserId";
        public static final String USER_ID = "userId";
        public static final String DATE = "date";

        /* Column names with table prefix */
        public static final String MY_USER_ID_TABLE_PREFIX = TABLE_FRIENDS + "." + MY_USER_ID;
        public static final String USER_ID_TABLE_PREFIX = TABLE_FRIENDS + "." + USER_ID;
        public static final String DATE_TABLE_PREFIX = TABLE_FRIENDS + "." + DATE;

        /* All column projection */
        public static final String[] FRIENDS_COLUMNS = {
                TABLE_FRIENDS + "." + FriendsEntry._ID,
                MY_USER_ID_TABLE_PREFIX,
                USER_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_MY_USER_ID = 1;
        public static final int COLUMN_USER_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_FRIEND_WITH_USER_URI */
        public static final String TABLES_FRIENDS_JOIN_USER =
                TABLE_FRIENDS + " INNER JOIN " + TABLE_USER + " ON "
                    + FriendsEntry.USER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /* URI for one friend */
        public static Uri buildFriendsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FRIENDS_URI, id);
        }
    }

    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_PARTICIPATION = "eventsParticipation";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_PARTICIPATION_WITH_USER = "eventsParticipation_user";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_PARTICIPATION_WITH_EVENT = "eventsParticipation_event";
    /* Used internally as the name of our events participation table. */
    public static final String TABLE_EVENTS_PARTICIPATION = "eventsParticipation";
    /* Inner class that defines the table contents of the eventsParticipation table
     * This table store a row for every participate relation between an user and an event*/
    public static final class EventsParticipationEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the eventsParticipation table from the content provider */
        public static final Uri CONTENT_EVENTS_PARTICIPATION_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_PARTICIPATION)
                .build();
        /* The base CONTENT_URI used to query the eventsParticipation table with user table from the content provider */
        public static final Uri CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_PARTICIPATION_WITH_USER)
                .build();
        /* The base CONTENT_URI used to query the eventsParticipation table with event table from the content provider */
        public static final Uri CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_PARTICIPATION_WITH_EVENT)
                .build();

        /* Column names */
        public static final String USER_ID = "userId";
        public static final String EVENT_ID = "eventId";
        public static final String PARTICIPATES = "participates";

        /* Column names with table prefix */
        public static final String USER_ID_TABLE_PREFIX = TABLE_EVENTS_PARTICIPATION + "." + USER_ID;
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENTS_PARTICIPATION + "." + EVENT_ID;
        public static final String PARTICIPATES_TABLE_PREFIX = TABLE_EVENTS_PARTICIPATION + "." + PARTICIPATES;

        /* All column projection */
        public static final String[] EVENTS_PARTICIPATION_COLUMNS = {
                TABLE_EVENTS_PARTICIPATION + "." + EventsParticipationEntry._ID,
                USER_ID_TABLE_PREFIX,
                EVENT_ID_TABLE_PREFIX,
                PARTICIPATES_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_USER_ID = 1;
        public static final int COLUMN_EVENT_ID = 2;
        public static final int COLUMN_PARTICIPATES = 3;

        /* Join for CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI */
        public static final String TABLES_EVENTS_PARTICIPATION_JOIN_USER =
                TABLE_EVENTS_PARTICIPATION + " INNER JOIN " + TABLE_USER + " ON "
                    + EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /* Join for CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI */
        public static final String TABLES_EVENTS_PARTICIPATION_JOIN_EVENT =
                TABLE_EVENTS_PARTICIPATION + " INNER JOIN " + TABLE_EVENT + " ON "
                    + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " = " + EventEntry.EVENT_ID_TABLE_PREFIX;

        /* URI for one event participation */
        public static Uri buildEventsParticipationUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENTS_PARTICIPATION_URI, id);
        }
    }

    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENT_INVITATIONS = "eventInvitations";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENT_INVITATIONS_WITH_USER = "eventInvitations_user";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENT_INVITATIONS_WITH_EVENT = "eventInvitations_event";
    /* Used internally as the name of our event invitations table. */
    public static final String TABLE_EVENT_INVITATIONS = "eventInvitations";
    /* Inner class that defines the table contents of the eventInvitations table
     * This table store a row for every event invitation received by the user*/
    public static final class EventsInvitationEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the eventInvitations table from the content provider */
        public static final Uri CONTENT_EVENT_INVITATIONS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_INVITATIONS)
                .build();
        /* The base CONTENT_URI used to query the eventInvitations table with user table from the content provider */
        public static final Uri CONTENT_EVENT_INVITATIONS_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_INVITATIONS_WITH_USER)
                .build();
        /* The base CONTENT_URI used to query the eventInvitations table with event table from the content provider */
        public static final Uri CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_INVITATIONS_WITH_EVENT)
                .build();

        /* Column names */
        public static final String RECEIVER_ID = "receiverId";
        public static final String SENDER_ID = "senderId";
        public static final String EVENT_ID = "eventId";
        public static final String DATE = "date";

        /* Column names with table prefix */
        public static final String RECEIVER_ID_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + RECEIVER_ID;
        public static final String SENDER_ID_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + SENDER_ID;
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + EVENT_ID;
        public static final String DATE_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + DATE;

        /* All column projection */
        public static final String[] EVENT_INVITATIONS_COLUMNS = {
                TABLE_EVENT_INVITATIONS + "."  +EventsInvitationEntry._ID,
                RECEIVER_ID_TABLE_PREFIX,
                SENDER_ID_TABLE_PREFIX,
                EVENT_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_RECEIVER_ID = 1;
        public static final int COLUMN_SENDER_ID = 2;
        public static final int COLUMN_EVENT_ID = 3;
        public static final int COLUMN_DATE = 4;

        /* Join for CONTENT_EVENT_INVITATIONS_WITH_USER_URI */
        public static final String TABLES_EVENT_INVITATIONS_JOIN_USER =
                TABLE_EVENT_INVITATIONS + " INNER JOIN " + TABLE_USER + " ON "
                    + EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /* Join for CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI */
        public static final String TABLES_EVENT_INVITATIONS_JOIN_EVENT =
                TABLE_EVENT_INVITATIONS + " INNER JOIN " + TABLE_EVENT + " ON "
                    + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " = " + EventEntry.EVENT_ID_TABLE_PREFIX;

        /* URI for one event invitation */
        public static Uri buildEventInvitationUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENT_INVITATIONS_URI, id);
        }
    }

    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_REQUESTS = "eventRequests";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_REQUESTS_WITH_USER = "eventRequests_user";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_REQUESTS_WITH_EVENT = "eventRequests_event";
    /* Used internally as the name of our event requests table. */
    public static final String TABLE_EVENTS_REQUESTS = "eventRequest";
    /* Inner class that defines the table contents of the eventRequest table
     * This table store a row for every event request from any user to one of our user events
     * and a row for every event request sent by our user*/
    public static final class EventRequestsEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the eventRequest table from the content provider */
        public static final Uri CONTENT_EVENTS_REQUESTS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_REQUESTS)
                .build();
        /* The base CONTENT_URI used to query the eventRequest table with user table from the content provider */
        public static final Uri CONTENT_EVENTS_REQUESTS_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_REQUESTS_WITH_USER)
                .build();
        /* The base CONTENT_URI used to query the eventRequest table with event table from the content provider */
        public static final Uri CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_REQUESTS_WITH_EVENT)
                .build();

        /* Column names */
        public static final String EVENT_ID = "eventId";
        public static final String SENDER_ID = "senderId";
        public static final String DATE = "date";

        /* Column names with table prefix */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENTS_REQUESTS + "." + EVENT_ID;
        public static final String SENDER_ID_TABLE_PREFIX = TABLE_EVENTS_REQUESTS + "." + SENDER_ID;
        public static final String DATE_TABLE_PREFIX = TABLE_EVENTS_REQUESTS + "." + DATE;

        /* All column projection */
        public static final String[] EVENTS_REQUESTS_COLUMNS = {
                TABLE_EVENTS_REQUESTS + "." + EventRequestsEntry._ID,
                EVENT_ID_TABLE_PREFIX,
                SENDER_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_EVENT_ID = 1;
        public static final int COLUMN_SENDER_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_EVENTS_REQUESTS_WITH_USER_URI */
        public static final String TABLES_EVENTS_REQUESTS_JOIN_USER =
                TABLE_EVENTS_REQUESTS + " INNER JOIN " + TABLE_USER + " ON "
                    + EventRequestsEntry.SENDER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;
        /* Join for CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI */
        public static final String TABLES_EVENTS_REQUESTS_JOIN_EVENT =
                TABLE_EVENTS_REQUESTS + " INNER JOIN " + TABLE_EVENT + " ON "
                        + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " = " + EventEntry.EVENT_ID_TABLE_PREFIX;

        /* URI for one event request */
        public static Uri buildEventRequestsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENTS_REQUESTS_URI, id);
        }
    }


    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND = "myEvent_friendUser";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME = "cityEvent_myUser";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME = "citySportEvent_myUser";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS = "friendsUser_myEvent";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_NOT_FRIENDS_USERS_FROM_CITY = "notFriendsCity_users";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_NOT_FRIENDS_USERS_WITH_NAME = "notFriendsName_users";
    public static final class JoinQueryEntries {
        /* The base CONTENT_URI used to query the event table for my events without
           relation with one particular friend from the content provider */
        // TODO: 08/07/2017 Incluir eventos en los que participo
        public static final Uri CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND)
                .build();
        /* JOIN for CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static final String TABLES_EVENTS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS =
                TABLE_EVENT
                    + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX
                        + " AND " + EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = ? )"
                    + " LEFT JOIN " + TABLE_EVENT_INVITATIONS + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX
                        + " AND " + EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX + " = ? )"
                    + " LEFT JOIN " + TABLE_EVENTS_REQUESTS + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX
                        + " AND " + EventRequestsEntry.SENDER_ID_TABLE_PREFIX + " = ? )";
        /* WHERE for CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static final String WHERE_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND =
                EventEntry.OWNER_TABLE_PREFIX + " = ? "
                    + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                    + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                    + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments fro JOIN and WHERE in CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static String[] queryMyEventsWithoutRelationWithFriendArguments(String ownerId, String friendId) {
            return new String[]{friendId, friendId, friendId, ownerId};
        }
        /* SELECT all columns from event table
         * FROM event
         *      LEFT JOIN eventsParticipation
         *          ON (event.eventId = eventsParticipation.eventId AND eventsParticipation.userId = ? )
         *      LEFT JOIN eventInvitations
         *          ON (event.eventId = eventInvitations.eventId AND eventInvitations.receiverId = ? )
         *      LEFT JOIN eventRequest
         *          ON (event.eventId = eventRequest.eventId AND eventRequest.senderId = ? )
         * WHERE (event.owner = ?
         *      AND eventsParticipation.eventId IS NULL
         *      AND eventInvitations.eventId IS NULL
         *      AND eventRequest.eventId IS NULL )
         * ORDER BY event.date ASC
         */


        /* The base CONTENT_URI used to query the event table for events
           in city without relation with me from the content provider */
        public static final Uri CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME)
                .build();
        /* JOIN for CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        /* Already defined TABLES_EVENTS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS */
        /* WHERE for CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        public static final String WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME =
                EventEntry.CITY_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.OWNER_TABLE_PREFIX + " <> ? "
                        + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments for JOIN and WHERE in CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        public static String[] queryCityEventsWithoutRelationWithMeArguments(String myUserId, String city) {
            return new String[]{myUserId, myUserId, myUserId, city, myUserId};
        }

        /* The base CONTENT_URI used to query the event table for events
           in city without relation with me AND for a sport from the content provider */
        public static final Uri CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME)
                .build();
        /* JOIN for CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        /* Already defined TABLES_EVENTS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS */
        /* WHERE for CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        public static final String WHERE_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME =
                EventEntry.CITY_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.SPORT_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.OWNER_TABLE_PREFIX + " <> ? "
                        + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments for JOIN and WHERE in CONTENT_CITY_SPORT_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        public static String[] queryCitySportEventsWithoutRelationWithMeArguments(String myUserId, String city, String sportId) {
            return new String[]{myUserId, myUserId, myUserId, city, sportId, myUserId};
        }


        /* The base CONTENT_URI used to query the user table for my friends without
           relation with one particular event from the content provider */
        public static final Uri CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS)
                .build();
        /* Join for CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI */
        public static final String TABLES_USERS_JOIN_FRIENDS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS =
                TABLE_USER
                        + " INNER JOIN " + TABLE_FRIENDS + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + FriendsEntry.USER_ID_TABLE_PREFIX + " )"
                        + " LEFT JOIN " + TABLE_EVENT + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + EventEntry.OWNER_TABLE_PREFIX
                        + " AND " + EventEntry.EVENT_ID_TABLE_PREFIX + " = ? )"
                        + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + EventsParticipationEntry.USER_ID_TABLE_PREFIX
                        + " AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " = ? )"
                        + " LEFT JOIN " + TABLE_EVENT_INVITATIONS + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX
                        + " AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " = ? )"
                        + " LEFT JOIN " + TABLE_EVENTS_REQUESTS + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + EventRequestsEntry.SENDER_ID_TABLE_PREFIX
                        + " AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " = ? )";
        /* WHERE for CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI */
        public static final String WHERE_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS =
                FriendsEntry.MY_USER_ID_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments fro JOIN and WHERE in CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static String[] queryMyFriendsWithoutRelationWithMyEventsArguments(String myUserId, String eventId) {
            return new String[]{eventId, eventId, eventId, eventId, myUserId};
        }
        /* SELECT all columns from users table
         * FROM user
         *      INNER JOIN friends
         *          ON (user.uid = friends.userId )
         *      LEFT JOIN eventsParticipation
         *          ON (user.uid = eventsParticipation.userId AND eventsParticipation.eventId = ? )
         *      LEFT JOIN eventInvitations
         *          ON (user.uid = eventInvitations.receiverId AND eventInvitations.eventId = ? )
         *      LEFT JOIN eventRequest
         *          ON (user.uid = eventRequest.senderId AND eventRequest.eventId = ? )
         * WHERE (friends.myUserId = ?
         *      AND eventsParticipation.eventId IS NULL
         *      AND eventInvitations.eventId IS NULL
         *      AND eventRequest.eventId IS NULL )
         * ORDER BY friends.date ASC
         */



        /* The base CONTENT_URI used to query the user table for not my friends
           in a particular city from the content provider */
        public static final Uri CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOT_FRIENDS_USERS_FROM_CITY)
                .build();
        /* Join for CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI */
        public static final String TABLES_USERS_JOIN_FRIENDS =
                TABLE_USER
                        + " LEFT JOIN " + TABLE_FRIENDS + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + FriendsEntry.USER_ID_TABLE_PREFIX
                        + " AND " + FriendsEntry.MY_USER_ID_TABLE_PREFIX + " = ? )";
        /* WHERE for CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI */
        public static final String WHERE_NOT_FRIENDS_USERS_FROM_CITY =
                UserEntry.CITY_TABLE_PREFIX + " = ? "
                        + "AND " + UserEntry.USER_ID_TABLE_PREFIX + " <> ? "
                        + "AND " + FriendsEntry.MY_USER_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments fro JOIN and WHERE in CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI */
        public static String[] queryNotFriendsUsersFromCityArguments(String myUserId, String city) {
            return new String[]{myUserId, city, myUserId};
        }
        /* SELECT all columns from users table
         * FROM user
         *      LEFT JOIN friends
         *          ON (user.uid = friend.userId AND friend.myuid = ? )
         * WHERE (user.city = ? AND user.userId <> ? AND friends.myUserId IS NULL )
         * ORDER BY user.name ASC
         */

        /* The base CONTENT_URI used to query the user table for not my friends
           with a particular name from the content provider */
        public static final Uri CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOT_FRIENDS_USERS_WITH_NAME)
                .build();
        /* Join for CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI */
        /* WHERE for CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI */
        public static final String WHERE_NOT_FRIENDS_USERS_WITH_NAME =
                UserEntry.NAME_TABLE_PREFIX + " LIKE ? "
                        + "AND " + UserEntry.USER_ID_TABLE_PREFIX + " <> ? "
                        + "AND " + FriendsEntry.MY_USER_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments fro JOIN and WHERE in CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI */
        public static String[] queryNotFriendsUsersWithNameArguments(String myUserId, String name) {
            return new String[]{myUserId, "%"+name+"%", myUserId};
        }
        /* SELECT all columns from users table
         * FROM user
         *      LEFT JOIN friends
         *          ON (user.uid = friend.userId AND friend.myuid = ? )
         * WHERE (user.name = ? AND user.userId <> ? AND friends.myUserId IS NULL )
         * ORDER BY user.name ASC
         */
    }

}

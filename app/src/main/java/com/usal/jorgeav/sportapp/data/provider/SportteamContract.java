package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

@SuppressWarnings({"unused", "WeakerAccess", "SpellCheckingInspection"})
public final class SportteamContract {

    /*
     * Name for the entire content provider. Is convenient to use for the
     * content authority the package name for the app.
     */
    public static final String CONTENT_AUTHORITY = "com.usal.jorgeav.sportapp";

    /* Use CONTENT_AUTHORITY to create the base of all URI's to contact the content provider. */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EMAIL_LOGGED = "email_logged";
    public static final String PATH_USERS = "users";
    public static final String PATH_USER_RELATION_USER = "user_relation_user";
    public static final String PATH_USER_RELATION_EVENT = "user_relation_event";
    public static final String PATH_USER_SPORT = "userSport";
    public static final String PATH_FIELDS = "fields";
    public static final String PATH_FIELDS_WITH_FIELD_SPORT = "fields_fieldSport";
    public static final String PATH_FIELD_SPORT = "fieldSport";
    public static final String PATH_ALARMS = "alarms";
    public static final String PATH_EVENTS = "events";
    public static final String PATH_EVENT_SIMULATED_PARTICIPANT = "eventSimulatedParticipant";
    public static final String PATH_FRIENDS_REQUESTS = "friendRequests";
    public static final String PATH_FRIENDS_REQUESTS_WITH_USER = "friendRequests_user";
    public static final String PATH_FRIENDS = "friends";
    public static final String PATH_FRIENDS_WITH_USER = "friends_user";
    public static final String PATH_EVENTS_PARTICIPATION = "eventsParticipation";
    public static final String PATH_EVENTS_PARTICIPATION_WITH_USER = "eventsParticipation_user";
    public static final String PATH_EVENTS_PARTICIPATION_WITH_EVENT = "eventsParticipation_event";
    public static final String PATH_EVENT_INVITATIONS = "eventInvitations";
    public static final String PATH_EVENT_INVITATIONS_WITH_USER = "eventInvitations_user";
    public static final String PATH_EVENT_INVITATIONS_WITH_EVENT = "eventInvitations_event";
    public static final String PATH_EVENTS_REQUESTS = "eventRequests";
    public static final String PATH_EVENTS_REQUESTS_WITH_USER = "eventRequests_user";
    public static final String PATH_EVENTS_REQUESTS_WITH_EVENT = "eventRequests_event";
    public static final String PATH_MY_EVENTS_AND_PARTICIPATION = "myEventAndParticipation";
    public static final String PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND = "myEvent_friendUser";
    public static final String PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME = "cityEvent_myUser";
    public static final String PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS = "friendsUser_myEvent";
    public static final String PATH_NOT_FRIENDS_USERS_FROM_CITY = "notFriendsCity_users";
    public static final String PATH_NOT_FRIENDS_USERS_WITH_NAME = "notFriendsName_users";

    /* Used internally as the name of email logged table. */
    public static final String TABLE_EMAIL_LOGGED = "email_logged";
    /* Inner class that defines the table contents of the email logged table */
    public static final class EmailLoggedEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the email logged table from the content provider */
        public static final Uri CONTENT_EMAIL_LOGGED_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EMAIL_LOGGED)
                .build();

        /* Column names */
        public static final String EMAIL = "email";

        /* Column names with table prefix*/
        public static final String EMAIL_TABLE_PREFIX = TABLE_EMAIL_LOGGED + "." + EMAIL;

        /* All column projection */
        public static final String[] EMAIL_LOGGED_COLUMNS = {
                TABLE_EMAIL_LOGGED + "." + EmailLoggedEntry._ID,
                EMAIL_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_EMAIL = 1;

        /* URI for one user */
        public static Uri buildUserUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EMAIL_LOGGED_URI, id);
        }
    }

    /* Used internally as the name of user table. */
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
        public static final String CITY_LATITUDE = "cityLatitude";
        public static final String CITY_LONGITUDE = "cityLongitude";
        public static final String PHOTO = "photo";

        /* Column names with table prefix*/
        public static final String USER_ID_TABLE_PREFIX = TABLE_USER + "." + USER_ID;
        public static final String EMAIL_TABLE_PREFIX = TABLE_USER + "." + EMAIL;
        public static final String NAME_TABLE_PREFIX = TABLE_USER + "." + NAME;
        public static final String AGE_TABLE_PREFIX = TABLE_USER + "." + AGE;
        public static final String CITY_TABLE_PREFIX = TABLE_USER + "." + CITY;
        public static final String CITY_LATITUDE_TABLE_PREFIX = TABLE_USER + "." + CITY_LATITUDE;
        public static final String CITY_LONGITUDE_TABLE_PREFIX = TABLE_USER + "." + CITY_LONGITUDE;
        public static final String PHOTO_TABLE_PREFIX = TABLE_USER + "." + PHOTO;

        /* All column projection */
        public static final String[] USER_COLUMNS = {
                TABLE_USER + "." + UserEntry._ID,
                USER_ID_TABLE_PREFIX,
                EMAIL_TABLE_PREFIX,
                NAME_TABLE_PREFIX,
                AGE_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                CITY_LATITUDE_TABLE_PREFIX,
                CITY_LONGITUDE_TABLE_PREFIX,
                PHOTO_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_USER_ID = 1;
        public static final int COLUMN_EMAIL = 2;
        public static final int COLUMN_NAME = 3;
        public static final int COLUMN_AGE = 4;
        public static final int COLUMN_CITY = 5;
        public static final int COLUMN_CITY_LATITUDE = 6;
        public static final int COLUMN_CITY_LONGITUDE = 7;
        public static final int COLUMN_PHOTO = 8;

        /* URI for one user */
        public static Uri buildUserUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_USER_URI, id);
        }
    }


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

    /* Used internally as the name of our field table. */
    public static final String TABLE_FIELD = "field";
    /* Inner class that defines the table contents of the field table */
    public static final class FieldEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the field table from the content provider */
        public static final Uri CONTENT_FIELD_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELDS)
                .build();
        /* The base CONTENT_URI used to query the field table with fieldSport table from the content provider */
        public static final Uri CONTENT_FIELDS_WITH_FIELD_SPORT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELDS_WITH_FIELD_SPORT)
                .build();

        /* Column names */
        public static final String FIELD_ID = "fieldId";
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String ADDRESS_LATITUDE = "addressLatitude";
        public static final String ADDRESS_LONGITUDE = "addressLongitude";
        public static final String CITY = "city";
        public static final String OPENING_TIME = "openingTime";
        public static final String CLOSING_TIME = "closingTime";
        public static final String CREATOR = "creator";

        /* Column names with table prefix*/
        public static final String FIELD_ID_TABLE_PREFIX = TABLE_FIELD + "." + FIELD_ID;
        public static final String NAME_TABLE_PREFIX = TABLE_FIELD + "." + NAME;
        public static final String ADDRESS_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS;
        public static final String ADDRESS_LATITUDE_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS_LATITUDE;
        public static final String ADDRESS_LONGITUDE_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS_LONGITUDE;
        public static final String CITY_TABLE_PREFIX = TABLE_FIELD + "." + CITY;
        public static final String OPENING_TIME_TABLE_PREFIX = TABLE_FIELD + "." + OPENING_TIME;
        public static final String CLOSING_TIME_TABLE_PREFIX = TABLE_FIELD + "." + CLOSING_TIME;
        public static final String CREATOR_TABLE_PREFIX = TABLE_FIELD + "." + CREATOR;

        /* All column projection */
        public static final String[] FIELDS_COLUMNS = {
                TABLE_FIELD + "." + FieldEntry._ID,
                FIELD_ID_TABLE_PREFIX,
                NAME_TABLE_PREFIX,
                ADDRESS_TABLE_PREFIX,
                ADDRESS_LATITUDE_TABLE_PREFIX,
                ADDRESS_LONGITUDE_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                OPENING_TIME_TABLE_PREFIX,
                CLOSING_TIME_TABLE_PREFIX,
                CREATOR_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_FIELD_ID = 1;
        public static final int COLUMN_NAME = 2;
        public static final int COLUMN_ADDRESS = 3;
        public static final int COLUMN_ADDRESS_LATITUDE = 4;
        public static final int COLUMN_ADDRESS_LONGITUDE = 5;
        public static final int COLUMN_CITY = 6;
        public static final int COLUMN_OPENING_TIME = 7;
        public static final int COLUMN_CLOSING_TIME = 8;
        public static final int COLUMN_CREATOR = 9;

        /* Join for CONTENT_FIELDS_WITH_FIELD_SPORT_URI */
        public static final String TABLES_FIELD_JOIN_FIELD_SPORT =
                TABLE_FIELD + " INNER JOIN " + TABLE_FIELD_SPORTS + " ON "
                        + FieldEntry.FIELD_ID_TABLE_PREFIX + " = " + FieldSportEntry.FIELD_ID_TABLE_PREFIX;

        /* URI for one field */
        public static Uri buildFieldUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FIELD_URI, id);
        }
    }

    /* Used internally as the name of our field sports table. */
    public static final String TABLE_FIELD_SPORTS = "fieldSport";
    /* Inner class that defines the table contents of the field sport table
     * This table store a row for every sport court in a Field in the field table */
    public static final class FieldSportEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the fieldSport table from the content provider */
        public static final Uri CONTENT_FIELD_SPORT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELD_SPORT)
                .build();

        /* Column names */
        public static final String FIELD_ID = "fieldId";
        public static final String SPORT = "sport";
        public static final String PUNCTUATION = "punctuation";
        public static final String VOTES = "votes";

        /* Column names with table prefix*/
        public static final String FIELD_ID_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + FIELD_ID;
        public static final String SPORT_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + SPORT;
        public static final String PUNCTUATION_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + PUNCTUATION;
        public static final String VOTES_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + VOTES;

        /* All column projection */
        public static final String[] FIELD_SPORT_COLUMNS = {
                TABLE_FIELD_SPORTS + "." + FieldSportEntry._ID,
                FIELD_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                PUNCTUATION_TABLE_PREFIX,
                VOTES_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_FIELD_ID = 1;
        public static final int COLUMN_SPORT = 2;
        public static final int COLUMN_PUNCTUATION = 3;
        public static final int COLUMN_VOTES = 4;

        /* URI for one fieldSport */
        public static Uri buildFieldSportUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FIELD_SPORT_URI, id);
        }
    }

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
        public static final String ADDRESS = "address";
        public static final String FIELD_LATITUDE = "fieldLatitude";
        public static final String FIELD_LONGITUDE = "fieldLongitude";
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
        public static final String ADDRESS_TABLE_PREFIX = TABLE_EVENT + "." + ADDRESS;
        public static final String FIELD_LATITUDE_TABLE_PREFIX = TABLE_EVENT + "." + FIELD_LATITUDE;
        public static final String FIELD_LONGITUDE_TABLE_PREFIX = TABLE_EVENT + "." + FIELD_LONGITUDE;
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
                ADDRESS_TABLE_PREFIX,
                FIELD_LATITUDE_TABLE_PREFIX,
                FIELD_LONGITUDE_TABLE_PREFIX,
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
        public static final int COLUMN_ADDRESS = 4;
        public static final int COLUMN_FIELD_LATITUDE = 5;
        public static final int COLUMN_FIELD_LONGITUDE = 6;
        public static final int COLUMN_NAME = 7;
        public static final int COLUMN_CITY = 8;
        public static final int COLUMN_DATE = 9;
        public static final int COLUMN_OWNER = 10;
        public static final int COLUMN_TOTAL_PLAYERS = 11;
        public static final int COLUMN_EMPTY_PLAYERS = 12;

        /* URI for one event */
        public static Uri buildEventUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENT_URI, id);
        }
    }

    /* Used internally as the name of the simulated participant table. */
    public static final String TABLE_EVENT_SIMULATED_PARTICIPANT = "eventSimulatedParticipant";
    /* Inner class that defines the table contents of the simulated participant table
     * This table store a row for every simulated participant in events */
    public static final class SimulatedParticipantEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the eventSimulatedParticipant table from the content provider */
        public static final Uri CONTENT_SIMULATED_PARTICIPANT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_SIMULATED_PARTICIPANT)
                .build();

        /* Column names */
        public static final String EVENT_ID = "eventId";
        public static final String SIMULATED_USER_ID = "simulatedUserId";
        public static final String ALIAS = "alias";
        public static final String PROFILE_PICTURE = "picture";
        public static final String AGE = "age";
        public static final String OWNER = "owner";

        /* Column names with table prefix*/
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + EVENT_ID;
        public static final String SIMULATED_USER_ID_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + SIMULATED_USER_ID;
        public static final String ALIAS_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + ALIAS;
        public static final String PROFILE_PICTURE_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + PROFILE_PICTURE;
        public static final String AGE_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + AGE;
        public static final String OWNER_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + OWNER;

        /* All column projection */
        public static final String[] SIMULATED_PARTICIPANTS_COLUMNS = {
                TABLE_EVENT_SIMULATED_PARTICIPANT + "." + SimulatedParticipantEntry._ID,
                EVENT_ID_TABLE_PREFIX,
                SIMULATED_USER_ID_TABLE_PREFIX,
                ALIAS_TABLE_PREFIX,
                PROFILE_PICTURE_TABLE_PREFIX,
                AGE_TABLE_PREFIX,
                OWNER_TABLE_PREFIX
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_EVENT_ID = 1;
        public static final int COLUMN_SIMULATED_USER_ID = 2;
        public static final int COLUMN_ALIAS = 3;
        public static final int COLUMN_PROFILE_PICTURE = 4;
        public static final int COLUMN_AGE = 5;
        public static final int COLUMN_OWNER = 6;

        /* URI for one eventSimulatedParticipant */
        public static Uri buildSimulatedParticipantUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_SIMULATED_PARTICIPANT_URI, id);
        }
    }

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

    /* Used internally as the name of our events participation table. */
    public static final String TABLE_EVENTS_PARTICIPATION = "eventsParticipation";
    /* Inner class that defines the table contents of the eventsParticipation table
     * This table store a row for every participation relation between an user and an event*/
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
                TABLE_EVENT + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " ON "
                    + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX;

        /* URI for one event participation */
        public static Uri buildEventsParticipationUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENTS_PARTICIPATION_URI, id);
        }
    }

    /* Used internally as the name of our event invitations table. */
    public static final String TABLE_EVENT_INVITATIONS = "eventInvitations";
    /* Inner class that defines the table contents of the eventInvitations table
     * This table store a row for every event invitation received or sent by the user*/
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
                TABLE_EVENT_INVITATIONS + "." + EventsInvitationEntry._ID,
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

    /* Used internally as the name of our event requests table. */
    public static final String TABLE_EVENTS_REQUESTS = "eventRequest";
    /* Inner class that defines the table contents of the eventRequest table
     * This table store a row for every event request from any user to one of our user's events
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



    /* Inner class that defines parameters for especially complex queries with multiple joins */
    public static final class JoinQueryEntries {
        /* The base CONTENT_URI used to query the event table looking for my events or my
           participation ones from the content provider */
        public static final Uri CONTENT_MY_EVENTS_AND_PARTICIPATION_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MY_EVENTS_AND_PARTICIPATION)
                .build();
        /* JOIN for CONTENT_MY_EVENTS_AND_PARTICIPATION_URI */
        public static final String TABLES_EVENTS_JOIN_PARTICIPATION =
                TABLE_EVENT
                        + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " )";
        /* WHERE for CONTENT_MY_EVENTS_AND_PARTICIPATION_URI */
        public static final String WHERE_MY_EVENTS_AND_PARTICIPATION =
                EventEntry.DATE_TABLE_PREFIX + " > ? "
                        + "AND ( " + EventEntry.OWNER_TABLE_PREFIX + " = ? "
                                   + "OR ( " + EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = ? "
                                             + "AND " + EventsParticipationEntry.PARTICIPATES_TABLE_PREFIX + " = 1 )) ";
        /* Arguments fro JOIN and WHERE in CONTENT_MY_EVENTS_AND_PARTICIPATION_URI */
        public static String[] queryMyEventsAndParticipationArguments(String ownerId) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            return new String[]{currentTime, ownerId, ownerId};
        }


        /* The base CONTENT_URI used to query the event table looking for my events or my
           participation ones without relation with one particular friend from the content provider */
        public static final Uri CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND)
                .build();
        /* JOIN for CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static final String TABLES_EVENTS_JOIN_PARTICIPATION_P_JOIN_INVITATIONS_JOIN_REQUESTS =
                TABLE_EVENT
                    + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " p1 ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = p1." + EventsParticipationEntry.EVENT_ID + " )"
                    + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " p2 ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = p2." + EventsParticipationEntry.EVENT_ID
                        + " AND p2." + EventsParticipationEntry.USER_ID + " = ? )"
                    + " LEFT JOIN " + TABLE_EVENT_INVITATIONS + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX
                        + " AND " + EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX + " = ? )"
                    + " LEFT JOIN " + TABLE_EVENTS_REQUESTS + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX
                        + " AND " + EventRequestsEntry.SENDER_ID_TABLE_PREFIX + " = ? )";
        /* WHERE for CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static final String WHERE_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND =
                EventEntry.DATE_TABLE_PREFIX + " > ? "
                    + "AND " + EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " > 0 "
                    + "AND ( " + EventEntry.OWNER_TABLE_PREFIX + " = ? OR p1." + EventsParticipationEntry.USER_ID + " = ? ) "
                    + "AND p2." + EventsParticipationEntry.EVENT_ID + " IS NULL "
                    + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                    + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments fro JOIN and WHERE in CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI */
        public static String[] queryMyEventsWithoutRelationWithFriendArguments(String ownerId, String friendId) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            return new String[]{friendId, friendId, friendId, currentTime, ownerId, ownerId};
        }
        /* SELECT "all columns in event table"
         * FROM event
         *      LEFT JOIN eventsParticipation p1
         *          ON (event.eventId = p1.eventId )
         *      LEFT JOIN eventsParticipation p2
         *          ON (event.eventId = p2.eventId AND p2.userId = friendId )
         *      LEFT JOIN eventInvitations
         *          ON (event.eventId = eventInvitations.eventId AND eventInvitations.receiverId = friendId )
         *      LEFT JOIN eventRequest
         *          ON (event.eventId = eventRequest.eventId AND eventRequest.senderId = friendId )
         * WHERE (
         *      event.date > currentTime
         *      AND event.emptyPlayers > 0
         *      AND ( event.owner = ownerId OR p1.userId = ownerId )
         *      AND p2.eventId IS NULL
         *      AND eventInvitations.eventId IS NULL
         *      AND eventRequest.eventId IS NULL )
         * ORDER BY event.date ASC
         */


        /* The base CONTENT_URI used to query the event table looking for events
           in city without relation with me from the content provider */
        public static final Uri CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME)
                .build();
        /* JOIN for CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
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
        /* WHERE for CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        public static final String WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME =
                EventEntry.DATE_TABLE_PREFIX + " > ? "
                        + "AND " + EventEntry.CITY_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.OWNER_TABLE_PREFIX + " <> ? "
                        + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";
        /* Arguments for JOIN and WHERE in CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI */
        public static String[] queryCityEventsWithoutRelationWithMeArguments(String myUserId, String city) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            return new String[]{myUserId, myUserId, myUserId, currentTime, city, myUserId};
        }


        /* The base CONTENT_URI used to query the user table looking for my friends without
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
        /* SELECT "all columns in users table"
         * FROM user
         *      INNER JOIN friends
         *          ON (user.uid = friends.userId )
         *      LEFT JOIN event
         *          ON (user.uid = event.owner AND event.eventId = eventId )
         *      LEFT JOIN eventsParticipation
         *          ON (user.uid = eventsParticipation.userId AND eventsParticipation.eventId = eventId )
         *      LEFT JOIN eventInvitations
         *          ON (user.uid = eventInvitations.receiverId AND eventInvitations.eventId = eventId )
         *      LEFT JOIN eventRequest
         *          ON (user.uid = eventRequest.senderId AND eventRequest.eventId = eventId )
         * WHERE (friends.myUserId = ?
         *      AND event.eventId IS NULL
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
        /* SELECT "all columns in users table"
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
         * WHERE (user.name LIKE ? AND user.userId <> ? AND friends.myUserId IS NULL )
         * ORDER BY user.name ASC
         */
    }

}

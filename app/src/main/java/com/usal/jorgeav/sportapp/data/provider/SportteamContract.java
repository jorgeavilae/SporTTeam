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
    /* Used internally as the name of our user table. */
    public static final String TABLE_USER = "user";
    /* Inner class that defines the table contents of the user table */
    public static final class UserEntry implements BaseColumns {

        /* The base CONTENT_URI used to query the user table from the content provider */
        public static final Uri CONTENT_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USERS)
                .build();

        /* Column names */
        public static final String USER_ID = "uid";
        public static final String EMAIL = "email";
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String CITY = "city";
        public static final String PHOTO = "photo";

        /* All column projection */
        public static final String[] USER_COLUMNS = {
                TABLE_USER+"."+UserEntry._ID,
                TABLE_USER+"."+UserEntry.USER_ID,
                TABLE_USER+"."+UserEntry.EMAIL,
                TABLE_USER+"."+UserEntry.NAME,
                TABLE_USER+"."+UserEntry.AGE,
                TABLE_USER+"."+UserEntry.CITY,
                TABLE_USER+"."+UserEntry.PHOTO
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

        /* All column projection */
        public static final String[] USER_SPORT_COLUMNS = {
                TABLE_USER_SPORTS+"."+UserSportEntry._ID,
                TABLE_USER_SPORTS+"."+UserSportEntry.USER_ID,
                TABLE_USER_SPORTS+"."+UserSportEntry.SPORT,
                TABLE_USER_SPORTS+"."+UserSportEntry.LEVEL,
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

        /* All column projection */
        public static final String[] FIELDS_COLUMNS = {
                TABLE_FIELD+"."+FieldEntry._ID,
                TABLE_FIELD+"."+FieldEntry.FIELD_ID,
                TABLE_FIELD+"."+FieldEntry.NAME,
                TABLE_FIELD+"."+FieldEntry.SPORT,
                TABLE_FIELD+"."+FieldEntry.ADDRESS,
                TABLE_FIELD+"."+FieldEntry.CITY,
                TABLE_FIELD+"."+FieldEntry.PUNCTUATION,
                TABLE_FIELD+"."+FieldEntry.VOTES,
                TABLE_FIELD+"."+FieldEntry.OPENING_TIME,
                TABLE_FIELD+"."+FieldEntry.CLOSING_TIME
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
        public static final String CITY = "city";
        public static final String DATE = "date";
        public static final String OWNER = "owner";
        public static final String TOTAL_PLAYERS = "totalPlayers";
        public static final String EMPTY_PLAYERS = "emptyPlayers";

        /* All column projection */
        public static final String[] EVENT_COLUMNS = {
                TABLE_EVENT+"."+EventEntry._ID,
                TABLE_EVENT+"."+EventEntry.EVENT_ID,
                TABLE_EVENT+"."+EventEntry.SPORT,
                TABLE_EVENT+"."+EventEntry.FIELD,
                TABLE_EVENT+"."+EventEntry.CITY,
                TABLE_EVENT+"."+EventEntry.DATE,
                TABLE_EVENT+"."+EventEntry.OWNER,
                TABLE_EVENT+"."+EventEntry.TOTAL_PLAYERS,
                TABLE_EVENT+"."+EventEntry.EMPTY_PLAYERS
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_EVENT_ID = 1;
        public static final int COLUMN_SPORT = 2;
        public static final int COLUMN_FIELD = 3;
        public static final int COLUMN_CITY = 4;
        public static final int COLUMN_DATE = 5;
        public static final int COLUMN_OWNER = 6;
        public static final int COLUMN_TOTAL_PLAYERS = 7;
        public static final int COLUMN_EMPTY_PLAYERS = 8;

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

        /* All column projection */
        public static final String[] FRIEND_REQUESTS_COLUMNS = {
                TABLE_FRIENDS_REQUESTS+"."+FriendRequestEntry._ID,
                TABLE_FRIENDS_REQUESTS+"."+FriendRequestEntry.RECEIVER_ID,
                TABLE_FRIENDS_REQUESTS+"."+FriendRequestEntry.SENDER_ID,
                TABLE_FRIENDS_REQUESTS+"."+FriendRequestEntry.DATE
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_RECEIVER_ID = 1;
        public static final int COLUMN_SENDER_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_FRIEND_REQUESTS_WITH_USER_URI */
        public static final String TABLES_FRIENDS_REQUESTS_JOIN_USER =
                TABLE_FRIENDS_REQUESTS + " INNER JOIN " + TABLE_USER + " ON "
                        + TABLE_FRIENDS_REQUESTS + "." + FriendRequestEntry.SENDER_ID
                        + " = "
                        + TABLE_USER + "." + UserEntry.USER_ID;

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

        /* All column projection */
        public static final String[] FRIENDS_COLUMNS = {
                TABLE_FRIENDS+"."+FriendsEntry._ID,
                TABLE_FRIENDS+"."+FriendsEntry.MY_USER_ID,
                TABLE_FRIENDS+"."+FriendsEntry.USER_ID,
                TABLE_FRIENDS+"."+FriendsEntry.DATE
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_MY_USER_ID = 1;
        public static final int COLUMN_USER_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_FRIEND_WITH_USER_URI */
        public static final String TABLES_FRIENDS_JOIN_USER =
                TABLE_FRIENDS + " INNER JOIN " + TABLE_USER + " ON "
                        + TABLE_FRIENDS + "." + FriendsEntry.USER_ID
                        + " = "
                        + TABLE_USER + "." + UserEntry.USER_ID;

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

        /* All column projection */
        public static final String[] EVENTS_PARTICIPATION_COLUMNS = {
                TABLE_EVENTS_PARTICIPATION+"."+EventsParticipationEntry._ID,
                TABLE_EVENTS_PARTICIPATION+"."+EventsParticipationEntry.USER_ID,
                TABLE_EVENTS_PARTICIPATION+"."+EventsParticipationEntry.EVENT_ID,
                TABLE_EVENTS_PARTICIPATION+"."+EventsParticipationEntry.PARTICIPATES
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_USER_ID = 1;
        public static final int COLUMN_EVENT_ID = 2;
        public static final int COLUMN_PARTICIPATES = 3;

        /* Join for CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI */
        public static final String TABLES_EVENTS_PARTICIPATION_JOIN_USER =
                TABLE_EVENTS_PARTICIPATION + " INNER JOIN " + TABLE_USER + " ON "
                        + TABLE_EVENTS_PARTICIPATION + "." + EventsParticipationEntry.USER_ID
                        + " = "
                        + TABLE_USER + "." + UserEntry.USER_ID;

        /* Join for CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI */
        public static final String TABLES_EVENTS_PARTICIPATION_JOIN_EVENT =
                TABLE_EVENTS_PARTICIPATION + " INNER JOIN " + TABLE_EVENT + " ON "
                        + TABLE_EVENTS_PARTICIPATION + "." + EventsParticipationEntry.EVENT_ID
                        + " = "
                        + TABLE_EVENT + "." + EventEntry.EVENT_ID;

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
        public static final String USER_ID = "userId";
        public static final String EVENT_ID = "eventId";
        public static final String DATE = "date";

        /* All column projection */
        public static final String[] EVENT_INVITATIONS_COLUMNS = {
                TABLE_EVENT_INVITATIONS+"."+EventsInvitationEntry._ID,
                TABLE_EVENT_INVITATIONS+"."+EventsInvitationEntry.USER_ID,
                TABLE_EVENT_INVITATIONS+"."+EventsInvitationEntry.EVENT_ID,
                TABLE_EVENT_INVITATIONS+"."+EventsInvitationEntry.DATE
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_USER_ID = 1;
        public static final int COLUMN_EVENT_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_EVENT_INVITATIONS_WITH_USER_URI */
        public static final String TABLES_EVENT_INVITATIONS_JOIN_USER =
                TABLE_EVENT_INVITATIONS + " INNER JOIN " + TABLE_USER + " ON "
                        + TABLE_EVENT_INVITATIONS + "." + EventsInvitationEntry.USER_ID
                        + " = "
                        + TABLE_USER + "." + UserEntry.USER_ID;

        /* Join for CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI */
        public static final String TABLES_EVENT_INVITATIONS_JOIN_EVENT =
                TABLE_EVENT_INVITATIONS + " INNER JOIN " + TABLE_EVENT + " ON "
                        + TABLE_EVENT_INVITATIONS + "." + EventsInvitationEntry.EVENT_ID
                        + " = "
                        + TABLE_EVENT + "." + EventEntry.EVENT_ID;

        /* URI for one event invitation */
        public static Uri buildEventInvitationUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENT_INVITATIONS_URI, id);
        }
    }

    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_REQUESTS = "eventRequests";
    /* Possible paths that can be appended to BASE_CONTENT_URI to form valid URI. */
    public static final String PATH_EVENTS_REQUESTS_WITH_USER = "eventRequests_user";
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

        /* Column names */
        public static final String EVENT_ID = "eventId";
        public static final String SENDER_ID = "senderId";
        public static final String DATE = "date";

        /* All column projection */
        public static final String[] EVENTS_REQUESTS_COLUMNS = {
                TABLE_EVENTS_REQUESTS+"."+EventRequestsEntry._ID,
                TABLE_EVENTS_REQUESTS+"."+EventRequestsEntry.EVENT_ID,
                TABLE_EVENTS_REQUESTS+"."+EventRequestsEntry.SENDER_ID,
                TABLE_EVENTS_REQUESTS+"."+EventRequestsEntry.DATE
        };

        /* Column indexes */
        public static final int COLUMN_ID = 0;
        public static final int COLUMN_EVENT_ID = 1;
        public static final int COLUMN_SENDER_ID = 2;
        public static final int COLUMN_DATE = 3;

        /* Join for CONTENT_EVENTS_REQUESTS_WITH_USER_URI */
        public static final String TABLES_EVENTS_REQUESTS_JOIN_USER =
                TABLE_EVENTS_REQUESTS + " INNER JOIN " + TABLE_USER + " ON "
                        + TABLE_EVENTS_REQUESTS + "." + EventRequestsEntry.SENDER_ID
                        + " = "
                        + TABLE_USER + "." + UserEntry.USER_ID;

        /* URI for one event request */
        public static Uri buildEventRequestsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENTS_REQUESTS_URI, id);
        }
    }
}

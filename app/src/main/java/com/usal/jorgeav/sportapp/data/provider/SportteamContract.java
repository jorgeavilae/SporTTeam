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
            UserEntry._ID,
            UserEntry.USER_ID,
            UserEntry.EMAIL,
            UserEntry.NAME,
            UserEntry.AGE,
            UserEntry.CITY,
            UserEntry.PHOTO
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
                UserSportEntry._ID,
                UserSportEntry.USER_ID,
                UserSportEntry.SPORT,
                UserSportEntry.LEVEL,
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
                EventEntry._ID,
                EventEntry.EVENT_ID,
                EventEntry.SPORT,
                EventEntry.FIELD,
                EventEntry.CITY,
                EventEntry.DATE,
                EventEntry.OWNER,
                EventEntry.TOTAL_PLAYERS,
                EventEntry.EMPTY_PLAYERS
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
                FieldEntry._ID,
                FieldEntry.FIELD_ID,
                FieldEntry.NAME,
                FieldEntry.SPORT,
                FieldEntry.ADDRESS,
                FieldEntry.CITY,
                FieldEntry.PUNCTUATION,
                FieldEntry.VOTES,
                FieldEntry.OPENING_TIME,
                FieldEntry.CLOSING_TIME
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
}

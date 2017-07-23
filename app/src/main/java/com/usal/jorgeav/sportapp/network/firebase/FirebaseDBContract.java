package com.usal.jorgeav.sportapp.network.firebase;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jorge Avila on 19/05/2017.
 */

public final class FirebaseDBContract {
    public static final String DATA = "data";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_FIELDS = "fields";
    public static final String TABLE_EVENTS = "events";

    public static final class User {
        public static final String ALIAS = "alias";
        public static final String EMAIL = "email";
        public static final String AGE = "age";
        public static final String PROFILE_PICTURE = "profile_picture";
        public static final String CITY = "city";
        public static final String COORD_LATITUDE = "coord_latitude";
        public static final String COORD_LONGITUDE = "coord_longitude";
        public static final String SPORTS_PRACTICED = "sports_practiced";
        public static final String FRIENDS = "friends";
        public static final String FRIENDS_REQUESTS_SENT = "friends_requests_sent";
        public static final String FRIENDS_REQUESTS_RECEIVED = "friends_requests_received";
        public static final String EVENTS_CREATED = "events_created";
        public static final String EVENTS_PARTICIPATION = "events_participation";
        public static final String EVENTS_INVITATIONS_SENT = "events_invitations_sent";
        public static final String EVENTS_INVITATIONS_RECEIVED = "events_invitations_received";
        public static final String EVENTS_REQUESTS = "events_requests";
        public static final String ALARMS = "alarms";
        public static final String NOTIFICATIONS = "notifications";
    }

    public static final class Event {
        public static final String SPORT = "sport_id";
        public static final String FIELD = "field_id";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String COORD_LATITUDE = "coord_latitude";
        public static final String COORD_LONGITUDE = "coord_longitude";
        public static final String NAME = "name";
        public static final String DATE = "date";
        public static final String TOTAL_PLAYERS = "total_players";
        public static final String EMPTY_PLAYERS = "empty_players";
        public static final String OWNER = "owner";
        public static final String PARTICIPANTS = "participants";
        public static final String SIMULATED_PARTICIPANTS = "simulated_participants";
        public static final String INVITATIONS = "invitations_sent";
        public static final String USER_REQUESTS = "user_requests";
    }

    public static final class Invitation {
        public static final String SENDER = "sender";
        public static final String RECEIVER = "receiver";
        public static final String EVENT = "event";
        public static final String DATE = "date";
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOTIFICATION_TYPE_ERROR, NOTIFICATION_TYPE_USER,
            NOTIFICATION_TYPE_EVENT, NOTIFICATION_TYPE_ALARM,
            NOTIFICATION_TYPE_NONE})
    public @interface NotificationDataTypes {}
    public static final int NOTIFICATION_TYPE_ERROR = -1;
    public static final int NOTIFICATION_TYPE_NONE = 0;
    public static final int NOTIFICATION_TYPE_USER = 1;
    public static final int NOTIFICATION_TYPE_EVENT = 2;
    public static final int NOTIFICATION_TYPE_ALARM = 3;
    public static final class Notification {
        public static final String NOTIFICATION_TYPE = "notification_type";
        public static final String CHECKED = "checked";
        public static final String TITLE = "title";
        public static final String MESSAGE = "message";
        public static final String EXTRA_DATA_ONE = "extra_data_one";
        public static final String EXTRA_DATA_TWO = "extra_data_two";
        public static final String DATA_TYPE = "data_type";
        public static final String DATE = "date";
    }

    public static final class Alarm {
        public static final String SPORT = "sport_id";
        public static final String FIELD = "field_id";
        public static final String CITY = "city";
        public static final String DATE_FROM = "date_from";
        public static final String DATE_TO = "date_to";
        public static final String TOTAL_PLAYERS_FROM = "total_players_from";
        public static final String TOTAL_PLAYERS_TO = "total_players_to";
        public static final String EMPTY_PLAYERS_FROM = "empty_players_from";
        public static final String EMPTY_PLAYERS_TO = "empty_players_to";
    }

    public static final class Field {
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String COORD_LATITUDE = "coord_latitude";
        public static final String COORD_LONGITUDE = "coord_longitude";
        public static final String CITY = "city";
        public static final String OPENING_TIME = "opening_time";
        public static final String CLOSING_TIME = "closing_time";
        public static final String SPORT = "sport";
        public static final String NEXT_EVENTS = "next_events";
        public static final String CREATOR = "creator";
    }

    public static final class SportCourt {
        public static final String SPORT_ID = "sport_id";
        public static final String PUNCTUATION = "punctuation";
        public static final String VOTES = "votes";
    }

    public static final class Storage {
        /* https://stackoverflow.com/questions/38779713/how-to-store-direct-link-to-an-image-using-firebase-storage */
        public static final String PROFILE_PICTURES = "profile_picture";
    }
}

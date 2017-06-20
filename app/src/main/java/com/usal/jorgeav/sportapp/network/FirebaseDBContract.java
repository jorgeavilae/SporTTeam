package com.usal.jorgeav.sportapp.network;

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
        public static final String SPORTS_PRACTICED = "sports_practiced";
        public static final String FRIENDS = "friends";
        public static final String FRIENDS_REQUESTS_SENT = "friends_requests_sent";
        public static final String FRIENDS_REQUESTS_RECEIVED = "friends_requests_received";
        public static final String EVENTS_CREATED = "events_created";
        public static final String EVENTS_PARTICIPATION = "events_participation";
        public static final String EVENTS_INVITATIONS = "events_invitations_received";
        public static final String EVENTS_REQUESTS = "events_requests";
        public static final String ALARMS = "alarms";
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

    public static final class Event {
        public static final String SPORT = "sport_id";
        public static final String FIELD = "field_id";
        public static final String CITY = "city";
        public static final String NAME = "name";
        public static final String DATE = "date";
        public static final String TOTAL_PLAYERS = "total_players";
        public static final String EMPTY_PLAYERS = "empty_players";
        public static final String OWNER = "owner";
        public static final String PARTICIPANTS = "participants";
        public static final String INVITATIONS = "invitations_sent";
        public static final String USER_REQUESTS = "user_requests";
    }

    public static final class Field {
        public static final String NAME = "name";
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String OPENING_TIME = "opening_time";
        public static final String CLOSING_TIME = "closing_time";
        public static final String SPORTS = "sports";
        public static final String PUNCTUATION = "punctuation";
        public static final String VOTES = "votes";
        public static final String NEXT_EVENTS = "next_events";
    }

    public static final class Storage {
        public static final String PROFILE_PICTURES = "profile_picture";
    }
}

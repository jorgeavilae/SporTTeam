package com.usal.jorgeav.sportapp.network.firebase;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Esta clase actúa como diccionario de claves para las etiquetas que se utilizan en la base de
 * datos de Firebase Realtime Database. Como es una base de datos de tipo clave/valor basada en un
 * documento de tipo JSON, cada dato almacenado debe ir acompañado de una etiqueta. Esas
 * etiquetas se listan aquí como constantes públicas para que sean accesibles y consistentes a los
 * largo de todas las clases de la aplicación.
 *
 * @see <a href= "https://firebase.google.com/docs/database/">Firebase Realtime Database</a>
 */
@SuppressWarnings("WeakerAccess")
public final class FirebaseDBContract {
    /**
     * Etiqueta para el conjunto de datos de alguno de los objetos de la base de datos
     */
    public static final String DATA = "data";
    /**
     * Etiqueta para la rama de usuarios
     */
    public static final String TABLE_USERS = "users";
    /**
     * Etiqueta para la rama de instalaciones
     */
    public static final String TABLE_FIELDS = "fields";
    /**
     * Etiqueta para la rama de partidos
     */
    public static final String TABLE_EVENTS = "events";
    /**
     * Etiqueta para la rama de token de aplicaciones cliente donde cada usuario tiene iniciada la
     * sesión
     */
    public static final String TABLE_TOKENS = "tokens";

    /**
     * Colección para las etiquetas relacionadas con los usuarios
     */
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

    /**
     * Colección para las etiquetas relacionadas con los partidos
     */
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

    /**
     * Colección para las etiquetas relacionadas con las invitaciones
     */
    public static final class Invitation {
        public static final String SENDER = "sender";
        public static final String RECEIVER = "receiver";
        public static final String EVENT = "event";
        public static final String DATE = "date";
    }

    /**
     * Modificador que, aplicado a una variable, le permite adquirir como valor solamente el
     * siguiente conjunto de constantes que representan los distintos tipos de objetos que pueden
     * adjuntarse a una notificación.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NOTIFICATION_TYPE_ERROR, NOTIFICATION_TYPE_USER,
            NOTIFICATION_TYPE_EVENT, NOTIFICATION_TYPE_ALARM,
            NOTIFICATION_TYPE_NONE})
    public @interface NotificationDataTypes {
    }

    /**
     * Error
     */
    public static final int NOTIFICATION_TYPE_ERROR = -1;
    /**
     * No hay ningún objeto adjunto a la notificación
     */
    public static final int NOTIFICATION_TYPE_NONE = 0;
    /**
     * Hay un identificador de usuario adjunto a la notificación
     */
    public static final int NOTIFICATION_TYPE_USER = 1;
    /**
     * Hay un identificador de partido adjunto a la notificación
     */
    public static final int NOTIFICATION_TYPE_EVENT = 2;
    /**
     * Hay un identificador de alarma y otro de partido que coincide con la alarma, adjuntos a la
     * notificación
     */
    public static final int NOTIFICATION_TYPE_ALARM = 3;

    /**
     * Colección para las etiquetas relacionadas con las notificaciones
     */
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

    /**
     * Colección para las etiquetas relacionadas con las alarmas
     */
    public static final class Alarm {
        public static final String SPORT = "sport_id";
        public static final String FIELD = "field_id";
        public static final String CITY = "city";
        public static final String COORD_LATITUDE = "coord_latitude";
        public static final String COORD_LONGITUDE = "coord_longitude";
        public static final String DATE_FROM = "date_from";
        public static final String DATE_TO = "date_to";
        public static final String TOTAL_PLAYERS_FROM = "total_players_from";
        public static final String TOTAL_PLAYERS_TO = "total_players_to";
        public static final String EMPTY_PLAYERS_FROM = "empty_players_from";
        public static final String EMPTY_PLAYERS_TO = "empty_players_to";
    }

    /**
     * Colección para las etiquetas relacionadas con las instalaciones
     */
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

    /**
     * Colección para las etiquetas relacionadas con las pistas de las instalaciones
     */
    public static final class SportCourt {
        public static final String SPORT_ID = "sport_id";
        public static final String PUNCTUATION = "punctuation";
        public static final String VOTES = "votes";
    }

    /**
     * Colección para las etiquetas relacionadas con Firebase Storage
     */
    public static final class Storage {
        /**
         * Nombre de la carpeta de Firebase Storage donde se guardan las fotos de perfil de los
         * usuarios y de los usuarios simulados
         */
        public static final String PROFILE_PICTURES = "profile_picture";
    }
}

package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Esta clase actúa como diccionario de nombres de los que se utilizan en la base de
 * datos del Proveedor de Contenido. Almacena como constantes globales públicas los nombres
 * utilizados en las rutas de acceso, las tablas y las columnas, además de construir las direcciones
 * completas de acceso {@link Uri} y las cláusulas JOIN y WHERE de SQL necesarias para las consultas
 * más complejas.
 * <p>
 * Cada tabla está representada por una clase interna a esta, que contiene: las {@link Uri} de
 * acceso (para esa tabla y para JOINs con esa tabla), los nombres de las columnas, los nombres de
 * las columnas con el nombre de la tabla como prefijo, el array de todas las columnas (para
 * proyectar todas las columnas en las consultas), el índice de las columnas, las posibles
 * sentencias JOIN en las que se pueda usar esa tabla y un método para construir una {@link Uri}
 * hacia una tupla de una tabla dado su identificador de {@link BaseColumns}.
 * <p>
 * Todas estas clase heredan de {@link BaseColumns} para obtener de ella un identificador único
 * para cada tupla y un contador.
 * <p>
 * Por último, se incluye una clase {@link JoinQueryEntries} con las constantes necesarias para
 * construir las sentencias de consulta más complejas que incluyen para cada una: la {@link Uri}
 * para realizar la consulta, las tablas utilizadas unidas por JOIN, la selección de columnas
 * necesarias del WHERE, y un método para devolver en forma de array los argumentos que necesita
 * la consulta completa.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class SportteamContract {
    /**
     * Nombre para el Proveedor de Contenido. Es conveniente usar el mismo que el del paquete de la
     * aplicación. Está declarado en archivo manifest (AndroidManifest.xml)
     */
    public static final String CONTENT_AUTHORITY = "com.usal.jorgeav.sportapp";

    /**
     * Usa {@link #CONTENT_AUTHORITY} para crear la Uri base para acceder a la base de datos
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de email de usuarios que
     * han iniciado sesión correctamente
     */
    public static final String PATH_EMAIL_LOGGED = "email_logged";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de usuarios
     */
    public static final String PATH_USERS = "users";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la relación entre dos usuarios
     */
    public static final String PATH_USER_RELATION_USER = "user_relation_user";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la relación entre un usuario y un partido
     */
    public static final String PATH_USER_RELATION_EVENT = "user_relation_event";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de los deportes practicados
     * por usuarios
     */
    public static final String PATH_USER_SPORT = "userSport";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de instalaciones
     */
    public static final String PATH_FIELDS = "fields";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de instalaciones
     * y de pistas de instalaciones
     */
    public static final String PATH_FIELDS_WITH_FIELD_SPORT = "fields_fieldSport";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de pistas de instalaciones
     */
    public static final String PATH_FIELD_SPORT = "fieldSport";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de alarmas
     */
    public static final String PATH_ALARMS = "alarms";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de partidos
     */
    public static final String PATH_EVENTS = "events";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de usuario simulados
     */
    public static final String PATH_EVENT_SIMULATED_PARTICIPANT = "eventSimulatedParticipant";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de peticiones de amistad
     */
    public static final String PATH_FRIENDS_REQUESTS = "friendRequests";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de peticiones de
     * amistad y de usuarios
     */
    public static final String PATH_FRIENDS_REQUESTS_WITH_USER = "friendRequests_user";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de amigos
     */
    public static final String PATH_FRIENDS = "friends";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de amigos y de
     * usuarios
     */
    public static final String PATH_FRIENDS_WITH_USER = "friends_user";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de participaciones
     */
    public static final String PATH_EVENTS_PARTICIPATION = "eventsParticipation";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de participaciones
     * y de usuarios
     */
    public static final String PATH_EVENTS_PARTICIPATION_WITH_USER = "eventsParticipation_user";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de participaciones
     * y de partidos
     */
    public static final String PATH_EVENTS_PARTICIPATION_WITH_EVENT = "eventsParticipation_event";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de invitaciones
     */
    public static final String PATH_EVENT_INVITATIONS = "eventInvitations";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de invitaciones
     * y de usuarios
     */
    public static final String PATH_EVENT_INVITATIONS_WITH_USER = "eventInvitations_user";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de invitaciones
     * y de partidos
     */
    public static final String PATH_EVENT_INVITATIONS_WITH_EVENT = "eventInvitations_event";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la tabla de peticiones de participación
     */
    public static final String PATH_EVENTS_REQUESTS = "eventRequests";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de peticiones
     * de participación y de usuarios
     */
    public static final String PATH_EVENTS_REQUESTS_WITH_USER = "eventRequests_user";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la unión de las tablas de peticiones
     * de participación y de partidos
     */
    public static final String PATH_EVENTS_REQUESTS_WITH_EVENT = "eventRequests_event";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la consulta de partidos del usuarios a
     * varias tablas unidas por JOINs
     */
    public static final String PATH_MY_EVENTS_AND_PARTICIPATION = "myEventAndParticipation";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la consulta de partidos del usuarios sin
     * relación con un amigo a varias tablas unidas por JOINs
     */
    public static final String PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND = "myEvent_friendUser";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la consulta de partidos de la ciudad sin
     * relación con un usuario a varias tablas unidas por JOINs
     */
    public static final String PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME = "cityEvent_myUser";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la consulta de amigos sin relación con
     * uno de los partidos del usuario a varias tablas unidas por JOINs
     */
    public static final String PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS = "friendsUser_myEvent";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la consulta de usuarios de una ciudad
     * sin relación con el usuario a varias tablas unidas por JOINs
     */
    public static final String PATH_NOT_FRIENDS_USERS_FROM_CITY = "notFriendsCity_users";
    /**
     * Ruta que se añade a {@link #BASE_CONTENT_URI} para la consulta de usuarios dado un nombre y
     * sin relación con el usuario a varias tablas unidas por JOINs
     */
    public static final String PATH_NOT_FRIENDS_USERS_WITH_NAME = "notFriendsName_users";

    /**
     * Nombre de la tabla de emails de usuarios que han iniciado sesión
     */
    public static final String TABLE_EMAIL_LOGGED = "email_logged";

    /**
     * Clase con las constantes relativas a la tabla de emails de usuarios que han iniciado sesión
     */
    public static final class EmailLoggedEntry implements BaseColumns {

        /**
         * Uri para la tabla de email de usuarios que han iniciado sesión correctamente
         */
        public static final Uri CONTENT_EMAIL_LOGGED_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EMAIL_LOGGED)
                .build();

        /**
         * Nombre de la columna del email
         */
        public static final String EMAIL = "email";

        /**
         * Nombre de la columna del email con el nombre de la tabla como prefijo
         */
        public static final String EMAIL_TABLE_PREFIX = TABLE_EMAIL_LOGGED + "." + EMAIL;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] EMAIL_LOGGED_COLUMNS = {
                TABLE_EMAIL_LOGGED + "." + EmailLoggedEntry._ID,
                EMAIL_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del email
         */
        public static final int COLUMN_EMAIL = 1;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildUserUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EMAIL_LOGGED_URI, id);
        }
    }

    /**
     * Nombre de la tabla de usuarios
     */
    public static final String TABLE_USER = "user";

    /**
     * Clase con las constantes relativas a la tabla de usuarios
     */
    public static final class UserEntry implements BaseColumns {

        /**
         * Uri para la tabla de usuarios
         */
        public static final Uri CONTENT_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USERS)
                .build();
        /**
         * Uri para la relación entre dos usuarios
         */
        public static final Uri CONTENT_USER_RELATION_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_RELATION_USER)
                .build();
        /**
         * Uri para la relación entre un usuario y un partido
         */
        public static final Uri CONTENT_USER_RELATION_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_RELATION_EVENT)
                .build();

        /**
         * Nombre de la columna del identificador de usuario
         */
        public static final String USER_ID = "uid";
        /**
         * Nombre de la columna del email de usuario
         */
        public static final String EMAIL = "email";
        /**
         * Nombre de la columna del nombre de usuario
         */
        public static final String NAME = "name";
        /**
         * Nombre de la columna de la edad del usuario
         */
        public static final String AGE = "age";
        /**
         * Nombre de la columna de la ciudad del usuario
         */
        public static final String CITY = "city";
        /**
         * Nombre de la columna de la componente latitud de la ciudad del usuario
         */
        public static final String CITY_LATITUDE = "cityLatitude";
        /**
         * Nombre de la columna de la componente longitud de la ciudad del usuario
         */
        public static final String CITY_LONGITUDE = "cityLongitude";
        /**
         * Nombre de la columna de la foto de perfil del usuario
         */
        public static final String PHOTO = "photo";


        /**
         * Nombre de la columna del identificador de usuario con el nombre de la tabla como prefijo
         */
        public static final String USER_ID_TABLE_PREFIX = TABLE_USER + "." + USER_ID;
        /**
         * Nombre de la columna del email de usuario con el nombre de la tabla como prefijo
         */
        public static final String EMAIL_TABLE_PREFIX = TABLE_USER + "." + EMAIL;
        /**
         * Nombre de la columna del nombre de usuario con el nombre de la tabla como prefijo
         */
        public static final String NAME_TABLE_PREFIX = TABLE_USER + "." + NAME;
        /**
         * Nombre de la columna de la edad del usuario con el nombre de la tabla como prefijo
         */
        public static final String AGE_TABLE_PREFIX = TABLE_USER + "." + AGE;
        /**
         * Nombre de la columna de la ciudad del usuario con el nombre de la tabla como prefijo
         */
        public static final String CITY_TABLE_PREFIX = TABLE_USER + "." + CITY;
        /**
         * Nombre de la columna de la componente latitud de la ciudad del usuario con el nombre
         * de la tabla como prefijo
         */
        public static final String CITY_LATITUDE_TABLE_PREFIX = TABLE_USER + "." + CITY_LATITUDE;
        /**
         * Nombre de la columna de la componente longitud de la ciudad del usuario con el nombre
         * de la tabla como prefijo
         */
        public static final String CITY_LONGITUDE_TABLE_PREFIX = TABLE_USER + "." + CITY_LONGITUDE;
        /**
         * Nombre de la columna de la foto de perfil del usuario con el nombre de la tabla
         * como prefijo
         */
        public static final String PHOTO_TABLE_PREFIX = TABLE_USER + "." + PHOTO;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
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

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador de usuario
         */
        public static final int COLUMN_USER_ID = 1;
        /**
         * Índice de la columna del email de usuario
         */
        public static final int COLUMN_EMAIL = 2;
        /**
         * Índice de la columna del nombre de usuario
         */
        public static final int COLUMN_NAME = 3;
        /**
         * Índice de la columna de la edad del usuario
         */
        public static final int COLUMN_AGE = 4;
        /**
         * Índice de la columna de la ciudad del usuario
         */
        public static final int COLUMN_CITY = 5;
        /**
         * Índice de la columna de la componente latitud de la ciudad del usuario
         */
        public static final int COLUMN_CITY_LATITUDE = 6;
        /**
         * Índice de la columna de la componente longitud de la ciudad del usuario
         */
        public static final int COLUMN_CITY_LONGITUDE = 7;
        /**
         * Índice de la columna de la foto de perfil del usuario
         */
        public static final int COLUMN_PHOTO = 8;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildUserUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_USER_URI, id);
        }
    }

    /**
     * Nombre de la tabla de deportes practicados por usuarios
     */
    public static final String TABLE_USER_SPORTS = "userSport";

    /**
     * Clase con las constantes relativas a la tabla de deportes practicados por usuarios. Contiene
     * una tupla por cada deporte practicado por cada usuario de la tabla de usuarios.
     */
    public static final class UserSportEntry implements BaseColumns {

        /**
         * Uri para la tabla de los deportes practicados por usuarios
         */
        public static final Uri CONTENT_USER_SPORT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER_SPORT)
                .build();

        /**
         * Nombre de la columna del identificador de usuario
         */
        public static final String USER_ID = "uid";
        /**
         * Nombre de la columna del identificador del deporte
         */
        public static final String SPORT = "sport";
        /**
         * Nombre de la columna del nivel de juego
         */
        public static final String LEVEL = "level";

        /**
         * Nombre de la columna del identificador de usuario con el nombre de la tabla como prefijo
         */
        public static final String USER_ID_TABLE_PREFIX = TABLE_USER_SPORTS + "." + USER_ID;
        /**
         * Nombre de la columna del identificador del deporte con el nombre de la tabla como prefijo
         */
        public static final String SPORT_TABLE_PREFIX = TABLE_USER_SPORTS + "." + SPORT;
        /**
         * Nombre de la columna del nivel de juego con el nombre de la tabla como prefijo
         */
        public static final String LEVEL_TABLE_PREFIX = TABLE_USER_SPORTS + "." + LEVEL;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] USER_SPORT_COLUMNS = {
                TABLE_USER_SPORTS + "." + UserSportEntry._ID,
                USER_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                LEVEL_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador de usuario
         */
        public static final int COLUMN_USER_ID = 1;
        /**
         * Índice de la columna del identificador del deporte
         */
        public static final int COLUMN_SPORT = 2;
        /**
         * Índice de la columna del nivel de juego
         */
        public static final int COLUMN_LEVEL = 3;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildUserSportUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_USER_SPORT_URI, id);
        }
    }

    /**
     * Nombre de la tabla de instalaciones
     */
    public static final String TABLE_FIELD = "field";

    /**
     * Clase con las constantes relativas a la tabla de instalaciones
     */
    public static final class FieldEntry implements BaseColumns {

        /**
         * Uri para la tabla de instalaciones
         */
        public static final Uri CONTENT_FIELD_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELDS)
                .build();
        /**
         * Uri para la unión de las tablas de instalaciones y de pistas de instalaciones
         */
        public static final Uri CONTENT_FIELDS_WITH_FIELD_SPORT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELDS_WITH_FIELD_SPORT)
                .build();

        /**
         * Nombre de la columna del identificador de la instalación
         */
        public static final String FIELD_ID = "fieldId";
        /**
         * Nombre de la columna del nombre de la instalación
         */
        public static final String NAME = "name";
        /**
         * Nombre de la columna de la dirección de la instalación
         */
        public static final String ADDRESS = "address";
        /**
         * Nombre de la columna de la componente latitud de las coordenadas de la dirección
         */
        public static final String ADDRESS_LATITUDE = "addressLatitude";
        /**
         * Nombre de la columna de la componente longitud de las coordenadas de la dirección
         */
        public static final String ADDRESS_LONGITUDE = "addressLongitude";
        /**
         * Nombre de la columna de la ciudad de la instalación
         */
        public static final String CITY = "city";
        /**
         * Nombre de la columna de la hora de apertura
         */
        public static final String OPENING_TIME = "openingTime";
        /**
         * Nombre de la columna de la hora de cierre
         */
        public static final String CLOSING_TIME = "closingTime";
        /**
         * Nombre de la columna del identificador del usuario creador
         */
        public static final String CREATOR = "creator";

        /**
         * Nombre de la columna del identificador de la instalación con el nombre de la tabla
         * como prefijo
         */
        public static final String FIELD_ID_TABLE_PREFIX = TABLE_FIELD + "." + FIELD_ID;
        /**
         * Nombre de la columna del nombre de la instalación con el nombre de la tabla como prefijo
         */
        public static final String NAME_TABLE_PREFIX = TABLE_FIELD + "." + NAME;
        /**
         * Nombre de la columna de la dirección de la instalación con el nombre de la tabla
         * como prefijo
         */
        public static final String ADDRESS_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS;
        /**
         * Nombre de la columna de la componente latitud de las coordenadas de la dirección con el
         * nombre de la tabla como prefijo
         */
        public static final String ADDRESS_LATITUDE_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS_LATITUDE;
        /**
         * Nombre de la columna de la componente longitud de las coordenadas de la dirección con el
         * nombre de la tabla como prefijo
         */
        public static final String ADDRESS_LONGITUDE_TABLE_PREFIX = TABLE_FIELD + "." + ADDRESS_LONGITUDE;
        /**
         * Nombre de la columna de la ciudad de la instalación con el nombre de la tabla como prefijo
         */
        public static final String CITY_TABLE_PREFIX = TABLE_FIELD + "." + CITY;
        /**
         * Nombre de la columna de la hora de apertura con el nombre de la tabla como prefijo
         */
        public static final String OPENING_TIME_TABLE_PREFIX = TABLE_FIELD + "." + OPENING_TIME;
        /**
         * Nombre de la columna de la hora de cierre con el nombre de la tabla como prefijo
         */
        public static final String CLOSING_TIME_TABLE_PREFIX = TABLE_FIELD + "." + CLOSING_TIME;
        /**
         * Nombre de la columna del identificador del usuario creador con el nombre de la tabla
         * como prefijo
         */
        public static final String CREATOR_TABLE_PREFIX = TABLE_FIELD + "." + CREATOR;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
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

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador de la instalación
         */
        public static final int COLUMN_FIELD_ID = 1;
        /**
         * Índice de la columna del nombre de la instalación
         */
        public static final int COLUMN_NAME = 2;
        /**
         * Índice de la columna de la dirección de la instalación
         */
        public static final int COLUMN_ADDRESS = 3;
        /**
         * Índice de la columna de la componente latitud de las coordenadas de la dirección
         */
        public static final int COLUMN_ADDRESS_LATITUDE = 4;
        /**
         * Índice de la columna de la componente longitud de las coordenadas de la dirección
         */
        public static final int COLUMN_ADDRESS_LONGITUDE = 5;
        /**
         * Índice de la columna de la ciudad de la instalación
         */
        public static final int COLUMN_CITY = 6;
        /**
         * Índice de la columna de la hora de apertura
         */
        public static final int COLUMN_OPENING_TIME = 7;
        /**
         * Índice de la columna de la hora de cierre
         */
        public static final int COLUMN_CLOSING_TIME = 8;
        /**
         * Índice de la columna del identificador del usuario creador
         */
        public static final int COLUMN_CREATOR = 9;

        /**
         * JOIN de la consulta de {@link #CONTENT_FIELDS_WITH_FIELD_SPORT_URI}, para la unión de
         * las tablas de instalaciones y de pistas de instalaciones
         */
        public static final String TABLES_FIELD_JOIN_FIELD_SPORT =
                TABLE_FIELD + " INNER JOIN " + TABLE_FIELD_SPORTS + " ON "
                        + FieldEntry.FIELD_ID_TABLE_PREFIX + " = " + FieldSportEntry.FIELD_ID_TABLE_PREFIX;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildFieldUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FIELD_URI, id);
        }
    }

    /**
     * Nombre de la tabla de pistas de instalaciones
     */
    public static final String TABLE_FIELD_SPORTS = "fieldSport";

    /**
     * Clase con las constantes relativas a la tabla de pistas de instalaciones. Contiene
     * una tupla por cada pista de deporte de cada instalación de la tabla de instalaciones.
     */
    public static final class FieldSportEntry implements BaseColumns {

        /**
         * Uri para la tabla de pistas de instalaciones
         */
        public static final Uri CONTENT_FIELD_SPORT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FIELD_SPORT)
                .build();

        /**
         * Nombre de la columna del identificador de la instalación
         */
        public static final String FIELD_ID = "fieldId";
        /**
         * Nombre de la columna del identificador del deporte de la pista
         */
        public static final String SPORT = "sport";
        /**
         * Nombre de la columna de la puntuación de la pista
         */
        public static final String PUNCTUATION = "punctuation";
        /**
         * Nombre de la columna del número de votos emitidos para la pista
         */
        public static final String VOTES = "votes";

        /**
         * Nombre de la columna del identificador de la instalación con el nombre de la tabla
         * como prefijo
         */
        public static final String FIELD_ID_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + FIELD_ID;
        /**
         * Nombre de la columna del identificador del deporte de la pista con el nombre de la tabla
         * como prefijo
         */
        public static final String SPORT_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + SPORT;
        /**
         * Nombre de la columna de la puntuación de la pista con el nombre de la tabla como prefijo
         */
        public static final String PUNCTUATION_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + PUNCTUATION;
        /**
         * Nombre de la columna del número de votos emitidos para la pista con el nombre de la tabla
         * como prefijo
         */
        public static final String VOTES_TABLE_PREFIX = TABLE_FIELD_SPORTS + "." + VOTES;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] FIELD_SPORT_COLUMNS = {
                TABLE_FIELD_SPORTS + "." + FieldSportEntry._ID,
                FIELD_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                PUNCTUATION_TABLE_PREFIX,
                VOTES_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador de la instalación
         */
        public static final int COLUMN_FIELD_ID = 1;
        /**
         * Índice de la columna del identificador del deporte de la pista
         */
        public static final int COLUMN_SPORT = 2;
        /**
         * Índice de la columna de la puntuación de la pista
         */
        public static final int COLUMN_PUNCTUATION = 3;
        /**
         * Índice de la columna del número de votos emitidos para la pista
         */
        public static final int COLUMN_VOTES = 4;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildFieldSportUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FIELD_SPORT_URI, id);
        }
    }

    /**
     * Nombre de la tabla de alarmas
     */
    public static final String TABLE_ALARM = "alarm";

    /**
     * Clase con las constantes relativas a la tabla de alarmas
     */
    public static final class AlarmEntry implements BaseColumns {

        /**
         * Uri para la tabla de alarmas
         */
        public static final Uri CONTENT_ALARM_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_ALARMS)
                .build();

        /**
         * Nombre de la columna del identificador de la alarma
         */
        public static final String ALARM_ID = "alarmId";
        /**
         * Nombre de la columna del identificador del deporte de la alarma
         */
        public static final String SPORT = "sport";
        /**
         * Nombre de la columna del identificador de la instalación
         */
        public static final String FIELD = "field";
        /**
         * Nombre de la columna de la ciudad de la alarma
         */
        public static final String CITY = "city";
        /**
         * Nombre de la columna de la componente latitud de la ciudad o la instalación de la alarma
         */
        public static final String COORD_LATITUDE = "coordLatitude";
        /**
         * Nombre de la columna de la componente longitud de la ciudad o la instalación de la alarma
         */
        public static final String COORD_LONGITUDE = "coordLongitude";
        /**
         * Nombre de la columna del limite inferior del periodo de fechas de la alarma
         */
        public static final String DATE_FROM = "dateFrom";
        /**
         * Nombre de la columna del limite superior del periodo de fechas de la alarma
         */
        public static final String DATE_TO = "dateTo";
        /**
         * Nombre de la columna del limite inferior del rango de puestos totales de la alarma
         */
        public static final String TOTAL_PLAYERS_FROM = "totalPlayersFrom";
        /**
         * Nombre de la columna del limite superior del rango de puestos totales de la alarma
         */
        public static final String TOTAL_PLAYERS_TO = "totalPlayersTo";
        /**
         * Nombre de la columna del limite inferior del rango de puestos vacantes de la alarma
         */
        public static final String EMPTY_PLAYERS_FROM = "emptyPlayersFrom";
        /**
         * Nombre de la columna del limite superior del rango de puestos vacantes de la alarma
         */
        public static final String EMPTY_PLAYERS_TO = "emptyPlayersTo";

        /**
         * Nombre de la columna del identificador de la alarma con el nombre de la tabla
         * como prefijo
         */
        public static final String ALARM_ID_TABLE_PREFIX = TABLE_ALARM + "." + ALARM_ID;
        /**
         * Nombre de la columna del identificador del deporte de la alarma con el nombre de la
         * tabla como prefijo
         */
        public static final String SPORT_TABLE_PREFIX = TABLE_ALARM + "." + SPORT;
        /**
         * Nombre de la columna del identificador de la instalación con el nombre de la tabla
         * como prefijo
         */
        public static final String FIELD_TABLE_PREFIX = TABLE_ALARM + "." + FIELD;
        /**
         * Nombre de la columna de la ciudad de la alarma con el nombre de la tabla como prefijo
         */
        public static final String CITY_TABLE_PREFIX = TABLE_ALARM + "." + CITY;
        /**
         * Nombre de la columna de la componente latitud de la ciudad o la instalación de la
         * alarma con el nombre de la tabla como prefijo
         */
        public static final String COORD_LATITUDE_TABLE_PREFIX = TABLE_ALARM + "." + COORD_LATITUDE;
        /**
         * Nombre de la columna de la componente longitud de la ciudad o la instalación de la
         * alarma con el nombre de la tabla como prefijo
         */
        public static final String COORD_LONGITUDE_TABLE_PREFIX = TABLE_ALARM + "." + COORD_LONGITUDE;
        /**
         * Nombre de la columna del limite inferior del periodo de fechas de la alarma con el
         * nombre de la tabla como prefijo
         */
        public static final String DATE_FROM_TABLE_PREFIX = TABLE_ALARM + "." + DATE_FROM;
        /**
         * Nombre de la columna del limite superior del periodo de fechas de la alarma con el
         * nombre de la tabla como prefijo
         */
        public static final String DATE_TO_TABLE_PREFIX = TABLE_ALARM + "." + DATE_TO;
        /**
         * Nombre de la columna del limite inferior del rango de puestos totales de la alarma
         * con el nombre de la tabla como prefijo
         */
        public static final String TOTAL_PLAYERS_FROM_TABLE_PREFIX = TABLE_ALARM + "." + TOTAL_PLAYERS_FROM;
        /**
         * Nombre de la columna del limite superior del rango de puestos totales de la alarma
         * con el nombre de la tabla como prefijo
         */
        public static final String TOTAL_PLAYERS_TO_TABLE_PREFIX = TABLE_ALARM + "." + TOTAL_PLAYERS_TO;
        /**
         * Nombre de la columna del limite inferior del rango de puestos vacantes de la alarma
         * con el nombre de la tabla como prefijo
         */
        public static final String EMPTY_PLAYERS_FROM_TABLE_PREFIX = TABLE_ALARM + "." + EMPTY_PLAYERS_FROM;
        /**
         * Nombre de la columna del limite superior del rango de puestos vacantes de la alarma
         * con el nombre de la tabla como prefijo
         */
        public static final String EMPTY_PLAYERS_TO_TABLE_PREFIX = TABLE_ALARM + "." + EMPTY_PLAYERS_TO;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] ALARM_COLUMNS = {
                TABLE_ALARM + "." + AlarmEntry._ID,
                ALARM_ID_TABLE_PREFIX,
                SPORT_TABLE_PREFIX,
                FIELD_TABLE_PREFIX,
                CITY_TABLE_PREFIX,
                COORD_LATITUDE,
                COORD_LONGITUDE,
                DATE_FROM_TABLE_PREFIX,
                DATE_TO_TABLE_PREFIX,
                TOTAL_PLAYERS_FROM_TABLE_PREFIX,
                TOTAL_PLAYERS_TO_TABLE_PREFIX,
                EMPTY_PLAYERS_FROM_TABLE_PREFIX,
                EMPTY_PLAYERS_TO_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador de la alarma
         */
        public static final int COLUMN_ALARM_ID = 1;
        /**
         * Índice de la columna del identificador del deporte de la alarma
         */
        public static final int COLUMN_SPORT = 2;
        /**
         * Índice de la columna del identificador de la instalación
         */
        public static final int COLUMN_FIELD = 3;
        /**
         * Índice de la columna de la ciudad de la alarma
         */
        public static final int COLUMN_CITY = 4;
        /**
         * Índice de la columna de la componente latitud de la ciudad o la instalación de la alarma
         */
        public static final int COLUMN_COORD_LATITUDE = 5;
        /**
         * Índice de la columna de la componente longitud de la ciudad o la instalación de la alarma
         */
        public static final int COLUMN_COORD_LONGITUDE = 6;
        /**
         * Índice de la columna del limite inferior del periodo de fechas de la alarma
         */
        public static final int COLUMN_DATE_FROM = 7;
        /**
         * Índice de la columna del limite superior del periodo de fechas de la alarma
         */
        public static final int COLUMN_DATE_TO = 8;
        /**
         * Índice de la columna del limite inferior del rango de puestos totales de la alarma
         */
        public static final int COLUMN_TOTAL_PLAYERS_FROM = 9;
        /**
         * Índice de la columna del limite superior del rango de puestos totales de la alarma
         */
        public static final int COLUMN_TOTAL_PLAYERS_TO = 10;
        /**
         * Índice de la columna del limite inferior del rango de puestos vacantes de la alarma
         */
        public static final int COLUMN_EMPTY_PLAYERS_FROM = 11;
        /**
         * Índice de la columna del limite superior del rango de puestos vacantes de la alarma
         */
        public static final int COLUMN_EMPTY_PLAYERS_TO = 12;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildAlarmUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_ALARM_URI, id);
        }
    }

    /**
     * Nombre de la tabla de partidos
     */
    public static final String TABLE_EVENT = "event";

    /**
     * Clase con las constantes relativas a la tabla de partidos
     */
    public static final class EventEntry implements BaseColumns {

        /**
         * Uri para la tabla de partidos
         */
        public static final Uri CONTENT_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS)
                .build();

        /**
         * Nombre de la columna del identificador del partido
         */
        public static final String EVENT_ID = "eventId";
        /**
         * Nombre de la columna del identificador del deporte del partido
         */
        public static final String SPORT = "sport";
        /**
         * Nombre de la columna del identificador de la instalación del partido
         */
        public static final String FIELD = "field";
        /**
         * Nombre de la columna de la dirección del partido
         */
        public static final String ADDRESS = "address";
        /**
         * Nombre de la columna de la componente latitud de las coordenadas de la dirección
         * del partido
         */
        public static final String FIELD_LATITUDE = "fieldLatitude";
        /**
         * Nombre de la columna de la componente longitud de las coordenadas de la dirección
         * del partido
         */
        public static final String FIELD_LONGITUDE = "fieldLongitude";
        /**
         * Nombre de la columna del nombre del partido
         */
        public static final String NAME = "name";
        /**
         * Nombre de la columna de la ciudad del partido
         */
        public static final String CITY = "city";
        /**
         * Nombre de la columna de la fecha y hora del partido
         */
        public static final String DATE = "date";
        /**
         * Nombre de la columna del identificador del usuario creador del partido
         */
        public static final String OWNER = "owner";
        /**
         * Nombre de la columna del número de puestos totales
         */
        public static final String TOTAL_PLAYERS = "totalPlayers";
        /**
         * Nombre de la columna del número de puestos vacantes
         */
        public static final String EMPTY_PLAYERS = "emptyPlayers";

        /**
         * Nombre de la columna del identificador del partido con el nombre de la tabla como
         * prefijo
         */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENT + "." + EVENT_ID;
        /**
         * Nombre de la columna del identificador del deporte del partido con el nombre de la
         * tabla como prefijo
         */
        public static final String SPORT_TABLE_PREFIX = TABLE_EVENT + "." + SPORT;
        /**
         * Nombre de la columna del identificador de la instalación del partido con el nombre de
         * la tabla como prefijo
         */
        public static final String FIELD_TABLE_PREFIX = TABLE_EVENT + "." + FIELD;
        /**
         * Nombre de la columna de la dirección del partido con el nombre de la tabla como prefijo
         */
        public static final String ADDRESS_TABLE_PREFIX = TABLE_EVENT + "." + ADDRESS;
        /**
         * Nombre de la columna de la componente latitud de las coordenadas de la dirección del
         * partido con el nombre de la tabla como prefijo
         */
        public static final String FIELD_LATITUDE_TABLE_PREFIX = TABLE_EVENT + "." + FIELD_LATITUDE;
        /**
         * Nombre de la columna de la componente longitud de las coordenadas de la dirección del
         * partido con el nombre de la tabla como prefijo
         */
        public static final String FIELD_LONGITUDE_TABLE_PREFIX = TABLE_EVENT + "." + FIELD_LONGITUDE;
        /**
         * Nombre de la columna del nombre del partido con el nombre de la tabla como prefijo
         */
        public static final String NAME_TABLE_PREFIX = TABLE_EVENT + "." + NAME;
        /**
         * Nombre de la columna de la ciudad del partido con el nombre de la tabla como prefijo
         */
        public static final String CITY_TABLE_PREFIX = TABLE_EVENT + "." + CITY;
        /**
         * Nombre de la columna de la fecha y hora del partido con el nombre de la tabla como
         * prefijo
         */
        public static final String DATE_TABLE_PREFIX = TABLE_EVENT + "." + DATE;
        /**
         * Nombre de la columna del identificador del usuario creador del partido con el nombre de
         * la tabla como prefijo
         */
        public static final String OWNER_TABLE_PREFIX = TABLE_EVENT + "." + OWNER;
        /**
         * Nombre de la columna el número de puestos totales con el nombre de la tabla como
         * prefijo
         */
        public static final String TOTAL_PLAYERS_TABLE_PREFIX = TABLE_EVENT + "." + TOTAL_PLAYERS;
        /**
         * Nombre de la columna del número de puestos vacantes con el nombre de la tabla como
         * prefijo
         */
        public static final String EMPTY_PLAYERS_TABLE_PREFIX = TABLE_EVENT + "." + EMPTY_PLAYERS;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
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

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del partido
         */
        public static final int COLUMN_EVENT_ID = 1;
        /**
         * Índice de la columna del identificador del deporte del partido
         */
        public static final int COLUMN_SPORT = 2;
        /**
         * Índice de la columna del identificador de la instalación del partido
         */
        public static final int COLUMN_FIELD = 3;
        /**
         * Índice de la columna de la dirección del partido
         */
        public static final int COLUMN_ADDRESS = 4;
        /**
         * Índice de la columna de la componente latitud de las coordenadas de la dirección
         * del partido
         */
        public static final int COLUMN_FIELD_LATITUDE = 5;
        /**
         * Índice de la columna de la componente longitud de las coordenadas de la dirección
         * del partido
         */
        public static final int COLUMN_FIELD_LONGITUDE = 6;
        /**
         * Índice de la columna del nombre del partido
         */
        public static final int COLUMN_NAME = 7;
        /**
         * Índice de la columna de la ciudad del partido
         */
        public static final int COLUMN_CITY = 8;
        /**
         * Índice de la columna de la fecha y hora del partido
         */
        public static final int COLUMN_DATE = 9;
        /**
         * Índice de la columna del identificador del usuario creador del partido
         */
        public static final int COLUMN_OWNER = 10;
        /**
         * Índice de la columna del número de puestos totales
         */
        public static final int COLUMN_TOTAL_PLAYERS = 11;
        /**
         * Índice de la columna del número de puestos vacantes
         */
        public static final int COLUMN_EMPTY_PLAYERS = 12;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildEventUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENT_URI, id);
        }
    }

    /**
     * Nombre de la tabla de usuarios simulados
     */
    public static final String TABLE_EVENT_SIMULATED_PARTICIPANT = "eventSimulatedParticipant";

    /**
     * Clase con las constantes relativas a la tabla de usuarios simulados. Contiene
     * una tupla por cada usuario simulados de cada partido de la tabla de partidos.
     */
    public static final class SimulatedParticipantEntry implements BaseColumns {

        /**
         * Uri para la tabla de usuario simulados
         */
        public static final Uri CONTENT_SIMULATED_PARTICIPANT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_SIMULATED_PARTICIPANT)
                .build();

        /**
         * Nombre de la columna del identificador del partido al que pertenece el usuario simulado
         */
        public static final String EVENT_ID = "eventId";
        /**
         * Nombre de la columna del identificador del usuario simulado
         */
        public static final String SIMULATED_USER_ID = "simulatedUserId";
        /**
         * Nombre de la columna del nombre del usuario simulado
         */
        public static final String ALIAS = "alias";
        /**
         * Nombre de la columna de la foto de perfil del usuario simulado
         */
        public static final String PROFILE_PICTURE = "picture";
        /**
         * Nombre de la columna de la edad del usuario simulado
         */
        public static final String AGE = "age";
        /**
         * Nombre de la columna del identificador del usuario creador del usuario simulado
         */
        public static final String OWNER = "owner";

        /**
         * Nombre de la columna del identificador del partido al que pertenece el usuario simulado
         * con el nombre de la tabla como prefijo
         */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + EVENT_ID;
        /**
         * Nombre de la columna del identificador del usuario simulado con el nombre de la tabla
         * como prefijo
         */
        public static final String SIMULATED_USER_ID_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + SIMULATED_USER_ID;
        /**
         * Nombre de la columna del nombre del usuario simulado con el nombre de la tabla
         * como prefijo
         */
        public static final String ALIAS_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + ALIAS;
        /**
         * Nombre de la columna de la foto de perfil del usuario simulado con el nombre de la
         * tabla como prefijo
         */
        public static final String PROFILE_PICTURE_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + PROFILE_PICTURE;
        /**
         * Nombre de la columna de la edad del usuario simulado con el nombre de la tabla
         * como prefijo
         */
        public static final String AGE_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + AGE;
        /**
         * Nombre de la columna del identificador del usuario creador del usuario simulado con
         * el nombre de la tabla como prefijo
         */
        public static final String OWNER_TABLE_PREFIX = TABLE_EVENT_SIMULATED_PARTICIPANT + "." + OWNER;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] SIMULATED_PARTICIPANTS_COLUMNS = {
                TABLE_EVENT_SIMULATED_PARTICIPANT + "." + SimulatedParticipantEntry._ID,
                EVENT_ID_TABLE_PREFIX,
                SIMULATED_USER_ID_TABLE_PREFIX,
                ALIAS_TABLE_PREFIX,
                PROFILE_PICTURE_TABLE_PREFIX,
                AGE_TABLE_PREFIX,
                OWNER_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del partido al que pertenece el usuario simulado
         */
        public static final int COLUMN_EVENT_ID = 1;
        /**
         * Índice de la columna del identificador del usuario simulado
         */
        public static final int COLUMN_SIMULATED_USER_ID = 2;
        /**
         * Índice de la columna del nombre del usuario simulado
         */
        public static final int COLUMN_ALIAS = 3;
        /**
         * Índice de la columna de la foto de perfil del usuario simulado
         */
        public static final int COLUMN_PROFILE_PICTURE = 4;
        /**
         * Índice de la columna de la edad del usuario simulado
         */
        public static final int COLUMN_AGE = 5;
        /**
         * Índice de la columna del identificador del usuario creador del usuario simulado
         */
        public static final int COLUMN_OWNER = 6;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildSimulatedParticipantUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_SIMULATED_PARTICIPANT_URI, id);
        }
    }

    /**
     * Nombre de la tabla de peticiones de amistad
     */
    public static final String TABLE_FRIENDS_REQUESTS = "friendRequest";

    /**
     * Clase con las constantes relativas a la tabla de peticiones de amistad. Contiene
     * una tupla por cada petición de amistad enviada o recibida por el usuario que tiene la sesión
     * iniciada.
     */
    public static final class FriendRequestEntry implements BaseColumns {

        /**
         * Uri para la tabla de peticiones de amistad
         */
        public static final Uri CONTENT_FRIEND_REQUESTS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_REQUESTS)
                .build();
        /**
         * Uri para la unión de las tablas de peticiones de amistad y de usuarios
         */
        public static final Uri CONTENT_FRIEND_REQUESTS_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_REQUESTS_WITH_USER)
                .build();

        /**
         * Nombre de la columna del identificador del usuario receptor de la petición de amistad
         */
        public static final String RECEIVER_ID = "receiverId";
        /**
         * Nombre de la columna del identificador del usuario emisor de la petición de amistad
         */
        public static final String SENDER_ID = "senderId";
        /**
         * Nombre de la columna de la fecha y hora de la petición de amistad
         */
        public static final String DATE = "date";

        /**
         * Nombre de la columna del identificador del usuario receptor de la petición de amistad
         * con el nombre de la tabla como prefijo
         */
        public static final String RECEIVER_ID_TABLE_PREFIX = TABLE_FRIENDS_REQUESTS + "." + RECEIVER_ID;
        /**
         * Nombre de la columna del identificador del usuario emisor de la petición de amistad
         * con el nombre de la tabla como prefijo
         */
        public static final String SENDER_ID_TABLE_PREFIX = TABLE_FRIENDS_REQUESTS + "." + SENDER_ID;
        /**
         * Nombre de la columna de la fecha y hora de la petición de amistad con el nombre de la
         * tabla como prefijo
         */
        public static final String DATE_TABLE_PREFIX = TABLE_FRIENDS_REQUESTS + "." + DATE;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] FRIEND_REQUESTS_COLUMNS = {
                TABLE_FRIENDS_REQUESTS + "." + FriendRequestEntry._ID,
                RECEIVER_ID_TABLE_PREFIX,
                SENDER_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del usuario receptor de la petición de amistad
         */
        public static final int COLUMN_RECEIVER_ID = 1;
        /**
         * Índice de la columna del identificador del usuario emisor de la petición de amistad
         */
        public static final int COLUMN_SENDER_ID = 2;
        /**
         * Índice de la columna de la fecha y hora de la petición de amistad
         */
        public static final int COLUMN_DATE = 3;

        /**
         * JOIN de la consulta de {@link #CONTENT_FRIEND_REQUESTS_WITH_USER_URI}, para la unión
         * de las tablas de peticiones de amistad y de usuarios
         */
        public static final String TABLES_FRIENDS_REQUESTS_JOIN_USER =
                TABLE_FRIENDS_REQUESTS + " INNER JOIN " + TABLE_USER + " ON "
                        + FriendRequestEntry.SENDER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildFriendRequestsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FRIEND_REQUESTS_URI, id);
        }
    }

    /**
     * Nombre de la tabla de amigos
     */
    public static final String TABLE_FRIENDS = "friends";

    /**
     * Clase con las constantes relativas a la tabla de amigos. Contiene una tupla por cada amigo
     * del usuario que tiene la sesión iniciada.
     */
    public static final class FriendsEntry implements BaseColumns {

        /**
         * Uri para la tabla de amigos
         */
        public static final Uri CONTENT_FRIENDS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS)
                .build();
        /**
         * Uri para la unión de las tablas de amigos y de usuarios
         */
        public static final Uri CONTENT_FRIEND_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_WITH_USER)
                .build();

        /**
         * Nombre de la columna del identificador del usuario actual
         */
        public static final String MY_USER_ID = "myUserId";
        /**
         * Nombre de la columna del identificador del usuario amigo
         */
        public static final String USER_ID = "userId";
        /**
         * Nombre de la columna de la fecha y hora de la amistad
         */
        public static final String DATE = "date";

        /**
         * Nombre de la columna del identificador del usuario actual con el nombre de la tabla
         * como prefijo
         */
        public static final String MY_USER_ID_TABLE_PREFIX = TABLE_FRIENDS + "." + MY_USER_ID;
        /**
         * Nombre de la columna del identificador del usuario amigo con el nombre de la tabla
         * como prefijo
         */
        public static final String USER_ID_TABLE_PREFIX = TABLE_FRIENDS + "." + USER_ID;
        /**
         * Nombre de la columna de la fecha y hora de la amistad con el nombre de la tabla
         * como prefijo
         */
        public static final String DATE_TABLE_PREFIX = TABLE_FRIENDS + "." + DATE;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] FRIENDS_COLUMNS = {
                TABLE_FRIENDS + "." + FriendsEntry._ID,
                MY_USER_ID_TABLE_PREFIX,
                USER_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del usuario actual
         */
        public static final int COLUMN_MY_USER_ID = 1;
        /**
         * Índice de la columna del identificador del usuario amigo
         */
        public static final int COLUMN_USER_ID = 2;
        /**
         * Índice de la columna de la fecha y hora de la amistad
         */
        public static final int COLUMN_DATE = 3;

        /**
         * JOIN de la consulta de {@link #CONTENT_FRIEND_WITH_USER_URI}, para la unión de las
         * tablas de amigos y de usuarios
         */
        public static final String TABLES_FRIENDS_JOIN_USER =
                TABLE_FRIENDS + " INNER JOIN " + TABLE_USER + " ON "
                        + FriendsEntry.USER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildFriendsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_FRIENDS_URI, id);
        }
    }

    /**
     * Nombre de la tabla de participaciones de usuarios en partidos
     */
    public static final String TABLE_EVENTS_PARTICIPATION = "eventsParticipation";

    /**
     * Clase con las constantes relativas a la tabla de participaciones de usuarios en partidos.
     * Contiene una tupla por cada relación de participación entre un usuario y un partido.
     */
    public static final class EventsParticipationEntry implements BaseColumns {

        /**
         * Uri para la tabla de participaciones
         */
        public static final Uri CONTENT_EVENTS_PARTICIPATION_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_PARTICIPATION)
                .build();
        /**
         * Uri para la unión de las tablas de participaciones y de usuarios
         */
        public static final Uri CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_PARTICIPATION_WITH_USER)
                .build();
        /**
         * Uri para la unión de las tablas de participaciones y de partidos
         */
        public static final Uri CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_PARTICIPATION_WITH_EVENT)
                .build();

        /**
         * Nombre de la columna del identificador del usuario
         */
        public static final String USER_ID = "userId";
        /**
         * Nombre de la columna del identificador del partido
         */
        public static final String EVENT_ID = "eventId";
        /**
         * Nombre de la columna de la participación (1=participante/0=bloqueado)
         */
        public static final String PARTICIPATES = "participates";

        /**
         * Nombre de la columna del identificador de usuario con el nombre de la tabla
         * como prefijo
         */
        public static final String USER_ID_TABLE_PREFIX = TABLE_EVENTS_PARTICIPATION + "." + USER_ID;
        /**
         * Nombre de la columna del identificador del partido con el nombre de la tabla
         * como prefijo
         */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENTS_PARTICIPATION + "." + EVENT_ID;
        /**
         * Nombre de la columna de la participación (1=participante/0=bloqueado) con el nombre
         * de la tabla como prefijo
         */
        public static final String PARTICIPATES_TABLE_PREFIX = TABLE_EVENTS_PARTICIPATION + "." + PARTICIPATES;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] EVENTS_PARTICIPATION_COLUMNS = {
                TABLE_EVENTS_PARTICIPATION + "." + EventsParticipationEntry._ID,
                USER_ID_TABLE_PREFIX,
                EVENT_ID_TABLE_PREFIX,
                PARTICIPATES_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del usuario
         */
        public static final int COLUMN_USER_ID = 1;
        /**
         * Índice de la columna del identificador del partido
         */
        public static final int COLUMN_EVENT_ID = 2;
        /**
         * Índice de la columna de la participación (1=participante/0=bloqueado)
         */
        public static final int COLUMN_PARTICIPATES = 3;

        /**
         * JOIN de la consulta de {@link #CONTENT_EVENTS_PARTICIPATION_WITH_USER_URI}, para la
         * unión de las tablas de participaciones y de usuarios
         */
        public static final String TABLES_EVENTS_PARTICIPATION_JOIN_USER =
                TABLE_EVENTS_PARTICIPATION + " INNER JOIN " + TABLE_USER + " ON "
                        + EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /**
         * JOIN de la consulta de {@link #CONTENT_EVENTS_PARTICIPATION_WITH_EVENT_URI}, para la
         * unión de las tablas de participaciones y de partidos
         */
        public static final String TABLES_EVENTS_PARTICIPATION_JOIN_EVENT =
                TABLE_EVENT + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " ON "
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildEventsParticipationUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENTS_PARTICIPATION_URI, id);
        }
    }

    /**
     * Nombre de la tabla de invitaciones
     */
    public static final String TABLE_EVENT_INVITATIONS = "eventInvitations";

    /**
     * Clase con las constantes relativas a la tabla de invitaciones. Contiene una tupla por cada
     * invitación enviada o recibida por el usuario que tiene la sesión iniciada.
     */
    public static final class EventsInvitationEntry implements BaseColumns {

        /**
         * Uri para la tabla de invitaciones
         */
        public static final Uri CONTENT_EVENT_INVITATIONS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_INVITATIONS)
                .build();
        /**
         * Uri para la unión de las tablas de invitaciones y de usuarios
         */
        public static final Uri CONTENT_EVENT_INVITATIONS_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_INVITATIONS_WITH_USER)
                .build();
        /**
         * Uri para la unión de las tablas de invitaciones y de partidos
         */
        public static final Uri CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENT_INVITATIONS_WITH_EVENT)
                .build();

        /**
         * Nombre de la columna del identificador del usuario receptor de la invitación
         */
        public static final String RECEIVER_ID = "receiverId";
        /**
         * Nombre de la columna del identificador del usuario emisor de la invitación
         */
        public static final String SENDER_ID = "senderId";
        /**
         * Nombre de la columna del identificador del partido de la invitación
         */
        public static final String EVENT_ID = "eventId";
        /**
         * Nombre de la columna de la fecha y hora de creación de la invitación
         */
        public static final String DATE = "date";

        /**
         * Nombre de la columna del identificador de usuario receptor de la invitación con
         * el nombre de la tabla como prefijo
         */
        public static final String RECEIVER_ID_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + RECEIVER_ID;
        /**
         * Nombre de la columna del identificador de usuario emisor de la invitación con
         * el nombre de la tabla como prefijo
         */
        public static final String SENDER_ID_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + SENDER_ID;
        /**
         * Nombre de la columna del identificador del partido de la invitación con el nombre de
         * la tabla como prefijo
         */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + EVENT_ID;
        /**
         * Nombre de la columna de la fecha y hora de creación de la invitación con el nombre de
         * la tabla como prefijo
         */
        public static final String DATE_TABLE_PREFIX = TABLE_EVENT_INVITATIONS + "." + DATE;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] EVENT_INVITATIONS_COLUMNS = {
                TABLE_EVENT_INVITATIONS + "." + EventsInvitationEntry._ID,
                RECEIVER_ID_TABLE_PREFIX,
                SENDER_ID_TABLE_PREFIX,
                EVENT_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del usuario receptor de la invitación
         */
        public static final int COLUMN_RECEIVER_ID = 1;
        /**
         * Índice de la columna del identificador del usuario emisor de la invitación
         */
        public static final int COLUMN_SENDER_ID = 2;
        /**
         * Índice de la columna del identificador del partido de la invitación
         */
        public static final int COLUMN_EVENT_ID = 3;
        /**
         * Índice de la columna de la fecha y hora de creación de la invitación
         */
        public static final int COLUMN_DATE = 4;

        /**
         * JOIN de la consulta de {@link #CONTENT_EVENT_INVITATIONS_WITH_USER_URI}, para la unión
         * de las tablas de invitaciones y de usuarios
         */
        public static final String TABLES_EVENT_INVITATIONS_JOIN_USER =
                TABLE_EVENT_INVITATIONS + " INNER JOIN " + TABLE_USER + " ON "
                        + EventsInvitationEntry.RECEIVER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /**
         * JOIN de la consulta de {@link #CONTENT_EVENT_INVITATIONS_WITH_EVENT_URI}, para la unión
         * de las tablas de invitaciones y de partidos
         */
        public static final String TABLES_EVENT_INVITATIONS_JOIN_EVENT =
                TABLE_EVENT_INVITATIONS + " INNER JOIN " + TABLE_EVENT + " ON "
                        + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " = " + EventEntry.EVENT_ID_TABLE_PREFIX;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildEventInvitationUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENT_INVITATIONS_URI, id);
        }
    }

    /**
     * Nombre de la tabla de peticiones de participación
     */
    public static final String TABLE_EVENTS_REQUESTS = "eventRequest";

    /**
     * Clase con las constantes relativas a la tabla de peticiones de participación. Contiene
     * una tupla por cada petición de amistad enviada por el usuario que tiene la sesión
     * iniciada o recibida en alguno de los partidos creados por el usuario que tiene la sesión
     * iniciada.
     */
    public static final class EventRequestsEntry implements BaseColumns {

        /**
         * Uri para la tabla de peticiones de participación
         */
        public static final Uri CONTENT_EVENTS_REQUESTS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_REQUESTS)
                .build();
        /**
         * Uri para la unión de las tablas de peticiones de participación y de usuarios
         */
        public static final Uri CONTENT_EVENTS_REQUESTS_WITH_USER_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_REQUESTS_WITH_USER)
                .build();
        /**
         * Uri para la unión de las tablas de peticiones de participación y de partidos
         */
        public static final Uri CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EVENTS_REQUESTS_WITH_EVENT)
                .build();

        /**
         * Nombre de la columna del identificador del partido de la petición de participación
         */
        public static final String EVENT_ID = "eventId";
        /**
         * Nombre de la columna del identificador del usuario emisor de la petición de participación
         */
        public static final String SENDER_ID = "senderId";
        /**
         * Nombre de la columna de la fecha y hora de creación de la petición de participación
         */
        public static final String DATE = "date";

        /**
         * Nombre de la columna del identificador del partido de la petición de participación con
         * el nombre de la tabla como prefijo
         */
        public static final String EVENT_ID_TABLE_PREFIX = TABLE_EVENTS_REQUESTS + "." + EVENT_ID;
        /**
         * Nombre de la columna del identificador del usuario emisor de la petición de
         * participación con el nombre de la tabla como prefijo
         */
        public static final String SENDER_ID_TABLE_PREFIX = TABLE_EVENTS_REQUESTS + "." + SENDER_ID;
        /**
         * Nombre de la columna de la fecha y hora de creación de la petición de participación con
         * el nombre de la tabla como prefijo
         */
        public static final String DATE_TABLE_PREFIX = TABLE_EVENTS_REQUESTS + "." + DATE;

        /**
         * Array de todos los nombres de las columnas de esta tabla
         */
        public static final String[] EVENTS_REQUESTS_COLUMNS = {
                TABLE_EVENTS_REQUESTS + "." + EventRequestsEntry._ID,
                EVENT_ID_TABLE_PREFIX,
                SENDER_ID_TABLE_PREFIX,
                DATE_TABLE_PREFIX
        };

        /**
         * Índice de la columna del identificador
         */
        public static final int COLUMN_ID = 0;
        /**
         * Índice de la columna del identificador del partido de la petición de participación
         */
        public static final int COLUMN_EVENT_ID = 1;
        /**
         * Índice de la columna del identificador del usuario emisor de la petición de participación
         */
        public static final int COLUMN_SENDER_ID = 2;
        /**
         * Índice de la columna de la fecha y hora de creación de la petición de participación
         */
        public static final int COLUMN_DATE = 3;

        /**
         * JOIN de la consulta de {@link #CONTENT_EVENTS_REQUESTS_WITH_USER_URI}, para la unión
         * de las tablas de peticiones de participación y de usuarios
         */
        public static final String TABLES_EVENTS_REQUESTS_JOIN_USER =
                TABLE_EVENTS_REQUESTS + " INNER JOIN " + TABLE_USER + " ON "
                        + EventRequestsEntry.SENDER_ID_TABLE_PREFIX + " = " + UserEntry.USER_ID_TABLE_PREFIX;

        /**
         * JOIN de la consulta de {@link #CONTENT_EVENTS_REQUESTS_WITH_EVENT_URI}, para la unión
         * de las tablas de peticiones de participación y de partidos
         */
        public static final String TABLES_EVENTS_REQUESTS_JOIN_EVENT =
                TABLE_EVENTS_REQUESTS + " INNER JOIN " + TABLE_EVENT + " ON "
                        + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " = " + EventEntry.EVENT_ID_TABLE_PREFIX;

        /**
         * Devuelve la Uri para una sola tupla de la tabla, dado su identificador
         *
         * @param id identificador de la tupla
         * @return Uri para una tupla
         */
        public static Uri buildEventRequestsUriWith(long id) {
            return ContentUris.withAppendedId(CONTENT_EVENTS_REQUESTS_URI, id);
        }
    }


    /**
     * Clase con las constantes relativas a las consultas más complejas que incluyen múltiples
     * JOIN y cláusulas WHERE con varios parámetros.
     */
    public static final class JoinQueryEntries {
        /**
         * Uri para la consulta de partidos del usuarios a varias tablas unidas por JOINs
         */
        public static final Uri CONTENT_MY_EVENTS_AND_PARTICIPATION_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MY_EVENTS_AND_PARTICIPATION)
                .build();
        /**
         * Tablas unidas por JOINs de la consulta de {@link #CONTENT_MY_EVENTS_AND_PARTICIPATION_URI}
         */
        public static final String TABLES_EVENTS_JOIN_PARTICIPATION =
                TABLE_EVENT
                        + " LEFT JOIN " + TABLE_EVENTS_PARTICIPATION + " ON ("
                        + EventEntry.EVENT_ID_TABLE_PREFIX + " = " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " )";
        /**
         * Cláusula WHERE de la consulta de {@link #CONTENT_MY_EVENTS_AND_PARTICIPATION_URI}
         */
        public static final String WHERE_MY_EVENTS_AND_PARTICIPATION =
                EventEntry.DATE_TABLE_PREFIX + " > ? "
                        + "AND ( " + EventEntry.OWNER_TABLE_PREFIX + " = ? "
                        + "OR ( " + EventsParticipationEntry.USER_ID_TABLE_PREFIX + " = ? "
                        + "AND " + EventsParticipationEntry.PARTICIPATES_TABLE_PREFIX + " = 1 )) ";

        /**
         * Devuelve el array de parámetros necesarios para completar la consulta de
         * {@link #CONTENT_MY_EVENTS_AND_PARTICIPATION_URI}
         *
         * @param ownerId identificador del usuario cuyos partidos se consultan
         * @return array de parámetros
         */
        public static String[] queryMyEventsAndParticipationArguments(String ownerId) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            return new String[]{currentTime, ownerId, ownerId};
        }


        /**
         * Uri para la consulta de partidos del usuarios sin relación con un amigo a varias tablas
         * unidas por JOINs
         */
        public static final Uri CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND)
                .build();
        /**
         * Tablas unidas por JOINs de la consulta de {@link #CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI}
         */
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
        /**
         * Cláusula WHERE de la consulta de {@link #CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI}
         */
        public static final String WHERE_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND =
                EventEntry.DATE_TABLE_PREFIX + " > ? "
                        + "AND " + EventEntry.EMPTY_PLAYERS_TABLE_PREFIX + " > 0 "
                        + "AND ( " + EventEntry.OWNER_TABLE_PREFIX + " = ? OR p1." + EventsParticipationEntry.USER_ID + " = ? ) "
                        + "AND p2." + EventsParticipationEntry.EVENT_ID + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";

        /**
         * Devuelve el array de parámetros necesarios para completar la consulta de
         * {@link #CONTENT_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND_URI}
         *
         * @param ownerId  identificador del usuario cuyos partidos se consultan
         * @param friendId identificador del usuario amigo que no debe tener relación con los
         *                 partidos
         * @return array de parámetros
         */
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


        /**
         * Uri para la consulta de partidos de la ciudad sin relación con un usuario a varias
         * tablas unidas por JOINs
         */
        public static final Uri CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CITY_EVENTS_WITHOUT_RELATION_WITH_ME)
                .build();
        /**
         * Tablas unidas por JOINs de la consulta de {@link #CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI}
         */
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
        /**
         * Cláusula WHERE de la consulta de {@link #CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI}
         */
        public static final String WHERE_CITY_EVENTS_WITHOUT_RELATION_WITH_ME =
                EventEntry.DATE_TABLE_PREFIX + " > ? "
                        + "AND " + EventEntry.CITY_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.OWNER_TABLE_PREFIX + " <> ? "
                        + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";

        /**
         * Devuelve el array de parámetros necesarios para completar la consulta de
         * {@link #CONTENT_CITY_EVENTS_WITHOUT_RELATION_WITH_ME_URI}
         *
         * @param myUserId identificador del usuario actual
         * @param city     ciudad del usuario actual
         * @return array de parámetros
         */
        public static String[] queryCityEventsWithoutRelationWithMeArguments(String myUserId, String city) {
            String currentTime = String.valueOf(System.currentTimeMillis());
            return new String[]{myUserId, myUserId, myUserId, currentTime, city, myUserId};
        }


        /**
         * Uri para la consulta de amigos sin relación con uno de los partidos del usuario a varias
         * tablas unidas por JOINs
         */
        public static final Uri CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS)
                .build();
        /**
         * Tablas unidas por JOINs de la consulta de {@link #CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI}
         */
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
        /**
         * Cláusula WHERE de la consulta de {@link #CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI}
         */
        public static final String WHERE_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS =
                FriendsEntry.MY_USER_ID_TABLE_PREFIX + " = ? "
                        + "AND " + EventEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsParticipationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventsInvitationEntry.EVENT_ID_TABLE_PREFIX + " IS NULL "
                        + "AND " + EventRequestsEntry.EVENT_ID_TABLE_PREFIX + " IS NULL ";

        /**
         * Devuelve el array de parámetros necesarios para completar la consulta de
         * {@link #CONTENT_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS_URI}
         *
         * @param myUserId identificador del usuario actual cuyos amigos se van a buscar
         * @param eventId  identificador del partido que no debe tener relación con los amigos
         * @return array de parámetros
         */
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


        /**
         * Uri para la consulta de usuarios de una ciudad sin relación con el usuario a varias
         * tablas unidas por JOINs
         */
        public static final Uri CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOT_FRIENDS_USERS_FROM_CITY)
                .build();
        /**
         * Cláusula WHERE de la consulta de {@link #CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI}
         */
        public static final String WHERE_NOT_FRIENDS_USERS_FROM_CITY =
                UserEntry.CITY_TABLE_PREFIX + " = ? "
                        + "AND " + UserEntry.USER_ID_TABLE_PREFIX + " <> ? "
                        + "AND " + FriendsEntry.MY_USER_ID_TABLE_PREFIX + " IS NULL ";

        /**
         * Devuelve el array de parámetros necesarios para completar la consulta de
         * {@link #CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI}
         *
         * @param myUserId identificador del usuario actual
         * @param city     ciudad del usuario actual
         * @return array de parámetros
         */
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


        /**
         * Uri para la consulta de usuarios dado un nombre y sin relación con el usuario a varias
         * tablas unidas por JOINs
         */
        public static final Uri CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NOT_FRIENDS_USERS_WITH_NAME)
                .build();
        /**
         * Tablas unidas por JOINs de la consulta de {@link #CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI}
         * y {@link #CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI}
         */
        public static final String TABLES_USERS_JOIN_FRIENDS =
                TABLE_USER
                        + " LEFT JOIN " + TABLE_FRIENDS + " ON ("
                        + UserEntry.USER_ID_TABLE_PREFIX + " = " + FriendsEntry.USER_ID_TABLE_PREFIX
                        + " AND " + FriendsEntry.MY_USER_ID_TABLE_PREFIX + " = ? )";
        /**
         * Cláusula WHERE de la consulta de {@link #CONTENT_NOT_FRIENDS_USERS_WITH_NAME_URI}
         */
        public static final String WHERE_NOT_FRIENDS_USERS_WITH_NAME =
                UserEntry.NAME_TABLE_PREFIX + " LIKE ? "
                        + "AND " + UserEntry.USER_ID_TABLE_PREFIX + " <> ? "
                        + "AND " + FriendsEntry.MY_USER_ID_TABLE_PREFIX + " IS NULL ";

        /**
         * Devuelve el array de parámetros necesarios para completar la consulta de
         * {@link #CONTENT_NOT_FRIENDS_USERS_FROM_CITY_URI}
         *
         * @param myUserId identificador del usuario actual
         * @param name     nombre de los usuarios que se van a buscar
         * @return array de parámetros
         */
        public static String[] queryNotFriendsUsersWithNameArguments(String myUserId, String name) {
            return new String[]{myUserId, "%" + name + "%", myUserId};
        }
        /* SELECT all columns from users table
         * FROM user
         *      LEFT JOIN friends
         *          ON (user.uid = friend.userId AND friend.myuid = ? )
         * WHERE (user.name LIKE ? AND user.uid <> ? AND friends.myUserId IS NULL )
         * ORDER BY user.name ASC
         */
    }

}

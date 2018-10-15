package com.usal.jorgeav.sportapp.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Esta clase representa a la base de datos en SQLite del dispositivo, alojada en el Proveedor de
 * Contenido.
 * Aquí, se crea y se inicia la base de datos ejecutando sentencias SQL. Se especifica su nombre y
 * su versión. También contiene la funcionalidad para actualizar el esquema de la base de datos.
 */
public class SportteamDBHelper extends SQLiteOpenHelper {

    /**
     * Nombre del archivo de base de datos. Debe acabar en ".db"
     */
    private static final String DATABASE_NAME = "sportteam.db";

    /**
     * Representa la versión de la base de datos. Al incrementarse, se ejecuta el método de
     * actualización del esquema.
     */
    private static final int DATABASE_VERSION = 27;

    /**
     * Constructor, llama al constructor de la superclase
     *
     * @param context contexto bajo el que se ejecuta.
     */
    public SportteamDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Invocado cuando es necesaria la creación de la base de datos, en la primera ejecución y al
     * actualizar su esquema. Crea y ejecuta las sentencias SQL necesarias para conformar el esquema.
     *
     * @param sqLiteDatabase la base de datos
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_EMAIL_LOGGED_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EMAIL_LOGGED + " (" +
                SportteamContract.EmailLoggedEntry._ID      + " INTEGER PRIMARY KEY,"   +
                SportteamContract.EmailLoggedEntry.EMAIL    + " TEXT NOT NULL,"         +
                " UNIQUE (" + SportteamContract.EmailLoggedEntry.EMAIL + ") ON CONFLICT IGNORE);";

        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + SportteamContract.TABLE_USER + " (" +
                SportteamContract.UserEntry._ID             + " INTEGER PRIMARY KEY,"       +
                SportteamContract.UserEntry.USER_ID         + " TEXT UNIQUE NOT NULL,"      +
                SportteamContract.UserEntry.EMAIL           + " TEXT NOT NULL,"             +
                SportteamContract.UserEntry.NAME            + " TEXT NOT NULL,"             +
                SportteamContract.UserEntry.AGE             + " INTEGER,"                   +
                SportteamContract.UserEntry.CITY            + " TEXT,"                      +
                SportteamContract.UserEntry.CITY_LATITUDE   + " REAL,"                      +
                SportteamContract.UserEntry.CITY_LONGITUDE  + " REAL,"                      +
                SportteamContract.UserEntry.PHOTO           + " TEXT,"                      +
                " UNIQUE (" + SportteamContract.UserEntry.USER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EVENT + " (" +
                SportteamContract.EventEntry._ID                + " INTEGER PRIMARY KEY,"       +
                SportteamContract.EventEntry.EVENT_ID           + " TEXT UNIQUE NOT NULL,"      +
                SportteamContract.EventEntry.SPORT              + " TEXT NOT NULL,"             +
                SportteamContract.EventEntry.FIELD              + " TEXT,"                      +
                SportteamContract.EventEntry.ADDRESS            + " TEXT,"                      +
                SportteamContract.EventEntry.FIELD_LATITUDE     + " REAL,"                      +
                SportteamContract.EventEntry.FIELD_LONGITUDE    + " REAL,"                      +
                SportteamContract.EventEntry.NAME               + " TEXT,"                      +
                SportteamContract.EventEntry.CITY               + " TEXT,"                      +
                SportteamContract.EventEntry.DATE               + " INTEGER,"                   +
                SportteamContract.EventEntry.OWNER              + " TEXT NOT NULL,"             +
                SportteamContract.EventEntry.TOTAL_PLAYERS      + " INTEGER,"                   +
                SportteamContract.EventEntry.EMPTY_PLAYERS      + " INTEGER,"                   +
                " UNIQUE (" + SportteamContract.EventEntry.EVENT_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_ALARM_TABLE = "CREATE TABLE " + SportteamContract.TABLE_ALARM + " (" +
                SportteamContract.AlarmEntry._ID                    + " INTEGER PRIMARY KEY,"   +
                SportteamContract.AlarmEntry.ALARM_ID               + " TEXT UNIQUE NOT NULL,"  +
                SportteamContract.AlarmEntry.SPORT                  + " TEXT NOT NULL,"         +
                SportteamContract.AlarmEntry.FIELD                  + " TEXT,"                  +
                SportteamContract.AlarmEntry.CITY                   + " TEXT NOT NULL,"         +
                SportteamContract.AlarmEntry.COORD_LATITUDE         + " REAL,"                  +
                SportteamContract.AlarmEntry.COORD_LONGITUDE        + " REAL,"                  +
                SportteamContract.AlarmEntry.DATE_FROM              + " INTEGER,"               +
                SportteamContract.AlarmEntry.DATE_TO                + " INTEGER,"               +
                SportteamContract.AlarmEntry.TOTAL_PLAYERS_FROM     + " INTEGER,"               +
                SportteamContract.AlarmEntry.TOTAL_PLAYERS_TO       + " INTEGER,"               +
                SportteamContract.AlarmEntry.EMPTY_PLAYERS_FROM     + " INTEGER,"               +
                SportteamContract.AlarmEntry.EMPTY_PLAYERS_TO       + " INTEGER,"               +
                " UNIQUE (" + SportteamContract.AlarmEntry.ALARM_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FIELD_TABLE = "CREATE TABLE " + SportteamContract.TABLE_FIELD + " (" +
                SportteamContract.FieldEntry._ID                + " INTEGER PRIMARY KEY,"   +
                SportteamContract.FieldEntry.FIELD_ID           + " TEXT NOT NULL,"         +
                SportteamContract.FieldEntry.NAME               + " TEXT NOT NULL,"         +
                SportteamContract.FieldEntry.ADDRESS            + " TEXT,"                  +
                SportteamContract.FieldEntry.ADDRESS_LATITUDE   + " REAL,"                  +
                SportteamContract.FieldEntry.ADDRESS_LONGITUDE  + " REAL,"                  +
                SportteamContract.FieldEntry.CITY               + " TEXT,"                  +
                SportteamContract.FieldEntry.OPENING_TIME       + " INTEGER,"               +
                SportteamContract.FieldEntry.CLOSING_TIME       + " INTEGER,"               +
                SportteamContract.FieldEntry.CREATOR            + " TEXT,"                  +
                " UNIQUE (" + SportteamContract.FieldEntry.FIELD_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_SPORT_TABLE = "CREATE TABLE " + SportteamContract.TABLE_USER_SPORTS + " (" +
                SportteamContract.UserSportEntry._ID            + " INTEGER PRIMARY KEY,"       +
                SportteamContract.UserSportEntry.USER_ID        + " TEXT NOT NULL,"             +
                SportteamContract.UserSportEntry.SPORT          + " TEXT NOT NULL,"             +
                SportteamContract.UserSportEntry.LEVEL          + " REAL NOT NULL,"             +
                " UNIQUE (" + SportteamContract.UserSportEntry.USER_ID  + ", "
                            + SportteamContract.UserSportEntry.SPORT    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FIELD_SPORT_TABLE = "CREATE TABLE " + SportteamContract.TABLE_FIELD_SPORTS + " (" +
                SportteamContract.FieldSportEntry._ID            + " INTEGER PRIMARY KEY,"       +
                SportteamContract.FieldSportEntry.FIELD_ID       + " TEXT NOT NULL,"             +
                SportteamContract.FieldSportEntry.SPORT          + " TEXT NOT NULL,"             +
                SportteamContract.FieldSportEntry.PUNCTUATION    + " REAL NOT NULL,"             +
                SportteamContract.FieldSportEntry.VOTES          + " INTEGER NOT NULL,"          +
                " UNIQUE (" + SportteamContract.FieldSportEntry.FIELD_ID + ", "
                            + SportteamContract.FieldSportEntry.SPORT    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_SIMULATED_PARTICIPANT_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EVENT_SIMULATED_PARTICIPANT + " (" +
                SportteamContract.SimulatedParticipantEntry._ID                 + " INTEGER PRIMARY KEY,"   +
                SportteamContract.SimulatedParticipantEntry.EVENT_ID            + " TEXT NOT NULL,"         +
                SportteamContract.SimulatedParticipantEntry.SIMULATED_USER_ID   + " TEXT NOT NULL,"         +
                SportteamContract.SimulatedParticipantEntry.ALIAS               + " TEXT NOT NULL,"         +
                SportteamContract.SimulatedParticipantEntry.PROFILE_PICTURE     + " TEXT,"                  +
                SportteamContract.SimulatedParticipantEntry.AGE                 + " INTEGER,"               +
                SportteamContract.SimulatedParticipantEntry.OWNER               + " TEXT,"                  +
                " UNIQUE (" + SportteamContract.SimulatedParticipantEntry.EVENT_ID + ", "
                + SportteamContract.SimulatedParticipantEntry.SIMULATED_USER_ID    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FRIEND_REQUESTS_TABLE = "CREATE TABLE " + SportteamContract.TABLE_FRIENDS_REQUESTS + " (" +
                SportteamContract.FriendRequestEntry._ID                + " INTEGER PRIMARY KEY,"       +
                SportteamContract.FriendRequestEntry.RECEIVER_ID        + " TEXT NOT NULL,"             +
                SportteamContract.FriendRequestEntry.SENDER_ID          + " TEXT NOT NULL,"             +
                SportteamContract.FriendRequestEntry.DATE               + " INTEGER NOT NULL,"          +
                " UNIQUE (" + SportteamContract.FriendRequestEntry.RECEIVER_ID  + ", "
                            + SportteamContract.FriendRequestEntry.SENDER_ID    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FRIENDS_TABLE = "CREATE TABLE " + SportteamContract.TABLE_FRIENDS + " (" +
                SportteamContract.FriendsEntry._ID              + " INTEGER PRIMARY KEY,"       +
                SportteamContract.FriendsEntry.MY_USER_ID       + " TEXT NOT NULL,"             +
                SportteamContract.FriendsEntry.USER_ID          + " TEXT NOT NULL,"             +
                SportteamContract.FriendsEntry.DATE             + " INTEGER NOT NULL,"          +
                " UNIQUE (" + SportteamContract.FriendsEntry.USER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_EVENT_PARTICIPATION_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EVENTS_PARTICIPATION + " (" +
                SportteamContract.EventsParticipationEntry._ID              + " INTEGER PRIMARY KEY,"       +
                SportteamContract.EventsParticipationEntry.USER_ID          + " TEXT NOT NULL,"             +
                SportteamContract.EventsParticipationEntry.EVENT_ID         + " TEXT NOT NULL,"             +
                SportteamContract.EventsParticipationEntry.PARTICIPATES     + " INTEGER NOT NULL,"          +
                " UNIQUE (" + SportteamContract.EventsParticipationEntry.USER_ID    + ", "
                            + SportteamContract.EventsParticipationEntry.EVENT_ID   + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_EVENT_INVITATIONS_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EVENT_INVITATIONS + " (" +
                SportteamContract.EventsInvitationEntry._ID         + " INTEGER PRIMARY KEY,"       +
                SportteamContract.EventsInvitationEntry.RECEIVER_ID + " TEXT NOT NULL,"             +
                SportteamContract.EventsInvitationEntry.SENDER_ID   + " TEXT NOT NULL,"             +
                SportteamContract.EventsInvitationEntry.EVENT_ID    + " TEXT NOT NULL,"             +
                SportteamContract.EventsInvitationEntry.DATE        + " INTEGER NOT NULL,"          +
                " UNIQUE (" + SportteamContract.EventsInvitationEntry.RECEIVER_ID   + ", "
                            + SportteamContract.EventsInvitationEntry.EVENT_ID      + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_EVENTS_REQUESTS_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EVENTS_REQUESTS + " (" +
                SportteamContract.EventRequestsEntry._ID                + " INTEGER PRIMARY KEY,"       +
                SportteamContract.EventRequestsEntry.EVENT_ID           + " TEXT NOT NULL,"             +
                SportteamContract.EventRequestsEntry.SENDER_ID          + " TEXT NOT NULL,"             +
                SportteamContract.EventRequestsEntry.DATE               + " INTEGER NOT NULL,"          +
                " UNIQUE (" + SportteamContract.EventRequestsEntry.EVENT_ID     + ", "
                            + SportteamContract.EventRequestsEntry.SENDER_ID    + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_EMAIL_LOGGED_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ALARM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FIELD_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_SPORT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FIELD_SPORT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SIMULATED_PARTICIPANT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIEND_REQUESTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIENDS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_PARTICIPATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_INVITATIONS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENTS_REQUESTS_TABLE);
    }

    /**
     * Invocado cuando es necesaria la actualización de la base de datos, al aumentar el número de
     * la versión {@link #DATABASE_VERSION}. Ejecuta las sentencias SQL necesarias para
     * borrar las tablas. A continuación se ejecuta {@link #onCreate(SQLiteDatabase)}
     *
     * @param sqLiteDatabase la base de datos
     * @param oldVersion     la versión antigua de la base de datos
     * @param newVersion     la versión nueva de la base de datos
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EMAIL_LOGGED);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EVENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_ALARM);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_FIELD);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_USER_SPORTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_FIELD_SPORTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EVENT_SIMULATED_PARTICIPANT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_FRIENDS_REQUESTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_FRIENDS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EVENTS_PARTICIPATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EVENT_INVITATIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EVENTS_REQUESTS);
        onCreate(sqLiteDatabase);
    }
}

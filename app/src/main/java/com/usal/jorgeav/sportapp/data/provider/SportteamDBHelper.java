package com.usal.jorgeav.sportapp.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class SportteamDBHelper extends SQLiteOpenHelper {

    /*
     * This is the name of our database. Database names should be descriptive and end with the
     * .db extension.
     */
    public static final String DATABASE_NAME = "sportteam.db";
    /*
     * If you change the database schema, you must increment the database version or the onUpgrade
     * method will not be called.
     */
    private static final int DATABASE_VERSION = 4;

    public SportteamDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
//        "foreign key (" + COLUMN_ARTICLE + ") references " + TABLE_ARTICLES + "(" + COLUMN_ARTICLE_ID + "));";
        final String SQL_CREATE_USER_TABLE = "CREATE TABLE " + SportteamContract.TABLE_USER + " (" +
                SportteamContract.UserEntry._ID         + " INTEGER PRIMARY KEY,"       +
                SportteamContract.UserEntry.USER_ID     + " TEXT UNIQUE NOT NULL,"      +
                SportteamContract.UserEntry.EMAIL       + " TEXT UNIQUE NOT NULL,"      +
                SportteamContract.UserEntry.NAME        + " TEXT UNIQUE NOT NULL,"      +
                SportteamContract.UserEntry.AGE         + " INTEGER,"                   +
                SportteamContract.UserEntry.CITY        + " TEXT,"                      +
                SportteamContract.UserEntry.PHOTO       + " TEXT,"                      +
                " UNIQUE (" + SportteamContract.UserEntry.USER_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_EVENT_TABLE = "CREATE TABLE " + SportteamContract.TABLE_EVENT + " (" +
                SportteamContract.EventEntry._ID                + " INTEGER PRIMARY KEY,"       +
                SportteamContract.EventEntry.EVENT_ID           + " TEXT UNIQUE NOT NULL,"      +
                SportteamContract.EventEntry.SPORT              + " TEXT NOT NULL,"             +
                SportteamContract.EventEntry.FIELD              + " TEXT,"                      +
                SportteamContract.EventEntry.CITY               + " TEXT,"                      +
                SportteamContract.EventEntry.DATE               + " INTEGER,"                   +
                SportteamContract.EventEntry.OWNER              + " TEXT NOT NULL,"             +
                SportteamContract.EventEntry.TOTAL_PLAYERS      + " INTEGER,"                   +
                SportteamContract.EventEntry.EMPTY_PLAYERS      + " INTEGER,"                   +
                " UNIQUE (" + SportteamContract.EventEntry.EVENT_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FIELD_TABLE = "CREATE TABLE " + SportteamContract.TABLE_FIELD + " (" +
                SportteamContract.FieldEntry._ID                + " INTEGER PRIMARY KEY,"   +
                SportteamContract.FieldEntry.FIELD_ID           + " TEXT NOT NULL,"         +
                SportteamContract.FieldEntry.NAME               + " TEXT NOT NULL,"         +
                SportteamContract.FieldEntry.SPORT              + " TEXT NOT NULL,"         +
                SportteamContract.FieldEntry.ADDRESS            + " TEXT,"                  +
                SportteamContract.FieldEntry.CITY               + " TEXT,"                  +
                SportteamContract.FieldEntry.PUNCTUATION        + " REAL,"                  +
                SportteamContract.FieldEntry.VOTES              + " INTEGER,"               +
                SportteamContract.FieldEntry.OPENING_TIME       + " INTEGER,"               +
                SportteamContract.FieldEntry.CLOSING_TIME       + " INTEGER,"               +
                " UNIQUE (" + SportteamContract.FieldEntry.FIELD_ID + ", "
                            + SportteamContract.FieldEntry.SPORT    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_USER_SPORT_TABLE = "CREATE TABLE " + SportteamContract.TABLE_USER_SPORTS + " (" +
                SportteamContract.UserSportEntry._ID            + " INTEGER PRIMARY KEY,"       +
                SportteamContract.UserSportEntry.USER_ID        + " TEXT NOT NULL,"             +
                SportteamContract.UserSportEntry.SPORT          + " TEXT NOT NULL,"             +
                SportteamContract.UserSportEntry.LEVEL          + " REAL NOT NULL,"             +
                " UNIQUE (" + SportteamContract.UserSportEntry.USER_ID + ", "
                + SportteamContract.UserSportEntry.SPORT    + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_FRIEND_REQUESTS_TABLE = "CREATE TABLE " + SportteamContract.TABLE_FRIENDS_REQUESTS + " (" +
                SportteamContract.FriendRequestEntry._ID                + " INTEGER PRIMARY KEY,"       +
                SportteamContract.FriendRequestEntry.RECEIVER_ID        + " TEXT NOT NULL,"             +
                SportteamContract.FriendRequestEntry.SENDER_ID          + " TEXT NOT NULL,"             +
                SportteamContract.FriendRequestEntry.DATE               + " INTEGER NOT NULL,"             +
                " UNIQUE (" + SportteamContract.FriendRequestEntry.SENDER_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_EVENT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FIELD_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_USER_SPORT_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FRIEND_REQUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_EVENT);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_FIELD);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_USER_SPORTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SportteamContract.TABLE_FRIENDS_REQUESTS);
        onCreate(sqLiteDatabase);
    }
}

package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract.JoinQueryEntries;

import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.CONTENT_AUTHORITY;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventRequestsEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventsInvitationEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.EventsParticipationEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.FieldEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.FriendRequestEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.FriendsEntry;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_PARTICIPATION;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_PARTICIPATION_WITH_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_PARTICIPATION_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENTS_REQUESTS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_INVITATIONS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_INVITATIONS_WITH_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_EVENT_INVITATIONS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FIELDS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_REQUESTS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_FRIENDS_WITH_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_USERS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.PATH_USER_SPORT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENT;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENTS_PARTICIPATION;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENTS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_EVENT_INVITATIONS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FIELD;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FRIENDS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_FRIENDS_REQUESTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_USER;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.TABLE_USER_SPORTS;
import static com.usal.jorgeav.sportapp.data.provider.SportteamContract.UserEntry;

public class SportteamProvider extends ContentProvider {
    private static final String TAG = SportteamProvider.class.getSimpleName();

    public static final int CODE_USERS = 100;
    public static final int CODE_USER_SPORT = 200;
    public static final int CODE_FIELDS = 300;
    public static final int CODE_EVENTS = 400;
    public static final int CODE_FRIEND_REQUEST = 500;
    public static final int CODE_FRIEND_REQUEST_WITH_USER = 510;
    public static final int CODE_FRIEND = 600;
    public static final int CODE_FRIEND_WITH_USER = 610;
    public static final int CODE_EVENTS_PARTICIPATION = 700;
    public static final int CODE_EVENTS_PARTICIPATION_WITH_USER = 710;
    public static final int CODE_EVENTS_PARTICIPATION_WITH_EVENT = 720;
    public static final int CODE_EVENT_INVITATIONS = 800;
    public static final int CODE_EVENT_INVITATIONS_WITH_USER = 810;
    public static final int CODE_EVENT_INVITATIONS_WITH_EVENT = 820;
    public static final int CODE_EVENTS_REQUESTS = 900;
    public static final int CODE_EVENTS_REQUESTS_WITH_USER = 910;

    public static final int CODE_EVENTS_WITHOUT_RELATION_WITH_FRIEND = 10;
    public static final int CODE_FRIENDS_WITHOUT_RELATION_WITH_EVENT = 20;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SportteamDBHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CONTENT_AUTHORITY;

        // This URI is content://com.usal.jorgeav.sportapp/users/
        matcher.addURI(authority, PATH_USERS, CODE_USERS);
        // This URI is content://com.usal.jorgeav.sportapp/userSport/
        matcher.addURI(authority, PATH_USER_SPORT, CODE_USER_SPORT);
        // This URI is content://com.usal.jorgeav.sportapp/fields/
        matcher.addURI(authority, PATH_FIELDS, CODE_FIELDS);
        // This URI is content://com.usal.jorgeav.sportapp/events/
        matcher.addURI(authority, PATH_EVENTS, CODE_EVENTS);
        // This URI is content://com.usal.jorgeav.sportapp/friendRequests/
        matcher.addURI(authority, PATH_FRIENDS_REQUESTS, CODE_FRIEND_REQUEST);
        // This URI is content://com.usal.jorgeav.sportapp/friendRequests_user/
        matcher.addURI(authority, PATH_FRIENDS_REQUESTS_WITH_USER, CODE_FRIEND_REQUEST_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/friends/
        matcher.addURI(authority, PATH_FRIENDS, CODE_FRIEND);
        // This URI is content://com.usal.jorgeav.sportapp/friends_user/
        matcher.addURI(authority, PATH_FRIENDS_WITH_USER, CODE_FRIEND_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventsParticipation/
        matcher.addURI(authority, PATH_EVENTS_PARTICIPATION, CODE_EVENTS_PARTICIPATION);
        // This URI is content://com.usal.jorgeav.sportapp/eventsParticipation_user/
        matcher.addURI(authority, PATH_EVENTS_PARTICIPATION_WITH_USER, CODE_EVENTS_PARTICIPATION_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventsParticipation_event/
        matcher.addURI(authority, PATH_EVENTS_PARTICIPATION_WITH_EVENT, CODE_EVENTS_PARTICIPATION_WITH_EVENT);
        // This URI is content://com.usal.jorgeav.sportapp/eventInvitations/
        matcher.addURI(authority, PATH_EVENT_INVITATIONS, CODE_EVENT_INVITATIONS);
        // This URI is content://com.usal.jorgeav.sportapp/eventInvitations_user/
        matcher.addURI(authority, PATH_EVENT_INVITATIONS_WITH_USER, CODE_EVENT_INVITATIONS_WITH_USER);
        // This URI is content://com.usal.jorgeav.sportapp/eventInvitations_event/
        matcher.addURI(authority, PATH_EVENT_INVITATIONS_WITH_EVENT, CODE_EVENT_INVITATIONS_WITH_EVENT);
        // This URI is content://com.usal.jorgeav.sportapp/eventRequests/
        matcher.addURI(authority, PATH_EVENTS_REQUESTS, CODE_EVENTS_REQUESTS);
        // This URI is content://com.usal.jorgeav.sportapp/eventRequests_user/
        matcher.addURI(authority, PATH_EVENTS_REQUESTS_WITH_USER, CODE_EVENTS_REQUESTS_WITH_USER);

        // This URI is content://com.usal.jorgeav.sportapp/myEvent_friendUser/
        matcher.addURI(authority, PATH_MY_EVENTS_WITHOUT_RELATION_WITH_FRIEND, CODE_EVENTS_WITHOUT_RELATION_WITH_FRIEND);
        // This URI is content://com.usal.jorgeav.sportapp/friendsUser_myEvent/
        matcher.addURI(authority, PATH_FRIENDS_WITHOUT_RELATION_WITH_MY_EVENTS, CODE_FRIENDS_WITHOUT_RELATION_WITH_EVENT);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new SportteamDBHelper(getContext());
        return false;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                return bulkInsert(uri, values, db, TABLE_EVENT);
            case CODE_FIELDS:
                return bulkInsert(uri, values, db, TABLE_FIELD);
            case CODE_USER_SPORT:
                return bulkInsert(uri, values, db, TABLE_USER_SPORTS);
            case CODE_FRIEND_REQUEST:
                return bulkInsert(uri, values, db, TABLE_FRIENDS_REQUESTS);
            case CODE_FRIEND:
                return bulkInsert(uri, values, db, TABLE_FRIENDS);
            case CODE_EVENTS_PARTICIPATION:
                return bulkInsert(uri, values, db, TABLE_EVENTS_PARTICIPATION);
            case CODE_EVENT_INVITATIONS:
                return bulkInsert(uri, values, db, TABLE_EVENT_INVITATIONS);
            case CODE_EVENTS_REQUESTS:
                return bulkInsert(uri, values, db, TABLE_EVENTS_REQUESTS);
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values, SQLiteDatabase db, String tableName) {
        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) rowsInserted++;
            }
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); }
        if (rowsInserted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsInserted;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri = null;
        long _id = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_USERS:
                _id = db.insert(TABLE_USER, null, values);
                if ( _id > 0 ) returnUri = UserEntry.buildUserUriWith(_id);
                break;
            case CODE_EVENTS:
                _id = db.insert(TABLE_EVENT, null, values);
                if ( _id > 0 ) returnUri = EventEntry.buildEventUriWith(_id);
                break;
            case CODE_FIELDS:
                _id = db.insert(TABLE_FIELD, null, values);
                if ( _id > 0 ) returnUri = FieldEntry.buildFieldUriWith(_id);
                break;
            case CODE_FRIEND_REQUEST:
                _id = db.insert(TABLE_FRIENDS_REQUESTS, null, values);
                if ( _id > 0 ) returnUri = FriendRequestEntry.buildFriendRequestsUriWith(_id);
                break;
            case CODE_FRIEND:
                _id = db.insert(TABLE_FRIENDS, null, values);
                if ( _id > 0 ) returnUri = FriendsEntry.buildFriendsUriWith(_id);
                break;
            case CODE_EVENTS_PARTICIPATION:
                _id = db.insert(TABLE_EVENTS_PARTICIPATION, null, values);
                if ( _id > 0 ) returnUri = EventsParticipationEntry.buildEventsParticipationUriWith(_id);
                break;
            case CODE_EVENT_INVITATIONS:
                _id = db.insert(TABLE_EVENT_INVITATIONS, null, values);
                if ( _id > 0 ) returnUri = EventsInvitationEntry.buildEventInvitationUriWith(_id);
                break;
            case CODE_EVENTS_REQUESTS:
                _id = db.insert(TABLE_EVENTS_REQUESTS, null, values);
                if ( _id > 0 ) returnUri = EventRequestsEntry.buildEventRequestsUriWith(_id);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_USERS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_USER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FIELDS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_FIELD,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_EVENT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_USER_SPORT:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_USER_SPORTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FRIEND_REQUEST:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_FRIENDS_REQUESTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FRIEND_REQUEST_WITH_USER:
                builder.setTables(FriendRequestEntry.TABLES_FRIENDS_REQUESTS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FRIEND:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_FRIENDS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FRIEND_WITH_USER:
                builder.setTables(FriendsEntry.TABLES_FRIENDS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS_PARTICIPATION:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_EVENTS_PARTICIPATION,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS_PARTICIPATION_WITH_USER:
                builder.setTables(EventsParticipationEntry.TABLES_EVENTS_PARTICIPATION_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS_PARTICIPATION_WITH_EVENT:
                builder.setTables(EventsParticipationEntry.TABLES_EVENTS_PARTICIPATION_JOIN_EVENT);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENT_INVITATIONS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_EVENT_INVITATIONS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENT_INVITATIONS_WITH_USER:
                builder.setTables(EventsInvitationEntry.TABLES_EVENT_INVITATIONS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENT_INVITATIONS_WITH_EVENT:
                builder.setTables(EventsInvitationEntry.TABLES_EVENT_INVITATIONS_JOIN_EVENT);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS_REQUESTS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TABLE_EVENTS_REQUESTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS_REQUESTS_WITH_USER:
                builder.setTables(EventRequestsEntry.TABLES_EVENTS_REQUESTS_JOIN_USER);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS_WITHOUT_RELATION_WITH_FRIEND:
                builder.setTables(JoinQueryEntries.TABLES_EVENTS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FRIENDS_WITHOUT_RELATION_WITH_EVENT:
                builder.setTables(JoinQueryEntries.TABLES_USERS_JOIN_FRIENDS_JOIN_PARTICIPATION_JOIN_INVITATIONS_JOIN_REQUESTS);
                cursor = builder.query(mOpenHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                Log.d(TAG, "query: "+builder.buildQuery(projection,selection,selectionArgs,null,null,sortOrder,null));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

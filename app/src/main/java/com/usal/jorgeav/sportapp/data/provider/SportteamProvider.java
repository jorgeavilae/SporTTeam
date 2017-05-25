package com.usal.jorgeav.sportapp.data.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class SportteamProvider extends ContentProvider {

    public static final int CODE_EVENTS = 100;
    public static final int CODE_FIELDS = 200;
    public static final int CODE_USERS = 300;
    public static final int CODE_USER_SPORT = 400;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SportteamDBHelper mOpenHelper;

    public static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SportteamContract.CONTENT_AUTHORITY;

        // This URI is content://com.usal.jorgeav.sportapp/events/
        matcher.addURI(authority, SportteamContract.PATH_EVENTS, CODE_EVENTS);
        // This URI is content://com.usal.jorgeav.sportapp/fields/
        matcher.addURI(authority, SportteamContract.PATH_FIELDS, CODE_FIELDS);
        // This URI is content://com.usal.jorgeav.sportapp/users/
        matcher.addURI(authority, SportteamContract.PATH_USERS, CODE_USERS);
        // This URI is content://com.usal.jorgeav.sportapp/userSport/
        matcher.addURI(authority, SportteamContract.PATH_USER_SPORT, CODE_USER_SPORT);

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
        int rowsInserted = 0;
        switch (sUriMatcher.match(uri)) {
            case CODE_EVENTS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SportteamContract.TABLE_EVENT, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_FIELDS:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SportteamContract.TABLE_FIELD, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_USER_SPORT:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(SportteamContract.TABLE_USER_SPORTS, null, value);
                        if (_id != -1) rowsInserted++;
                    }
                    db.setTransactionSuccessful();
                } finally { db.endTransaction(); }
                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsInserted;
            default:
                return super.bulkInsert(uri, values);
        }
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
                _id = db.insert(SportteamContract.TABLE_USER, null, values);
                if ( _id > 0 )
                    returnUri = SportteamContract.UserEntry.buildUserUriWith(_id);
                break;
            case CODE_EVENTS:
                _id = db.insert(SportteamContract.TABLE_EVENT, null, values);
                if ( _id > 0 )
                    returnUri = SportteamContract.EventEntry.buildEventUriWith(_id);
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
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_USERS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SportteamContract.TABLE_USER,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_FIELDS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SportteamContract.TABLE_FIELD,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_EVENTS:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SportteamContract.TABLE_EVENT,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CODE_USER_SPORT:
                cursor = mOpenHelper.getReadableDatabase().query(
                        SportteamContract.TABLE_USER_SPORTS,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
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

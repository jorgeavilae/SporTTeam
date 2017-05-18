package com.usal.jorgeav.sportapp.network;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 18/05/2017.
 */

public class FirebaseDatabaseActions {
    public static final String TAG = FirebaseDatabaseActions.class.getSimpleName();

    public static void loadEvents(Context context/*, query arguments*/) {
        Log.d(TAG, "loadEvents (Network Call)");


        String mId = "67ht67ty9hi485g94u5hi";
        String mSport = "basketball";
        String mField = "An awesome field!";
        String mCity = "My city";
        Long mDate = System.currentTimeMillis();
        String mOwner = "3kjfnf44fuen498fwieufb834fg7h";
        int mTotalPlayers = 10;
        int mEmptyPlayers = 10;

        ContentValues[] contentValues = new ContentValues[20];
        for (int i = 0; i < contentValues.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.EventEntry.EVENT_ID, mId + i);
            cv.put(SportteamContract.EventEntry.SPORT, mSport);
            cv.put(SportteamContract.EventEntry.FIELD, mField);
            cv.put(SportteamContract.EventEntry.CITY, mCity);
            cv.put(SportteamContract.EventEntry.DATE, mDate);
            cv.put(SportteamContract.EventEntry.OWNER, mOwner);
            cv.put(SportteamContract.EventEntry.TOTAL_PLAYERS, mTotalPlayers);
            cv.put(SportteamContract.EventEntry.EMPTY_PLAYERS, mEmptyPlayers);
            contentValues[i] = cv;
        }

        context.getContentResolver()
                .bulkInsert(SportteamContract.EventEntry.CONTENT_EVENT_URI, contentValues);
    }

    public static void loadFields(Context context/*, query arguments*/) {
        Log.d(TAG, "loadFields (Network Call)");

        String mId = "67ht67ty9hi485g94u5hi";
        String mName = "Salas Bajas";
        String mSport = "basketball";
        String mAddress = "Calle falsa, nº 123";
        String mCity = "My city";
        float mRating = 3.5f;
        int mVotes = 5;
        long mOpeningTime = 0;
        long mClosingTime = 0;

        ContentValues[] contentValues = new ContentValues[20];
        for (int i = 0; i < contentValues.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.FieldEntry.FIELD_ID, mId + i);
            cv.put(SportteamContract.FieldEntry.NAME, mName);
            cv.put(SportteamContract.FieldEntry.SPORT, mSport);
            cv.put(SportteamContract.FieldEntry.ADDRRESS, mAddress);
            cv.put(SportteamContract.FieldEntry.CITY, mCity);
            cv.put(SportteamContract.FieldEntry.PUNTUATION, mRating);
            cv.put(SportteamContract.FieldEntry.VOTES, mVotes);
            cv.put(SportteamContract.FieldEntry.OPENING_TIME, mOpeningTime);
            cv.put(SportteamContract.FieldEntry.CLOSING_TIME, mClosingTime);
            contentValues[i] = cv;
        }

        context.getContentResolver()
                .bulkInsert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, contentValues);
    }

    public static void loadProfile(Context context) {
        //With FirebaseUser.getUID()
        Log.d(TAG, "loadMiProfile (Network Call)");
        //On callback: load friends, events, and get notifications

        //And with the data...
        String mId = "67ht67ty9hi485g94u5hi";
        String mEmail = "email@email.com";
        String mName = "Nombre Apellido";
        String mCity = "Ciudad, Pais";
        int mAge = 30;
        String mPhotoUrl = "cadena de texto que es una url";

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.UserEntry.USER_ID, mId);
        cv.put(SportteamContract.UserEntry.EMAIL, mEmail);
        cv.put(SportteamContract.UserEntry.NAME, mName);
        cv.put(SportteamContract.UserEntry.CITY, mCity);
        cv.put(SportteamContract.UserEntry.AGE, mAge);
        cv.put(SportteamContract.UserEntry.PHOTO, mPhotoUrl);

        context.getContentResolver()
                .insert(SportteamContract.UserEntry.CONTENT_USER_URI, cv);
    }
}

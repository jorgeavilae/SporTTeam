package com.usal.jorgeav.sportapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class EventsRepository {
    private static final String TAG = EventsRepository.class.getSimpleName();

    public EventsRepository() {
    }

//    public List<Event> getDataset() {
//        if (this.mDataset == null) {
//            mDataset = new ArrayList<>();
//            loadEvents();
//        }
//        return mDataset;
//    }

    public void loadEvents(Context context/*query arguments*/) {
        Log.d(TAG, "loadEvents (Network Call)");

        ContentValues[] contentValues = new ContentValues[20];

        String mId = "67ht67ty9hi485g94u5hi";
        String mSport = "basketball";
        String mField = "An awesome field!";
        String mCity = "My city";
        Long mDate = System.currentTimeMillis();
        String mOwner = "3kjfnf44fuen498fwieufb834fg7h";
        int mTotalPlayers = 10;
        int mEmptyPlayers = 10;

        for (int i = 0; i < contentValues.length; i++) {
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.EventEntry.EVENT_ID, mId+i);
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
}

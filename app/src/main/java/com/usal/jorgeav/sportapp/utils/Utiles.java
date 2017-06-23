package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.database.Cursor;

import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class Utiles {
    private static final String TAG = Utiles.class.getSimpleName();

    public static String getCurrentCity(Context context, String currentUserID) {
        // TODO: 23/06/2017 obtener de sharedPreferences
        String result = null;
        Cursor c = context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{currentUserID},
                null);
        if (c != null && c.moveToFirst()) {
            result = c.getString(SportteamContract.UserEntry.COLUMN_CITY);
            c.close();
        }
        return result;
    }

    public static Alarm cursorToAlarm(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            String alarmId = cursor.getString(SportteamContract.AlarmEntry.COLUMN_ALARM_ID);
            String sport = cursor.getString(SportteamContract.AlarmEntry.COLUMN_SPORT);
            String field = cursor.getString(SportteamContract.AlarmEntry.COLUMN_FIELD);
            String city = cursor.getString(SportteamContract.AlarmEntry.COLUMN_CITY);
            Long dateFrom = null;
            if (!cursor.isNull(SportteamContract.AlarmEntry.COLUMN_DATE_FROM))
                dateFrom = cursor.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_FROM);
            Long dateTo = null;
            if (!cursor.isNull(SportteamContract.AlarmEntry.COLUMN_DATE_TO))
                dateTo = cursor.getLong(SportteamContract.AlarmEntry.COLUMN_DATE_TO);
            Long totalPlFrom = null;
            if (!cursor.isNull(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_FROM))
                totalPlFrom = cursor.getLong(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_FROM);
            Long totalPlTo = null;
            if (!cursor.isNull(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_TO))
                totalPlTo = cursor.getLong(SportteamContract.AlarmEntry.COLUMN_TOTAL_PLAYERS_TO);
            Long emptyPlFrom = null;
            if (!cursor.isNull(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_FROM))
                emptyPlFrom = cursor.getLong(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_FROM);
            Long emptyPlTo = null;
            if (!cursor.isNull(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_TO))
                emptyPlTo = cursor.getLong(SportteamContract.AlarmEntry.COLUMN_EMPTY_PLAYERS_TO);

            return new Alarm(alarmId, sport, field, city,
                    dateFrom, dateTo,
                    totalPlFrom, totalPlTo,
                    emptyPlFrom, emptyPlTo);
    }
        return null;
    }
}

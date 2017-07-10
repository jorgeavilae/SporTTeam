package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

import java.util.ArrayList;

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

    public static User getUserFromContentProvider(String userId) {
        User u = null;
        Cursor c = SportteamLoader.simpleQueryUserId(MyApplication.getAppContext(), userId);
        if (c != null) {
            if (c.getCount() == 1 && c.moveToFirst()) {
                String email = c.getString(SportteamContract.UserEntry.COLUMN_EMAIL);
                String name = c.getString(SportteamContract.UserEntry.COLUMN_NAME);
                String city = c.getString(SportteamContract.UserEntry.COLUMN_CITY);
                int age = c.getInt(SportteamContract.UserEntry.COLUMN_AGE);
                String photoUrl = c.getString(SportteamContract.UserEntry.COLUMN_PHOTO);
                u = new User(userId, email, name, city, age, photoUrl, null);
            } else if (c.getCount() == 0)
                Log.e(TAG, "getUserFromContentProvider: User with ID "+userId+" not found");
            else
                Log.e(TAG, "getUserFromContentProvider: More than one user with ID "+userId+" ("+c.getCount()+")");
            c.close();
        } else
            Log.e(TAG, "getUserFromContentProvider: Error with user "+userId);
        return u;
    }

    public static Event getEventFromContentProvider(String eventId) {
        Event e = null;
        Cursor c = SportteamLoader.simpleQueryEventId(MyApplication.getAppContext(), eventId);
        if (c != null) {
            if (c.getCount() == 1 && c.moveToFirst()) {
                String sport = c.getString(SportteamContract.EventEntry.COLUMN_SPORT);
                String field = c.getString(SportteamContract.EventEntry.COLUMN_FIELD);
                String name = c.getString(SportteamContract.EventEntry.COLUMN_NAME);
                String city = c.getString(SportteamContract.EventEntry.COLUMN_CITY);
                Long date = c.getLong(SportteamContract.EventEntry.COLUMN_DATE);
                String owner = c.getString(SportteamContract.EventEntry.COLUMN_OWNER);
                int totalPl = c.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
                int emptyPl = c.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);
                e = new Event(eventId, sport, field, name, city, date, owner, totalPl, emptyPl, null);
            } else if (c.getCount() == 0)
                Log.e(TAG, "getEventFromContentProvider: Event with ID "+eventId+" not found");
            else
                Log.e(TAG, "getEventFromContentProvider: More than one event with ID "+eventId+" ("+c.getCount()+")");
            c.close();
        } else
            Log.e(TAG, "getEventFromContentProvider: Error with event "+eventId);
        return e;
    }

    public static ArrayList<Alarm> getAllAlarms(Context context) {
        Cursor c = context.getContentResolver().query(
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                null, null, null);
        if (c != null) {
            ArrayList<Alarm> result = new ArrayList<>();
            if (c.getCount() > 0) {
                while (c.moveToNext()) {
                    Alarm a = cursorToAlarm(c); // TODO: 10/07/2017 error memory en este metodo
                    if (a != null) result.add(a);
                }
            } else
                Log.e(TAG, "getAllAlarms: No alarms");
            c.close();
            return result;
        } else
            Log.e(TAG, "getAllAlarms: Error with alarms");
        return null;
    }

    public static boolean thereIsEventCoincidence(Context context, String alarmId, String myUserId) {
        CursorLoader cl = SportteamLoader.cursorLoaderAlarmCoincidence(context, alarmId, myUserId);
        if (cl != null) {
            Cursor c = MyApplication.getAppContext().getContentResolver().query(
                    cl.getUri(), cl.getProjection(), cl.getSelection(),
                    cl.getSelectionArgs(), cl.getSortOrder());
            if (c != null) {
                if (c.getCount() > 0) return true;
                c.close();
            }
        }
        return false;
    }
}

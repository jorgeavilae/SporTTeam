package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;

import java.util.ArrayList;

public class UtilesContentProvider {
    private static final String TAG = UtilesContentProvider.class.getSimpleName();

    public static Alarm cursorToSingleAlarm(Cursor cursor) {
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

    public static ArrayList<Alarm> cursorToMultipleAlarm(Cursor cursor) {
        ArrayList<Alarm> result = new ArrayList<>();
        while (cursor.moveToNext()) {
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

            result.add(new Alarm(alarmId, sport, field, city, dateFrom,
                    dateTo, totalPlFrom, totalPlTo, emptyPlFrom, emptyPlTo));
        }
        return result;
    }

    public static ArrayList<Field> cursorToMultipleField(Cursor cursor) {
        ArrayList<Field> result = new ArrayList<>();
        while (cursor.moveToNext()) {
            String fieldId = cursor.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID);
            String name = cursor.getString(SportteamContract.FieldEntry.COLUMN_NAME);
            String sport = cursor.getString(SportteamContract.FieldEntry.COLUMN_SPORT);
            String address = cursor.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
            double lat = cursor.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
            double lng = cursor.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
            Log.d(TAG, "cursorToMultipleField: "+lat+" "+lng);
            LatLng coords = null; if (lat != 0 && lng != 0) coords = new LatLng(lat, lng);
            String city = cursor.getString(SportteamContract.FieldEntry.COLUMN_CITY);
            float rating = cursor.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION);
            int votes = cursor.getInt(SportteamContract.FieldEntry.COLUMN_VOTES);
            long openTime = cursor.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
            long closeTime = cursor.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
            String creator = cursor.getString(SportteamContract.FieldEntry.COLUMN_CREATOR);

            result.add(new Field(fieldId, name, sport, address, coords, city, rating, votes, openTime, closeTime, creator));
        }
        return result;
    }

    public static User getUserFromContentProvider(@NonNull String userId) {
        User u = null;
        Cursor c = SportteamLoader.simpleQueryUserId(MyApplication.getAppContext(), userId);
        if (c != null) {
            if (c.getCount() == 1 && c.moveToFirst()) {
                String email = c.getString(SportteamContract.UserEntry.COLUMN_EMAIL);
                String name = c.getString(SportteamContract.UserEntry.COLUMN_NAME);
                String city = c.getString(SportteamContract.UserEntry.COLUMN_CITY);
                double latitude = c.getDouble(SportteamContract.UserEntry.COLUMN_CITY_LATITUDE);
                double longitude = c.getDouble(SportteamContract.UserEntry.COLUMN_CITY_LONGITUDE);
                Log.d(TAG, "getUserFromContentProvider: "+latitude);
                Log.d(TAG, "getUserFromContentProvider: "+longitude);
                int age = c.getInt(SportteamContract.UserEntry.COLUMN_AGE);
                String photoUrl = c.getString(SportteamContract.UserEntry.COLUMN_PHOTO);

                LatLng coord = null;
                if (latitude > 0 && longitude > 0) coord = new LatLng(latitude, longitude);
                u = new User(userId, email, name, city, coord, age, photoUrl, null);
            } else if (c.getCount() == 0)
                Log.e(TAG, "getUserFromContentProvider: User with ID "+userId+" not found");
            else
                Log.e(TAG, "getUserFromContentProvider: More than one user with ID "+userId+" ("+c.getCount()+")");
            c.close();
        } else
            Log.e(TAG, "getUserFromContentProvider: Error with user "+userId);
        return u;
    }
    static String getCurrentUserCityFromContentProvider() {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;

        String result = null;
        Cursor c = MyApplication.getAppContext().getContentResolver().query(
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
    static LatLng getCurrentUserCityCoordsFromContentProvider() {
        String currentUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(currentUserID)) return null;

        LatLng result = null;
        Cursor c = MyApplication.getAppContext().getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{currentUserID},
                null);
        if (c != null && c.moveToFirst()) {
            double latitude = c.getDouble(SportteamContract.UserEntry.COLUMN_CITY_LATITUDE);
            double longitude = c.getDouble(SportteamContract.UserEntry.COLUMN_CITY_LONGITUDE);
            result = new LatLng(latitude, longitude);
            c.close();
        }
        return result;
    }

    public static Event getEventFromContentProvider(@NonNull String eventId) {
        Event e = null;
        Cursor c = SportteamLoader.simpleQueryEventId(MyApplication.getAppContext(), eventId);
        if (c != null) {
            if (c.getCount() == 1 && c.moveToFirst()) {
                String sport = c.getString(SportteamContract.EventEntry.COLUMN_SPORT);
                String field = c.getString(SportteamContract.EventEntry.COLUMN_FIELD);
                String address = c.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
                double latitude = c.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
                double longitude = c.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
                LatLng coord = null; if (latitude != 0 && longitude != 0) coord = new LatLng(latitude, longitude);
                String name = c.getString(SportteamContract.EventEntry.COLUMN_NAME);
                String city = c.getString(SportteamContract.EventEntry.COLUMN_CITY);
                Long date = c.getLong(SportteamContract.EventEntry.COLUMN_DATE);
                String owner = c.getString(SportteamContract.EventEntry.COLUMN_OWNER);
                int totalPl = c.getInt(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
                int emptyPl = c.getInt(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);

                e = new Event(eventId, sport, field, address, coord, name, city, date, owner, totalPl, emptyPl, null, null);
            } else if (c.getCount() == 0)
                Log.e(TAG, "getEventFromContentProvider: Event with ID "+eventId+" not found");
            else
                Log.e(TAG, "getEventFromContentProvider: More than one event with ID "+eventId+" ("+c.getCount()+")");
            c.close();
        } else
            Log.e(TAG, "getEventFromContentProvider: Error with event "+eventId);
        return e;
    }

    public static Field getFieldFromContentProvider(@NonNull String fieldId, @NonNull String sportId) {
        Field f = null;
        Cursor c = SportteamLoader.simpleQueryFieldId(MyApplication.getAppContext(), fieldId, sportId);
        if (c != null) {
            if (c.getCount() == 1 && c.moveToFirst()) {
                String name = c.getString(SportteamContract.FieldEntry.COLUMN_NAME);
                String address = c.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
                double latitude = c.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
                double longitude = c.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
                LatLng coord = null; if (latitude != 0 && longitude != 0) coord = new LatLng(latitude, longitude);
                String city = c.getString(SportteamContract.FieldEntry.COLUMN_CITY);
                float rating = c.getFloat(SportteamContract.FieldEntry.COLUMN_PUNCTUATION);
                int votes = c.getInt(SportteamContract.FieldEntry.COLUMN_VOTES);
                long opening = c.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
                long closing = c.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
                String creator = c.getString(SportteamContract.FieldEntry.COLUMN_CREATOR);

                f = new Field(fieldId, name, sportId, address, coord, city, rating, votes, opening, closing, creator);
                } else if (c.getCount() == 0)
                Log.e(TAG, "getFieldFromContentProvider: Field with ID "+fieldId+" and sportId "+sportId+" not found");
            else
                Log.e(TAG, "getFieldFromContentProvider: More than one field with ID "+fieldId+" and sportId "+sportId+" ("+c.getCount()+")");
            c.close();
        } else
            Log.e(TAG, "getFieldFromContentProvider: Error with field "+fieldId+" and sportId "+sportId);
        return f;
    }

    public static Alarm getAlarmFromContentProvider(@NonNull String alarmId) {
        Alarm a = null;
        Cursor c = SportteamLoader.simpleQueryAlarmId(MyApplication.getAppContext(), alarmId);
        if (c != null) {
            if (c.getCount() == 1 && c.moveToFirst()) {
                a = cursorToSingleAlarm(c);
            } else if (c.getCount() == 0)
                Log.e(TAG, "getAlarmFromContentProvider: Alarm with ID "+alarmId+" not found");
            else
                Log.e(TAG, "getAlarmFromContentProvider: More than one alarm with ID "+alarmId+" ("+c.getCount()+")");
            c.close();
        } else
            Log.e(TAG, "getAlarmFromContentProvider: Error with alarm "+alarmId);
        return a;
    }

    public static ArrayList<Alarm> getAllAlarmsFromContentProvider(Context context) {
        Cursor c = context.getContentResolver().query(
                SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                SportteamContract.AlarmEntry.ALARM_COLUMNS,
                null, null, null);
        if (c != null) {
            ArrayList<Alarm> result = new ArrayList<>();
            if (c.getCount() > 0) {
                result.addAll(cursorToMultipleAlarm(c));
            } else
                Log.e(TAG, "getAllAlarmsFromContentProvider: No alarms");
            c.close();
            return result;
        } else
            Log.e(TAG, "getAllAlarmsFromContentProvider: Error with alarms");
        return null;
    }

    public static String eventsCoincidenceAlarmFromContentProvider(Alarm alarm, String myUserId) {
        String result = null;
        Cursor c = SportteamLoader.cursorAlarmCoincidence(MyApplication.getAppContext().getContentResolver(), alarm, myUserId);
        if (c != null) {
            if (c.getCount() > 0 && c.moveToFirst()) result = c.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID);
            c.close();
        }
        return result;
    }
}

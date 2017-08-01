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
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.calendarevent.MyCalendarEvent;
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
        if (cursor != null)
            //Move to first position to prevent errors
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
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
        if (cursor != null) {
            /* https://stackoverflow.com/questions/10723770/whats-the-best-way-to-iterate-an-android-cursor#comment33274077_10723771 */
            //Move to first position to prevent errors
            for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
                String fieldId = cursor.getString(SportteamContract.FieldEntry.COLUMN_FIELD_ID);
                String name = cursor.getString(SportteamContract.FieldEntry.COLUMN_NAME);
                String address = cursor.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
                Double lat = cursor.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
                Double lng = cursor.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
                String city = cursor.getString(SportteamContract.FieldEntry.COLUMN_CITY);
                Long openTime = cursor.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
                Long closeTime = cursor.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
                String creator = cursor.getString(SportteamContract.FieldEntry.COLUMN_CREATOR);
                ArrayList<SportCourt> sports = getFieldSportFromContentProvider(fieldId);

                result.add(new Field(fieldId, name, address, lat, lng, city,
                        openTime, closeTime, creator, sports));
            }
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
                Double latitude = c.getDouble(SportteamContract.UserEntry.COLUMN_CITY_LATITUDE);
                Double longitude = c.getDouble(SportteamContract.UserEntry.COLUMN_CITY_LONGITUDE);
                Long age = c.getLong(SportteamContract.UserEntry.COLUMN_AGE);
                String photoUrl = c.getString(SportteamContract.UserEntry.COLUMN_PHOTO);

                u = new User(userId, email, name, city, latitude, longitude, age, photoUrl, null);
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
        if (TextUtils.isEmpty(eventId)) return null; // TODO comprobar esto en los demas y comprobar loq devuelve esta funcion
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
                Long totalPl = c.getLong(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
                Long emptyPl = c.getLong(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);

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

    public static Field getFieldFromContentProvider(@NonNull String fieldId) {
        Field f = null;
        Cursor cursorField = SportteamLoader.simpleQueryFieldId(MyApplication.getAppContext(), fieldId);
        if (cursorField != null) {
            if (cursorField.getCount() == 1 && cursorField.moveToFirst()) {
                String name = cursorField.getString(SportteamContract.FieldEntry.COLUMN_NAME);
                String address = cursorField.getString(SportteamContract.FieldEntry.COLUMN_ADDRESS);
                Double lat = cursorField.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LATITUDE);
                Double lng = cursorField.getDouble(SportteamContract.FieldEntry.COLUMN_ADDRESS_LONGITUDE);
                String city = cursorField.getString(SportteamContract.FieldEntry.COLUMN_CITY);
                Long openTime = cursorField.getLong(SportteamContract.FieldEntry.COLUMN_OPENING_TIME);
                Long closeTime = cursorField.getLong(SportteamContract.FieldEntry.COLUMN_CLOSING_TIME);
                String creator = cursorField.getString(SportteamContract.FieldEntry.COLUMN_CREATOR);
                ArrayList<SportCourt> sports = getFieldSportFromContentProvider(fieldId);

                f = new Field(fieldId, name, address, lat, lng, city,
                        openTime, closeTime, creator, sports);
                } else if (cursorField.getCount() == 0)
                Log.e(TAG, "getFieldFromContentProvider: Field with ID "+fieldId+" not found");
            else
                Log.e(TAG, "getFieldFromContentProvider: More than one field with ID "+fieldId+" ("+cursorField.getCount()+")");
            cursorField.close();
        } else
            Log.e(TAG, "getFieldFromContentProvider: Error with field "+fieldId);
        return f;
    }
    public static ArrayList<SportCourt> getFieldSportFromContentProvider(@NonNull String fieldId) {
        ArrayList<SportCourt> result = new ArrayList<>();
        Cursor cursorFieldSport = SportteamLoader.simpleQuerySportsOfFieldId(MyApplication.getAppContext(), fieldId);
        if (cursorFieldSport != null) {
            if (cursorFieldSport.getCount() > 0)
                //Move to first position to prevent errors
                for(cursorFieldSport.moveToFirst(); !cursorFieldSport.isAfterLast(); cursorFieldSport.moveToNext()) {
                    String sportId = cursorFieldSport.getString(SportteamContract.FieldSportEntry.COLUMN_SPORT);
                    Double punctuation = cursorFieldSport.getDouble(SportteamContract.FieldSportEntry.COLUMN_PUNCTUATION);
                    Long votes = cursorFieldSport.getLong(SportteamContract.FieldSportEntry.COLUMN_VOTES);

                    result.add(new SportCourt(sportId, punctuation, votes));
                }
            else
                Log.e(TAG, "getFieldSportFromContentProvider: Sports of Field with ID "+fieldId+" not found");
            cursorFieldSport.close();
        } else
            Log.e(TAG, "getFieldSportFromContentProvider: Error with Sports of Field "+fieldId);
        return result;
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

    public static ArrayList<MyCalendarEvent> cursorToMultipleCalendarEvent(Cursor c, int color) {
        ArrayList<MyCalendarEvent> result = new ArrayList<>();
        if (c != null) {
            //Move to first position to prevent errors
            for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                Field field = getFieldFromContentProvider(c.getString(SportteamContract.EventEntry.COLUMN_FIELD));

                String eventId = c.getString(SportteamContract.EventEntry.COLUMN_EVENT_ID);
                String sport = c.getString(SportteamContract.EventEntry.COLUMN_SPORT);
                String fieldId = c.getString(SportteamContract.EventEntry.COLUMN_FIELD);
                String address = c.getString(SportteamContract.EventEntry.COLUMN_ADDRESS);
                double latitude = c.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LATITUDE);
                double longitude = c.getDouble(SportteamContract.EventEntry.COLUMN_FIELD_LONGITUDE);
                LatLng coord = null; if (latitude != 0 && longitude != 0) coord = new LatLng(latitude, longitude);
                String name = c.getString(SportteamContract.EventEntry.COLUMN_NAME);
                String city = c.getString(SportteamContract.EventEntry.COLUMN_CITY);
                Long date = c.getLong(SportteamContract.EventEntry.COLUMN_DATE);
                String owner = c.getString(SportteamContract.EventEntry.COLUMN_OWNER);
                Long totalPl = c.getLong(SportteamContract.EventEntry.COLUMN_TOTAL_PLAYERS);
                Long emptyPl = c.getLong(SportteamContract.EventEntry.COLUMN_EMPTY_PLAYERS);

                Event event = new Event(eventId, sport, fieldId, address, coord, name, city, date, owner, totalPl, emptyPl, null, null);

                result.add(MyCalendarEvent.Builder.newInstance(event, field, color));
            }
        }
        Log.d(TAG, "cursorToMultipleCalendarEvent: "+result);
        return result;
    }
}

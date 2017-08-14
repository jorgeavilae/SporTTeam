package com.usal.jorgeav.sportapp.utils;

import android.content.ContentValues;

import com.google.firebase.database.DataSnapshot;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

import java.util.ArrayList;
import java.util.Map;

public class UtilesContentValues {
    @SuppressWarnings("unused")
    private static final String TAG = UtilesContentValues.class.getSimpleName();

    public static ContentValues alarmToContentValues(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.AlarmEntry.ALARM_ID, alarm.getId());
        cv.put(SportteamContract.AlarmEntry.SPORT, alarm.getSport_id());
        cv.put(SportteamContract.AlarmEntry.FIELD, alarm.getField_id());
        cv.put(SportteamContract.AlarmEntry.CITY, alarm.getCity());
        cv.put(SportteamContract.AlarmEntry.DATE_FROM, alarm.getDate_from());
        cv.put(SportteamContract.AlarmEntry.DATE_TO, alarm.getDate_to());
        cv.put(SportteamContract.AlarmEntry.TOTAL_PLAYERS_FROM, alarm.getTotal_players_from());
        cv.put(SportteamContract.AlarmEntry.TOTAL_PLAYERS_TO, alarm.getTotal_players_to());
        cv.put(SportteamContract.AlarmEntry.EMPTY_PLAYERS_FROM, alarm.getEmpty_players_from());
        cv.put(SportteamContract.AlarmEntry.EMPTY_PLAYERS_TO, alarm.getEmpty_players_to());
        return cv;
    }

    public static ContentValues eventToContentValues(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventEntry.EVENT_ID, event.getEvent_id());
        cv.put(SportteamContract.EventEntry.SPORT, event.getSport_id());
        cv.put(SportteamContract.EventEntry.FIELD, event.getField_id());
        cv.put(SportteamContract.EventEntry.ADDRESS, event.getAddress());
        cv.put(SportteamContract.EventEntry.FIELD_LATITUDE, event.getCoord_latitude());
        cv.put(SportteamContract.EventEntry.FIELD_LONGITUDE, event.getCoord_longitude());
        cv.put(SportteamContract.EventEntry.NAME, event.getName());
        cv.put(SportteamContract.EventEntry.CITY, event.getCity());
        cv.put(SportteamContract.EventEntry.DATE, event.getDate());
        cv.put(SportteamContract.EventEntry.OWNER, event.getOwner());
        cv.put(SportteamContract.EventEntry.TOTAL_PLAYERS, event.getTotal_players());
        cv.put(SportteamContract.EventEntry.EMPTY_PLAYERS, event.getEmpty_players());
        return cv;
    }

    public static ContentValues fieldToContentValues(Field field) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.FieldEntry.FIELD_ID, field.getId());
        cv.put(SportteamContract.FieldEntry.NAME, field.getName());
        cv.put(SportteamContract.FieldEntry.ADDRESS, field.getAddress());
        cv.put(SportteamContract.FieldEntry.ADDRESS_LATITUDE, field.getCoord_latitude());
        cv.put(SportteamContract.FieldEntry.ADDRESS_LONGITUDE, field.getCoord_longitude());
        cv.put(SportteamContract.FieldEntry.CITY, field.getCity());
        cv.put(SportteamContract.FieldEntry.OPENING_TIME, field.getOpening_time());
        cv.put(SportteamContract.FieldEntry.CLOSING_TIME, field.getClosing_time());
        cv.put(SportteamContract.FieldEntry.CREATOR, field.getCreator());
        return cv;
    }
    public static ArrayList<ContentValues> fieldSportToContentValues(Field field) {
        ArrayList<ContentValues> cvArray = new ArrayList<>();
        if (field.getSport() != null)
            for (Map.Entry<String, SportCourt> entry : field.getSport().entrySet()) {
                ContentValues cv = new ContentValues();
                cv.put(SportteamContract.FieldSportEntry.FIELD_ID, field.getId());
                cv.put(SportteamContract.FieldSportEntry.SPORT, entry.getValue().getSport_id());
                cv.put(SportteamContract.FieldSportEntry.PUNCTUATION, entry.getValue().getPunctuation());
                cv.put(SportteamContract.FieldSportEntry.VOTES, entry.getValue().getVotes());
                cvArray.add(cv);
            }
        return cvArray;
    }

    public static ContentValues dataUserToContentValues(User user) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.UserEntry.USER_ID, user.getUid());
        cv.put(SportteamContract.UserEntry.EMAIL, user.getEmail());
        cv.put(SportteamContract.UserEntry.NAME, user.getAlias());
        cv.put(SportteamContract.UserEntry.CITY, user.getCity());
        cv.put(SportteamContract.UserEntry.CITY_LATITUDE, user.getCoord_latitude());
        cv.put(SportteamContract.UserEntry.CITY_LONGITUDE, user.getCoord_longitude());
        cv.put(SportteamContract.UserEntry.AGE, user.getAge());
        cv.put(SportteamContract.UserEntry.PHOTO, user.getProfile_picture());
        return cv;
    }
    public static ArrayList<ContentValues> sportUserToContentValues(User user) {
        ArrayList<ContentValues> cvArray = new ArrayList<>();
        if (user.getSports_practiced() != null)
            for (Map.Entry<String, Double> entry : user.getSports_practiced().entrySet()) {
                ContentValues cv = new ContentValues();
                cv.put(SportteamContract.UserSportEntry.USER_ID, user.getUid());
                cv.put(SportteamContract.UserSportEntry.SPORT, entry.getKey());
                cv.put(SportteamContract.UserSportEntry.LEVEL, entry.getValue());
                cvArray.add(cv);
            }
        return cvArray;
    }

    public static ContentValues invitationToContentValues(Invitation invitation) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventsInvitationEntry.RECEIVER_ID, invitation.getReceiver());
        cv.put(SportteamContract.EventsInvitationEntry.SENDER_ID, invitation.getSender());
        cv.put(SportteamContract.EventsInvitationEntry.EVENT_ID, invitation.getEvent());
        cv.put(SportteamContract.EventsInvitationEntry.DATE, invitation.getDate());
        return cv;
    }

    public static ContentValues dataSnapshotFriendRequestToContentValues(DataSnapshot dataSnapshot, String key, boolean keyIsTheSender) {
        String senderId, receiverId;
        if(keyIsTheSender) {
            receiverId = dataSnapshot.getKey();
            senderId = key;
        } else {
            receiverId = key;
            senderId = dataSnapshot.getKey();
        }
        Long date = dataSnapshot.getValue(Long.class);

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.FriendRequestEntry.RECEIVER_ID, receiverId);
        cv.put(SportteamContract.FriendRequestEntry.SENDER_ID, senderId);
        cv.put(SportteamContract.FriendRequestEntry.DATE, date);
        return cv;
    }

    public static ContentValues dataSnapshotFriendToContentValues(DataSnapshot dataSnapshot, String myUserID) {
        String userId = dataSnapshot.getKey();
        Long date = dataSnapshot.getValue(Long.class);

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.FriendsEntry.MY_USER_ID, myUserID);
        cv.put(SportteamContract.FriendsEntry.USER_ID, userId);
        cv.put(SportteamContract.FriendsEntry.DATE, date);
        return cv;
    }

    public static ContentValues dataSnapshotEventsParticipationToContentValues(DataSnapshot dataSnapshot, String myUserID) {
        String eventId = dataSnapshot.getKey();

        //Cast boolean Firebase value to int ContentProvider value (true:1 false:0)
        int participation = 0;
        Boolean participationBoolean = dataSnapshot.getValue(Boolean.class);
        if (participationBoolean != null)
            participation = participationBoolean ? 1 : 0;

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventsParticipationEntry.USER_ID, myUserID);
        cv.put(SportteamContract.EventsParticipationEntry.EVENT_ID, eventId);
        cv.put(SportteamContract.EventsParticipationEntry.PARTICIPATES, participation);
        return cv;
    }


    public static ContentValues dataSnapshotEventsRequestsToContentValues(DataSnapshot dataSnapshot, String key, boolean iAmTheSender) {
        String eventId, senderId;
        if(iAmTheSender) {
            eventId = dataSnapshot.getKey();
            senderId = key;
        } else {
            eventId = key;
            senderId = dataSnapshot.getKey();
        }
        Long date = dataSnapshot.getValue(Long.class);

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventRequestsEntry.EVENT_ID, eventId);
        cv.put(SportteamContract.EventRequestsEntry.SENDER_ID, senderId);
        cv.put(SportteamContract.EventRequestsEntry.DATE, date);
        return cv;
    }
}

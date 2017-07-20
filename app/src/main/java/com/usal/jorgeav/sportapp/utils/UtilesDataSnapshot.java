package com.usal.jorgeav.sportapp.utils;

import android.content.ContentValues;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Invitation;
import com.usal.jorgeav.sportapp.data.SimulatedUser;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class UtilesDataSnapshot {
    private static final String TAG = UtilesDataSnapshot.class.getSimpleName();

    public static Alarm dataSnapshotToAlarm(DataSnapshot data) {
        String id = data.getKey();
        String sport = data.child(FirebaseDBContract.Alarm.SPORT).getValue(String.class);
        String field = data.child(FirebaseDBContract.Alarm.FIELD).getValue(String.class);
        String city = data.child(FirebaseDBContract.Alarm.CITY).getValue(String.class);
        Long dateFrom = data.child(FirebaseDBContract.Alarm.DATE_FROM).getValue(Long.class);
        Long dateTo = data.child(FirebaseDBContract.Alarm.DATE_TO).getValue(Long.class);
        Long totalFrom = data.child(FirebaseDBContract.Alarm.TOTAL_PLAYERS_FROM).getValue(Long.class);
        Long totalTo = data.child(FirebaseDBContract.Alarm.TOTAL_PLAYERS_TO).getValue(Long.class);
        Long emptyFrom = data.child(FirebaseDBContract.Alarm.EMPTY_PLAYERS_FROM).getValue(Long.class);
        Long emptyTo = data.child(FirebaseDBContract.Alarm.EMPTY_PLAYERS_TO).getValue(Long.class);

        Alarm a = new Alarm(id,sport,field,city,dateFrom,dateTo,totalFrom,totalTo,emptyFrom,emptyTo);
        return a;
    }
    public static ContentValues alarmToContentValues(Alarm alarm) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.AlarmEntry.ALARM_ID, alarm.getmId());
        cv.put(SportteamContract.AlarmEntry.SPORT, alarm.getmSport());
        cv.put(SportteamContract.AlarmEntry.FIELD, alarm.getmField());
        cv.put(SportteamContract.AlarmEntry.CITY, alarm.getmCity());
        cv.put(SportteamContract.AlarmEntry.DATE_FROM, alarm.getmDateFrom());
        cv.put(SportteamContract.AlarmEntry.DATE_TO, alarm.getmDateTo());
        cv.put(SportteamContract.AlarmEntry.TOTAL_PLAYERS_FROM, alarm.getmTotalPlayersFrom());
        cv.put(SportteamContract.AlarmEntry.TOTAL_PLAYERS_TO, alarm.getmTotalPlayersTo());
        cv.put(SportteamContract.AlarmEntry.EMPTY_PLAYERS_FROM, alarm.getmEmptyPlayersFrom());
        cv.put(SportteamContract.AlarmEntry.EMPTY_PLAYERS_TO, alarm.getmEmptyPlayersTo());
        return cv;
    }

    public static Event dataSnapshotToEvent(DataSnapshot data) {
        String id = data.getKey();
        DataSnapshot dataNode = data.child(FirebaseDBContract.DATA);

        String sport = dataNode.child(FirebaseDBContract.Event.SPORT).getValue(String.class);
        String field = dataNode.child(FirebaseDBContract.Event.FIELD).getValue(String.class);
        Double latitude = dataNode.child(FirebaseDBContract.Event.COORD_LATITUDE).getValue(Double.class);
        Double longitude = dataNode.child(FirebaseDBContract.Event.COORD_LONGITUDE).getValue(Double.class);
        LatLng coord = null; if (latitude != null && longitude != null) coord = new LatLng(latitude, longitude);
        String name = dataNode.child(FirebaseDBContract.Event.NAME).getValue(String.class);
        String city = dataNode.child(FirebaseDBContract.Event.CITY).getValue(String.class);
        String owner = dataNode.child(FirebaseDBContract.Event.OWNER).getValue(String.class);
        Long date = dataNode.child(FirebaseDBContract.Event.DATE).getValue(Long.class);
        Long total = dataNode.child(FirebaseDBContract.Event.TOTAL_PLAYERS).getValue(Long.class);
        int totalValue = 0; if (total != null) totalValue = total.intValue();
        Long empty = dataNode.child(FirebaseDBContract.Event.EMPTY_PLAYERS).getValue(Long.class);
        int emptyValue = 0; if (empty != null) emptyValue = empty.intValue();
        return new Event(id, sport, field, coord, name, city, date, owner,
                totalValue, emptyValue, new HashMap<String, Boolean>(),
                new HashMap<String, SimulatedUser>());
    }
    public static ContentValues eventToContentValues(Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventEntry.EVENT_ID, event.getEvent_id());
        cv.put(SportteamContract.EventEntry.SPORT, event.getSport_id());
        cv.put(SportteamContract.EventEntry.FIELD, event.getField_id());
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

    public static List<Field> dataSnapshotToFieldList(DataSnapshot data) {
        ArrayList<Field> result = new ArrayList<>();
        String id = data.getKey();
        DataSnapshot dataNode = data.child(FirebaseDBContract.DATA);

        String name = dataNode.child(FirebaseDBContract.Field.NAME).getValue(String.class);
        String address = dataNode.child(FirebaseDBContract.Field.ADDRESS).getValue(String.class);
        String city = dataNode.child(FirebaseDBContract.Field.CITY).getValue(String.class);

        Double lat =  dataNode.child(FirebaseDBContract.Field.COORD_LATITUDE).getValue(Double.class);
        Double lng =  dataNode.child(FirebaseDBContract.Field.COORD_LONGITUDE).getValue(Double.class);
        LatLng coords = null;
        if (lat != null && lat != 0 && lng != null && lng != 0)
            coords = new LatLng(lat, lng);

        Long openingTime = dataNode.child(FirebaseDBContract.Field.OPENING_TIME).getValue(Long.class);
        Long closingTime = dataNode.child(FirebaseDBContract.Field.CLOSING_TIME).getValue(Long.class);

        String creator = dataNode.child(FirebaseDBContract.Field.CREATOR).getValue(String.class);

        for (DataSnapshot d : dataNode.child(FirebaseDBContract.Field.SPORT).getChildren()) {
            String sport = d.getKey();
            Double rating = d.child(FirebaseDBContract.Field.PUNCTUATION).getValue(Double.class);
            Long votes = d.child(FirebaseDBContract.Field.VOTES).getValue(Long.class);
            result.add(new Field(id,name,sport,address,
                            coords,city,rating.floatValue(),
                            votes.intValue(),openingTime,closingTime, creator));
        }
        return result;
    }
    public static List<ContentValues> fieldsListToContentValues(List<Field> fields) {
        ArrayList<ContentValues> cvArray = new ArrayList<>();
        for (Field f : fields) {
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.FieldEntry.FIELD_ID, f.getmId());
            cv.put(SportteamContract.FieldEntry.NAME, f.getmName());
            cv.put(SportteamContract.FieldEntry.SPORT, f.getmSport());
            cv.put(SportteamContract.FieldEntry.ADDRESS, f.getmAddress());
            if (f.getmCoords() != null) {
                cv.put(SportteamContract.FieldEntry.ADDRESS_LATITUDE, f.getmCoords().latitude);
                cv.put(SportteamContract.FieldEntry.ADDRESS_LONGITUDE, f.getmCoords().longitude);
            }
            cv.put(SportteamContract.FieldEntry.CITY, f.getmCity());
            cv.put(SportteamContract.FieldEntry.PUNCTUATION, f.getmRating());
            cv.put(SportteamContract.FieldEntry.VOTES, f.getmVotes());
            cv.put(SportteamContract.FieldEntry.OPENING_TIME, f.getmOpeningTime());
            cv.put(SportteamContract.FieldEntry.CLOSING_TIME, f.getmClosingTime());
            cvArray.add(cv);
        }
        return cvArray;
    }

    public static User dataSnapshotToUser(DataSnapshot data) {
        String datakey = FirebaseDBContract.DATA + "/";
        String id = data.getKey();
        String email = data.child(datakey + FirebaseDBContract.User.EMAIL).getValue(String.class);
        String name = data.child(datakey + FirebaseDBContract.User.ALIAS).getValue(String.class);
        String city = data.child(datakey + FirebaseDBContract.User.CITY).getValue(String.class);
        Double latitude = data.child(datakey + FirebaseDBContract.Event.COORD_LATITUDE).getValue(Double.class);
        Double longitude = data.child(datakey + FirebaseDBContract.Event.COORD_LONGITUDE).getValue(Double.class);
        Long age = data.child(datakey + FirebaseDBContract.User.AGE).getValue(Long.class);
        String photoUrl = data.child(datakey + FirebaseDBContract.User.PROFILE_PICTURE).getValue(String.class);

        ArrayList<Sport> arraySports = new ArrayList<>();
        for (DataSnapshot d : data.child(FirebaseDBContract.User.SPORTS_PRACTICED).getChildren())
            arraySports.add(new Sport(d.getKey(), ((Number)d.getValue()).floatValue(), 0));

        LatLng coord = null;
        if (latitude != null && longitude != null) coord = new LatLng(latitude, longitude);
        return new User(id, email, name, city, coord, age.intValue(), photoUrl, arraySports);
    }
    public static ContentValues dataUserToContentValues(User user) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.UserEntry.USER_ID, user.getmId());
        cv.put(SportteamContract.UserEntry.EMAIL, user.getmEmail());
        cv.put(SportteamContract.UserEntry.NAME, user.getmName());
        cv.put(SportteamContract.UserEntry.CITY, user.getmCity());
        LatLng latLng = user.getmCoord();
        if (latLng != null) {
            cv.put(SportteamContract.UserEntry.CITY_LATITUDE, latLng.latitude);
            cv.put(SportteamContract.UserEntry.CITY_LONGITUDE, latLng.longitude);
        }
        cv.put(SportteamContract.UserEntry.AGE, user.getmAge());
        cv.put(SportteamContract.UserEntry.PHOTO, user.getmPhotoUrl());
        return cv;
    }
    public static ArrayList<ContentValues> sportUserToContentValues(User user) {
        ArrayList<ContentValues> cvArray = new ArrayList<ContentValues>();
        for (Sport s : user.getmSportList()) {
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.UserSportEntry.USER_ID, user.getmId());
            cv.put(SportteamContract.UserSportEntry.SPORT, s.getmName());
            cv.put(SportteamContract.UserSportEntry.LEVEL, s.getPunctuation());
            cvArray.add(cv);
        }
        return cvArray;
    }

    //dataSnapshotToInvitation: dataSnapshot.getValue(Invitation.class)
    public static ContentValues invitationToContentValues(Invitation invitation) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventsInvitationEntry.RECEIVER_ID, invitation.getReceiver());
        cv.put(SportteamContract.EventsInvitationEntry.SENDER_ID, invitation.getSender());
        cv.put(SportteamContract.EventsInvitationEntry.EVENT_ID, invitation.getEvent());
        cv.put(SportteamContract.EventsInvitationEntry.DATE, invitation.getDate());
        return cv;
    }

    public static ContentValues dataSnapshotFriendRequestToContentValues(DataSnapshot dataSnapshot, String key, boolean iAmTheSender) {
        String senderId, receiverId;
        if(iAmTheSender) {
            receiverId = dataSnapshot.getKey();
            senderId = key;
        } else {
            receiverId = key;
            senderId = dataSnapshot.getKey();
        }
        long date = ((Number)dataSnapshot.getValue()).longValue();

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.FriendRequestEntry.RECEIVER_ID, receiverId);
        cv.put(SportteamContract.FriendRequestEntry.SENDER_ID, senderId);
        cv.put(SportteamContract.FriendRequestEntry.DATE, date);
        return cv;
    }

    public static ContentValues dataSnapshotFriendToContentValues(DataSnapshot dataSnapshot, String myUserID) {
        String myUserId = myUserID;
        String userId = dataSnapshot.getKey();
        long date = ((Number)dataSnapshot.getValue()).longValue();

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.FriendsEntry.MY_USER_ID, myUserId);
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
        long date = ((Number)dataSnapshot.getValue()).longValue();

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventRequestsEntry.EVENT_ID, eventId);
        cv.put(SportteamContract.EventRequestsEntry.SENDER_ID, senderId);
        cv.put(SportteamContract.EventRequestsEntry.DATE, date);
        return cv;
    }
}

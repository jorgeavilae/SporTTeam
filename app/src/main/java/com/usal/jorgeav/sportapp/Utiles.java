package com.usal.jorgeav.sportapp;

import android.content.ContentValues;

import com.google.firebase.database.DataSnapshot;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.Sport;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.FirebaseDBContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class Utiles {

    public static String millisToDateTimeString(long millis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMMM/yy hh:mm", Locale.getDefault());
        return sdf.format(new Date(millis));
    }

    public static long timeStringToMillis(String time) {
        int quoteInd = time.indexOf(":");
        int hor = Integer.valueOf(time.substring(0, quoteInd));
        int min = Integer.valueOf(time.substring(++quoteInd, time.length()));
        return (((hor * 60) + min) * 60 * 1000);
    }

    public static String millisToTimeString(long millis) {
        long min = millis/(60*1000);
        long hor = min/60;
        min -= hor*60;
        return String.format(Locale.getDefault(), "%02d:%02d", hor,min);
    }


    public static Event datasnapshotToEvent (DataSnapshot data) {
        String id = data.getKey();
        DataSnapshot dataNode = data.child(FirebaseDBContract.DATA);

        String sport = dataNode.child(FirebaseDBContract.Event.SPORT).getValue().toString();
        String field = dataNode.child(FirebaseDBContract.Event.FIELD).getValue().toString();
        String city = dataNode.child(FirebaseDBContract.Event.CITY).getValue().toString();
        String owner = dataNode.child(FirebaseDBContract.Event.OWNER).getValue().toString();

        String dateStr = dataNode.child(FirebaseDBContract.Event.DATE).getValue().toString();
        String totalPlayersStr = dataNode.child(FirebaseDBContract.Event.TOTAL_PLAYERS).getValue().toString();
        String emptyPlayersStr = dataNode.child(FirebaseDBContract.Event.EMPTY_PLAYERS).getValue().toString();
        Long date = Long.valueOf(dateStr);
        int total = Integer.valueOf(totalPlayersStr);
        int empty = Integer.valueOf(emptyPlayersStr);

        return new Event(id,sport,field,city,date,owner,total,empty);
    }
    public static ContentValues eventToContentValues (Event event) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.EventEntry.EVENT_ID, event.getmId());
        cv.put(SportteamContract.EventEntry.SPORT, event.getmSport());
        cv.put(SportteamContract.EventEntry.FIELD, event.getmField());
        cv.put(SportteamContract.EventEntry.CITY, event.getmCity());
        cv.put(SportteamContract.EventEntry.DATE, event.getmDate());
        cv.put(SportteamContract.EventEntry.OWNER, event.getmOwner());
        cv.put(SportteamContract.EventEntry.TOTAL_PLAYERS, event.getmTotalPlayers());
        cv.put(SportteamContract.EventEntry.EMPTY_PLAYERS, event.getmEmptyPlayers());
        return cv;
    }

    public static List<Field> datasnapshotToField (DataSnapshot data) {
        ArrayList<Field> result = new ArrayList<>();
        String id = data.getKey();
        DataSnapshot dataNode = data.child(FirebaseDBContract.DATA);

        String name = dataNode.child(FirebaseDBContract.Field.NAME).getValue().toString();
        String address = dataNode.child(FirebaseDBContract.Field.ADDRESS).getValue().toString();
        String city = dataNode.child(FirebaseDBContract.Field.CITY).getValue().toString();
        String openingTimeStr = dataNode.child(FirebaseDBContract.Field.OPENING_TIME).getValue().toString();
        String closingTimeStr = dataNode.child(FirebaseDBContract.Field.CLOSING_TIME).getValue().toString();
        long openingTime = Long.valueOf(openingTimeStr);
        long closingTime = Long.valueOf(closingTimeStr);

        for (DataSnapshot d : data.child(FirebaseDBContract.Field.SPORTS).getChildren()) {
            String sport = d.getKey();
            String ratingStr = d.child(FirebaseDBContract.Field.PUNCTUATION).getValue().toString();
            String votesStr = d.child(FirebaseDBContract.Field.VOTES).getValue().toString();
            float rating = Float.valueOf(ratingStr);
            int votes = Integer.valueOf(votesStr);
            result.add(new Field(id,name,sport,address,city,rating,votes,openingTime,closingTime));
        }
        return result;
    }
    public static List<ContentValues> fieldsArrayToContentValues (List<Field> fields) {
        ArrayList<ContentValues> cvArray = new ArrayList<>();
        for (Field f : fields) {
            ContentValues cv = new ContentValues();
            cv.put(SportteamContract.FieldEntry.FIELD_ID, f.getmId());
            cv.put(SportteamContract.FieldEntry.NAME, f.getmName());
            cv.put(SportteamContract.FieldEntry.SPORT, f.getmSport());
            cv.put(SportteamContract.FieldEntry.ADDRESS, f.getmAddress());
            cv.put(SportteamContract.FieldEntry.CITY, f.getmCity());
            cv.put(SportteamContract.FieldEntry.PUNCTUATION, f.getmRating());
            cv.put(SportteamContract.FieldEntry.VOTES, f.getmVotes());
            cv.put(SportteamContract.FieldEntry.OPENING_TIME, f.getmOpeningTime());
            cv.put(SportteamContract.FieldEntry.CLOSING_TIME, f.getmClosingTime());
            cvArray.add(cv);
        }
        return cvArray;
    }

    public static User datasnapshotToUser (DataSnapshot data, String userID) {
        String datakey = FirebaseDBContract.DATA + "/";
        String id = userID;
        String email = data.child(datakey + FirebaseDBContract.User.EMAIL).getValue().toString();
        String name = data.child(datakey + FirebaseDBContract.User.ALIAS).getValue().toString();
        String city = data.child(datakey + FirebaseDBContract.User.CITY).getValue().toString();
        String ageStr = data.child(datakey + FirebaseDBContract.User.AGE).getValue().toString();
        int age = Integer.valueOf(ageStr);
        String photoUrl = data.child(datakey + FirebaseDBContract.User.PROFILE_PICTURE).getValue().toString();

        ArrayList<Sport> arraySports = new ArrayList<>();
        for (DataSnapshot d : data.child(FirebaseDBContract.User.SPORTS_PRACTICED).getChildren())
            arraySports.add(new Sport(d.getKey(), ((Number)d.getValue()).floatValue(), 0));

        return new User(id,email,name,city,age,photoUrl,arraySports);
    }
    public static ContentValues dataUserToContentValues (User user) {
        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.UserEntry.USER_ID, user.getmId());
        cv.put(SportteamContract.UserEntry.EMAIL, user.getmEmail());
        cv.put(SportteamContract.UserEntry.NAME, user.getmName());
        cv.put(SportteamContract.UserEntry.CITY, user.getmCity());
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
            cv.put(SportteamContract.UserSportEntry.LEVEL, s.getmLevel());
            cvArray.add(cv);
        }
        return cvArray;
    }

    public static ContentValues datasnapshotFriendRequestToContentValues(DataSnapshot dataSnapshot, String myUserID) {
        String receiverId = myUserID;
        String senderId = dataSnapshot.getKey();
        long date = ((Number)dataSnapshot.getValue()).longValue();

        ContentValues cv = new ContentValues();
        cv.put(SportteamContract.FriendRequestEntry.RECEIVER_ID, receiverId);
        cv.put(SportteamContract.FriendRequestEntry.SENDER_ID, senderId);
        cv.put(SportteamContract.FriendRequestEntry.DATE, date);
        return cv;
    }
}

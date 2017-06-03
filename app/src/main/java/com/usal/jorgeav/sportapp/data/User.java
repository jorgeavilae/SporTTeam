package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.FirebaseDBContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class User {
    String mId;
    String mEmail;
    String mName;
    String mCity;
    int mAge;
    String mPhotoUrl;
    List<Sport> mSportList;

    public User(String mId, String mEmail, String mName, String mCity, int mAge, String mPhotoUrl, List<Sport> sportList) {
        this.mId = mId;
        this.mEmail = mEmail;
        this.mName = mName;
        this.mCity = mCity;
        this.mAge = mAge;
        this.mPhotoUrl = mPhotoUrl;
        this.mSportList = sportList;
    }

    public String getmId() {
        return mId;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmName() {
        return mName;
    }

    public String getmCity() {
        return mCity;
    }

    public int getmAge() {
        return mAge;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public List<Sport> getmSportList() {
        return mSportList;
    }

    @Override
    public String toString() {
        return "User{" +
                "mId='" + mId + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mName='" + mName + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mAge=" + mAge +
                ", mPhotoUrl='" + mPhotoUrl + '\'' +
                ", mSportList=" + mSportList +
                '}';
    }

    public Object toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.DATA, dataToMap());
        result.put(FirebaseDBContract.User.SPORTS_PRACTICED, sportsToMap());
        result.put(FirebaseDBContract.User.FRIENDS, new HashMap<String, Long>());
        result.put(FirebaseDBContract.User.FRIENDS_REQUESTS, new HashMap<String, Long>());
        result.put(FirebaseDBContract.User.EVENTS_CREATED, new HashMap<String, Long>());
        result.put(FirebaseDBContract.User.EVENTS_PARTICIPATION, new HashMap<String, Boolean>());
        result.put(FirebaseDBContract.User.EVENTS_INVITATIONS, new HashMap<String, Long>());
        result.put(FirebaseDBContract.User.EVENTS_REQUESTS, new HashMap<String, Long>());
        return result;
    }

    private Map<String, Object> dataToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.User.ALIAS, this.mName);
        result.put(FirebaseDBContract.User.EMAIL, this.mEmail);
        result.put(FirebaseDBContract.User.AGE, this.mAge);
        result.put(FirebaseDBContract.User.PROFILE_PICTURE, this.mPhotoUrl);
        result.put(FirebaseDBContract.User.CITY, this.mCity);
        return result;
    }

    private Map<String, Float> sportsToMap() {
        HashMap<String, Float> result = new HashMap<>();
        for (Sport sport : mSportList) {
            result.put(sport.getmName(), sport.getmLevel());
        }
        return result;
    }
}

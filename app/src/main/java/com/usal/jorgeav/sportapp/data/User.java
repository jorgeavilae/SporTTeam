package com.usal.jorgeav.sportapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

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
    Double coord_latitude;
    Double coord_longitude;
    int mAge;
    String mPhotoUrl;
    List<Sport> mSportList;

    public User(String mId, String mEmail, String mName, String mCity, LatLng coord, int mAge, String mPhotoUrl, List<Sport> sportList) {
        this.mId = mId;
        this.mEmail = mEmail;
        this.mName = mName;
        this.mCity = mCity;
        if (coord != null) {
            this.coord_latitude = coord.latitude;
            this.coord_longitude = coord.longitude;
        } else {
            this.coord_latitude = null;
            this.coord_longitude = null;
        }
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

    public Double getCoord_latitude() {
        return coord_latitude;
    }

    public Double getCoord_longitude() {
        return coord_longitude;
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
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", mAge=" + mAge +
                ", mPhotoUrl='" + mPhotoUrl + '\'' +
                ", mSportList=" + mSportList +
                '}';
    }

    public Object toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.DATA, dataToMap());
        result.put(FirebaseDBContract.User.SPORTS_PRACTICED, sportsToMap());
        return result;
    }

    private Map<String, Object> dataToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.User.ALIAS, this.mName);
        result.put(FirebaseDBContract.User.EMAIL, this.mEmail);
        result.put(FirebaseDBContract.User.AGE, this.mAge);
        result.put(FirebaseDBContract.User.PROFILE_PICTURE, this.mPhotoUrl);
        result.put(FirebaseDBContract.User.CITY, this.mCity);
        result.put(FirebaseDBContract.User.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.User.COORD_LONGITUDE, this.coord_longitude);
        return result;
    }

    private Map<String, Float> sportsToMap() {
        HashMap<String, Float> result = new HashMap<>();
        for (Sport sport : mSportList) {
            result.put(sport.getmName(), sport.getPunctuation());
        }
        return result;
    }
}

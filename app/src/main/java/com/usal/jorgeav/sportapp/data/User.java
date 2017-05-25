package com.usal.jorgeav.sportapp.data;

import java.util.List;

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
}

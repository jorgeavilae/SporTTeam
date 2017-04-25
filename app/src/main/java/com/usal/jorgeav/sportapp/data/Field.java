package com.usal.jorgeav.sportapp.data;

import java.util.UUID;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class Field {
    String mId;
    String mName;
    String mAddress;
    Float mRating;
    String mSport;
    String mOpeningTime;
    String mClosingTime;

    public Field(String mName, String mAddress, Float mRrating, String mSsport, String mOpeningTime, String mClosingTime) {
        this.mId = UUID.randomUUID().toString();
        this.mName = mName;
        this.mAddress = mAddress;
        this.mRating = mRrating;
        this.mSport = mSsport;
        this.mOpeningTime = mOpeningTime;
        this.mClosingTime = mClosingTime;
    }

    public String getmId() {
        return mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmAddress() {
        return mAddress;
    }

    public Float getmRating() {
        return mRating;
    }

    public String getmSport() {
        return mSport;
    }

    public String getmOpeningTime() {
        return mOpeningTime;
    }

    public String getmClosingTime() {
        return mClosingTime;
    }
}

package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class Alarm {
    String mId;
    String mSport;
    String mField;
    String mCity;
    Long mDateFrom;
    Long mDateTo;
    int mTotalPlayersFrom;
    int mTotalPlayersTo;
    int mEmptyPlayersFrom;
    int mEmptyPlayersTo;

    public Alarm(String mId, String mSport, String mField, String mCity,
                 Long mDateFrom, Long mDateTo,
                 int mTotalPlayersFrom, int mTotalPlayersTo,
                 int mEmptyPlayersFrom, int mEmptyPlayersTo) {
        this.mId = mId;
        this.mSport = mSport;
        this.mField = mField;
        this.mCity = mCity;
        this.mDateFrom = mDateFrom;
        this.mDateTo = mDateTo;
        this.mTotalPlayersFrom = mTotalPlayersFrom;
        this.mTotalPlayersTo = mTotalPlayersTo;
        this.mEmptyPlayersFrom = mEmptyPlayersFrom;
        this.mEmptyPlayersTo = mEmptyPlayersTo;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmSport() {
        return mSport;
    }

    public String getmField() {
        return mField;
    }

    public String getmCity() {
        return mCity;
    }

    public Long getmDateFrom() {
        return mDateFrom;
    }

    public Long getmDateTo() {
        return mDateTo;
    }

    public int getmTotalPlayersFrom() {
        return mTotalPlayersFrom;
    }

    public int getmTotalPlayersTo() {
        return mTotalPlayersTo;
    }

    public int getmEmptyPlayersFrom() {
        return mEmptyPlayersFrom;
    }

    public int getmEmptyPlayersTo() {
        return mEmptyPlayersTo;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Alarm.SPORT, this.mSport);
        result.put(FirebaseDBContract.Alarm.FIELD, this.mField);
        result.put(FirebaseDBContract.Alarm.CITY, this.mCity);
        result.put(FirebaseDBContract.Alarm.DATE_FROM, this.mDateFrom);
        result.put(FirebaseDBContract.Alarm.DATE_TO, this.mDateTo);
        result.put(FirebaseDBContract.Alarm.TOTAL_PLAYERS_FROM, this.mTotalPlayersFrom);
        result.put(FirebaseDBContract.Alarm.TOTAL_PLAYERS_TO, this.mTotalPlayersTo);
        result.put(FirebaseDBContract.Alarm.EMPTY_PLAYERS_FROM, this.mEmptyPlayersFrom);
        result.put(FirebaseDBContract.Alarm.EMPTY_PLAYERS_TO, this.mEmptyPlayersTo);
        return result;

    }
}

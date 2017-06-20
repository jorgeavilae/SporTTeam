package com.usal.jorgeav.sportapp.data;

import android.text.TextUtils;

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
    Long mTotalPlayersFrom;
    Long mTotalPlayersTo;
    Long mEmptyPlayersFrom;
    Long mEmptyPlayersTo;

    public Alarm(){}

    public Alarm(String mId, String mSport, String mField, String mCity,
                 Long mDateFrom, Long mDateTo,
                 Long mTotalPlayersFrom, Long mTotalPlayersTo,
                 Long mEmptyPlayersFrom, Long mEmptyPlayersTo) {
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

    public void setmSport(String mSport) {
        this.mSport = mSport;
    }

    public void setmField(String mField) {
        if (!TextUtils.isEmpty(mField)) this.mField = mField;
        else this.mField = null;
    }

    public void setmCity(String mCity) {
        this.mCity = mCity;
    }

    public void setmDateFrom(Long mDateFrom) {
        this.mDateFrom = mDateFrom;
    }

    public void setmDateTo(Long mDateTo) {
        if (mDateTo > 0) this.mDateTo = mDateTo;
        else this.mDateTo = null;
    }

    public void setmTotalPlayersFrom(Long mTotalPlayersFrom) {
        this.mTotalPlayersFrom = mTotalPlayersFrom;
    }

    public void setmTotalPlayersTo(Long mTotalPlayersTo) {
        this.mTotalPlayersTo = mTotalPlayersTo;
    }

    public void setmEmptyPlayersFrom(Long mEmptyPlayersFrom) {
        this.mEmptyPlayersFrom = mEmptyPlayersFrom;
    }

    public void setmEmptyPlayersTo(Long mEmptyPlayersTo) {
        this.mEmptyPlayersTo = mEmptyPlayersTo;
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

    public Long getmTotalPlayersFrom() {
        return mTotalPlayersFrom;
    }

    public Long getmTotalPlayersTo() {
        return mTotalPlayersTo;
    }

    public Long getmEmptyPlayersFrom() {
        return mEmptyPlayersFrom;
    }

    public Long getmEmptyPlayersTo() {
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

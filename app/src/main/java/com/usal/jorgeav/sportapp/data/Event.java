package com.usal.jorgeav.sportapp.data;

import java.util.UUID;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class Event {
    String mId;
    String mSport;
    String mPlace;
    String mDate;
    String mTime;
    int mTotalPlayers;
    int mEmptyPlayers;

    public Event(String mSport, String mPlace, String mDate, String mTime, int mTotalPlayers) {
        this.mId = UUID.randomUUID().toString();
        this.mSport = mSport;
        this.mPlace = mPlace;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mTotalPlayers = mTotalPlayers;
        this.mEmptyPlayers = mTotalPlayers;
    }

    public String getmId() {
        return mId;
    }

    public String getmSport() {
        return mSport;
    }

    public String getmPlace() {
        return mPlace;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmTime() {
        return mTime;
    }

    public int getmTotalPlayers() {
        return mTotalPlayers;
    }

    public int getmEmptyPlayers() {
        return mEmptyPlayers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (mTotalPlayers != event.mTotalPlayers) return false;
        if (mEmptyPlayers != event.mEmptyPlayers) return false;
        if (mId != null ? !mId.equals(event.mId) : event.mId != null) return false;
        if (mSport != null ? !mSport.equals(event.mSport) : event.mSport != null) return false;
        if (mPlace != null ? !mPlace.equals(event.mPlace) : event.mPlace != null) return false;
        if (mDate != null ? !mDate.equals(event.mDate) : event.mDate != null) return false;
        return mTime != null ? mTime.equals(event.mTime) : event.mTime == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mSport != null ? mSport.hashCode() : 0);
        result = 31 * result + (mPlace != null ? mPlace.hashCode() : 0);
        result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
        result = 31 * result + (mTime != null ? mTime.hashCode() : 0);
        result = 31 * result + mTotalPlayers;
        result = 31 * result + mEmptyPlayers;
        return result;
    }
}



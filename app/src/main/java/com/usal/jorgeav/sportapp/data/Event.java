package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class Event implements Parcelable {
    String mId;
    String mSport;
    String mField;
    String mCity;
    Long mDate;
    String mOwner;
    int mTotalPlayers;
    int mEmptyPlayers;

    public Event(String mId, String mSport, String mField, String mCity, Long mDate,
                 String mOwner, int mTotalPlayers, int mEmptyPlayers) {
        this.mId = mId;
        this.mSport = mSport;
        this.mField = mField;
        this.mCity = mCity;
        this.mDate = mDate;
        this.mOwner = mOwner;
        this.mTotalPlayers = mTotalPlayers;
        this.mEmptyPlayers = mEmptyPlayers;
    }

    public String getmId() {
        return mId;
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

    public Long getmDate() {
        return mDate;
    }

    public String getmOwner() {
        return mOwner;
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
        if (mField != null ? !mField.equals(event.mField) : event.mField != null) return false;
        if (mCity != null ? !mCity.equals(event.mCity) : event.mCity != null) return false;
        if (mDate != null ? !mDate.equals(event.mDate) : event.mDate != null) return false;
        return mOwner != null ? mOwner.equals(event.mOwner) : event.mOwner == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mSport != null ? mSport.hashCode() : 0);
        result = 31 * result + (mField != null ? mField.hashCode() : 0);
        result = 31 * result + (mCity != null ? mCity.hashCode() : 0);
        result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
        result = 31 * result + (mOwner != null ? mOwner.hashCode() : 0);
        result = 31 * result + mTotalPlayers;
        result = 31 * result + mEmptyPlayers;
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mSport);
        dest.writeString(this.mField);
        dest.writeString(this.mCity);
        dest.writeValue(this.mDate);
        dest.writeString(this.mOwner);
        dest.writeInt(this.mTotalPlayers);
        dest.writeInt(this.mEmptyPlayers);
    }

    protected Event(Parcel in) {
        this.mId = in.readString();
        this.mSport = in.readString();
        this.mField = in.readString();
        this.mCity = in.readString();
        this.mDate = (Long) in.readValue(Long.class.getClassLoader());
        this.mOwner = in.readString();
        this.mTotalPlayers = in.readInt();
        this.mEmptyPlayers = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString() {
        return "Event{" +
                "mId='" + mId + '\'' +
                ", mSport='" + mSport + '\'' +
                ", mField='" + mField + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mDate=" + mDate +
                ", mOwner='" + mOwner + '\'' +
                ", mTotalPlayers=" + mTotalPlayers +
                ", mEmptyPlayers=" + mEmptyPlayers +
                '}';
    }
}



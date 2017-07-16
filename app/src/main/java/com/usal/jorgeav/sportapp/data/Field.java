package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class Field implements Parcelable {
    String mId;
    String mName;
    String mSport;
    String mAddress;
    LatLng mCoords;
    String mCity;
    float mRating;
    int mVotes;
    long mOpeningTime;
    long mClosingTime;
    String mCreator;

    public Field(String mId, String mName, String mSport, String mAddress, LatLng mCoords, String mCity, float mRating, int mVotes, long mOpeningTime, long mClosingTime, String creator) {
        this.mId = mId;
        this.mName = mName;
        this.mSport = mSport;
        this.mAddress = mAddress;
        this.mCoords = mCoords;
        this.mCity = mCity;
        this.mRating = mRating;
        this.mVotes = mVotes;
        this.mOpeningTime = mOpeningTime;
        this.mClosingTime = mClosingTime;
        this.mCreator = creator;
    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public String getmSport() {
        return mSport;
    }

    public String getmAddress() {
        return mAddress;
    }

    public LatLng getmCoords() {
        return mCoords;
    }

    public String getmCity() {
        return mCity;
    }

    public float getmRating() {
        return mRating;
    }

    public int getmVotes() {
        return mVotes;
    }

    public long getmOpeningTime() {
        return mOpeningTime;
    }

    public long getmClosingTime() {
        return mClosingTime;
    }

    public String getmCreator() {
        return mCreator;
    }

    @Override
    public String toString() {
        return "Field{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mSport='" + mSport + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mCoords=" + mCoords +
                ", mCity='" + mCity + '\'' +
                ", mRating=" + mRating +
                ", mVotes=" + mVotes +
                ", mOpeningTime=" + mOpeningTime +
                ", mClosingTime=" + mClosingTime +
                ", mCreator='" + mCreator + '\'' +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.DATA, dataToMap());
        result.put(FirebaseDBContract.Field.NEXT_EVENTS, new HashMap<String, Long>());
        return result;
    }

    private Map<String, Object> dataToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Field.NAME, this.mName);
        result.put(FirebaseDBContract.Field.ADDRESS, this.mAddress);
        result.put(FirebaseDBContract.Field.COORD_LATITUDE, this.mCoords.latitude);
        result.put(FirebaseDBContract.Field.COORD_LONGITUDE, this.mCoords.longitude);
        result.put(FirebaseDBContract.Field.CITY, this.mCity);
        result.put(FirebaseDBContract.Field.OPENING_TIME, this.mOpeningTime);
        result.put(FirebaseDBContract.Field.CLOSING_TIME, this.mClosingTime);
        result.put(FirebaseDBContract.Field.SPORT, sportToMap());
        result.put(FirebaseDBContract.Field.CREATOR, this.mCreator);
        return result;
    }

    private Object sportToMap() {
        HashMap<String, Object> sport = new HashMap<>();
        sport.put(FirebaseDBContract.Field.PUNCTUATION, this.mRating);
        sport.put(FirebaseDBContract.Field.VOTES, this.mVotes);

        HashMap<String, Object> result = new HashMap<>();
        result.put(this.mSport, sport);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mSport);
        dest.writeString(this.mAddress);
        dest.writeParcelable(this.mCoords, flags);
        dest.writeString(this.mCity);
        dest.writeFloat(this.mRating);
        dest.writeInt(this.mVotes);
        dest.writeLong(this.mOpeningTime);
        dest.writeLong(this.mClosingTime);
        dest.writeString(this.mCreator);
    }

    protected Field(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mSport = in.readString();
        this.mAddress = in.readString();
        this.mCoords = in.readParcelable(LatLng.class.getClassLoader());
        this.mCity = in.readString();
        this.mRating = in.readFloat();
        this.mVotes = in.readInt();
        this.mOpeningTime = in.readLong();
        this.mClosingTime = in.readLong();
        this.mCreator = in.readString();
    }

    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel source) {
            return new Field(source);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };
}

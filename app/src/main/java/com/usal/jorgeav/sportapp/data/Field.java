package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class Field implements Parcelable {
    String mId;
    String mName;
    String mSport;
    String mAddress;
    String mCity;
    float mRating;
    int mVotes;
    long mOpeningTime;
    long mClosingTime;

    public Field(String mId, String mName, String mSport, String mAddress, String mCity, float mRating, int mVotes, long mOpeningTime, long mClosingTime) {
        this.mId = mId;
        this.mName = mName;
        this.mSport = mSport;
        this.mAddress = mAddress;
        this.mCity = mCity;
        this.mRating = mRating;
        this.mVotes = mVotes;
        this.mOpeningTime = mOpeningTime;
        this.mClosingTime = mClosingTime;
    }

    public String getmId() {
        return mId;
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
        dest.writeString(this.mCity);
        dest.writeFloat(this.mRating);
        dest.writeInt(this.mVotes);
        dest.writeLong(this.mOpeningTime);
        dest.writeLong(this.mClosingTime);
    }

    protected Field(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mSport = in.readString();
        this.mAddress = in.readString();
        this.mCity = in.readString();
        this.mRating = in.readFloat();
        this.mVotes = in.readInt();
        this.mOpeningTime = in.readLong();
        this.mClosingTime = in.readLong();
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

    @Override
    public String toString() {
        return "Field{" +
                "mId='" + mId + '\'' +
                ", mName='" + mName + '\'' +
                ", mSport='" + mSport + '\'' +
                ", mAddress='" + mAddress + '\'' +
                ", mCity='" + mCity + '\'' +
                ", mRating=" + mRating +
                ", mVotes=" + mVotes +
                ", mOpeningTime=" + mOpeningTime +
                ", mClosingTime=" + mClosingTime +
                '}';
    }

    public LatLng getLatLong() {
        return null;
    }
}

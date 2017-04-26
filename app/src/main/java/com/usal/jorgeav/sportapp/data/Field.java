package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class Field implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeString(this.mAddress);
        dest.writeValue(this.mRating);
        dest.writeString(this.mSport);
        dest.writeString(this.mOpeningTime);
        dest.writeString(this.mClosingTime);
    }

    protected Field(Parcel in) {
        this.mId = in.readString();
        this.mName = in.readString();
        this.mAddress = in.readString();
        this.mRating = (Float) in.readValue(Float.class.getClassLoader());
        this.mSport = in.readString();
        this.mOpeningTime = in.readString();
        this.mClosingTime = in.readString();
    }

    public static final Parcelable.Creator<Field> CREATOR = new Parcelable.Creator<Field>() {
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

package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class Sport implements Parcelable {
    String mName;
    float mLevel;
    int mVotes;

    public Sport(String mName, float mLevel, int mVotes) {
        this.mName = mName;
        this.mLevel = mLevel;
        this.mVotes = mVotes;
    }

    public String getmName() {
        return mName;
    }

    public float getmLevel() {
        return mLevel;
    }

    public int getmVotes() {
        return mVotes;
    }

    public void setmLevel(float mLevel) {
        this.mLevel = mLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeFloat(this.mLevel);
        dest.writeInt(this.mVotes);
    }

    protected Sport(Parcel in) {
        this.mName = in.readString();
        this.mLevel = in.readFloat();
        this.mVotes = in.readInt();
    }

    public static final Parcelable.Creator<Sport> CREATOR = new Parcelable.Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel source) {
            return new Sport(source);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };
}

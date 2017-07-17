package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.MyApplication;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class Sport implements Parcelable {
    String mName;
    int iconDrawableId;
    float punctuation;
    int votes;

    public Sport() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Sport(String mName, float punctuation, int votes) {
        this.mName = mName;
        this.iconDrawableId = MyApplication.getAppContext().getResources()
                .getIdentifier(mName , "drawable", MyApplication.getAppContext().getPackageName());
        this.punctuation = punctuation;
        this.votes = votes;
    }

    public String getmName() {
        return mName;
    }

    public int getIconDrawableId() {
        return iconDrawableId;
    }

    public float getPunctuation() {
        return punctuation;
    }

    public int getVotes() {
        return votes;
    }

    public void setPunctuation(float punctuation) {
        this.punctuation = punctuation;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "Sport{" +
                "mName='" + mName + '\'' +
                ", iconDrawableId=" + iconDrawableId +
                ", punctuation=" + punctuation +
                ", votes=" + votes +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mName);
        dest.writeFloat(this.punctuation);
        dest.writeInt(this.votes);
    }

    protected Sport(Parcel in) {
        this.mName = in.readString();
        this.punctuation = in.readFloat();
        this.votes = in.readInt();
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

package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.MyApplication;

/**
 * Created by Jorge Avila on 25/05/2017.
 */

public class Sport implements Parcelable {
    String sportID;
    int iconDrawableId;
    float punctuation;
    int votes;

    public Sport(String sportID, float punctuation, int votes) {
        this.sportID = sportID;
        this.iconDrawableId = MyApplication.getAppContext().getResources()
                .getIdentifier(sportID , "drawable", MyApplication.getAppContext().getPackageName());
        this.punctuation = punctuation;
        this.votes = votes;
    }

    public String getSportID() {
        return sportID;
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
                "sportID='" + sportID + '\'' +
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
        dest.writeString(this.sportID);
        dest.writeInt(this.iconDrawableId);
        dest.writeFloat(this.punctuation);
        dest.writeInt(this.votes);
    }

    protected Sport(Parcel in) {
        this.sportID = in.readString();
        this.iconDrawableId = in.readInt();
        this.punctuation = in.readFloat();
        this.votes = in.readInt();
    }

    public static final Creator<Sport> CREATOR = new Creator<Sport>() {
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

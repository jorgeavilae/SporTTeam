package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;

/**
 * Parcelable
 * se guarda en lista
 * {@link SportsListFragment#newInstance(String, ArrayList)}
 * se recupera en lista
 * {@link SportsListFragment#loadSports()}
 * se pasa como argumento al fragment para poner sus puntuaciones en las rating bar
 */
@SuppressWarnings("unused")
public class Sport implements Parcelable {
    private String sportID;
    private Integer iconDrawableId;
    private Double punctuation;
    private Integer votes;

    public Sport(String sportID, Double punctuation, Integer votes) {
        this.sportID = sportID;
        this.iconDrawableId = Utiles.getSportIconFromResource(sportID);
        this.punctuation = punctuation;
        this.votes = votes;
    }

    public String getSportID() {
        return sportID;
    }

    public Integer getIconDrawableId() {
        return iconDrawableId;
    }

    public Double getPunctuation() {
        return punctuation;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setPunctuation(Double punctuation) {
        this.punctuation = punctuation;
    }

    public void setVotes(Integer votes) {
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
        dest.writeValue(this.iconDrawableId);
        dest.writeValue(this.punctuation);
        dest.writeValue(this.votes);
    }

    protected Sport(Parcel in) {
        this.sportID = in.readString();
        this.iconDrawableId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.punctuation = (Double) in.readValue(Double.class.getClassLoader());
        this.votes = (Integer) in.readValue(Integer.class.getClassLoader());
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

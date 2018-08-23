package com.usal.jorgeav.sportapp.data;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Parcelable
 * se guarda en lista
 * {@link com.usal.jorgeav.sportapp.mainactivities.FieldsActivity#onSaveInstanceState(Bundle)}
 * se recupera en lista
 * {@link com.usal.jorgeav.sportapp.mainactivities.FieldsActivity#onRestoreInstanceState(Bundle)}
 * si necesita q se mantenga para la creacion de nueva instalc, primero introducir deportes - segundo introducir datos - tercero envia a firebase
 * no necesita q se mantenga para la creacion de nuevo usuario, primero introducir datos - segundo introducir deportes - tercero envia a firebase
 * si durante el segundo paso se cambia de app y se vuelve, los deportes se perderian en la creacion de instalacion si no se guardan en parcelable
 *
 * Serializable
 * {@link Field#writeToParcel(Parcel, int)} en {@link Parcel#writeSerializable(Serializable)}
 */
public class SportCourt implements Parcelable, Serializable {
    private String sport_id;
    private Double punctuation;
    private Long votes;

    public SportCourt() {
        // Default constructor required for calls to DataSnapshot.getValue(SportCourt.class)
    }

    public SportCourt(String sport_id, Double punctuation, Long votes) {
        this.sport_id = sport_id;
        this.punctuation = punctuation;
        this.votes = votes;
    }

    public String getSport_id() {
        return sport_id;
    }

    public Double getPunctuation() {
        return punctuation;
    }

    public Long getVotes() {
        return votes;
    }

    public void setPunctuation(Double punctuation) {
        this.punctuation = punctuation;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "SportCourt{" +
                "sport_id='" + sport_id + '\'' +
                ", punctuation=" + punctuation +
                ", votes=" + votes +
                '}';
    }

    public Object toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.SportCourt.SPORT_ID, this.sport_id);
        result.put(FirebaseDBContract.SportCourt.PUNCTUATION, this.punctuation);
        result.put(FirebaseDBContract.SportCourt.VOTES, this.votes);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sport_id);
        dest.writeValue(this.punctuation);
        dest.writeValue(this.votes);
    }

    protected SportCourt(Parcel in) {
        this.sport_id = in.readString();
        this.punctuation = (Double) in.readValue(Double.class.getClassLoader());
        this.votes = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<SportCourt> CREATOR = new Parcelable.Creator<SportCourt>() {
        @Override
        public SportCourt createFromParcel(Parcel source) {
            return new SportCourt(source);
        }

        @Override
        public SportCourt[] newArray(int size) {
            return new SportCourt[size];
        }
    };
}

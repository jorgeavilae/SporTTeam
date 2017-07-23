package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class Field implements Parcelable {
    String id;
    String name;
    String address;
    Double coord_latitude;
    Double coord_longitude;
    String city;
    Long opening_time;
    Long closing_time;
    String creator;
    HashMap<String, SportCourt> sport;

    public Field() {
    }

    public Field(String id, String name, String address, Double coord_latitude, Double coord_longitude, String city, Long opening_time, Long closing_time, String creator, List<SportCourt> sport) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.coord_latitude = coord_latitude;
        this.coord_longitude = coord_longitude;
        this.city = city;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.creator = creator;
        this.sport = new HashMap<>();
        if (sport != null)
            for (SportCourt sc : sport)
                this.sport.put(sc.getSport_id(), sc);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getCoord_latitude() {
        return coord_latitude;
    }

    public Double getCoord_longitude() {
        return coord_longitude;
    }

    public String getCity() {
        return city;
    }

    public Long getOpening_time() {
        return opening_time;
    }

    public Long getClosing_time() {
        return closing_time;
    }

    public String getCreator() {
        return creator;
    }

    public HashMap<String, SportCourt> getSport() {
        return sport;
    }

    public boolean containsSportCourt(String sportId) {
        return this.sport.containsKey(sportId);
    }

    @Override
    public String toString() {
        return "Field{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", city='" + city + '\'' +
                ", opening_time=" + opening_time +
                ", closing_time=" + closing_time +
                ", creator='" + creator + '\'' +
                ", sport=" + sport +
                '}';
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.DATA, dataToMap());
        return result;
    }

    private Map<String, Object> dataToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Field.NAME, this.name);
        result.put(FirebaseDBContract.Field.ADDRESS, this.address);
        result.put(FirebaseDBContract.Field.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.Field.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.Field.CITY, this.city);
        result.put(FirebaseDBContract.Field.OPENING_TIME, this.opening_time);
        result.put(FirebaseDBContract.Field.CLOSING_TIME, this.closing_time);
        result.put(FirebaseDBContract.Field.SPORT, this.sport);
        result.put(FirebaseDBContract.Field.CREATOR, this.creator);
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeValue(this.coord_latitude);
        dest.writeValue(this.coord_longitude);
        dest.writeString(this.city);
        dest.writeValue(this.opening_time);
        dest.writeValue(this.closing_time);
        dest.writeString(this.creator);
        dest.writeSerializable(this.sport);
    }

    protected Field(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.coord_latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.coord_longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.city = in.readString();
        this.opening_time = (Long) in.readValue(Long.class.getClassLoader());
        this.closing_time = (Long) in.readValue(Long.class.getClassLoader());
        this.creator = in.readString();
        this.sport = (HashMap<String, SportCourt>) in.readSerializable();
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

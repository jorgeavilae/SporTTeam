package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class User {
    String uid;
    String email;
    String alias;
    String city;
    Double coord_latitude;
    Double coord_longitude;
    Long age;
    String profile_picture;
    Map<String, Double> sports_practiced;

    public User() {
    }

    public User(String uid, String email, String alias, String city, Double coord_latitude, Double coord_longitude, Long age, String profile_picture, Map<String, Double> sports_practiced) {
        this.uid = uid;
        this.email = email;
        this.alias = alias;
        this.city = city;
        this.coord_latitude = coord_latitude;
        this.coord_longitude = coord_longitude;
        this.age = age;
        this.profile_picture = profile_picture;
        this.sports_practiced = sports_practiced;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlias() {
        return alias;
    }

    public String getCity() {
        return city;
    }

    public Double getCoord_latitude() {
        return coord_latitude;
    }

    public Double getCoord_longitude() {
        return coord_longitude;
    }

    public Long getAge() {
        return age;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Map<String, Double> getSports_practiced() {
        return sports_practiced;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", alias='" + alias + '\'' +
                ", city='" + city + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", age=" + age +
                ", profile_picture='" + profile_picture + '\'' +
                ", sports_practiced=" + sports_practiced +
                '}';
    }

    public Object toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.DATA, dataToMap());
        return result;
    }

    private Map<String, Object> dataToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.User.ALIAS, this.alias);
        result.put(FirebaseDBContract.User.EMAIL, this.email);
        result.put(FirebaseDBContract.User.AGE, this.age);
        result.put(FirebaseDBContract.User.PROFILE_PICTURE, this.profile_picture);
        result.put(FirebaseDBContract.User.CITY, this.city);
        result.put(FirebaseDBContract.User.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.User.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.User.SPORTS_PRACTICED, this.sports_practiced);
        return result;
    }
}

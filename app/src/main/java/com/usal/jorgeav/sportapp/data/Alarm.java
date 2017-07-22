package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 15/06/2017.
 */

public class Alarm {
    String id;
    String sport_id;
    String field_id;
    String city;
    Long date_from;
    Long date_to;
    Long total_players_from;
    Long total_players_to;
    Long empty_players_from;
    Long empty_players_to;

    public Alarm(){}

    public Alarm(String id, String sport_id, String field_id, String city, Long date_from, Long date_to, Long total_players_from, Long total_players_to, Long empty_players_from, Long empty_players_to) {
        this.id = id;
        this.sport_id = sport_id;
        this.field_id = field_id;
        this.city = city;
        this.date_from = date_from;
        this.date_to = date_to;
        this.total_players_from = total_players_from;
        this.total_players_to = total_players_to;
        this.empty_players_from = empty_players_from;
        this.empty_players_to = empty_players_to;
    }

    public String getId() {
        return id;
    }

    public String getSport_id() {
        return sport_id;
    }

    public String getField_id() {
        return field_id;
    }

    public String getCity() {
        return city;
    }

    public Long getDate_from() {
        return date_from;
    }

    public Long getDate_to() {
        return date_to;
    }

    public Long getTotal_players_from() {
        return total_players_from;
    }

    public Long getTotal_players_to() {
        return total_players_to;
    }

    public Long getEmpty_players_from() {
        return empty_players_from;
    }

    public Long getEmpty_players_to() {
        return empty_players_to;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSport_id(String sport_id) {
        this.sport_id = sport_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDate_from(Long date_from) {
        this.date_from = date_from;
    }

    public void setDate_to(Long date_to) {
        this.date_to = date_to;
    }

    public void setTotal_players_from(Long total_players_from) {
        this.total_players_from = total_players_from;
    }

    public void setTotal_players_to(Long total_players_to) {
        this.total_players_to = total_players_to;
    }

    public void setEmpty_players_from(Long empty_players_from) {
        this.empty_players_from = empty_players_from;
    }

    public void setEmpty_players_to(Long empty_players_to) {
        this.empty_players_to = empty_players_to;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Alarm.SPORT, this.sport_id);
        result.put(FirebaseDBContract.Alarm.FIELD, this.field_id);
        result.put(FirebaseDBContract.Alarm.CITY, this.city);
        result.put(FirebaseDBContract.Alarm.DATE_FROM, this.date_from);
        result.put(FirebaseDBContract.Alarm.DATE_TO, this.date_to);
        result.put(FirebaseDBContract.Alarm.TOTAL_PLAYERS_FROM, this.total_players_from);
        result.put(FirebaseDBContract.Alarm.TOTAL_PLAYERS_TO, this.total_players_to);
        result.put(FirebaseDBContract.Alarm.EMPTY_PLAYERS_FROM, this.empty_players_from);
        result.put(FirebaseDBContract.Alarm.EMPTY_PLAYERS_TO, this.empty_players_to);
        return result;
    }

    @Override
    public String toString() {
        return "Alarm{" +
                "id='" + id + '\'' +
                ", sport_id='" + sport_id + '\'' +
                ", field_id='" + field_id + '\'' +
                ", city='" + city + '\'' +
                ", date_from=" + date_from +
                ", date_to=" + date_to +
                ", total_players_from=" + total_players_from +
                ", total_players_to=" + total_players_to +
                ", empty_players_from=" + empty_players_from +
                ", empty_players_to=" + empty_players_to +
                '}';
    }
}

package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;

/**
 * Created by Jorge Avila on 22/07/2017.
 */

public class SportCourt {
    String name;
    String sport_id;
    Double punctuation;
    Long votes;

    public SportCourt() {
    }

    public SportCourt(String name, String sport_id, Double punctuation, Long votes) {
        this.name = name;
        this.sport_id = sport_id;
        this.punctuation = punctuation;
        this.votes = votes;
    }

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return "SportCourt{" +
                "name='" + name + '\'' +
                ", sport_id='" + sport_id + '\'' +
                ", punctuation=" + punctuation +
                ", votes=" + votes +
                '}';
    }

    public Object toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.SportCourt.NAME, this.name);
        result.put(FirebaseDBContract.SportCourt.SPORT_ID, this.sport_id);
        result.put(FirebaseDBContract.SportCourt.PUNCTUATION, this.punctuation);
        result.put(FirebaseDBContract.SportCourt.VOTES, this.votes);
        return result;
    }

}

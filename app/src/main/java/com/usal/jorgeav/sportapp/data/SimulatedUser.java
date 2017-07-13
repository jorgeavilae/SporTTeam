package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 13/07/2017.
 */

public class SimulatedUser {
    String alias;
    String profile_picture;
    Long age;
    String owner;

    public SimulatedUser() {
        // Default constructor required for calls to DataSnapshot.getValue(Invitation.class)
    }

    public SimulatedUser(String alias, String profile_picture, Long age, String owner) {
        this.alias = alias;
        this.profile_picture = profile_picture;
        this.age = age;
        this.owner = owner;
    }

    public String getAlias() {
        return alias;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Long getAge() {
        return age;
    }

    public String getOwner() {
        return owner;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.User.ALIAS, this.alias);
        result.put(FirebaseDBContract.User.PROFILE_PICTURE, this.profile_picture);
        result.put(FirebaseDBContract.User.AGE, this.age);
        result.put(FirebaseDBContract.Event.OWNER, this.owner);
        return result;
    }

    @Override
    public String toString() {
        return "SimulatedUser{" +
                "alias='" + alias + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", age=" + age +
                ", owner=" + owner +
                '}';
    }
}

package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 08/07/2017.
 */

public class Invitation {
    String sender;
    String receiver;
    String event;
    Long date;

    public Invitation() {
        // Default constructor required for calls to DataSnapshot.getValue(Invitation.class)
    }

    public Invitation(String sender, String receiver, String event, Long date) {
        this.sender = sender;
        this.receiver = receiver;
        this.event = event;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getEvent() {
        return event;
    }

    public Long getDate() {
        return date;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Invitation.SENDER, this.sender);
        result.put(FirebaseDBContract.Invitation.RECEIVER, this.receiver);
        result.put(FirebaseDBContract.Invitation.EVENT, this.event);
        result.put(FirebaseDBContract.Invitation.DATE, this.date);
        return result;
    }
}

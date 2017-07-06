package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 06/07/2017.
 */

public class MyNotification {
    Boolean checked;
    String message;
    String extra_data;
    // TODO: 06/07/2017 necesita mas parametros, hay que ponerlos tambien en FirebaseActions.java

    public MyNotification() {
        // Default constructor required for calls to DataSnapshot.getValue(MyNotification.class)
    }

    public MyNotification(Boolean checked, String message, String extra_data) {
        this.checked = checked;
        this.message = message;
        this.extra_data = extra_data;
    }

    public Boolean getChecked() {
        return checked;
    }

    public String getMessage() {
        return message;
    }

    public String getExtra_data() {
        return extra_data;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Notification.CHECKED, checked);
        result.put(FirebaseDBContract.Notification.MESSAGE, message);
        result.put(FirebaseDBContract.Notification.EXTRA_DATA, extra_data);
        return result;
    }
}

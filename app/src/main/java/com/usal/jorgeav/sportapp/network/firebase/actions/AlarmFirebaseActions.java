package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

@SuppressWarnings("WeakerAccess")
public class AlarmFirebaseActions {
    @SuppressWarnings("unused")
    private static final String TAG = AlarmFirebaseActions.class.getSimpleName();

    public static void addAlarm(Alarm alarm, String myUserId) {
        DatabaseReference myUserAlarmsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserId)
                .child(FirebaseDBContract.User.ALARMS);

        if (TextUtils.isEmpty(alarm.getId()))
            alarm.setId(myUserAlarmsRef.push().getKey());

        myUserAlarmsRef.child(alarm.getId()).setValue(alarm.toMap());
    }

    public static void deleteAlarm(String userId, String alarmId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(userId).child(FirebaseDBContract.User.ALARMS).child(alarmId).removeValue();
    }

    public static void deleteAllAlarms(String userId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(userId).child(FirebaseDBContract.User.ALARMS).removeValue();
    }
}

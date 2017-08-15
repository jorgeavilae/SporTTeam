package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

/**
 * Created by Jorge Avila on 14/08/2017.
 */

public class AlarmsFirebaseSync {
    public static final String TAG = AlarmsFirebaseSync.class.getSimpleName();

    // Alarms
    public static void loadAnAlarm(@NonNull String alarmId) {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.ALARMS);

        myUserRef.child(alarmId)
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Alarm a = dataSnapshot.getValue(Alarm.class);
                            if (a == null) {
                                Log.e(TAG, "loadAnAlarm: onChildAddedExecutor: Error parsing alarm");
                                return;
                            }
                            a.setId(dataSnapshot.getKey());

                            ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                            if (a.getField_id() != null)
                                FieldsFirebaseSync.loadAField(a.getField_id());
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static void loadAnAlarmAndNotify(final String notificationRef, final MyNotification notification) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID + "/" + FirebaseDBContract.User.ALARMS);

        if (notification.getExtra_data_one() != null)
        myUserRef.child(notification.getExtra_data_one())
                .addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Alarm a = dataSnapshot.getValue(Alarm.class);
                            if (a == null) {
                                Log.e(TAG, "loadAnAlarmAndNotify: onChildAddedExecutor: Error parsing alarm");
                                return;
                            }
                            a.setId(dataSnapshot.getKey());

                            ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                            if (a.getField_id() != null)
                                FieldsFirebaseSync.loadAField(a.getField_id());


                            String eventId = notification.getExtra_data_two();
                            if (eventId != null)
                                database.getReference(FirebaseDBContract.TABLE_EVENTS)
                                        .child(eventId).addListenerForSingleValueEvent(new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()){
                                @Override
                                protected void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                                        if (e == null) {
                                            Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Error parsing Event");
                                            return;
                                        }
                                        e.setEvent_id(dataSnapshot.getKey());

                                        ContentValues cv = UtilesContentValues.eventToContentValues(e);
                                        MyApplication.getAppContext().getContentResolver()
                                                .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);
                                        UsersFirebaseSync.loadAProfile(null, e.getOwner(), false);
                                        FieldsFirebaseSync.loadAField(e.getField_id());

                                        //Notify
                                        UtilesNotification.createNotification(MyApplication.getAppContext(), notification, a);
                                        NotificationsFirebaseActions.checkNotification(notificationRef);

                                    } else {
                                        Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Event "
                                                + notification.getExtra_data_two() + " doesn't exist");
                                        FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                                    }
                                }

                                @Override
                                protected void onCancelledExecutor(DatabaseError databaseError) {

                                }
                            });
                        } else {
                            Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Alarm "
                                    + notification.getExtra_data_one() + " doesn't exist");
                            FirebaseDatabase.getInstance().getReferenceFromUrl(notificationRef).removeValue();
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    public static ExecutorChildEventListener getListenerToLoadAlarmsFromMyAlarms() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Alarm a = dataSnapshot.getValue(Alarm.class);
                    if (a == null) {
                        Log.e(TAG, "loadAlarmsFromMyAlarms: onChildAddedExecutor: Error parsing alarm");
                        return;
                    }
                    a.setId(dataSnapshot.getKey());

                    ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);
                    if (a.getField_id() != null)
                        FieldsFirebaseSync.loadAField(a.getField_id());
                }

            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String alarmId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver().delete(
                        SportteamContract.AlarmEntry.CONTENT_ALARM_URI,
                        SportteamContract.AlarmEntry.ALARM_ID + " = ? ",
                        new String[]{alarmId});

            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }
}

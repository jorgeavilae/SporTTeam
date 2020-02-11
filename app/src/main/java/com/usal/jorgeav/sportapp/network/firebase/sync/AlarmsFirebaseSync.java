package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.content.Context;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.MyNotification;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.NotificationsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.Utiles;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;
import com.usal.jorgeav.sportapp.utils.UtilesNotification;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para sincronizar los datos de
 * Firebase Realtime Database con el Proveedor de Contenido. Concretamente, los datos relativos a
 * las alarmas del usuario actual.
 * <p>
 * Proporciona tanto métodos para sincronizar datos en una sola consulta, como métodos para obtener
 * los Listeners que se vincularán a los datos que necesiten una escucha continuada, en
 * {@link FirebaseSync#syncFirebaseDatabase(LoginActivity)}
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class AlarmsFirebaseSync {
    /**
     * Nombre de la clase
     */
    private static final String TAG = AlarmsFirebaseSync.class.getSimpleName();

    /**
     * Sincroniza los datos de una alarma. También se asegura que los datos relativos a la posible
     * instalación sobre la que está establecida la alarma estén en el Proveedor de Contenido.
     *
     * @param alarmId identificador de la alarma que se debe sincronizar
     */
    public static void loadAnAlarm(@NonNull String alarmId) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS).child(alarmId);

        ref.addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Alarm a = dataSnapshot.getValue(Alarm.class);
                            if (a == null) {
                                Log.e(TAG, "loadAnAlarm: onChildAddedExecutor: Error parsing alarm");
                                return;
                            }
                            a.setId(dataSnapshot.getKey());

                            // Store alarm in Content Provider
                            ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);

                            // Load field
                            if (a.getField_id() != null)
                                FieldsFirebaseSync.loadAField(a.getField_id());
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Sincroniza los datos de una alarma cuyo identificador ha venido insertado en una notificación
     * desde Firebase Cloud Messaging. Para mostrar los datos de dicha alarma al pulsar la
     * notificación, primero deben estar en el Proveedor de Contenido, por lo que se sincronizan
     * junto con los del partido encontrado que coincide con ella.
     * A continuación, se invoca
     * {@link UtilesNotification#createNotification(Context, MyNotification, Alarm)}
     *
     * @param notificationRef identificador de la notificación
     * @param notification    objeto notificación del que extraer el identificador de usuario
     */
    public static void loadAnAlarmAndNotify(final String notificationRef,
                                            final MyNotification notification) {
        String myUserID = Utiles.getCurrentUserId();
        if (TextUtils.isEmpty(myUserID)) return;

        if (notification.getExtra_data_one() == null) return;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myUserRef = database.getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserID).child(FirebaseDBContract.User.ALARMS).child(notification.getExtra_data_one());

        myUserRef.addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final Alarm a = dataSnapshot.getValue(Alarm.class);
                            if (a == null) {
                                Log.e(TAG, "loadAnAlarmAndNotify: onChildAddedExecutor: Error parsing alarm");
                                return;
                            }
                            a.setId(dataSnapshot.getKey());

                            // Store alarm in Content Provider
                            ContentValues cv = UtilesContentValues.alarmToContentValues(a);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.AlarmEntry.CONTENT_ALARM_URI, cv);

                            // Load field
                            if (a.getField_id() != null)
                                FieldsFirebaseSync.loadAField(a.getField_id());


                            String eventId = notification.getExtra_data_two();
                            if (eventId == null) return;
                            FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_EVENTS)
                                    .child(eventId).addListenerForSingleValueEvent(
                                    new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                                        @Override
                                        protected void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Event e = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Event.class);
                                                if (e == null) {
                                                    Log.e(TAG, "loadAnAlarmAndNotify: onDataChangeExecutor: Error parsing Event");
                                                    return;
                                                }
                                                e.setEvent_id(dataSnapshot.getKey());

                                                // Store event in Content Provider
                                                ContentValues cv = UtilesContentValues.eventToContentValues(e);
                                                MyApplication.getAppContext().getContentResolver()
                                                        .insert(SportteamContract.EventEntry.CONTENT_EVENT_URI, cv);

                                                // Load owner and field
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

    /**
     * Crea un Listener para vincularlo sobre la lista de alarmas del usuario actual y sincronizarlas
     * con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadAlarmsFromMyAlarms()
     */
    static ExecutorChildEventListener getListenerToLoadAlarmsFromMyAlarms() {
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

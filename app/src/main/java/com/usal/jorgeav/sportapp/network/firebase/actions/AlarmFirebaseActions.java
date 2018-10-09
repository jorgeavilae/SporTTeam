package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.text.TextUtils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a las alarmas del usuario
 * actual.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class AlarmFirebaseActions {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = AlarmFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar la alarma especificada en la base de datos del servidor. Obtiene una
     * referencia a la rama correspondiente del archivo JSON y establece la alarma con
     * {@link Alarm#toMap()}
     *
     * @param alarm    objeto {@link Alarm} listo para ser añadido
     * @param myUserId identificador del usuario actual al que debe añadirse esta alarma
     */
    public static void addAlarm(Alarm alarm, String myUserId) {
        DatabaseReference myUserAlarmsRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_USERS)
                .child(myUserId)
                .child(FirebaseDBContract.User.ALARMS);

        if (TextUtils.isEmpty(alarm.getId()))
            alarm.setId(myUserAlarmsRef.push().getKey());

        myUserAlarmsRef.child(alarm.getId()).setValue(alarm.toMap());
    }

    /**
     * Invocado para borrar la alarma especificada de la base de datos del servidor. Obtiene la
     * referencia a la rama correspondiente del archivo JSON y borra los datos asociados.
     *
     * @param userId  identificador del usuario creador de la alarma
     * @param alarmId identificador de la alarma que debe ser borrada
     */
    public static void deleteAlarm(String userId, String alarmId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(userId).child(FirebaseDBContract.User.ALARMS).child(alarmId).removeValue();
    }

    /**
     * Invocado para borrar todas las alarmas del usuario en la base de datos del servidor. Obtiene
     * la referencia a la rama correspondiente del archivo JSON y borra los datos asociados.
     *
     * @param userId identificador del usuario creador de las alarmas
     */
    static void deleteAllAlarms(String userId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_USERS)
                .child(userId).child(FirebaseDBContract.User.ALARMS).removeValue();
    }
}

package com.usal.jorgeav.sportapp.network.firebase.sync;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.AppExecutor;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorChildEventListener;
import com.usal.jorgeav.sportapp.network.firebase.ExecutorValueEventListener;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.actions.FieldsFirebaseActions;
import com.usal.jorgeav.sportapp.utils.UtilesContentValues;

import java.util.List;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para sincronizar los datos de
 * Firebase Realtime Database con el Proveedor de Contenido. Concretamente, los datos relativos a
 * las instalaciones de la ciudad del usuario actual.
 * <p>
 * Proporciona tanto un método para sincronizar datos en una sola consulta, como otro método para
 * obtener el Listener que se vinculará a las instalaciones que necesiten una escucha continuada, en
 * {@link FirebaseSync#syncFirebaseDatabase(LoginActivity)}
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class FieldsFirebaseSync {
    /**
     * Nombre de la clase
     */
    private static final String TAG = FieldsFirebaseSync.class.getSimpleName();

    /**
     * Sincroniza los datos de una instalación, incluidas sus pistas con la puntuación y votos
     * actuales. También comprueba si hay partidos pasados en la rama
     * {@link FirebaseDBContract.Field#NEXT_EVENTS} del servidor y los borra.
     *
     * @param fieldId identificador de la instalación que se debe sincronizar
     */
    public static void loadAField(String fieldId) {
        if (fieldId == null || TextUtils.isEmpty(fieldId)) return;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference(FirebaseDBContract.TABLE_FIELDS).child(fieldId);

        ref.addListenerForSingleValueEvent(
                new ExecutorValueEventListener(AppExecutor.getInstance().getExecutor()) {
                    @Override
                    public void onDataChangeExecutor(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Field field = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Field.class);
                            if (field == null) {
                                Log.e(TAG, "loadAField: onDataChangeExecutor: Error parsing Field");
                                return;
                            }
                            field.setId(dataSnapshot.getKey());

                            // Check and delete old events under "next_events" in this Field
                            long currentTimeMillis = System.currentTimeMillis();
                            for (DataSnapshot nextEventDataSnapshot :
                                    dataSnapshot.child(FirebaseDBContract.Field.NEXT_EVENTS).getChildren()) {
                                Long nextEventTimeMillis = nextEventDataSnapshot.getValue(Long.class);
                                if (nextEventTimeMillis != null && nextEventTimeMillis < currentTimeMillis)
                                    FieldsFirebaseActions.deleteNextEventInField
                                            (dataSnapshot.getKey(), nextEventDataSnapshot.getKey());
                            }

                            // Store Field in Content Provider
                            ContentValues cvData = UtilesContentValues.fieldToContentValues(field);
                            MyApplication.getAppContext().getContentResolver()
                                    .insert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, cvData);

                            // Delete Field's courts in case some of them was deleted
                            MyApplication.getAppContext().getContentResolver()
                                    .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                            SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                            new String[]{field.getId()});

                            // Store Field's courts in ContentProvider
                            List<ContentValues> cvSports = UtilesContentValues.fieldSportToContentValues(field);
                            MyApplication.getAppContext().getContentResolver()
                                    .bulkInsert(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                            cvSports.toArray(new ContentValues[0]));
                        }
                    }

                    @Override
                    public void onCancelledExecutor(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Crea un Listener para vincularlo sobre la lista de instalaciones de la ciudad del usuario
     * actual y sincronizarlas con el Proveedor de Contenido
     *
     * @return una nueva instancia de {@link ExecutorChildEventListener}
     * @see FirebaseSync#loadFieldsFromCity(String, boolean)
     */
    static ExecutorChildEventListener getListenerToLoadFieldsFromCity() {
        return new ExecutorChildEventListener(AppExecutor.getInstance().getExecutor()) {
            @Override
            public void onChildAddedExecutor(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()) {
                    Field field = dataSnapshot.child(FirebaseDBContract.DATA).getValue(Field.class);
                    if (field == null) {
                        Log.e(TAG, "loadFieldsFromCity: onDataChangeExecutor: Error parsing Field from "
                                + dataSnapshot.child(FirebaseDBContract.DATA).getRef());
                        return;
                    }
                    field.setId(dataSnapshot.getKey());

                    ContentValues cvData = UtilesContentValues.fieldToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .insert(SportteamContract.FieldEntry.CONTENT_FIELD_URI, cvData);

                    MyApplication.getAppContext().getContentResolver()
                            .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                    new String[]{field.getId()});

                    List<ContentValues> cvSports = UtilesContentValues.fieldSportToContentValues(field);
                    MyApplication.getAppContext().getContentResolver()
                            .bulkInsert(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                    cvSports.toArray(new ContentValues[0]));
                }
            }

            @Override
            public void onChildChangedExecutor(DataSnapshot dataSnapshot, String s) {
                onChildAdded(dataSnapshot, s);
            }

            @Override
            public void onChildRemovedExecutor(DataSnapshot dataSnapshot) {
                String fieldId = dataSnapshot.getKey();
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FieldEntry.CONTENT_FIELD_URI,
                                SportteamContract.FieldEntry.FIELD_ID + " = ? ",
                                new String[]{fieldId});
                MyApplication.getAppContext().getContentResolver()
                        .delete(SportteamContract.FieldSportEntry.CONTENT_FIELD_SPORT_URI,
                                SportteamContract.FieldSportEntry.FIELD_ID + " = ? ",
                                new String[]{fieldId});
            }

            @Override
            public void onChildMovedExecutor(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelledExecutor(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Realiza una única consulta a la base de datos de Firebase buscando los próximos partidos
     * de una instalación concreta y aplica el {@link ValueEventListener} proporcionado.
     * Utilizado para comprobar la existencia de próximos partidos en la instalación.
     *
     * @param fieldId            identificador de la instalación buscada
     * @param valueEventListener listener con las acciones a realizar al recibir los resultados
     *                           de la consulta
     */
    public static void queryNextEventsFromField(String fieldId, ValueEventListener valueEventListener) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.Field.NEXT_EVENTS)
                .addListenerForSingleValueEvent(valueEventListener);
    }
}

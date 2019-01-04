package com.usal.jorgeav.sportapp.network.firebase.actions;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.SportCourt;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;
import com.usal.jorgeav.sportapp.network.firebase.sync.FieldsFirebaseSync;

import java.util.HashMap;
import java.util.Map;

/**
 * Los métodos de esta clase contienen la funcionalidad necesaria para actuar sobre los datos de
 * Firebase Realtime Database. Concretamente, sobre los datos relativos a las instalaciones.
 *
 * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/database/FirebaseDatabase">
 * FirebaseDatabase</a>
 */
public class FieldsFirebaseActions {
    /**
     * Nombre de la clase
     */
    private static final String TAG = FieldsFirebaseActions.class.getSimpleName();

    /**
     * Invocado para insertar la instalación especificada en la base de datos del servidor.
     * Obtiene una referencia a la rama correspondiente del archivo JSON y establece la instalación
     * con {@link Field#toMap()}
     *
     * @param field objeto {@link Field} listo para ser añadido
     */
    public static void addField(Field field) {
        DatabaseReference fieldTable = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS);
        field.setId(fieldTable.push().getKey());

        fieldTable.child(field.getId()).child(FirebaseDBContract.DATA).setValue(field.toMap());
    }

    /**
     * Invocado para actualizar una instalación especificada en la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama de la instalación para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre esa rama. Con ella, se
     * consultan los datos de la instalación, se modifican y se reinsertan en la base de datos de
     * forma atómica. Esto es necesario para actualizar la dirección, ciudad o coordenadas de todos
     * los partidos que se encuentren bajo la rama {@link FirebaseDBContract.Field#NEXT_EVENTS}
     * de la instalación.
     * <p>
     * A continuación, actualiza los propios datos de la instalación.
     *
     * @param field objeto {@link Field} listo para ser añadido
     */
    public static void updateField(final Field field) {
        DatabaseReference fieldRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(field.getId());
        fieldRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                //Update field data in Events
                Map<String, Object> childUpdates = new HashMap<>();
                for (MutableData md : mutableData.child(FirebaseDBContract.Field.NEXT_EVENTS).getChildren()) {
                    //Event reference
                    String eventRef = "/" + FirebaseDBContract.TABLE_EVENTS + "/" + md.getKey() + "/"
                            + FirebaseDBContract.DATA;
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.ADDRESS, field.getAddress());
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.CITY, field.getCity());
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.COORD_LATITUDE, field.getCoord_latitude());
                    childUpdates.put(eventRef + "/" + FirebaseDBContract.Event.COORD_LONGITUDE, field.getCoord_longitude());
                }
                FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates);

                //Update field
                field.setId(null);
                mutableData.child(FirebaseDBContract.DATA).setValue(field);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    /**
     * Actualiza las pistas de una instalación mediante un {@link Map} que sustituye los datos que
     * hubiera previamente en la rama correspondiente de la instalación dentro del archivo JSON.
     *
     * @param fieldId   identificador de la instalación
     * @param sportsMap mapa cuya clave del par es el identificador del deporte, y cuyo valor del
     *                  par es el objeto mapeado que representa la pista {@link SportCourt#toMap()}.
     */
    public static void updateFieldSports(String fieldId, Map<String, Object> sportsMap) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT).setValue(sportsMap);
    }

    /**
     * Invocado para insertar una pista a las ya existentes en la instalación indicada dentro de la
     * base de datos del servidor.
     *
     * @param fieldId identificador de la instalación
     * @param sport   pista nueva
     */
    public static void addFieldSport(String fieldId, SportCourt sport) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT).child(sport.getSport_id()).setValue(sport.toMap());
    }

    /**
     * Invocado para borrar la instalación de la base de datos del servidor.
     *
     * @param fieldId identificador de la instalación
     */
    public static void deleteField(String fieldId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId).removeValue();
    }

    /**
     * Invocado para borrar uno de los partidos de la lista de próximos partidos de una instalación
     *
     * @param fieldId identificador de la instalación
     * @param eventId identificador del partido
     */
    public static void deleteNextEventInField(String fieldId, String eventId) {
        FirebaseDatabase.getInstance().getReference(FirebaseDBContract.TABLE_FIELDS).child(fieldId)
                .child(FirebaseDBContract.Field.NEXT_EVENTS).child(eventId).removeValue();
    }

    /**
     * Invocado para votar y actualizar la puntuación de una de las pistas de una instalación
     * especificada de la base de datos del servidor.
     * <p>
     * Obtiene la referencia a la rama de la pista de la instalación para realizar una transacción
     * {@link DatabaseReference#runTransaction(Transaction.Handler)} sobre esa rama. Con ella, se
     * consultan los datos de la pista, se modifican y se reinsertan en la base de datos de
     * forma atómica. Esto es necesario para actualizar el número de votos y la puntuación de la
     * pista de la instalación.
     * <p>
     * La puntuación se calcula con la media aritmética de todos los votos y redondeando a .5 o .0
     *
     * @param fieldId identificador de la instalación que contiene la pista
     * @param sportId identificador del deporte, y por tanto, de la pista por la que se vota
     * @param rate    puntuación del voto
     */
    public static void voteField(final String fieldId, String sportId, final float rate) {
        //Add vote to count and recalculate average rating
        DatabaseReference fieldSportRef = FirebaseDatabase.getInstance()
                .getReference(FirebaseDBContract.TABLE_FIELDS)
                .child(fieldId)
                .child(FirebaseDBContract.DATA)
                .child(FirebaseDBContract.Field.SPORT)
                .child(sportId);
        fieldSportRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                SportCourt sport = mutableData.getValue(SportCourt.class);
                if (sport == null) return Transaction.success(mutableData);

                double newRating = (sport.getPunctuation() * sport.getVotes() + rate) / (sport.getVotes() + 1);
                sport.setVotes(sport.getVotes() + 1);

                // Round to .5 or .0 https://stackoverflow.com/a/23449769/4235666
                sport.setPunctuation(Math.round(newRating * 2) / 2.0);

                mutableData.setValue(sport);

                FieldsFirebaseSync.loadAField(fieldId);

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.i(TAG, "voteField: onComplete:" + databaseError);
            }
        });
    }
}

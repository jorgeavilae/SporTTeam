package com.usal.jorgeav.sportapp.data;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Representa una pista de una instalación
 * <p>
 * Implementa la interfaz Parcelable para poder guardar este {@link Object} en variables de
 * estado entre cambios de configuración
 * ({@link android.app.Activity#onConfigurationChanged(Configuration)}, o en {@link android.content.Intent}
 * para enviarla a otra {@link android.app.Activity}
 * <p>
 * Implementa la interfaz Serializable para poder guardar este {@link Object} en Parcel
 * de {@link Field} en el método {@link Field#writeToParcel(Parcel, int)}
 *
 * @see com.usal.jorgeav.sportapp.mainactivities.FieldsActivity#onSaveInstanceState(Bundle)
 * @see com.usal.jorgeav.sportapp.mainactivities.FieldsActivity#onRestoreInstanceState(Bundle)
 */
public class SportCourt implements Parcelable, Serializable {
    /**
     * Identificador del deporte de la pista
     */
    private String sport_id;
    /**
     * Puntuación de la pista
     */
    private Double punctuation;
    /**
     * Cantidad de votos emitidos para la pista
     */
    private Long votes;

    /**
     * Constructor sin argumentos. Permite transformar un datos obtenidos desde Firebase Realtime
     * Database con DataSnapshot.getValue(Class), siempre y cuando las variables tengan el mismo
     * nombre que las etiquetas de la base de datos del servidor.
     *
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     * DataSnapshot.getValue(Class)</a>
     */
    public SportCourt() {
        // Default constructor required for calls to DataSnapshot.getValue(SportCourt.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param sport_id    identificador de deporte
     * @param punctuation puntuación
     * @param votes       número de votos
     */
    public SportCourt(String sport_id, Double punctuation, Long votes) {
        this.sport_id = sport_id;
        this.punctuation = punctuation;
        this.votes = votes;
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

    public void setPunctuation(Double punctuation) {
        this.punctuation = punctuation;
    }

    public void setVotes(Long votes) {
        this.votes = votes;
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link SportCourt}
     */
    public Object toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.SportCourt.SPORT_ID, this.sport_id);
        result.put(FirebaseDBContract.SportCourt.PUNCTUATION, this.punctuation);
        result.put(FirebaseDBContract.SportCourt.VOTES, this.votes);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "SportCourt{" +
                "sport_id='" + sport_id + '\'' +
                ", punctuation=" + punctuation +
                ", votes=" + votes +
                '}';
    }

    /**
     * Describe los contenidos del objeto mediante un entero que representa una máscara de bits
     *
     * @return 0 (máscara de bits)
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Escribe este {@link SportCourt} en un {@link Parcel}
     *
     * @param dest  Destino de la operación
     * @param flags opcional
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sport_id);
        dest.writeValue(this.punctuation);
        dest.writeValue(this.votes);
    }

    /**
     * Constructor que ejecuta una operación inversa a {@link #writeToParcel(Parcel, int)}
     *
     * @param in Parcel del que extraer los datos para {@link SportCourt}
     */
    protected SportCourt(Parcel in) {
        this.sport_id = in.readString();
        this.punctuation = (Double) in.readValue(Double.class.getClassLoader());
        this.votes = (Long) in.readValue(Long.class.getClassLoader());
    }

    /**
     * Variable estática usada para crear instancias de {@link SportCourt} a partir del
     * {@link Parcel} creado en {@link #writeToParcel(Parcel, int)}
     */
    public static final Parcelable.Creator<SportCourt> CREATOR = new Parcelable.Creator<SportCourt>() {
        @Override
        public SportCourt createFromParcel(Parcel source) {
            return new SportCourt(source);
        }

        @Override
        public SportCourt[] newArray(int size) {
            return new SportCourt[size];
        }
    };
}

package com.usal.jorgeav.sportapp.data;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.usal.jorgeav.sportapp.mainactivities.MapsActivity;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representa una Instalación del modelo.
 *
 * <p>Implementa la interfaz Parcelable para poder guardar este {@link Object} en variables de
 * estado entre cambios de configuración
 * ({@link android.app.Activity#onConfigurationChanged(Configuration)}), o en {@link Intent}
 * para enviarla a otra {@link android.app.Activity}
 *
 * @see com.usal.jorgeav.sportapp.events.addevent.NewEventFragment#onSaveInstanceState(Bundle)
 * @see com.usal.jorgeav.sportapp.events.addevent.NewEventFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
 * @see MapsActivity#onOptionsItemSelected(MenuItem)
 * @see com.usal.jorgeav.sportapp.mainactivities.EventsActivity#onActivityResult(int, int, Intent)
 */
public class Field implements Parcelable {
    /**
     * Identificador único de la instalación
     */
    private String id;
    /**
     * Nombre de la instalación
     */
    private String name;
    /**
     * Dirección de la instalación
     */
    private String address;
    /**
     * Componente latitud de la coordenada correspondiente a la dirección
     */
    private Double coord_latitude;
    /**
     * Componente longitud de la coordenada correspondiente a la dirección
     */
    private Double coord_longitude;
    /**
     * Nombre de la ciudad en la que se encuentra la instalación
     */
    private String city;
    /**
     * Hora de apertura de la instalación
     */
    private Long opening_time;
    /**
     * Hora de cierre de la instalación
     */
    private Long closing_time;
    /**
     * Identificador único del usuario creador de la instalación
     */
    private String creator;
    /**
     * Map de las pistas que pertenecen a esta instalación. Identificados por el deporte
     * de la pista. El valor de cada elemento es un {@link SportCourt} con los votos y la puntuación
     */
    private HashMap<String, SportCourt> sport;

    /**
     * Constructor sin argumentos. Necesario para el parseo de este objeto desde Firebase
     * Realtime Database con DataSnapshot.getValue(Class)
     *
     * @see
     * <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     *     DataSnapshot.getValue(Class)
     * </a>
     */
    public Field() {
        // Default constructor required for calls to DataSnapshot.getValue(Field.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param id Identificador de la instalación
     * @param name Nombre de la instalación
     * @param address Dirección de la instalación
     * @param coord_latitude Latitud de la dirección de la instalación
     * @param coord_longitude Longitud de la dirección de la instalación
     * @param city Nombre de la ciudad de la instalación
     * @param opening_time Hora de apertura de la instalación
     * @param closing_time Hora de cierre de la instalacion
     * @param creator Identificador del usuario creador de la instalación
     * @param sport Map de las pistas de la instalación
     */
    public Field(String id, String name, String address, Double coord_latitude, Double coord_longitude,
                 String city, Long opening_time, Long closing_time, String creator, List<SportCourt> sport) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.coord_latitude = coord_latitude;
        this.coord_longitude = coord_longitude;
        this.city = city;
        this.opening_time = opening_time;
        this.closing_time = closing_time;
        this.creator = creator;
        this.sport = new HashMap<>();
        if (sport != null)
            for (SportCourt sc : sport)
                this.sport.put(sc.getSport_id(), sc);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Double getCoord_latitude() {
        return coord_latitude;
    }

    public Double getCoord_longitude() {
        return coord_longitude;
    }

    public String getCity() {
        return city;
    }

    public Long getOpening_time() {
        return opening_time;
    }

    public Long getClosing_time() {
        return closing_time;
    }

    public String getCreator() {
        return creator;
    }

    public HashMap<String, SportCourt> getSport() {
        return sport;
    }

    /**
     * Comprueba si un deporte pertenece al conjunto de pistas de la instalación
     *
     * @param sportId identificador del deporte
     * @return true si el deporte ya forma parte de la instalación, false en otro caso
     */
    public boolean containsSportCourt(String sportId) {
        return this.sport.containsKey(sportId);
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link Field}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Field.NAME, this.name);
        result.put(FirebaseDBContract.Field.ADDRESS, this.address);
        result.put(FirebaseDBContract.Field.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.Field.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.Field.CITY, this.city);
        result.put(FirebaseDBContract.Field.OPENING_TIME, this.opening_time);
        result.put(FirebaseDBContract.Field.CLOSING_TIME, this.closing_time);
        result.put(FirebaseDBContract.Field.SPORT, this.sport);
        result.put(FirebaseDBContract.Field.CREATOR, this.creator);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "Field{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", city='" + city + '\'' +
                ", opening_time=" + opening_time +
                ", closing_time=" + closing_time +
                ", creator='" + creator + '\'' +
                ", sport=" + sport +
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
     * Escribe este {@link Field} en un {@link Parcel}
     *
     * @param dest Destino de la operación
     * @param flags opcional
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeValue(this.coord_latitude);
        dest.writeValue(this.coord_longitude);
        dest.writeString(this.city);
        dest.writeValue(this.opening_time);
        dest.writeValue(this.closing_time);
        dest.writeString(this.creator);
        dest.writeSerializable(this.sport);
    }

    /**
     * Constructor que ejecuta una operación inversa a {@link #writeToParcel(Parcel, int)}
     *
     * @param in Parcel del que extraer los datos para {@link Field}
     */
    protected Field(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.coord_latitude = (Double) in.readValue(Double.class.getClassLoader());
        this.coord_longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.city = in.readString();
        this.opening_time = (Long) in.readValue(Long.class.getClassLoader());
        this.closing_time = (Long) in.readValue(Long.class.getClassLoader());
        this.creator = in.readString();
        this.sport = (HashMap<String, SportCourt>) in.readSerializable();
    }

    /**
     * Variable estática usada para crear instancias de {@link Field} a partir del
     * {@link Parcel} creado en {@link #writeToParcel(Parcel, int)}
     */
    public static final Creator<Field> CREATOR = new Creator<Field>() {
        @Override
        public Field createFromParcel(Parcel source) {
            return new Field(source);
        }

        @Override
        public Field[] newArray(int size) {
            return new Field[size];
        }
    };
}

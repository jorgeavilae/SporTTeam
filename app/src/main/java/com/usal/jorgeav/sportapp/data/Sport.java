package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.usal.jorgeav.sportapp.sportselection.SportsListFragment;
import com.usal.jorgeav.sportapp.utils.Utiles;

import java.util.ArrayList;

/**
 * Representa una Deporte del modelo.
 * <p></p>
 * Implementa la interfaz Parcelable para poder guardar este {@link Object} en
 * el {@link android.os.Bundle} que se asocia a {@link SportsListFragment} en su creación y se
 * utiliza para pasarle deportes.
 *
 * @see SportsListFragment#newInstance(String, ArrayList)
 */
@SuppressWarnings("unused")
public class Sport implements Parcelable {
    /**
     * Identificador único del deporte
     */
    private String sportID;
    /**
     * Referencia de la imagen que representa el deporte.
     * Recurso de {@link com.usal.jorgeav.sportapp.R}
     */
    private Integer iconDrawableId;
    /**
     * Puntuación del deporte
     */
    private Double punctuation;
    /**
     * Número de votos. Usado cuando esta clase se utiliza como {@link SportCourt}
     */
    private Integer votes;

    /**
     * Constructor con argumentos
     *
     * @param sportID identificador del deporte
     * @param punctuation puntuación del deporte
     * @param votes número de votos
     */
    public Sport(String sportID, Double punctuation, Integer votes) {
        this.sportID = sportID;
        this.iconDrawableId = Utiles.getSportIconFromResource(sportID);
        this.punctuation = punctuation;
        this.votes = votes;
    }

    public String getSportID() {
        return sportID;
    }

    public Integer getIconDrawableId() {
        return iconDrawableId;
    }

    public Double getPunctuation() {
        return punctuation;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setPunctuation(Double punctuation) {
        this.punctuation = punctuation;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "Sport{" +
                "sportID='" + sportID + '\'' +
                ", iconDrawableId=" + iconDrawableId +
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
     * Escribe este {@link Sport} en un {@link Parcel}
     *
     * @param dest Destino de la operación
     * @param flags opcional
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.sportID);
        dest.writeValue(this.iconDrawableId);
        dest.writeValue(this.punctuation);
        dest.writeValue(this.votes);
    }

    /**
     * Constructor que ejecuta una operación inversa a {@link #writeToParcel(Parcel, int)}
     *
     * @param in Parcel del que extraer los datos para {@link Sport}
     */
    protected Sport(Parcel in) {
        this.sportID = in.readString();
        this.iconDrawableId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.punctuation = (Double) in.readValue(Double.class.getClassLoader());
        this.votes = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    /**
     * Variable estática usada para crear instancias de {@link Sport} a partir del
     * {@link Parcel} creado en {@link #writeToParcel(Parcel, int)}
     */
    public static final Parcelable.Creator<Sport> CREATOR = new Parcelable.Creator<Sport>() {
        @Override
        public Sport createFromParcel(Parcel source) {
            return new Sport(source);
        }

        @Override
        public Sport[] newArray(int size) {
            return new Sport[size];
        }
    };
}

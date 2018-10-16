package com.usal.jorgeav.sportapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase representa a una Alarma del modelo.
 */
public class Alarm {
    /**
     * Identificador único
     */
    private String id;
    /**
     * Identificador del deportes que busca esta alarma
     */
    private String sport_id;
    /**
     * Identificador de la instalación que observa esta alarma o null
     */
    private String field_id;
    /**
     * Identificador de la ciudad donde está puesta esta alarma
     */
    private String city;
    /**
     * Componente latitud de la dirección donde está puesta esta alarma. Si hay una instalación
     * en {@link #field_id}, coincide con la componente latitud de la dirección de la instalación.
     */
    private Double coord_latitude;
    /**
     * Componente longitud de la dirección donde está puesta esta alarma. Si hay una instalación
     * en {@link #field_id}, coincide con la componente longitud de la dirección de la instalación.
     */
    private Double coord_longitude;
    /**
     * Limite inferior del periodo de fechas en el que deben jugarse los partidos que busca
     * esta alarma
     */
    private Long date_from;
    /**
     * Limite inferior del periodo de fechas en el que deben jugarse los partidos que busca
     * esta alarma
     */
    private Long date_to;
    /**
     * Limite inferior del número de jugadores totales que deben tener los partidos que busca
     * esta alarma
     */
    private Long total_players_from;
    /**
     * Limite superior del número de jugadores totales que deben tener los partidos que busca
     * esta alarma
     */
    private Long total_players_to;
    /**
     * Limite inferior del número de puestos vacantes que deben tener los partidos que busca
     * esta alarma
     */
    private Long empty_players_from;
    /**
     * Limite superior del número de jugadores totales que deben tener los partidos que busca
     * esta alarma
     */
    private Long empty_players_to;

    /**
     * Constructor sin argumentos. Permite transformar un datos obtenidos desde Firebase Realtime
     * Database con DataSnapshot.getValue(Class), siempre y cuando las variables tengan el mismo
     * nombre que las etiquetas de la base de datos del servidor.
     *
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     * DataSnapshot.getValue(Class)</a>
     */
    public Alarm() {
        // Default constructor required for calls to DataSnapshot.getValue(Alarm.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param id                 identificador de alarma
     * @param sport_id           identificador de deporte
     * @param field_id           identificador de instalación o null
     * @param city               nombre de ciudad
     * @param coord              coordenadas del lugar de la alarma
     * @param date_from          limite inferior del periodo de fechas
     * @param date_to            limite superior del periodo de fechas
     * @param total_players_from limite inferior de puestos totales
     * @param total_players_to   limite superior de puestos totales
     * @param empty_players_from limite inferior de puestos vacantes
     * @param empty_players_to   limite superior de puestos vacantes
     */
    public Alarm(String id, String sport_id, String field_id, String city, LatLng coord,
                 Long date_from, Long date_to,
                 Long total_players_from, Long total_players_to,
                 Long empty_players_from, Long empty_players_to) {
        this.id = id;
        this.sport_id = sport_id;
        this.field_id = field_id;
        this.city = city;
        if (coord != null) {
            this.coord_latitude = coord.latitude;
            this.coord_longitude = coord.longitude;
        } else {
            this.coord_latitude = null;
            this.coord_longitude = null;
        }
        this.date_from = date_from;
        this.date_to = date_to;
        this.total_players_from = total_players_from;
        this.total_players_to = total_players_to;
        this.empty_players_from = empty_players_from;
        this.empty_players_to = empty_players_to;
    }

    public String getId() {
        return id;
    }

    public String getSport_id() {
        return sport_id;
    }

    public String getField_id() {
        return field_id;
    }

    public String getCity() {
        return city;
    }

    public Double getCoord_latitude() {
        return coord_latitude;
    }

    public Double getCoord_longitude() {
        return coord_longitude;
    }

    public Long getDate_from() {
        return date_from;
    }

    public Long getDate_to() {
        return date_to;
    }

    public Long getTotal_players_from() {
        return total_players_from;
    }

    public Long getTotal_players_to() {
        return total_players_to;
    }

    public Long getEmpty_players_from() {
        return empty_players_from;
    }

    public Long getEmpty_players_to() {
        return empty_players_to;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSport_id(String sport_id) {
        this.sport_id = sport_id;
    }

    public void setField_id(String field_id) {
        this.field_id = field_id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCoord_latitude(Double coord_latitude) {
        this.coord_latitude = coord_latitude;
    }

    public void setCoord_longitude(Double coord_longitude) {
        this.coord_longitude = coord_longitude;
    }

    public void setDate_from(Long date_from) {
        this.date_from = date_from;
    }

    public void setDate_to(Long date_to) {
        this.date_to = date_to;
    }

    public void setTotal_players_from(Long total_players_from) {
        this.total_players_from = total_players_from;
    }

    public void setTotal_players_to(Long total_players_to) {
        this.total_players_to = total_players_to;
    }

    public void setEmpty_players_from(Long empty_players_from) {
        this.empty_players_from = empty_players_from;
    }

    public void setEmpty_players_to(Long empty_players_to) {
        this.empty_players_to = empty_players_to;
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link Alarm}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Alarm.SPORT, this.sport_id);
        result.put(FirebaseDBContract.Alarm.FIELD, this.field_id);
        result.put(FirebaseDBContract.Alarm.CITY, this.city);
        result.put(FirebaseDBContract.Alarm.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.Alarm.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.Alarm.DATE_FROM, this.date_from);
        result.put(FirebaseDBContract.Alarm.DATE_TO, this.date_to);
        result.put(FirebaseDBContract.Alarm.TOTAL_PLAYERS_FROM, this.total_players_from);
        result.put(FirebaseDBContract.Alarm.TOTAL_PLAYERS_TO, this.total_players_to);
        result.put(FirebaseDBContract.Alarm.EMPTY_PLAYERS_FROM, this.empty_players_from);
        result.put(FirebaseDBContract.Alarm.EMPTY_PLAYERS_TO, this.empty_players_to);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "Alarm{" +
                "id='" + id + '\'' +
                ", sport_id='" + sport_id + '\'' +
                ", field_id='" + field_id + '\'' +
                ", city='" + city + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", date_from=" + date_from +
                ", date_to=" + date_to +
                ", total_players_from=" + total_players_from +
                ", total_players_to=" + total_players_to +
                ", empty_players_from=" + empty_players_from +
                ", empty_players_to=" + empty_players_to +
                '}';
    }
}

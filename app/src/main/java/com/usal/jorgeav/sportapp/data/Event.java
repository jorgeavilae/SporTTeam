package com.usal.jorgeav.sportapp.data;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Representa un Partido del modelo
 */
public class Event {
    /**
     * Identificador único del partido
     */
    private String event_id;
    /**
     * Identificador único del deporte del partido
     */
    private String sport_id;
    /**
     * Identificador único de la instalación dónde se juega, si lo hay
     */
    private String field_id;
    /**
     * Nombre del partido
     */
    private String name;
    /**
     * Dirección dónde se juega el partido. Si hay una instalación en {@link #field_id},
     * coincide con la dirección de la instalación.
     */
    private String address;
    /**
     * Ciudad dónde se juega el partido. Si hay una instalación en {@link #field_id},
     * coincide con la ciudad de la instalación.
     */
    private String city;
    /**
     * Componente latitud de la dirección dónde se juega el partido. Si hay una instalación
     * en {@link #field_id}, coincide con la componente latitud de la dirección de la instalación.
     */
    private Double coord_latitude;
    /**
     * Componente longitud de la dirección dónde se juega el partido. Si hay una instalación
     * en {@link #field_id}, coincide con la componente longitud de la dirección de la instalación.
     */
    private Double coord_longitude;
    /**
     * Fecha y hora a la que empieza el partido
     */
    private Long date;
    /**
     * Identificador del usuario creador del partido
     */
    private String owner;
    /**
     * Número total de jugadores del partido
     */
    private Long total_players;
    /**
     * Número de puestos vacantes para el partido
     */
    private Long empty_players;
    /**
     * Colección de participantes al evento. Cada participante representa un usuario registrado
     * en la aplicación, mediante su identificador de usuario. El valor es true si
     * participa en el partido y false si está bloqueado.
     */
    private HashMap<String, Boolean> participants;
    /**
     * Colección de participantes simulados. Cada {@link SimulatedUser} representa una persona
     * invitada por alguno de los participantes. Se guardan bajo su identificador único de
     * usuario simulado.
     */
    private HashMap<String, SimulatedUser> simulated_participants;

    /**
     * Constructor sin argumentos. Permite transformar un datos obtenidos desde Firebase Realtime
     * Database con DataSnapshot.getValue(Class), siempre y cuando las variables tengan el mismo
     * nombre que las etiquetas de la base de datos del servidor.
     *
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     * DataSnapshot.getValue(Class)</a>
     */
    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param mId                    Identificador del partido
     * @param mSport                 Identificador del deporte
     * @param mField                 Identificador de la instalación o null
     * @param address                Dirección del partido
     * @param coord                  Coordenadas de la dirección partido
     * @param mName                  Nombre del partido
     * @param mCity                  Ciudad del partido
     * @param mDate                  Fecha y hora del partido en milisegundos
     * @param mOwner                 Identificador del usuario creador del partido
     * @param mTotalPlayers          Puestos totales en el partido
     * @param mEmptyPlayers          Puestos vacantes para el partido
     * @param participants           Map de participantes
     * @param simulated_participants Map de participantes simulados
     */
    public Event(String mId, String mSport, String mField, String address, LatLng coord, String mName,
                 String mCity, Long mDate, String mOwner, Long mTotalPlayers, Long mEmptyPlayers,
                 HashMap<String, Boolean> participants, HashMap<String, SimulatedUser> simulated_participants) {
        this.event_id = mId;
        this.sport_id = mSport;
        this.field_id = mField;
        this.address = address;
        this.name = mName;
        this.city = mCity;
        if (coord != null) {
            this.coord_latitude = coord.latitude;
            this.coord_longitude = coord.longitude;
        } else {
            this.coord_latitude = null;
            this.coord_longitude = null;
        }
        this.date = mDate;
        this.owner = mOwner;
        this.total_players = mTotalPlayers;
        this.empty_players = mEmptyPlayers;
        this.participants = participants;
        this.simulated_participants = simulated_participants;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getSport_id() {
        return sport_id;
    }

    public String getField_id() {
        return field_id;
    }

    public String getAddress() {
        return address;
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

    public String getName() {
        return (name != null ? name : "");
    }

    public Long getDate() {
        return date;
    }

    public String getOwner() {
        return owner;
    }

    public Long getTotal_players() {
        return total_players;
    }

    public Long getEmpty_players() {
        return empty_players;
    }

    public void setEmpty_players(Long empty_players) {
        this.empty_players = empty_players;
    }

    public HashMap<String, Boolean> getParticipants() {
        return participants;
    }

    public HashMap<String, SimulatedUser> getSimulated_participants() {
        return simulated_participants;
    }

    /**
     * Añade un participante a la lista
     *
     * @param userId       identificador del usuario participante
     * @param participates true si participa, false si está bloqueado
     */
    public void addToParticipants(String userId, Boolean participates) {
        if (this.participants == null) this.participants = new HashMap<>();
        this.participants.put(userId, participates);
    }

    /**
     * Elimina uno de los participantes de la lista
     *
     * @param userId Identificador del participante a eliminar
     */
    public void deleteParticipant(String userId) {
        if (this.participants != null) {
            this.participants.remove(userId);
            if (this.participants.size() == 0) this.participants = null;
        }
    }

    /**
     * Añade un participante simulado a su lista
     *
     * @param key         identificador del usuario simulado
     * @param participant usuario simulado que se añade
     */
    public void addToSimulatedParticipants(String key, SimulatedUser participant) {
        if (this.simulated_participants == null) this.simulated_participants = new HashMap<>();
        this.simulated_participants.put(key, participant);
    }

    /**
     * Elimina uno de los participantes simulados de la lista
     *
     * @param key Identificador del participante simulado a eliminar
     */
    public void deleteSimulatedParticipant(String key) {
        if (this.simulated_participants != null) {
            this.simulated_participants.remove(key);
            if (this.simulated_participants.size() == 0) this.simulated_participants = null;
        }
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link Event}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Event.SPORT, this.sport_id);
        result.put(FirebaseDBContract.Event.FIELD, this.field_id);
        result.put(FirebaseDBContract.Event.ADDRESS, this.address);
        result.put(FirebaseDBContract.Event.CITY, this.city);
        result.put(FirebaseDBContract.Event.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.Event.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.Event.NAME, this.name);
        result.put(FirebaseDBContract.Event.OWNER, this.owner);
        result.put(FirebaseDBContract.Event.DATE, this.date);
        result.put(FirebaseDBContract.Event.TOTAL_PLAYERS, this.total_players);
        result.put(FirebaseDBContract.Event.EMPTY_PLAYERS, this.empty_players);
        result.put(FirebaseDBContract.Event.PARTICIPANTS, this.participants);
        result.put(FirebaseDBContract.Event.SIMULATED_PARTICIPANTS, this.simulated_participants);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "Event{" +
                "event_id='" + event_id + '\'' +
                ", sport_id='" + sport_id + '\'' +
                ", field_id='" + field_id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", date=" + date +
                ", owner='" + owner + '\'' +
                ", total_players=" + total_players +
                ", empty_players=" + empty_players +
                ", participants=" + participants +
                ", simulated_participants=" + simulated_participants +
                '}';
    }

    /**
     * Comprueba si este partido es igual a un {@link Object} dado
     *
     * @param o Presumiblemente, el partido con el que comparar este
     * @return true si son iguales, false en cualquier otro caso
     */
    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (total_players.longValue() != event.total_players.longValue()) return false;
        if (empty_players.longValue() != event.empty_players.longValue()) return false;
        if (event_id != null ? !event_id.equals(event.event_id) : event.event_id != null)
            return false;
        if (sport_id != null ? !sport_id.equals(event.sport_id) : event.sport_id != null)
            return false;
        if (field_id != null ? !field_id.equals(event.field_id) : event.field_id != null)
            return false;
        if (name != null ? !name.equals(event.name) : event.name != null) return false;
        if (address != null ? !address.equals(event.address) : event.address != null) return false;
        if (city != null ? !city.equals(event.city) : event.city != null) return false;
        if (coord_latitude != null ? !coord_latitude.equals(event.coord_latitude) : event.coord_latitude != null)
            return false;
        if (coord_longitude != null ? !coord_longitude.equals(event.coord_longitude) : event.coord_longitude != null)
            return false;
        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        if (owner != null ? !owner.equals(event.owner) : event.owner != null) return false;
        if (participants != null ? !participants.equals(event.participants) : event.participants != null)
            return false;
        if (simulated_participants != null ? !simulated_participants.equals(event.simulated_participants) : event.simulated_participants != null)
            return false;

        return true;
    }
}
package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Representación de una Invitación del modelo
 */
@SuppressWarnings("unused")
public class Invitation {
    /**
     * Identificador único del usuario que envía la invitación
     */
    private String sender;
    /**
     * Identificador único del usuario que recibe la invitación
     */
    private String receiver;
    /**
     * Identificador único del partido al que refiere la invitación
     */
    private String event;
    /**
     * Fecha y hora en el que se creó la invitación, en milisegundos
     */
    private Long date;

    /**
     * Constructor sin argumentos.  Permite transformar un datos obtenidos desde Firebase Realtime
     * Database con DataSnapshot.getValue(Class), siempre y cuando las variables tengan el mismo
     * nombre que las etiquetas de la base de datos del servidor.
     *
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     * DataSnapshot.getValue(Class)</a>
     */
    public Invitation() {
        // Default constructor required for calls to DataSnapshot.getValue(Invitation.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param sender   identificador del usuario emisor
     * @param receiver identificador del usuario receptor
     * @param event    identificador del evento al que refiere
     * @param date     fecha y hora de creación de la invitación, en milisegundos
     */
    public Invitation(String sender, String receiver, String event, Long date) {
        this.sender = sender;
        this.receiver = receiver;
        this.event = event;
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getEvent() {
        return event;
    }

    public Long getDate() {
        return date;
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link Invitation}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Invitation.SENDER, this.sender);
        result.put(FirebaseDBContract.Invitation.RECEIVER, this.receiver);
        result.put(FirebaseDBContract.Invitation.EVENT, this.event);
        result.put(FirebaseDBContract.Invitation.DATE, this.date);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "Invitation{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", event='" + event + '\'' +
                ", date=" + date +
                '}';
    }
}

package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Representación de un usuario simulado del modelo
 */
@SuppressWarnings("unused")
public class SimulatedUser {
    /**
     * Nombre del usuario simulado
     */
    private String alias;
    /**
     * Foto de perfil del usuario simulado, URL a Firebase Storage o null
     */
    private String profile_picture;
    /**
     * Edad del usuario simulado
     */
    private Long age;
    /**
     * Identificador único del usuario que ha añadido a este usuario simulado
     */
    private String owner;

    /**
     * Constructor sin argumentos. Permite transformar un datos obtenidos desde Firebase Realtime
     * Database con DataSnapshot.getValue(Class), siempre y cuando las variables tengan el mismo
     * nombre que las etiquetas de la base de datos del servidor.
     *
     * @see <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     * DataSnapshot.getValue(Class)</a>
     */
    public SimulatedUser() {
        // Default constructor required for calls to DataSnapshot.getValue(SimulatedUser.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param alias           nombre del usuario simulado
     * @param profile_picture foto de perfil del usuario simulado
     * @param age             edad del usuario
     * @param owner           identificador del usuario creador del usuario simulado
     */
    public SimulatedUser(String alias, String profile_picture, Long age, String owner) {
        this.alias = alias;
        this.profile_picture = profile_picture;
        this.age = age;
        this.owner = owner;
    }

    public String getAlias() {
        return alias;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Long getAge() {
        return age;
    }

    public String getOwner() {
        return owner;
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link SimulatedUser}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.User.ALIAS, this.alias);
        result.put(FirebaseDBContract.User.PROFILE_PICTURE, this.profile_picture);
        result.put(FirebaseDBContract.User.AGE, this.age);
        result.put(FirebaseDBContract.Event.OWNER, this.owner);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "SimulatedUser{" +
                "alias='" + alias + '\'' +
                ", profile_picture='" + profile_picture + '\'' +
                ", age=" + age +
                ", owner=" + owner +
                '}';
    }
}

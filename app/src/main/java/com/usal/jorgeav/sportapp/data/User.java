package com.usal.jorgeav.sportapp.data;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Esta clase representa a un Usuario del modelo.
 * Mantiene una variable para todos sus datos, incluidos los deportes que practica
 */
public class User {
    /**
     * Identificador único
     */
    private String uid;
    /**
     * Dirección de correo eléctronico
     */
    private String email;
    /**
     * Nombre del ususario
     */
    private String alias;
    /**
     * Nombre de la ciudad
     */
    private String city;
    /**
     * Componente latitude de las coordenadas correspondientes al centro de la ciudad
     */
    private Double coord_latitude;
    /**
     * Componente longitud de las coordenadas correspondientes al centro de la ciudad
     */
    private Double coord_longitude;
    /**
     * Edad del usuario, en años
     */
    private Long age;
    /**
     * URL de la imagen de perfil, almacenada en Firebase Storage
     */
    private String profile_picture;
    /**
     * Deportes practicados. La clave es el identidicador del deporte y el valor es el nivel
     * de juego que tiene en él. Sólo aparecen los que tienen una puntuación mayor que cero.
     */
    private Map<String, Double> sports_practiced;

    /**
     * Constructor sin argumentos. Necesario para el parseo de este objeto desde Firebase
     * Realtime Database con
     * {@link
     * <a href= "https://firebase.google.com/docs/reference/admin/java/reference/com/google/firebase/database/DataSnapshot.html#getValue(com.google.firebase.database.GenericTypeIndicator%3CT%3E)">
     *     DataSnapshot.getValue(Class)
     * </a>}
     */
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    /**
     * Constructor con argumentos
     *
     * @param uid identificador de usuario
     * @param email correo eléctronico
     * @param alias nombre
     * @param city ciudad
     * @param coord_latitude coordenada latitud de la ciudad
     * @param coord_longitude oordenada longitud de la ciudad
     * @param age edad, en años
     * @param profile_picture URL de la imagen de perfil
     * @param sports_practiced deportes practicados
     */
    public User(String uid, String email, String alias, String city,
                Double coord_latitude, Double coord_longitude, Long age, String profile_picture,
                Map<String, Double> sports_practiced) {
        this.uid = uid;
        this.email = email;
        this.alias = alias;
        this.city = city;
        this.coord_latitude = coord_latitude;
        this.coord_longitude = coord_longitude;
        this.age = age;
        this.profile_picture = profile_picture;
        this.sports_practiced = sports_practiced;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAlias() {
        return alias;
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

    public Long getAge() {
        return age;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public Map<String, Double> getSports_practiced() {
        return sports_practiced;
    }

    /**
     * Introduce los datos de este objeto en un Map para Firebase Realtime Database.
     * La clave se obtiene de la clase diccionario {@link FirebaseDBContract} y como valor se
     * utilizan las variables de la clase.
     *
     * @return Map con los datos de {@link User}
     */
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.User.ALIAS, this.alias);
        result.put(FirebaseDBContract.User.EMAIL, this.email);
        result.put(FirebaseDBContract.User.AGE, this.age);
        result.put(FirebaseDBContract.User.PROFILE_PICTURE, this.profile_picture);
        result.put(FirebaseDBContract.User.CITY, this.city);
        result.put(FirebaseDBContract.User.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.User.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.User.SPORTS_PRACTICED, this.sports_practiced);
        return result;
    }

    /**
     * Representación del objeto en cadena de texto
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", alias='" + alias + '\'' +
                ", city='" + city + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", age=" + age +
                ", profile_picture='" + profile_picture + '\'' +
                ", sports_practiced=" + sports_practiced +
                '}';
    }
}

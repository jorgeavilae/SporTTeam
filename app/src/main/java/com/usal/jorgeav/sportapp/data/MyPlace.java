package com.usal.jorgeav.sportapp.data;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

/**
 * Representa un lugar del mapa. Utilizado en la selección manual de direcciones en el mapa,
 * durante la geocodificación inversa.
 * <p>
 * Se crea a partir de la respuesta obtenida de la consulta a Google Geocoding API. Mas
 * información en Geocoding Responses.
 * <p>
 * Implementa la interfaz Parcelable para poder guardar este {@link Object} en variables de
 * estado entre cambios de configuración
 * ({@link android.app.Activity#onConfigurationChanged(Configuration)}, o en {@link Intent}
 * para enviarla a otra {@link android.app.Activity}
 *
 * @see com.usal.jorgeav.sportapp.mainactivities.MapsActivity#onOptionsItemSelected(MenuItem)
 * @see com.usal.jorgeav.sportapp.mainactivities.EventsActivity#onActivityResult(int, int, Intent)
 * @see <a href= "https://developers.google.com/maps/documentation/geocoding/start">
 * Google Geocoding API</a>
 * @see <a href= "https://developers.google.com/maps/documentation/geocoding/intro#GeocodingResponses">
 * Geocoding Responses</a>
 */
@SuppressWarnings("unused")
public class MyPlace implements Parcelable {
    /**
     * Código de la respuesta obtenida del servidor a realizar geocodificación inversa
     */
    private String status;
    /**
     * Identificador del lugar, obtenido de la respuesta del servidor
     */
    private String placeId;
    /**
     * Dirección del lugar
     */
    private String address;
    /**
     * Nombre de la ciudad o pueblo en la que se encuentra este lugar
     */
    private String shortNameLocality; //Ciudad - Pueblo
    /**
     * Nombre de la provincia en la que se encuentra este lugar
     */
    private String city; //Provincia
    /**
     * Coordenadas de este lugar
     */
    private LatLng coordinates;
    /**
     * Coordenadas del noreste del marco recomendado por el servidor para mostrar la dirección
     */
    private LatLng viewPortNortheast;
    /**
     * Coordenadas del sudoeste del marco recomendado por el servidor para mostrar la dirección
     */
    private LatLng viewPortSouthwest;

    /**
     * Constructor con argumentos
     *
     * @param status            código de estado de la respuesta del servidor
     * @param placeId           identificador del lugar
     * @param address           dirección del lugar
     * @param shortNameLocality nombre de la ciudad o pueblo
     * @param city              nombre de la provincia
     * @param coordinates       coordenadas del lugar
     * @param viewPortNortheast coordenadas del noreste del marco para mostrar el lugar
     * @param viewPortSouthwest coordenadas del sudoeste del marco para mostrar el lugar
     */
    public MyPlace(String status, String placeId, String address, String shortNameLocality,
                   String city, LatLng coordinates, LatLng viewPortNortheast, LatLng viewPortSouthwest) {
        this.status = status;
        this.placeId = placeId;
        this.address = address;
        this.shortNameLocality = shortNameLocality;
        this.city = city;
        this.coordinates = coordinates;
        this.viewPortNortheast = viewPortNortheast;
        this.viewPortSouthwest = viewPortSouthwest;
    }

    /**
     * Constructor con un sólo argumento. Usado cuando el código de la respuesta es error
     *
     * @param status código del estado de la respuesta obtenida del servidor
     */
    public MyPlace(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Comprueba si la respuesta, y por tanto el {@link MyPlace} creado, son correctos
     *
     * @return true si el código del estado de la respuesta es correcto, falso en otro caso
     */
    public boolean isSucceed() {
        return this.status.equals("OK");
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getAddress() {
        return address;
    }

    public String getShortNameLocality() {
        return shortNameLocality;
    }

    public String getCity() {
        return city;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public LatLng getViewPortNortheast() {
        return viewPortNortheast;
    }

    public LatLng getViewPortSouthwest() {
        return viewPortSouthwest;
    }

    /**
     * Representación del objeto en cadena de texto
     *
     * @return la cadena de texto con los datos del objeto
     */
    @Override
    public String toString() {
        return "MyPlace{" +
                "status='" + status + '\'' +
                " placeId='" + placeId + '\'' +
                ", address='" + address + '\'' +
                ", shortNameLocality='" + shortNameLocality + '\'' +
                ", city='" + city + '\'' +
                ", coordinates=" + coordinates +
                ", viewPortNortheast=" + viewPortNortheast +
                ", viewPortSouthwest=" + viewPortSouthwest +
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
     * Escribe este {@link MyPlace} en un {@link Parcel}
     *
     * @param dest  Destino de la operación
     * @param flags opcional
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.status);
        dest.writeString(this.placeId);
        dest.writeString(this.address);
        dest.writeString(this.shortNameLocality);
        dest.writeString(this.city);
        dest.writeParcelable(this.coordinates, flags);
        dest.writeParcelable(this.viewPortNortheast, flags);
        dest.writeParcelable(this.viewPortSouthwest, flags);
    }

    /**
     * Constructor que ejecuta una operación inversa a {@link #writeToParcel(Parcel, int)}
     *
     * @param in Parcel del que extraer los datos para {@link MyPlace}
     */
    private MyPlace(Parcel in) {
        this.status = in.readString();
        this.placeId = in.readString();
        this.address = in.readString();
        this.shortNameLocality = in.readString();
        this.city = in.readString();
        this.coordinates = in.readParcelable(LatLng.class.getClassLoader());
        this.viewPortNortheast = in.readParcelable(LatLng.class.getClassLoader());
        this.viewPortSouthwest = in.readParcelable(LatLng.class.getClassLoader());
    }

    /**
     * Variable estática usada para crear instancias de {@link MyPlace} a partir del
     * {@link Parcel} creado en {@link #writeToParcel(Parcel, int)}
     */
    public static final Creator<MyPlace> CREATOR = new Creator<MyPlace>() {
        @Override
        public MyPlace createFromParcel(Parcel source) {
            return new MyPlace(source);
        }

        @Override
        public MyPlace[] newArray(int size) {
            return new MyPlace[size];
        }
    };
}

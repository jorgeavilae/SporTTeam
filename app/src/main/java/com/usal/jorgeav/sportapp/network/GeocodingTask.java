package com.usal.jorgeav.sportapp.network;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.data.MyPlace;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * En esta clase se implementan los métodos necesarios para realizar la tarea de la geocodificación
 * inversa proporcionada por la Geocoding API, uno de los servicios web de Google Maps Platform.
 * <p>
 * Implementa un método para construir la URL necesaria para la geocodificación inversa y otro
 * método para transformar la respuesta, en formato JSON, a objetos de la aplicación {@link MyPlace}
 * <p>
 * La URL se construye con unos parámetros que indican el tipo de dirección que se está buscando y
 * con las coordenadas indicadas por el usuario en el mapa. A partir de ella, se obtiene la
 * respuesta de los servidores de Google Maps con las posibles direcciones con las que coinciden
 * esas coordenadas.
 *
 * @see <a href= "https://developers.google.com/maps/documentation">
 * Google Maps Platform</a>
 * @see <a href= "https://developers.google.com/maps/documentation/geocoding/intro">
 * Geocoding API</a>
 * @see <a href= "https://developers.google.com/maps/documentation/geocoding/intro#ReverseGeocoding">
 * ReverseGeocoding</a>
 */
public class GeocodingTask {
    /**
     * Nombre de la clase
     */
    public static final String TAG = GeocodingTask.class.getSimpleName();

    /**
     * Base de la URL necesaria para realizar la consulta
     */
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/";
    /**
     * Formato en el que se quiere la respuesta
     */
    private static final String RESPONSE_TYPE = "json";
    /**
     * Etiqueta para indicar el parámetro "coordenadas"
     */
    private static final String PARAM_LATLNG = "latlng";
    /**
     * Etiqueta para indicar el parámetro "clave", donde especificar la clave de desarrollador
     * proporcionada para esta aplicación.
     */
    private static final String PARAM_KEY = "key";
    /**
     * Etiqueta para indicar los tipos de resultados que deben buscarse. En este caso: direcciones
     * de calles, carreteras e intersecciones.
     *
     * @see <a href= "https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#Types">
     * Geocoding Types</a>
     */
    private static final String PARAM_RESULT_TYPE = "result_type";
    /**
     * Valor que se aplica a la etiqueta {@link #PARAM_RESULT_TYPE}
     */
    private static final String RESULT_TYPE = "street_address" + "|" + "route" + "|" + "intersection";

    /**
     * Obtiene, mediante geocodificación inversa, una dirección a partir de unas coordenadas. Es el
     * método base de la clase y el único público. Construye la URL
     * {@link #buildGeocodingUrl(String, Double, Double)}, obtiene la respuesta del servidor
     * {@link #getResponseFromHttpUrl(URL)}, y traduce la respuesta a un {@link MyPlace} con
     * {@link #jsonStringToMyPlace(String)}.
     *
     * @param apiKey clave de desarrollador proporcionada para usar Google Maps API
     * @param point  coordenadas que deben buscarse
     * @return un objeto {@link MyPlace} con la dirección y otros datos correspondientes a esas
     * coordenadas
     */
    synchronized public static MyPlace getMyPlaceObjectFromLatLngLocation(String apiKey, LatLng point) {
        try {
            /* The getUrl method will return the URL that we need to get the Places in JSON. */
            URL requestUrl = buildGeocodingUrl(apiKey, point.latitude, point.longitude);
            if (requestUrl == null) return null;

            /* Use the URL to retrieve the JSON */
            String jsonResponse = getResponseFromHttpUrl(requestUrl);

            /* Parse the JSON into a MyPlaces object */
            return jsonStringToMyPlace(jsonResponse);

        } catch (Exception e) {
            /* Server probably invalid */
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Construye la URL de consulta al servicio de geocodificación mediante los parámetros
     * proporcionados y las variables globales de la clase.
     *
     * @param apiKey    clave de desarrollador proporcionada para usar Google Maps API
     * @param latitude  componente latitud de las coordenadas que deben buscarse
     * @param longitude componente longitud de las coordenadas que deben buscarse
     * @return un objeto {@link URL} con la dirección a la que conectarse
     */
    private static URL buildGeocodingUrl(String apiKey, Double latitude, Double longitude) {
        Uri queryUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(RESPONSE_TYPE)
                .appendQueryParameter(PARAM_LATLNG, latitude + "," + longitude)
                .appendQueryParameter(PARAM_RESULT_TYPE, RESULT_TYPE)
                .appendQueryParameter(PARAM_KEY, apiKey)
                .build();

        try {
            URL queryUrl = new URL(queryUri.toString());
            Log.v(TAG, "URL: " + queryUrl);
            return queryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Se conecta mediante HTTP a la URL proporcionada y obtiene una respuesta en formato texto.
     *
     * @param url dirección al servidor de Google para realizar la geocodificación inversa
     * @return la respuesta del servidor en formato cadena de texto
     * @throws IOException excepción de lectura si le es imposible leer a través de la conexión HTTP,
     *                     la respuesta del servidor.
     */
    private static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Transforma la cadena de texto proporcionada en un objeto {@link MyPlace} sabiendo que la
     * cadena de texto está en formato JSON y conociendo los nombres de las etiquetas.
     * <p>
     * Primero se comprueba que el código de la respuesta sea el correcto y luego se extraen los
     * parámetros necesarios para la creación de un {@link MyPlace} que será devuelto como resultado.
     *
     * @param response cadena de texto que debe procesarse
     * @return un objeto {@link MyPlace} con los datos extraídos de la cadena de texto
     * @throws JSONException excepción lanzada en caso de que se produzca algún error al extraer
     *                       parámetros del {@link JSONObject}
     * @see <a href= "https://developers.google.com/maps/documentation/geocoding/intro#reverse-status">
     * ReverseGeocoding response types</a>
     * @see <a href= "https://developers.google.com/maps/documentation/geocoding/intro#ReverseGeocoding">
     * ReverseGeocoding JSON response example</a>
     */
    private static MyPlace jsonStringToMyPlace(String response)
            throws JSONException {

        JSONObject json = new JSONObject(response);

        /* Is there an error? */
        if (!json.has("status")) return new MyPlace("UNKNOWN_ERROR");
        String statusCode = json.getString("status");

        // https://developers.google.com/maps/documentation/geocoding/intro?hl=es-419#reverse-response
        switch (statusCode) {
            case "OK":
                break;
            case "ZERO_RESULTS":
                /* Maybe latlng in a remote location */
                Log.e(TAG, "getMyPlaceFromJson: Maybe latlng in a remote location " + statusCode);
                return new MyPlace("ZERO_RESULTS");
            case "OVER_QUERY_LIMIT":
                /* Over your quota. */
                Log.e(TAG, "getMyPlaceFromJson: Over quota " + statusCode);
                return new MyPlace("OVER_QUERY_LIMIT");
            case "REQUEST_DENIED":
                /* API key invalid */
                Log.e(TAG, "getMyPlaceFromJson: API key invalid " + statusCode);
                return new MyPlace("REQUEST_DENIED");
            case "INVALID_REQUEST":
                /* Missing latlng or error in result_type */
                Log.e(TAG, "getMyPlaceFromJson: Missing latlng or error in result_type " + statusCode);
                return new MyPlace("INVALID_REQUEST");
            case "UNKNOWN_ERROR":
                /* Probably a bad connection */
                Log.e(TAG, "getMyPlaceFromJson: Probably a bad connection " + statusCode);
                return new MyPlace("UNKNOWN_ERROR");
            default:
                return new MyPlace("UNKNOWN_ERROR");
        }

        JSONObject jsonFirstResult = json.getJSONArray("results").getJSONObject(0);

        String placeId = jsonFirstResult.getString("place_id");
        String address = jsonFirstResult.getString("formatted_address");
        String shortNameLocality = "";
        String city = "";
        JSONArray jsonArrayAddresses = jsonFirstResult.getJSONArray("address_components");
        for (int i = 0; i < jsonArrayAddresses.length(); i++) {
            JSONObject addressComponent = jsonArrayAddresses.getJSONObject(i);
            JSONArray addressTypes = addressComponent.getJSONArray("types");
            for (int j = 0; j < addressTypes.length(); j++) {
                if (addressTypes.getString(j).equals("locality")) //Ciudad - Pueblo
                    shortNameLocality = addressComponent.getString("long_name");
                if (addressTypes.getString(j).equals("administrative_area_level_2")) //Provincia
                    city = addressComponent.getString("long_name");
            }
        }
        if (TextUtils.isEmpty(city)) city = shortNameLocality;

        // Check if address belongs to current user's city
        String currentUserCity = UtilesPreferences.getCurrentUserCity(MyApplication.getAppContext());
        if (!TextUtils.equals(shortNameLocality, currentUserCity)
                && !TextUtils.equals(city, currentUserCity)) {
            Log.e(TAG, "getMyPlaceFromJson: Point out of current city (" + currentUserCity + ") bounds."
                    + " Point in " + shortNameLocality + ", " + city);
            return new MyPlace("OUT_OF_BOUNDS");
        }

        double lat = jsonFirstResult.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        double lng = jsonFirstResult.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        LatLng coordinates = new LatLng(lat, lng);

        double viewPortNortheastLat = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("northeast").getDouble("lat");
        double viewPortNortheastLng = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("northeast").getDouble("lng");
        LatLng viewPortNortheast = new LatLng(viewPortNortheastLat, viewPortNortheastLng);

        double viewPortSouthwestLat = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("southwest").getDouble("lat");
        double viewPortSouthwestLng = jsonFirstResult.getJSONObject("geometry").getJSONObject("viewport")
                .getJSONObject("southwest").getDouble("lng");
        LatLng viewPortSouthwest = new LatLng(viewPortSouthwestLat, viewPortSouthwestLng);

        /*
         * "geometry" : {
         *    "location" : {
         *        "lat" : 37.4224764,
         *        "lng" : -122.0842499
         *    },
         *    "location_type" : "ROOFTOP",
         *    "viewport" : {
         *        "northeast" : {
         *            "lat" : 37.4238253802915,
         *            "lng" : -122.0829009197085
         *        },
         *        "southwest" : {
         *            "lat" : 37.4211274197085,
         *            "lng" : -122.0855988802915
         *        }
         *    }
         * },
         */

        return new MyPlace(statusCode, placeId, address, shortNameLocality,
                city, coordinates, viewPortNortheast, viewPortSouthwest);
    }

}

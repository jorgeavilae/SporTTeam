package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.preferences.SettingsFragment;

/**
 * Clase con métodos auxiliares, invocados desde varios puntos de la aplicación, que proveen de
 * funcionalidad útil para guardar y extraer datos de {@link SharedPreferences}
 */
@SuppressWarnings("unused")
public class UtilesPreferences {
    /**
     * Nombre de la clase
     */
    private static final String TAG = UtilesPreferences.class.getSimpleName();

    /**
     * Coordenada latitud de la ciudad de Salamanca. Para utilizarla por defecto.
     */
    public static final double SALAMANCA_LATITUDE = 40.9701039;
    /**
     * Coordenada longitud de la ciudad de Salamanca. Para utilizarla por defecto.
     */
    public static final double SALAMANCA_LONGITUDE = -5.6635397;
    /**
     * Coordenada latitud de la ciudad de Cáceres. Para utilizarla por defecto.
     */
    public static final double CACERES_LATITUDE = 39.4752765;
    /**
     * Coordenada longitud de la ciudad de Cáceres. Para utilizarla por defecto.
     */
    public static final double CACERES_LONGITUDE = -6.3724247;

    /**
     * Nombre del archivo de {@link SharedPreferences} asociado a esta aplicación
     */
    private static final String PREFS_FILE = "PREFS_FILE";
    /**
     * Etiqueta bajo la cual se almacena la coordenada latitud de la ciudad actual del usuario.
     */
    private static final String KEY_PREF_COORD_LAT = "pref_coord_lat";
    /**
     * Etiqueta bajo la cual se almacena la coordenada longitud de la ciudad actual del usuario.
     */
    private static final String KEY_PREF_COORD_LNG = "pref_coord_lng";

    /**
     * Consulta a la base de datos y guarda la ciudad del usuario actual en {@link SharedPreferences}
     *
     * @param context contexto bajo el que se accede a {@link SharedPreferences}
     */
    public static void setCurrentUserCity(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        String city = UtilesContentProvider.getCurrentUserCityFromContentProvider();
        if (city == null) return;
        editor.putString(SettingsFragment.KEY_PREF_CITY, city);
        editor.apply();
    }

    /**
     * Consulta a la base de datos y guarda las coordenadas de la ciudad del usuario actual en
     * {@link SharedPreferences}
     *
     * @param context contexto bajo el que se accede a {@link SharedPreferences}
     */
    public static void setCurrentUserCityCoords(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        LatLng coord = UtilesContentProvider.getCurrentUserCityCoordsFromContentProvider();
        if (coord == null) return;
        editor.putFloat(KEY_PREF_COORD_LAT, (float) coord.latitude);
        editor.putFloat(KEY_PREF_COORD_LNG, (float) coord.longitude);
        editor.apply();
    }

    /**
     * Obtiene de {@link SharedPreferences} la ciudad del usuario actual
     *
     * @param context contexto bajo el que se accede a {@link SharedPreferences}
     * @return nombre de la ciudad del usuario actual
     */
    public static String getCurrentUserCity(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        String city = settings.getString(SettingsFragment.KEY_PREF_CITY, null);
        if (city != null) return city;
        else {
            setCurrentUserCity(context);
            return UtilesContentProvider.getCurrentUserCityFromContentProvider();
        }
    }

    /**
     * Obtiene de {@link SharedPreferences} las coordenadas de la ciudad del usuario actual
     *
     * @param context contexto bajo el que se accede a {@link SharedPreferences}
     * @return coordenadas de la ciudad del usuario actual
     */
    public static LatLng getCurrentUserCityCoords(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        float latitude = settings.getFloat(KEY_PREF_COORD_LAT, 0);
        float longitude = settings.getFloat(KEY_PREF_COORD_LNG, 0);
        if (latitude > 0 && longitude > 0) return new LatLng(latitude, longitude);
        else {
            setCurrentUserCityCoords(context);
            return UtilesContentProvider.getCurrentUserCityCoordsFromContentProvider();
        }
    }
}

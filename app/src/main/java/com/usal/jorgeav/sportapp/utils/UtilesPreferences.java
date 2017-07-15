package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Jorge Avila on 15/07/2017.
 */

public class UtilesPreferences {
    private static final String TAG = UtilesPreferences.class.getSimpleName();

    private static final String PREFS_FILE = "PREFS_FILE";
    private static final String CITY_PREF_KEY = "CITY_PREF_KEY";
    private static final String COORD_LAT_PREF_KEY = "COORD_LAT_PREF_KEY";
    private static final String COORD_LNG_PREF_KEY = "COORD_LNG_PREF_KEY";

    public static void setCurrentUserCity (Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        String city = UtilesContentProvider.getCurrentUserCityFromContentProvider();
        if (city == null) return;
        editor.putString(CITY_PREF_KEY, city);
        editor.apply();
    }

    public static void setCurrentUserCityCoords (Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        LatLng coord = UtilesContentProvider.getCurrentUserCityCoordsFromContentProvider();
        if (coord == null) return;
        editor.putFloat(COORD_LAT_PREF_KEY, (float) coord.latitude);
        editor.putFloat(COORD_LNG_PREF_KEY, (float) coord.longitude);
        editor.apply();
    }

    public static String getCurrentUserCity(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        String city = settings.getString(CITY_PREF_KEY, null);
        if (city != null) return city;
        else {
            setCurrentUserCity(context);
            return UtilesContentProvider.getCurrentUserCityFromContentProvider();
        }
    }

    public static LatLng getCurrentUserCityCoords(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        float latitude = settings.getFloat(COORD_LAT_PREF_KEY, 0);
        float longitude = settings.getFloat(COORD_LNG_PREF_KEY, 0);
        if (latitude > 0 && longitude > 0) return new LatLng(latitude, longitude);
        else {
            setCurrentUserCityCoords(context);
            return UtilesContentProvider.getCurrentUserCityCoordsFromContentProvider();
        }
    }
}

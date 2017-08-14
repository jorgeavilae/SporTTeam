package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.preferences.SettingsFragment;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class UtilesPreferences {
    // Used in case COORD_PREF are not set
    public static final double SALAMANCA_LATITUDE = 40.9701039;
    public static final double SALAMANCA_LONGITUDE = -5.6635397;
    public static final double CACERES_LATITUDE = 39.4752765;
    public static final double CACERES_LONGITUDE = -6.3724247;

    private static final String TAG = UtilesPreferences.class.getSimpleName();

    private static final String PREFS_FILE = "PREFS_FILE";
    private static final String COORD_LAT_PREF_KEY = "COORD_LAT_PREF_KEY";
    private static final String COORD_LNG_PREF_KEY = "COORD_LNG_PREF_KEY";

    public static void setCurrentUserCity (Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor editor = settings.edit();
        String city = UtilesContentProvider.getCurrentUserCityFromContentProvider();
        if (city == null) return;
        editor.putString(SettingsFragment.KEY_PREF_CITY, city);
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
        String city = settings.getString(SettingsFragment.KEY_PREF_CITY, null);
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

package com.usal.jorgeav.sportapp.utils;

import android.content.res.Resources;
import android.util.TypedValue;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.Field;

import java.util.List;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class Utiles {
    private static final String TAG = Utiles.class.getSimpleName();

    //TODO mover a settings
    public static final double DISTANCE_ALLOWED = 50;

    public static String getFirebaseStorageRootReference() {
        /* https://firebase.google.com/docs/storage/android/create-reference?hl=es-419 */
        /* https://stackoverflow.com/a/40647158/4235666 */
        FirebaseOptions opts = FirebaseApp.getInstance().getOptions();
        return "gs://" + opts.getStorageBucket();
    }

    public static String getCurrentUserId() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = ""; if (fUser != null) myUserID = fUser.getUid();
        return myUserID;
    }

    public static float getFloatFromResources(Resources resources, int resourceID) {
        TypedValue outValue = new TypedValue();
        resources.getValue(resourceID, outValue, true);
        return outValue.getFloat();
    }

    public static int searchCoordinatesInFieldList(List<Field> fieldsList, LatLng coordinates) {
        for (int i = 0; i < fieldsList.size(); i++) {
            Field f = fieldsList.get(i);
            if (f.getCoord_latitude() == coordinates.latitude
                    && f.getCoord_longitude() == coordinates.longitude) return i;
        }
        return -1;
    }

    public static int searchClosestFieldInList(List<Field> fieldsList, LatLng coordinates) {
        for (int i = 0; i < fieldsList.size(); i++) {
            Field f = fieldsList.get(i);

            double distance = distanceHaversine(f.getCoord_latitude(), f.getCoord_longitude(),
                    coordinates.latitude, coordinates.longitude);

            if (distance <= DISTANCE_ALLOWED) return i;
        }
        return -1;
    }

    /* https://stackoverflow.com/a/123305/4235666 */
    /* https://en.wikipedia.org/wiki/Haversine_formula */
    public static double distanceHaversine(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; // in meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = sindLat * sindLat + sindLng * sindLng
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return (earthRadius * c);
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");  //match a number without '-' and decimal.
    }
}

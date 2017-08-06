package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;

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

    public static String getCurrentUserEmail() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserEmail = ""; if (fUser != null) myUserEmail = fUser.getEmail();
        return myUserEmail;
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

    /* http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) (dpWidth / 180);
    }

    public static boolean isNumeric(String str) {
        return str.matches("\\d+");  //match a number without '-' and decimal.
    }

    public static void checkEmailFromDatabaseIsCorrect(FirebaseUser fUser, User myUserDatabase) {
        String fUserEmail = ""; if (fUser != null) fUserEmail = fUser.getEmail();
        // If the user try to change his email but cancel process by clicking in url
        // from email received, could has an email address in FirebaseUser and a different
        // one in FirebaseDatabase. So it needs to update.
        if (fUserEmail != null && !TextUtils.isEmpty(fUserEmail)) {
            if (!fUserEmail.equals(myUserDatabase.getEmail())) {
                //Update email in FirebaseDatabase
                FirebaseActions.updateUserEmail(myUserDatabase.getUid(), fUserEmail);

                myUserDatabase.setEmail(fUserEmail);
            }
        }
    }

    public static int getSportIconFromResource(String sportId) {
        return MyApplication.getAppContext().getResources()
                .getIdentifier(sportId, "drawable", MyApplication.getAppContext().getPackageName());
    }

    public static int getPlayerIconFromResource(long empty_players, long total_players) {
        if (empty_players >= 0 && total_players >= 0 && empty_players <= total_players) {
            float proportion = ((float)empty_players / (float)total_players) * 100;
            if (proportion == 0)
                return R.drawable.logo_full;
            else if (proportion > 0 && proportion < 35)
                return R.drawable.logo_almost_full;
            else if (proportion >= 35 && proportion < 65)
                return R.drawable.logo_half;
            else if (proportion >= 65 && proportion < 100)
                return R.drawable.logo_almost_empty;
            else if (proportion == 100)
                return R.drawable.logo_empty;
        }
        return -1;
    }
}

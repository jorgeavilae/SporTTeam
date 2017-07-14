package com.usal.jorgeav.sportapp.utils;

import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.data.provider.SportteamContract;

/**
 * Created by Jorge Avila on 17/05/2017.
 */

public class Utiles {
    private static final String TAG = Utiles.class.getSimpleName();

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

    public static String getCurrentUserCity(Context context, String currentUserID) {
        // TODO: 23/06/2017 obtener de sharedPreferences
        String result = null;
        Cursor c = context.getContentResolver().query(
                SportteamContract.UserEntry.CONTENT_USER_URI,
                SportteamContract.UserEntry.USER_COLUMNS,
                SportteamContract.UserEntry.USER_ID + " = ?",
                new String[]{currentUserID},
                null);
        if (c != null && c.moveToFirst()) {
            result = c.getString(SportteamContract.UserEntry.COLUMN_CITY);
            c.close();
        }
        return result;
    }

    public static LatLng getCurrentUserCityLatLong(Context context, String currentUserID) {
        return null;
    }

}

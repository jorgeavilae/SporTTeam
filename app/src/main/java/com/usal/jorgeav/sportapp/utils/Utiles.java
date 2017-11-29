package com.usal.jorgeav.sportapp.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.User;
import com.usal.jorgeav.sportapp.mainactivities.FieldsActivity;
import com.usal.jorgeav.sportapp.network.firebase.actions.UserFirebaseActions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Utiles {
    private static final String TAG = Utiles.class.getSimpleName();

    /* Request code for ask permissions to gallery and camera */
    public static final int RC_GALLERY_CAMERA_PERMISSIONS = 3;
    private static final double DISTANCE_ALLOWED = 50;

    public static String getFirebaseStorageRootReference() {
        /* https://firebase.google.com/docs/storage/android/create-reference?hl=es-419 */
        /* https://stackoverflow.com/a/40647158/4235666 */
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        if (firebaseApp != null) {
            FirebaseOptions opts = firebaseApp.getOptions();
            return "gs://" + opts.getStorageBucket();
        }
        return "";
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
    private static double distanceHaversine(double lat1, double lng1, double lat2, double lng2) {
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

        /* https://stackoverflow.com/a/16276351/4235666 */
        int image_size = (int) (context.getResources().getDimension(R.dimen.grid_item_image_size)
                / context.getResources().getDisplayMetrics().density);

        return (int) (dpWidth / image_size);
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
                UserFirebaseActions.updateUserEmail(myUserDatabase.getUid(), fUserEmail);

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


    /* Checks if external storage is available for read and write */
    public static boolean isStorageCameraPermissionGranted(Activity activityContext) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activityContext.checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && activityContext.checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permissions are granted");
                return true;
            } else {
                Log.v(TAG, "Permissions are revoked");
                ActivityCompat.requestPermissions(activityContext, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, RC_GALLERY_CAMERA_PERMISSIONS);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permissions are granted");
            return true;
        }
    }

    /* Starts Crop Activity (UCrop), to crop given Uri photo, in the given Activity context */
    public static void startCropActivity(Uri photoFilesystemUri, Activity activity) {
        long millis = System.currentTimeMillis();
        // Uri to store cropped photo in filesystem
        Uri croppedPhotoFilesystemUri;
        if (photoFilesystemUri.getLastPathSegment().contains("."))
            croppedPhotoFilesystemUri = getAlbumStorageDir(photoFilesystemUri.getLastPathSegment().replace(".", "_cropped" + millis + "."));
        else
            croppedPhotoFilesystemUri = getAlbumStorageDir(photoFilesystemUri.getLastPathSegment() + "_cropped" + millis);
        UCrop.of(photoFilesystemUri, croppedPhotoFilesystemUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .start(activity);
    }

    /* Returns directory in filesystem to store cropped photo */
    private static Uri getAlbumStorageDir(@NonNull String path) {
        // Get the directory for the user's public pictures directory.
        File f = MyApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri uri = Uri.fromFile(f).buildUpon().appendPath(path).build();
        File file = new File(uri.getPath());
        if (!file.exists()) {
            try { if (!file.createNewFile())
                Log.e(TAG, "getAlbumStorageDir: file not created");
            } catch (IOException e) { e.printStackTrace(); }
        }
        return Uri.fromFile(file);
    }

    public static void startFieldsActivityAndNewField(Activity activity) {
        /* https://stackoverflow.com/a/24927301/4235666 */
        Intent startActivityIntent = Intent.makeRestartActivityTask(
                new ComponentName(activity, FieldsActivity.class));

        startActivityIntent.putExtra(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "dummy");
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(startActivityIntent);
        activity.finish();
    }

    // Check if the sport doesn't need a field
    public static boolean sportNeedsField(String sportId) {
        if (sportId == null || TextUtils.isEmpty(sportId)) try {
            throw new Exception("Invalid sportId value: " + sportId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String[] arraySports = MyApplication.getAppContext().getResources().getStringArray(R.array.sport_id_values);

        // Not Running and Not Biking
        return !sportId.equals(arraySports[0]) && !sportId.equals(arraySports[1]);
    }

    // Check if the sport doesn't need teams (infinite participants allowed)
    public static boolean sportNeedsTeams(String sportId) {
        if (sportId == null || TextUtils.isEmpty(sportId)) try {
            throw new Exception("Invalid sportId value: " + sportId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String[] arraySports = MyApplication.getAppContext().getResources().getStringArray(R.array.sport_id_values);

        // Not Running and Not Biking and Not Skating
        return !sportId.equals(arraySports[0]) && !sportId.equals(arraySports[1]) && !sportId.equals(arraySports[2]);
    }

    public static Marker setCoordinatesInMap(Context context, GoogleMap map, LatLng coords) {
        // Prevent null coords
        boolean coordsAreCurrentCity = false;
        if (coords == null) {
            coordsAreCurrentCity = true;
            coords = UtilesPreferences.getCurrentUserCityCoords(context);
        }

        Marker marker = null;
        if (map != null && coords != null) {
            // Add a marker if coords aren't current city
            if (!coordsAreCurrentCity) {
                Resources res = context.getResources();
                float hue = Utiles.getFloatFromResources(res, R.dimen.hue_of_colorSportteam_logo);
                marker = map.addMarker(new MarkerOptions().position(coords)
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            }

            // Move the camera
            double bound = 0.00135;
            if (coordsAreCurrentCity) bound += 0.002;
            LatLng southwest = new LatLng(coords.latitude - bound, coords.longitude - bound);
            LatLng northeast = new LatLng(coords.latitude + bound, coords.longitude + bound);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
        }

        return marker;
    }
}

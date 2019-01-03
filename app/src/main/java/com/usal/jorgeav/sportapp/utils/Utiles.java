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

/**
 * Clase con métodos auxiliares que proveen de funcionalidad útil que se repite en varios puntos
 * de la aplicación.
 */
public class Utiles {
    /**
     * Nombre de la clase
     */
    private static final String TAG = Utiles.class.getSimpleName();

    /**
     * Identificador de la petición de permisos de uso de la cámara y la galería de fotos al
     * sistema Android. Se utiliza para identificar el resultado de la petición de permisos en
     * {@link Activity#onRequestPermissionsResult(int, String[], int[])}
     */
    public static final int RC_GALLERY_CAMERA_PERMISSIONS = 3;

    /**
     * Radio de la Tierra en metros.
     */
    private static final double EARTH_RADIUS = 6371000;
    /**
     * Distancia mínima necesaria para concluir que dos coordenadas diferentes no representan la
     * misma dirección. Expresada en la unidad de medida de {@link #EARTH_RADIUS}
     *
     * @see #distanceHaversine(double, double, double, double)
     */
    private static final double DISTANCE_ALLOWED = 100;

    /**
     * Obtiene la URL raíz del almacenamiento en el servidor de Firebase Storage.
     *
     * @return URL raíz del almacenamiento en Firebase Storage
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/storage/FirebaseStorage">
     * FirebaseStorage</a>
     */
    public static String getFirebaseStorageRootReference() {
        /* https://stackoverflow.com/a/40647158/4235666 */
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        if (firebaseApp != null) {
            FirebaseOptions opts = firebaseApp.getOptions();
            return "gs://" + opts.getStorageBucket();
        }
        return "";
    }

    /**
     * Obtiene el identificador del usuario actual a partir de Firebase User
     *
     * @return identificador del usuario actual
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     * FirebaseUser</a>
     */
    public static String getCurrentUserId() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserID = "";
        if (fUser != null) myUserID = fUser.getUid();
        return myUserID;
    }

    /**
     * Obtiene el email del usuario actual a partir de Firebase User
     *
     * @return email del usuario actual
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     * FirebaseUser</a>
     */
    public static String getCurrentUserEmail() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserEmail = "";
        if (fUser != null) myUserEmail = fUser.getEmail();
        return myUserEmail;
    }

    /**
     * Obtiene la URL de la foto de perfil del usuario actual a partir de Firebase User
     *
     * @return URL en formato texto de la foto de perfil del usuario actual
     * @see <a href= "https://firebase.google.com/docs/reference/android/com/google/firebase/auth/FirebaseUser">
     * FirebaseUser</a>
     */
    public static String getCurrentUserPhoto() {
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        Uri myUserPhotoUri = null;
        if (fUser != null) myUserPhotoUri = fUser.getPhotoUrl();
        String myUserPhoto = "";
        if (myUserPhotoUri != null) myUserPhoto = myUserPhotoUri.toString();
        return myUserPhoto;
    }

    /**
     * Obtiene un número en coma flotante almacenado en una de los archivos de recursos
     *
     * @param resources  referencia al almacén de recursos de la aplicación
     * @param resourceID identificador del número buscado
     * @return float buscado
     */
    public static float getFloatFromResources(Resources resources, int resourceID) {
        TypedValue outValue = new TypedValue();
        resources.getValue(resourceID, outValue, true);
        return outValue.getFloat();
    }

    /**
     * Busca las coordenadas indicadas entre la lista de instalaciones proporcionada. Devuelve el
     * índice de la instalación con las mismas coordenadas o -1 si no coinciden ningunas.
     *
     * @param coordinates coordenadas buscadas en la lista
     * @param fieldsList  lista de instalaciones {@link Field}
     * @return índice, dentro de la lista, de la instalación con las coordenadas indicadas; o null
     * si no coincide ninguna
     */
    public static int searchCoordinatesInFieldList(List<Field> fieldsList, LatLng coordinates) {
        for (int i = 0; i < fieldsList.size(); i++) {
            Field f = fieldsList.get(i);
            if (f.getCoord_latitude() == coordinates.latitude
                    && f.getCoord_longitude() == coordinates.longitude) return i;
        }
        return -1;
    }

    /**
     * De entre la lista de instalaciones proporcionada, busca la instalación cuyas coordenadas
     * guarden una distancia menor de la indicada en {@link #DISTANCE_ALLOWED} con las coordenadas
     * que se pasan como parámetro. Devuelve el índice de la instalación con las coordenadas que
     * guarden menos de esa distancia o -1 si no encuentra ningunas.
     *
     * @param coordinates coordenadas buscadas en la lista
     * @param fieldsList  lista de instalaciones {@link Field}
     * @return el índice de la instalación con las coordenadas que guarden menos de esa distancia
     * o -1 si no encuentra ningunas.
     * @see #distanceHaversine(double, double, double, double)
     */
    public static int searchClosestFieldInList(List<Field> fieldsList, LatLng coordinates) {
        for (int i = 0; i < fieldsList.size(); i++) {
            Field f = fieldsList.get(i);

            double distance = distanceHaversine(f.getCoord_latitude(), f.getCoord_longitude(),
                    coordinates.latitude, coordinates.longitude);

            if (distance <= DISTANCE_ALLOWED) return i;
        }
        return -1;
    }

    /**
     * Obtiene la distancia entre dos coordenadas utilizando la fórmula de Haversine. Necesita el
     * radio de la circunferencia sobre la que se posicionan esas dos coordenadas indicado en
     * {@link #EARTH_RADIUS}.
     *
     * @param lat1 latitud de la primera coordenada
     * @param lng1 longitud de la primera coordenada
     * @param lat2 latitud de la segunda coordenada
     * @param lng2 longitud de la segunda coordenada
     * @return distancia entre las dos coordenadas en la unidad de medida de {@link #EARTH_RADIUS}
     * @see <a href= "https://en.wikipedia.org/wiki/Haversine_formula">Fórmula de Haversine (Wikipedia)</a>
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    private static double distanceHaversine(double lat1, double lng1, double lat2, double lng2) {
        double latitude1_radians = Math.toRadians(lat1);
        double latitude2_radians = Math.toRadians(lat2);

        double latitude_difference_radians = Math.toRadians(lat2 - lat1);
        double sinOf_latitude_difference = Math.sin(latitude_difference_radians / 2);
        double haversine_latitude_difference = sinOf_latitude_difference * sinOf_latitude_difference;

        double longitude_difference_radians = Math.toRadians(lng2 - lng1);
        double sinOf_longitude_difference = Math.sin(longitude_difference_radians / 2);
        double haversine_longitude_difference = sinOf_longitude_difference * sinOf_longitude_difference;

        double haversine_dR = haversine_latitude_difference +
                Math.cos(latitude1_radians) * Math.cos(latitude2_radians) * haversine_longitude_difference;

        double arcSinOf_haversine_dR = Math.asin(Math.sqrt(haversine_dR));

        // Distance in earthRadius's units.
        double distance = 2 * EARTH_RADIUS * arcSinOf_haversine_dR;

        return distance;
    }

    /**
     * Dado el tamaño de un ítem de una colección tipo {@link android.widget.GridLayout} y dado el
     * ancho de la pantalla, calcula el número de items que caben en horizontal para establecer
     * ese número como número de columnas de la colección tipo {@link android.widget.GridLayout}.
     * <p>
     * Extrae el tamaño del ítem del archivo de recursos dimens.xml y el ancho de la pantalla del
     * objeto {@link DisplayMetrics}.
     *
     * @param context contexto bajo el que se ejecuta este método
     * @return número de columnas que caben en el ancho de la pantalla
     * @see <a href= "http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns">
     * GridLayout: autofit columns (StackOverflow)</a>
     */
    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        /* https://stackoverflow.com/a/16276351/4235666 */
        int image_size = (int) (context.getResources().getDimension(R.dimen.grid_item_image_size)
                / context.getResources().getDisplayMetrics().density);

        return (int) (dpWidth / image_size);
    }

    /**
     * Comprueba si una cadena de texto es un número decimal mayor que cero.
     *
     * @param str cadena de texto que se comprueba
     * @return true si cumple las condiciones, false en otro caso
     */
    public static boolean isNumeric(String str) {
        return str.matches("\\d+");  //match a number without '-' and decimal.
    }

    /**
     * Al cambiar la dirección de email, el usuario recibe un correo de aviso con un enlace para
     * revocar el cambio. Si esto ocurre, el usuario tendría una dirección de email en Firebase
     * Auth y otra diferente en Firebase Realtime Database. Este método comprueba que las dos
     * direcciones de email sean iguales y, si no lo son, establece la dirección de Firebase Auth
     * (la actualizada al revocar el cambio) en el usuario dentro de Firebase Realtime Database.
     *
     * @param fUser          usuario de Firebase Auth
     * @param myUserDatabase mismo usuario pero construido a partir de los datos de Firebase
     *                       Realtime Database.
     */
    public static void checkEmailFromDatabaseIsCorrect(FirebaseUser fUser, User myUserDatabase) {
        String fUserEmail = "";
        if (fUser != null) fUserEmail = fUser.getEmail();
        // If the user try to change his email but cancel process by clicking in url
        // from email received, could has an email address in FirebaseUser and a different
        // one in FirebaseDatabase. So it needs to update.
        if (fUserEmail != null && !TextUtils.isEmpty(fUserEmail)) {
            if (!fUserEmail.equals(myUserDatabase.getEmail())) {
                //Update email in FirebaseDatabase
                UserFirebaseActions.updateUserEmail(myUserDatabase.getUid(), fUserEmail);
                //Update email in object
                myUserDatabase.setEmail(fUserEmail);
            }
        }
    }

    /**
     * Obtiene el identificador de un recurso de imagen correspondiente al icono de un deporte, a
     * partir del identificador de dicho deporte.
     *
     * @param sportId identificador del deporte
     * @return identificador del recurso de imagen correspondiente al deporte
     */
    public static int getSportIconFromResource(String sportId) {
        return MyApplication.getAppContext().getResources()
                .getIdentifier(sportId, "drawable", MyApplication.getAppContext().getPackageName());
    }

    /**
     * Calcula el porcentaje de ocupación de un partido en base a sus puestos vacantes y sus puestos
     * totales y obtiene el identificador de un recurso de imagen correspondiente al icono
     * representativo de dicho porcentaje.
     *
     * @param empty_players puestos vacantes
     * @param total_players puestos totales
     * @return identificador del recurso de imagen correspondiente al porcentaje
     */
    public static int getPlayerIconFromResource(long empty_players, long total_players) {
        if (empty_players >= 0 && total_players > 0 && empty_players <= total_players) {
            float proportion = ((float) empty_players / (float) total_players) * 100;
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

    /**
     * Comprueba si se han concedido los permisos para acceder a la cámara de fotos y a la galería
     * de fotos.
     *
     * @param activityContext Actividad desde la que se invoca este método
     * @return true si se han concedido o false si no.
     */
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

    /**
     * Inicia la Actividad perteneciente a la librería uCrop, que permite recortar y rotar la foto
     * escogida en la aplicación de la cámara o en la galería de fotos.
     *
     * @param photoFilesystemUri ruta dentro del sistema de archivos del dispositivo de la imagen
     *                           escogida
     * @param activity           Actividad que desea iniciar las funciones de la librería uCrop y
     *                           que recibirá el resultado.
     * @see <a href= "https://github.com/Yalantis/uCrop">uCrop (Github)</a>
     */
    public static void startCropActivity(Uri photoFilesystemUri, Activity activity) {
        long millis = System.currentTimeMillis();
        // Uri to store cropped photo in filesystem
        Uri croppedPhotoFilesystemUri;
        String lastPathSegment = photoFilesystemUri.getLastPathSegment();
        if (lastPathSegment != null && lastPathSegment.contains("."))
            croppedPhotoFilesystemUri = getAlbumStorageDir(
                    lastPathSegment.replace(".", "_cropped_" + millis + "."));
        else
            croppedPhotoFilesystemUri = getAlbumStorageDir(
                    lastPathSegment + "_cropped_" + millis);
        UCrop.of(photoFilesystemUri, croppedPhotoFilesystemUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .start(activity);
    }

    /**
     * Devuelve una ruta del sistema de archivos construida a partir del directorio de la aplicación
     * y el nombre indicado. Aquí se guardarán las imágenes recortadas por uCrop.
     *
     * @param path nombre del archivo nuevo
     * @return ruta del directorio completada con el archivo indicado
     */
    /* Returns directory in filesystem to store cropped photo */
    private static Uri getAlbumStorageDir(@NonNull String path) {
        // Get the directory for the user's public pictures directory.
        File f = MyApplication.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Uri uri = Uri.fromFile(f).buildUpon().appendPath(path).build();
        File file = null;
        if (uri.getPath() != null)
            file = new File(uri.getPath());
        if (file != null && !file.exists()) {
            try {
                if (!file.createNewFile())
                    Log.e(TAG, "getAlbumStorageDir: file not created");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(file);
    }

    /**
     * Inicia el proceso de creación de una instalación nueva creando {@link FieldsActivity} y
     * añadiendo el indicador para crear la instalación nueva
     * {@link FieldsActivity#INTENT_EXTRA_CREATE_NEW_FIELD}. Finaliza la Actividad actual.
     *
     * @param activity Actividad actual desde la que se invoca este método.
     */
    public static void startFieldsActivityAndNewField(Activity activity) {
        Intent startActivityIntent = Intent.makeRestartActivityTask(
                new ComponentName(activity, FieldsActivity.class));

        startActivityIntent.putExtra(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "dummy");
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(startActivityIntent);
        activity.finish();
    }

    /**
     * Comprueba si el deporte indicado necesita una instalación para ser practicado. En la práctica,
     * comprueba si el deporte indicado es running o biking, en cuyo caso no necesitan instalación.
     *
     * @param sportId identificador del deporte
     * @return true si el deporte necesita una instalación, false en otro caso.
     */
    public static boolean sportNeedsField(String sportId) {
        if (sportId == null || TextUtils.isEmpty(sportId)) try {
            throw new Exception("Invalid sportId value: " + sportId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String[] arraySports = MyApplication.getAppContext().getResources().
                getStringArray(R.array.sport_id_values);

        // Not Running and Not Biking
        return !sportId.equals(arraySports[0]) && !sportId.equals(arraySports[1]);
    }

    // Check if the sport doesn't need teams (infinite participants allowed)

    /**
     * Comprueba si el deporte indicado permite un número infinito de participantes, es decir, si
     * no necesita equipos. En la práctica, comprueba si el deporte indicado es running, biking o
     * skating en cuyo caso no necesita equipos y permite infinitos participantes.
     *
     * @param sportId identificador del deporte
     * @return true si el deporte necesita una equipos, false en otro caso.
     */
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

    /**
     * Invoca {@link #setCoordinatesInMap(Context, GoogleMap, LatLng, boolean)} con
     * coordsAreCity = false.
     *
     * @param context contexto en el que se invoca este método
     * @param map     mapa de la interfaz sobre el que colocar la marca
     * @param coords  coordenadas en las que colocar la marca
     * @return la marca creada y colocada en el mapa, o null.
     */
    public static Marker setCoordinatesInMap(Context context, GoogleMap map, LatLng coords) {
        return setCoordinatesInMap(context, map, coords, false);
    }

    /**
     * Coloca una marca sobre el mapa en las coordenadas indicadas y centra la vista del mapa sobre
     * ellas. Comprueba si las coordenadas pertenecen a una ciudad, en cuyo caso sólo centra la
     * vista. Si las coordenadas son null, obtiene las coordenadas de la ciudad del usuario actual
     * de {@link UtilesPreferences#getCurrentUserCityCoords(Context)} y centra la cámara sobre ellas,
     * sin crear la marca. En otros casos crea una marca sobre la dirección indicada en las
     * coordenadas y la devuelve como resultado.
     *
     * @param context       contexto en el que se invoca este método
     * @param map           mapa de la interfaz sobre el que colocar la marca
     * @param coords        coordenadas en las que colocar la marca
     * @param coordsAreCity true si las coordenadas indican una ciudad en general, false si
     *                      indican una dirección en particular
     * @return la marca creada y colocada en el mapa, o null.
     */
    public static Marker setCoordinatesInMap(Context context, GoogleMap map, LatLng coords, boolean coordsAreCity) {
        // Prevent null coords
        if (coords == null) {
            coordsAreCity = true;
            coords = UtilesPreferences.getCurrentUserCityCoords(context);
        }

        Marker marker = null;
        if (map != null && coords != null) {
            // Add a marker if coords aren't current city
            if (!coordsAreCity) {
                Resources res = context.getResources();
                float hue = Utiles.getFloatFromResources(res, R.dimen.hue_of_colorSportteam_logo);
                marker = map.addMarker(new MarkerOptions().position(coords)
                        .icon(BitmapDescriptorFactory.defaultMarker(hue)));
            }

            // Move the camera
            double bound = 0.00135;
            if (coordsAreCity) bound += 0.002;
            LatLng southwest = new LatLng(coords.latitude - bound, coords.longitude - bound);
            LatLng northeast = new LatLng(coords.latitude + bound, coords.longitude + bound);
            LatLngBounds llb = new LatLngBounds(southwest, northeast);
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(llb, 0));
        }

        return marker;
    }
}

package com.usal.jorgeav.sportapp.network;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.usal.jorgeav.sportapp.mainactivities.LoginActivity;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;

import java.util.concurrent.TimeUnit;

/**
 * Realiza las tareas de inicialización para la sincronización con la base de datos en el momento
 * en el que la aplicación se inicia y existe un usuario identificado. Además, establece el
 * Servicio que debe ejecutarse cada cierto tiempo, en segundo plano, para mantener actualizada los
 * datos de la aplicación.
 * <p>
 * También se encarga de deshacer todas estas acciones si el usuario cierra su sesión.
 */
public class SportteamSyncInitialization {
    /**
     * Nombre de la clase
     */
    public static final String TAG = SportteamSyncInitialization.class.getSimpleName();

    /**
     * True si las acciones ya han sido programadas, false en caso contrario. Esta variable
     * impide que se creen varios veces los Listeners sobre los mismos datos de Firebase o que se
     * repita el Servicio que actualiza los datos en segundo plano.
     */
    private static boolean sScheduled = false;

    /**
     * Etiqueta identificativa del Servicio de actualización de datos.
     */
    private static final String SPORTTEAM_SYNC_TAG = "SPORTTEAM_SYNC_TAG";
    /**
     * Intervalo de tiempo que debe pasar entre cada ejecución del Servicio. En horas.
     */
    private static final int SYNC_INTERVAL_HOURS = 3;
    /**
     * Intervalo de tiempo que debe pasar entre cada ejecución del Servicio. En segundos.
     */
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    /**
     * Ventana de tiempo en el que el Servicio es ejecutado. Si por alguna razón no se ejecuta cada
     * {@link #SYNC_INTERVAL_SECONDS} este intervalo indica el tiempo que tiene para reintentarlo.
     */
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / SYNC_INTERVAL_HOURS;

    /**
     * Vincula los Listeners sobre los datos relevantes de Firebase Realtime Database por medio de
     * {@link FirebaseSync}. Crea y programa el Servicio que mantiene actualizado los datos por
     * medio de {@link #scheduleFirebaseJobDispatcherSync(Context)}
     *
     * @param context contexto de la Actividad que invoca este método
     */
    synchronized public static void initialize(@NonNull final Context context) {
        Log.i(TAG, "initialize: " + sScheduled);

        // Only perform schedule once per app lifetime, but asure Listeners are attached.
        if (sScheduled) {
            FirebaseSync.attachListeners();
            return;
        }
        sScheduled = true;

        //Populate Content Provider with data and pass LoginActivity if needed
        LoginActivity loginActivity = null;
        if (context instanceof LoginActivity) loginActivity = (LoginActivity) context;
        FirebaseSync.syncFirebaseDatabase(loginActivity);

        // This method create a task to synchronize Firebase data periodically.
        scheduleFirebaseJobDispatcherSync(context);
    }

    /**
     * Crea y programa el Servicio para mantener actualizados los datos. Esta tarea se realiza con
     * la ayuda de la librería Firebase JobDispatcher. Con ella se crea un {@link Job} con una
     * serie de parámetros como el Servicio que ejecuta ({@link SportteamFirebaseJobService}), la
     * política de reintentos, restricciones, tiempo de vida de la tarea, etc.
     *
     * @param context contexto de la Actividad bajo la que se ejecuta este método
     * @see <a href= "https://github.com/firebase/firebase-jobdispatcher-android">
     * Firebase JobDispatcher</a>
     */
    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context) {
        /* Init GooglePlayDriver and FirebaseJobDispatcher */
        Driver driver = new GooglePlayDriver(context);
        // TODO migrar FirebaseJobDispatcher to WorkManager
        //  https://developer.android.com/topic/libraries/architecture/workmanager/migrating-fb
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync */
        Job syncJob = dispatcher.newJobBuilder()
                .setService(SportteamFirebaseJobService.class)
                .setTag(SPORTTEAM_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncJob);
    }

    /**
     * Desvincula los Listeners sobre Firebase Realtime Database y borra la ejecución periódica del
     * Servicio especificada en {@link #scheduleFirebaseJobDispatcherSync(Context)}. Este es el
     * método opuesto a {@link #initialize(Context)}.
     *
     * @param context contexto de la Actividad bajo la que se ejecuta este método
     */
    synchronized public static void finalize(@NonNull final Context context) {
        Log.i(TAG, "finalize: " + sScheduled);
        FirebaseSync.detachListeners();

        // Only cancel schedule once per app lifetime.
        if (!sScheduled) return;
        sScheduled = false;

        /* This cancel the task which synchronize Firebase data periodically. */
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(SPORTTEAM_SYNC_TAG);
    }
}

package com.usal.jorgeav.sportapp.network;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.usal.jorgeav.sportapp.network.firebase.sync.EventsFirebaseSync;
import com.usal.jorgeav.sportapp.network.firebase.sync.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

/**
 * Servicio utilizado por {@link SportteamSyncInitialization} para mantener los datos actualizados.
 * Este Servicio se ejecuta cada cierto tiempo y se encarga de cargar las notificaciones (y mostrar
 * las que no estén comprobadas ya) y se encarga de cargar los partidos con los que el usuario
 * actual no tiene relación y comprobar si coinciden con alguna alarma (en cuyo caso mostraría una
 * notificación).
 * <p>
 * Para que ocurra periódicamente, debe ser programada por
 * {@link com.firebase.jobdispatcher.FirebaseJobDispatcher} y para ello necesita ser un
 * {@link android.app.Service} que herede de {@link JobService}, el tipo de Servicio con el que esta
 * librería trabaja.
 *
 * @see <a href= "https://github.com/firebase/firebase-jobdispatcher-android">
 * Firebase JobDispatcher</a>
 */
public class SportteamFirebaseJobService extends JobService {
    /**
     * Nombre de la clase
     */
    public static final String TAG = SportteamFirebaseJobService.class.getSimpleName();

    /**
     * Sincroniza (y comprueba) las notificaciones del usuario y sincroniza (y compara con las
     * alarmas establecidas) los partidos de la base de datos del servidor con los que el usuario
     * actual no tiene relación.
     * <p>
     * Función principal de {@link JobService}. Aquí se especifican las acciones a realizar en la
     * activación del Servicio.
     *
     * @param jobParameters parámetros
     * @return true si queda trabajo por realizar en otro hilo iniciado por este, false si el
     * trabajo ha terminado
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        FirebaseSync.loadMyNotifications(null);
        EventsFirebaseSync.loadEventsFromCity(UtilesPreferences.getCurrentUserCity(this));

        jobFinished(jobParameters, false);
        return false;
    }

    /**
     * Invoca {@link #jobFinished(JobParameters, boolean)} para parar la ejecución del Servicio.
     *
     * @param jobParameters parámetros
     * @return true si el Servicio debe reintentar su ejecución, false en otro caso.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobFinished(jobParameters, false);
        return false;
    }
}

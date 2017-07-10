package com.usal.jorgeav.sportapp.network;

import android.text.TextUtils;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

public class SportteamFirebaseJobService extends JobService {
    public static final String TAG = SportteamFirebaseJobService.class.getSimpleName();

    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "onStartJob: ");
        FirebaseSync.loadMyNotifications();
        /* Avisar al usuario cuando:
         *  - Recibe peticion de amistad                                        UID sender
         *  - Contestan peticion de amistad enviada por el (aceptan o rechazan) UID receiver
         *
         *  - Recibe invitacion a evento                                        EID
         *  - Acepta/Rechaza invitacion a evento                                EID
         *  - Recibe peticion de usuario a evento                               EID
         *  - Contestan peticion a evento enviada por el (aceptan o rechazan)   EID
         *
         *  - Se completa un evento al que asisto                               EID
         *  - Se va alguien de un evento al que asisto                          EID
         *  - Se cambia o borra un evento al que asisto                         EID
         *
        // TODO: 07/07/2017
         *  - Se crea un evento que coincide con alguna de mis alarmas          AID
         *  - Recordatorio de que un evento se va a producir                    EID
         *  - Despues de producirse un evento para calificar a los demas o poner resultado
         *
         */

        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUid = ""; if (fUser != null) myUid = fUser.getUid();
        if (!TextUtils.isEmpty(myUid))
            FirebaseSync.loadEventsFromCity(Utiles.getCurrentCity(this, myUid));

        jobFinished(jobParameters, false);
        return false;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobFinished(jobParameters, false);
        return false;
    }
}

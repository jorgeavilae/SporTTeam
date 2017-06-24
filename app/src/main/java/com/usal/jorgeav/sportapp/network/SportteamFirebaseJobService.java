package com.usal.jorgeav.sportapp.network;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;


/**
 * Created by Jorge Avila on 22/06/2017.
 */

// TODO: 22/06/2017 Alternativa para versiones anteriores
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SportteamFirebaseJobService extends JobService {

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
        /* Avisar al usuario cuando:
         *  - Recibe peticion de amistad
         *  - Contestan peticion de amistad enviada por el (aceptan o rechazan)
         *
         *  - Recibe invitacion a evento
         *  - Contestan peticion a evento enviada por el (aceptan o rechazan)
         *
         *  - Se completa un evento al que asisto
         *  - Se cambia un evento al que asisto
         *
         *  - Se crea un evento que coincide con alguna de mis alarmas
         */
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
        return false;
    }
}

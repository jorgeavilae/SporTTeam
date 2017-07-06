package com.usal.jorgeav.sportapp.network;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

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
         *  - Recibe peticion de amistad                                        UID sender
         *  - Contestan peticion de amistad enviada por el (aceptan o rechazan) UID receiver
         *
         *  - Recibe invitacion a evento                                        EID
         *  - Contestan peticion a evento enviada por el (aceptan o rechazan)   EID
         *
         *  - Se completa un evento al que asisto                               EID
         *  - Se cambia o borra un evento al que asisto                         EID
         *
         *  - Recordatorio de que un evento se va a producir                    EID
         *  - Despues de producirse un evento para calificar a los demas o poner resultado
         *
         *  - Se crea un evento que coincide con alguna de mis alarmas          AID
         */
        FirebaseSync.loadMyNotifications();
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

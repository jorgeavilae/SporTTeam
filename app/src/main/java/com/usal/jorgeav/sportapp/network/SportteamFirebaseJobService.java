package com.usal.jorgeav.sportapp.network;

import android.text.TextUtils;

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
        FirebaseSync.loadMyNotifications();
        /* Avisar al usuario cuando:
         *  - Recibe peticion de amistad - uid sender + "friends_requests_sent"             UID sender
         *  - Aceptan peticion de amistad - uid receiver + "friends"                        UID receiver
         *
         *  - Recibe invitacion a evento - eid + "events_invitations_received"              EID
         *  - Acepta/Rechaza invitacion a evento - uid + "events_invitations_sent" + eid    EID
         *  - Recibe peticion de usuario a evento - uid + "events_requests" + eid           EID
         *  - Aceptan/Rechaza peticion a evento - eid + "events_participation"              EID
         *
         *  - Se completa un evento al que asisto - eid + "empty_players"                   EID
         *  - Se va alguien de un evento al que asisto - eid + "empty_players"              EID
         *  - Se cambia un evento al que asisto - eid + "owner"                             EID
         *  - Se borra un evento al que asisto - eid + "owner"                              EID
         *
         *  - Se crea un evento que coincide con alguna alarma - aid + "alarms"             AID
        // TODO: 07/07/2017
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

package com.usal.jorgeav.sportapp.network;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

import java.util.concurrent.TimeUnit;

/**
 * Created by Jorge Avila on 22/06/2017.
 */

public class SportteamSyncUtils {
    private static boolean sScheduled;
    private static final String SPORTTEAM_SYNC_TAG = "SPORTTEAM_SYNC_TAG";

    /*
     * Interval at which to sync. Use TimeUnit for convenience, rather than
     * writing out a bunch of multiplication ourselves and risk making a silly mistake.
     */
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / SYNC_INTERVAL_HOURS;
//    private static final int SYNC_INTERVAL_SECONDS = 5;
//    private static final int SYNC_FLEXTIME_SECONDS = 3;
    // TODO: 06/07/2017  cambiar times
    // TODO: 06/07/2017
    // TODO: 06/07/2017

    /**
     * Creates periodic sync tasks and attach the listeners to Firebase Database.
     *
     * @param context Context that will be passed to other methods
     */
    synchronized public static void initialize(@NonNull final Context context) {

        FirebaseSync.syncFirebaseDatabase();

        /*
         * Only perform schedule once per app lifetime. If schedule has already been
         * performed, we have nothing else to do in this method.
         */
        if (sScheduled) return;   sScheduled = true;

        // TODO: 06/07/2017 inciar aunque no se haya inciado la app porque cuando se fuerza la detencion ya no se poduce este Job
        /* This method create its task to synchronize Firebase data periodically. */
        scheduleFirebaseJobDispatcherSync(context);
    }

    /**
     * Schedules a repeating sync of Users notification using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context){
        /* Init GooglePlayDriver and FirebaseJobDispatcher */
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        /* Create the Job to periodically sync */
        Job syncJob = dispatcher.newJobBuilder()
                /* The Service that will be used to sync data */
                .setService(SportteamFirebaseJobService.class)
                /* Set the UNIQUE tag used to identify this Job */
                .setTag(SPORTTEAM_SYNC_TAG)
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                .setConstraints(Constraint.ON_ANY_NETWORK)
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                .setLifetime(Lifetime.FOREVER)
                /*
                 * We want data to stay up to date, so we tell this Job to recur.
                 */
                .setRecurring(true)
                /*
                 * We want the data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                .setReplaceCurrent(true)
                /*
                 * RetryStrategy represents an approach to handling job execution failures.
                 * Expected schedule is: [30s, 60s, 120s, 240s, ..., 3600s]
                 */
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                /* Once the Job is ready, call the builder's build method to return the Job */
                .build();

        /* Schedule the Job with the dispatcher */
        dispatcher.schedule(syncJob);
    }
}

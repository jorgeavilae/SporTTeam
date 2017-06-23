package com.usal.jorgeav.sportapp.network;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * Created by Jorge Avila on 22/06/2017.
 */

public class SportteamSyncUtils {
    private static boolean sScheduled;

    /**
     * Creates periodic sync tasks and attach the listeners to Firebase Database.
     *
     * @param context Context that will be passed to other methods
     */
    synchronized public static void initialize(@NonNull final Context context) {
        /*
         * This method starts an IntentService to attach listeners from FirebaseDatabase
         * Those listeners performs inserts into Content Provider
         */
        startServiceToAttachListeners(context);

        /*
         * Only perform schedule once per app lifetime. If schedule has already been
         * performed, we have nothing else to do in this method.
         */
        if (sScheduled) return;   sScheduled = true;

        /* This method create its task to synchronize Firebase data periodically. */
        scheduleFirebaseJobDispatcherSync(context);

    }

    /**
     * Schedules a repeating sync of Sunshine's weather data using FirebaseJobDispatcher.
     * @param context Context used to create the GooglePlayDriver that powers the
     *                FirebaseJobDispatcher
     */
    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context){}

    /* Init GooglePlayDriver and FirebaseJobDispatcher */

    /* Create the Job to periodically sync Sunshine */
                /* The Service that will be used to sync Sunshine's data */
                /* Set the UNIQUE tag used to identify this Job */
                /*
                 * Network constraints on which this Job should run. We choose to run on any
                 * network, but you can also choose to run only on un-metered networks or when the
                 * device is charging. It might be a good idea to include a preference for this,
                 * as some users may not want to download any data on their mobile plan. ($$$)
                 */
                /*
                 * setLifetime sets how long this job should persist. The options are to keep the
                 * Job "forever" or to have it die the next time the device boots up.
                 */
                /*
                 * We want Sunshine's weather data to stay up to date, so we tell this Job to recur.
                 */
                /*
                 * We want the weather data to be synced every 3 to 4 hours. The first argument for
                 * Trigger's static executionWindow method is the start of the time frame when the
                 * sync should be performed. The second argument is the latest point in time at
                 * which the data should be synced. Please note that this end time is not
                 * guaranteed, but is more of a guideline for FirebaseJobDispatcher to go off of.
                 */
                /*
                 * If a Job with the tag with provided already exists, this new job will replace
                 * the old one.
                 */
                /* Once the Job is ready, call the builder's build method to return the Job */

        /* Schedule the Job with the dispatcher */

    /**
     * Helper method to perform a sync using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    public static void startServiceToAttachListeners(@NonNull final Context context){
        Intent intentToSync = new Intent(context, SportteamSyncIntentService.class);
        context.startService(intentToSync);
    }
}

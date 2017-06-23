package com.usal.jorgeav.sportapp.network;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by Jorge Avila on 22/06/2017.
 */

public class SportteamSyncUtils {
    private static boolean sInitialized;

    /**
     * Creates periodic sync tasks and checks to see if an immediate sync is required. If an
     * immediate sync is required, this method will take care of making sure that sync occurs.
     *
     * @param context Context that will be passed to other methods and used to access the
     *                ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {
        /*
         * Only perform initialization once per app lifetime. If initialization has already been
         * performed, we have nothing to do in this method.
         */
        if (sInitialized) return;   sInitialized = true;

        /*
         * This method call triggers Sunshine to create its task to synchronize weather data
         * periodically.
         */
        scheduleFirebaseJobDispatcherSync(context);

    /*
     * We need to check to see if our ContentProvider has data to display in our forecast
     * list. However, performing a query on the main thread is a bad idea as this may
     * cause our UI to lag. Therefore, we create a thread in which we will run the query
     * to check the contents of our ContentProvider.
     */

    /* URI for every row of weather data in our weather table*/

    /*
     * Since this query is going to be used only as a check to see if we have any
     * data (rather than to display data), we just need to PROJECT the ID of each
     * row. In our queries where we display data, we need to PROJECT more columns
     * to determine what weather details need to be displayed.
     */

    /* Here, we perform the query to check to see if we have any weather data */

    /*
     * A Cursor object can be null for various different reasons. A few are
     * listed below.
     *
     *   1) Invalid URI
     *   2) A certain ContentProvider's query method returns null
     *   3) A RemoteException was thrown.
     *
     * Bottom line, it is generally a good idea to check if a Cursor returned
     * from a ContentResolver is null.
     *
     * If the Cursor was null OR if it was empty, we need to sync immediately to
     * be able to display data to the user.
     */

    /* Make sure to close the Cursor to avoid memory leaks! */

    /* Finally, once the thread is prepared, fire it off to perform our checks. */
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
     * Helper method to perform a sync immediately using an IntentService for asynchronous
     * execution.
     *
     * @param context The Context used to start the IntentService for the sync.
     */
    //public static void startImmediateSync(@NonNull final Context context)
}

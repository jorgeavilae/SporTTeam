package com.usal.jorgeav.sportapp.network;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class SportteamSyncInitialization {
    public static final String TAG = SportteamSyncInitialization.class.getSimpleName();

    private static boolean sScheduled = false;
    private static final String SPORTTEAM_SYNC_TAG = "SPORTTEAM_SYNC_TAG";

    /* Interval at which to sync */
    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / SYNC_INTERVAL_HOURS;

    // Creates periodic sync tasks and attach the listeners to Firebase Database.
    synchronized public static void initialize(@NonNull final Context context) {
        Log.i(TAG, "initialize: "+sScheduled);

        // Only perform schedule once per app lifetime.
        if (sScheduled) return;   sScheduled = true;

        //Populate Content Provider with data and pass LoginActivity if needed
        LoginActivity loginActivity = null;
        if (context instanceof LoginActivity) loginActivity = (LoginActivity) context;
        FirebaseSync.syncFirebaseDatabase(loginActivity);

        // This method create a task to synchronize Firebase data periodically.
        scheduleFirebaseJobDispatcherSync(context);
    }

    // Schedules a repeating sync of Users notification using FirebaseJobDispatcher.
    private static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context){
        /* Init GooglePlayDriver and FirebaseJobDispatcher */
        Driver driver = new GooglePlayDriver(context);
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

    synchronized public static void finalize(@NonNull final Context context) {
        Log.i(TAG, "finalize: "+sScheduled);
        FirebaseSync.detachListeners();

        // Only cancel schedule once per app lifetime.
        if (!sScheduled) return;   sScheduled = false;

        /* This cancel the task which synchronize Firebase data periodically. */
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        dispatcher.cancel(SPORTTEAM_SYNC_TAG);
    }
}

package com.usal.jorgeav.sportapp.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Jorge Avila on 22/06/2017.
 */
/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SportteamSyncIntentService extends IntentService {

    public SportteamSyncIntentService() {
        super("SportteamSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        /**
         * Performs the network request for updated weather, parses the JSON from that request, and
         * inserts the new weather information into our ContentProvider. Will notify the user that new
         * weather has been loaded if the user hasn't been notified of the weather within the last day
         * AND they haven't disabled notifications in the preferences screen.
         *
         * @param context Used to access utility methods and the ContentResolver
         */
        // listeners de Firebase Database-

    }
}

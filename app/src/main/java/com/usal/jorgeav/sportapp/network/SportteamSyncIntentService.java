package com.usal.jorgeav.sportapp.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;

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
        /*
         * Performs the network request for updated database: attach listeners to
         * FirebaseDatabaseReferences, parses the DataSnapshot from that listeners, and
         * inserts the data into our ContentProvider.
         */
        FirebaseSync.syncFirebaseDatabase();

    }
}

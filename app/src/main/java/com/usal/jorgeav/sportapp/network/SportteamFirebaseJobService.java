package com.usal.jorgeav.sportapp.network;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.UtilesPreferences;

public class SportteamFirebaseJobService extends JobService {
    public static final String TAG = SportteamFirebaseJobService.class.getSimpleName();

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        FirebaseSync.loadMyNotifications(null);
        FirebaseSync.loadEventsFromCity(UtilesPreferences.getCurrentUserCity(this));

        jobFinished(jobParameters, false);
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobFinished(jobParameters, false);
        return false;
    }
}

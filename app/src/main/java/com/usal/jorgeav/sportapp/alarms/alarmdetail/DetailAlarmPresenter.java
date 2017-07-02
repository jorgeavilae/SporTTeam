package com.usal.jorgeav.sportapp.alarms.alarmdetail;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.google.firebase.auth.FirebaseAuth;
import com.usal.jorgeav.sportapp.data.Alarm;
import com.usal.jorgeav.sportapp.data.provider.SportteamLoader;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseActions;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseSync;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailAlarmPresenter implements DetailAlarmContract.Presenter, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = DetailAlarmPresenter.class.getSimpleName();

    DetailAlarmContract.View mView;

    public DetailAlarmPresenter(@NonNull DetailAlarmContract.View view) {
        this.mView = view;
    }

    @Override
    public void openAlarm(LoaderManager loaderManager, Bundle b) {
        String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        FirebaseSync.loadAnAlarm(alarmId);
        loaderManager.initLoader(SportteamLoader.LOADER_ALARM_ID, b, this);
        loaderManager.initLoader(SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID, b, this);
    }

    @Override
    public void deleteAlarm(Bundle b) {
        String alarmId = b.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseActions.deleteAlarm(userId, alarmId);
        ((Activity) mView.getActivityContext()).onBackPressed();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String alarmId = args.getString(DetailAlarmFragment.BUNDLE_ALARM_ID);
        switch (id) {
            case SportteamLoader.LOADER_ALARM_ID:
                return SportteamLoader
                        .cursorLoaderOneAlarm(mView.getActivityContext(), alarmId);
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                String myUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                return SportteamLoader
                        .cursorLoaderAlarmCoincidence(mView.getActivityContext(), alarmId, myUserId);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(data);
                break;
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                mView.showEvents(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SportteamLoader.LOADER_ALARM_ID:
                showAlarmDetails(null);
                break;
            case SportteamLoader.LOADER_ALARM_EVENTS_COINCIDENCE_ID:
                mView.showEvents(null);
                break;
        }
    }

    private void showAlarmDetails(Cursor data) {
        Alarm a = Utiles.cursorToAlarm(data);
        if (a != null) {
            mView.showAlarmId(a.getmId());
            mView.showAlarmSport(a.getmSport());
            mView.showAlarmPlace(a.getmField(), a.getmSport());
            mView.showAlarmDate(a.getmDateFrom(), a.getmDateTo());
            mView.showAlarmTotalPlayers(a.getmTotalPlayersFrom(), a.getmTotalPlayersTo());
            mView.showAlarmEmptyPlayers(a.getmEmptyPlayersFrom(), a.getmEmptyPlayersTo());
        } else {
            mView.clearUI();
        }
    }
}
